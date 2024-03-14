package com.amorabot.inscripted.skills;

import com.amorabot.inscripted.APIs.MessageAPI;
import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.APIs.damageAPI.DamageRouter;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Skills {

    public static void playerAbility(Player player, SkillTypes skillType, WeaponTypes weapon){
        switch (weapon){
            case AXE -> {
                switch (skillType){
                    case BASIC_ATTACK -> {
                        axeBasicAttackBy(player);
                    }
                    case MOVEMENT -> {
                        if (GlobalCooldownManager.skillcastBy(player.getUniqueId(),skillType,10)){
                            marauderMovement(player);
                        } else {
                            SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "block.note_block.basedrum", 0.9F, 1.0F);
                        }
                    }
                }
            }
            case SWORD -> {
                switch (skillType){
                    case BASIC_ATTACK -> {
                        swordBasicAttackBy(player);
                    }
                    case MOVEMENT -> {
                        if (GlobalCooldownManager.skillcastBy(player.getUniqueId(),skillType,3)){
                            gladiatorMovement(player);
                        } else {
                            SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "block.note_block.basedrum", 0.9F, 1.0F);
                        }
                    }
                }
            }
            case BOW -> {
                switch (skillType){
                    case BASIC_ATTACK -> {
                        bowBasicAttackBy(player);
                    }
                    case MOVEMENT -> {
                        if (GlobalCooldownManager.skillcastBy(player.getUniqueId(),skillType,5)){
                            mercenaryMovement(player);
                        } else {
                            SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "block.note_block.basedrum", 0.9F, 1.0F);
                        }
                    }
                }
            }
            case DAGGER -> {
                switch (skillType){
                    case BASIC_ATTACK -> {
                        daggerBasicAttackBy(player);
                    }
                    case MOVEMENT -> {
                        if (GlobalCooldownManager.fetchRemainingCooldownFor(player.getUniqueId(),skillType) > 0){
                            player.sendMessage("Movement skill on cooldown!");
                            return;
                        }
                        if (GlobalCooldownManager.skillcastBy(player.getUniqueId(),skillType,12)){
                            rogueMovement(player);
                        } else {
                            SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "block.note_block.basedrum", 0.9F, 1.0F);
                        }
                    }
                }
            }
            case WAND -> {
                switch (skillType){
                    case BASIC_ATTACK -> {
                        wandBasicAttackBy(player);
                    }
                    case MOVEMENT -> {
                        if (GlobalCooldownManager.fetchRemainingCooldownFor(player.getUniqueId(),skillType) > 0){
                            player.sendMessage("Movement skill on cooldown!");
                            return;
                        }
                        if (GlobalCooldownManager.skillcastBy(player.getUniqueId(),skillType,5)){
                            sorcererMovement(player);
                        } else {
                            SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "block.note_block.basedrum", 0.9F, 1.0F);
                        }
                    }
                }
            }
            case SCEPTRE -> {
                switch (skillType){
                    case BASIC_ATTACK -> {
                        sceptreBasicAttackBy(player);
                    }
                    case MOVEMENT -> {
                        if (GlobalCooldownManager.fetchRemainingCooldownFor(player.getUniqueId(),skillType) > 0){
                            player.sendMessage("Movement skill on cooldown!");
                            return;
                        }
                        if (GlobalCooldownManager.skillcastBy(player.getUniqueId(),skillType,7)){
                            templarMovement(player);
                        } else {
                            SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "block.note_block.basedrum", 0.9F, 1.0F);
                        }
                    }
                }
            }
        }
    }

    private static void swordBasicAttackBy(Player player){
        World playerWorld = player.getWorld();

        //Get player location -> Access to direction vector later
        Location playerLocation = player.getLocation();
        //Slash setup variables
        double arc = 40;
        int steps = 8;
        int duration = 10; //In ticks
        int durationPerStep = Math.max(Math.round((float) duration /steps), 1);
        int animationSpeed = 3; //Each runnable iteration equates to "animationSpeed" number of frames
        double angleStep = (arc/steps)/180*Math.PI;
        double initialAngleOffset = (arc/2)/180*Math.PI;
        if (Math.random() > 0.5){
            angleStep = -angleStep;
            initialAngleOffset = -initialAngleOffset;
        }

        double offset = 1.3;
        double length = 1.6;

        double finalAngleStep = angleStep;
        double finalInitialAngleOffset = initialAngleOffset;
        int taskID = new BukkitRunnable(){

            int frameCount = 0;
            Vector dir = LinalgMath.rotateAroundY(playerLocation.clone().getDirection().setY(0).normalize(), finalInitialAngleOffset);
            final Vector playerPos = playerLocation.toVector().clone().add(new Vector(0, 0.8, 0));

            final Set<Player> hitPlayers = new HashSet<>();
            final List<Player> nearbyPlayers = (List<Player>) playerPos.toLocation(playerWorld).getNearbyPlayers(offset+length);
            @Override
            public void run() {
                if (frameCount >= duration){
                    this.cancel();
                }
                if (frameCount > steps){
                    frameCount++;
                    return;
                }
                nearbyPlayers.remove(player);

                for (int i = 0; i < animationSpeed; i++){
                    dir = LinalgMath.rotateAroundY(dir, -finalAngleStep);

                    double t = ((double) frameCount /steps); //If framecount goes beyond steps, ignore
                    t = t*t;
                    double endHeight = Utils.getParametricValue(1.5, -1, t);
                    double handleHeight = endHeight*offset / (offset + length);
                    Vector begin = dir.clone().multiply(offset).add(playerPos).add(new Vector(0, handleHeight, 0));
                    Vector end = dir.clone().multiply(offset+length).add(playerPos).add(new Vector(0, endHeight, 0));
                    ParticlePlotter.lerpParticlesBetween(begin, end, 0.25F, Particle.CRIT, playerWorld);

                    for (Player p : nearbyPlayers){
                        if (!hitPlayers.contains(p)){
                            BoundingBox playerAABB = p.getBoundingBox();
                            RayTraceResult collisionResult = playerAABB.rayTrace(begin, dir.clone(), length);
                            if (collisionResult != null){
                                DamageRouter.playerAttack(player, p);
                                hitPlayers.add(p);
                            }
                        }
                    }

                    frameCount++;
                }
            }
        }.runTaskTimer(Inscripted.getPlugin(), 0, durationPerStep).getTaskId();
    }

    private static void axeBasicAttackBy(Player player){
        World playerWorld = player.getWorld();

        //Get player location -> Access to direction vector later
        Location playerLocation = player.getLocation();
        //Slash setup variables
        double arc = 100;
        int steps = 10;
        int duration = 10; //In ticks
        int durationPerStep = Math.max(Math.round((float) duration /steps), 1);
        int animationSpeed = 3; //Each runnable iteration equates to "animationSpeed" number of frames
        double angleStep = (arc/steps)/180*Math.PI;
        double initialAngleOffset = (arc/2)/180*Math.PI;

        double offset = 2.7;
        double length = 1.1;

        int taskID = new BukkitRunnable(){

            int frameCount = 0;
            final Vector facing = playerLocation.clone().getDirection().setY(0).normalize();
            final Vector rightVec = facing.getCrossProduct(new Vector(0, 1, 0)).normalize();
            Vector dir = LinalgMath.rotateAroundGenericAxis(rightVec.clone(), (LinalgMath.rotateAroundGenericAxis(rightVec.clone(), facing.clone(), initialAngleOffset)), Math.PI/6);
            final Vector startingHandPos = playerLocation.toVector().clone().add(new Vector(0, 1.1, 0));

            final Set<Player> hitPlayers = new HashSet<>();
            final List<Player> nearbyPlayers = (List<Player>) startingHandPos.toLocation(playerWorld).getNearbyPlayers(offset+length);

            @Override
            public void run() {
                if (frameCount >= duration){
                    this.cancel();
                }
                if (frameCount > steps){
                    frameCount++;
                    return;
                }
                //Removes multiple times -> Needs optimizations that take care of runnable structure
                nearbyPlayers.remove(player);

                for (int i = 0; i < animationSpeed; i++){
                    dir = LinalgMath.rotateAroundGenericAxis(rightVec.clone(), dir, -angleStep);

                    double t = ((double) frameCount /steps); //If framecount goes beyond steps, ignore (t>1)
                    double bladeTipOffset = Utils.getParametricValue(0.5, -0.5, t);
                    double handleEndOffset = bladeTipOffset*offset / (offset + length);
                    Vector handleEnd = dir.clone().multiply(offset).add(startingHandPos).add(rightVec.clone().multiply(handleEndOffset));
                    Vector bladeTip = dir.clone().multiply(offset+length).add(startingHandPos).add(rightVec.clone().multiply(bladeTipOffset));
                    ParticlePlotter.lerpParticlesBetween(handleEnd, bladeTip, 0.25F, Particle.CRIT, playerWorld);

                    for (Player p : nearbyPlayers){
                        if (!hitPlayers.contains(p)){
                            BoundingBox playerAABB = p.getBoundingBox();
                            RayTraceResult collisionResult = playerAABB.rayTrace(handleEnd, dir.clone(), length);
                            if (collisionResult != null){
                                DamageRouter.playerAttack(player, p);
                                hitPlayers.add(p);
                            }
                        }
                    }

                    frameCount++;
                    if (bladeTip.getY()<playerLocation.y()){
                        Location bladeTipLoc = bladeTip.toLocation(playerWorld);
                        if (bladeTipLoc.getBlock().isSolid()){
                            this.cancel();
                            return;
                        }
                    }
                }
            }
        }.runTaskTimer(Inscripted.getPlugin(), 0, durationPerStep).getTaskId();
    }

    private static void bowBasicAttackBy(Player player){
        World playerWorld = player.getWorld();
        Location playerLocation = player.getLocation().clone().add(0,1.5,0);

//        int shootConeAngle = 90;
//        int projectiles = player.getProj
        int arrowRange = 20;
        double projSpeed = 30; //Blocks/s (Scales with proj speed stat)
        int delta = 1; //In ticks (1/20s)
        double distStep = projSpeed * ((double) delta /20);
        Vector initialPosition = playerLocation.toVector().clone();
        Vector targetPosition;
        Vector currentPosition = initialPosition.clone();

        //TODO: Make specialized raytracer for handling transparent blocks
        RayTraceResult result = player.rayTraceBlocks(arrowRange);
        if (result != null && result.getHitBlock() != null){
            targetPosition = result.getHitPosition();
        } else {
            targetPosition = playerLocation.toVector().add(playerLocation.getDirection().clone().multiply(arrowRange));
        }
        int iterations = (int) ((targetPosition.distance(initialPosition))/distStep);
        final Vector velocityVector = targetPosition.clone().subtract(initialPosition).normalize().multiply(distStep);

        int taskID = new BukkitRunnable(){
            int index = 0;

            @Override
            public void run() {
                ParticlePlotter.spawnParticleAt(targetPosition, playerWorld, Particle.END_ROD);
                if (index > iterations){
                    this.cancel();
                    return;
                }
                ParticlePlotter.spawnColoredParticleAt(currentPosition, playerWorld, 91, 245, 56, 1.2F,1);
                ParticlePlotter.spawnParticleAt(currentPosition, playerWorld, Particle.CRIT);

                //Add shotgunning and collision for multiple proj
                List<Player> nearbyPlayers = (List<Player>) currentPosition.toLocation(playerWorld).getNearbyPlayers(0.4);
                nearbyPlayers.remove(player);
                if (!nearbyPlayers.isEmpty()){
                    DamageRouter.playerAttack(player, nearbyPlayers.get(0));//Will be the first one for now, but ideally it should represent the closest one
                    this.cancel();
                }

                currentPosition.add(velocityVector);
                index++;
            }
        }.runTaskTimer(Inscripted.getPlugin(), 0, delta).getTaskId();
    }

    private static void daggerBasicAttackBy(Player player){
        World playerWorld = player.getWorld();

        //Get player location -> Access to direction vector later
        Location playerLocation = player.getLocation();
        //Slash setup variables
        double arc = 30;
        int steps = 6;
        int duration = 5; //In ticks
        int durationPerStep = Math.max(Math.round((float) duration /steps), 1);
        int animationSpeed = 3; //Each runnable iteration equates to "animationSpeed" number of frames
        double angleStep = (arc/steps)/180*Math.PI;
        double initialAngleOffset = (arc/2)/180*Math.PI;
        if (Math.random() > 0.5){
            angleStep = -angleStep;
            initialAngleOffset = -initialAngleOffset;
        }

        double offset = 1.2;
        double length = 0.8;

        double finalInitialAngleOffset = initialAngleOffset;
        double finalAngleStep = angleStep;
        int taskID = new BukkitRunnable(){

            int frameCount = 0;
            final Vector facing = playerLocation.clone().getDirection().setY(0).normalize();
            final Vector rightVec = facing.getCrossProduct(new Vector(0, 1, 0)).normalize();
            Vector dir = LinalgMath.rotateAroundGenericAxis(rightVec.clone(), (LinalgMath.rotateAroundGenericAxis(rightVec.clone(), facing.clone(), finalInitialAngleOffset)), Math.PI/6);
            final Vector startingHandPos = playerLocation.toVector().clone().add(new Vector(0, 0.8, 0));

            final Set<Player> hitPlayers = new HashSet<>();
            final List<Player> nearbyPlayers = (List<Player>) startingHandPos.toLocation(playerWorld).getNearbyPlayers(offset+length);

            @Override
            public void run() {
                if (frameCount >= duration){
                    this.cancel();
                }
                if (frameCount > steps){
                    frameCount++;
                    return;
                }
                //Removes multiple times -> Needs optimizations that take care of runnable structure
                nearbyPlayers.remove(player);

                for (int i = 0; i < animationSpeed; i++){
                    dir = LinalgMath.rotateAroundGenericAxis(rightVec.clone(), dir, -finalAngleStep);

                    double t = ((double) frameCount /steps); //If framecount goes beyond steps, ignore (t>1)
                    double bladeTipOffset = Utils.getParametricValue(0.3, -0.2, t);
                    double handleEndOffset = bladeTipOffset*offset / (offset + length);
                    Vector handleEnd = dir.clone().multiply(offset).add(startingHandPos).add(rightVec.clone().multiply(handleEndOffset));
                    Vector bladeTip = dir.clone().multiply(offset+length).add(startingHandPos).add(rightVec.clone().multiply(bladeTipOffset));
                    ParticlePlotter.coloredParticleLerp(handleEnd, bladeTip, 0.2F, playerWorld, 160,160,160, 1.1F);

                    for (Player p : nearbyPlayers){
                        if (!hitPlayers.contains(p)){
                            BoundingBox playerAABB = p.getBoundingBox();
                            RayTraceResult collisionResult = playerAABB.rayTrace(handleEnd, dir.clone(), length);
                            if (collisionResult != null){
                                DamageRouter.playerAttack(player, p);
                                hitPlayers.add(p);
                            }
                        }
                    }

                    frameCount++;
                }
            }
        }.runTaskTimer(Inscripted.getPlugin(), 0, durationPerStep).getTaskId();
    }

    private static void wandBasicAttackBy(Player player){
        World playerWorld = player.getWorld();
        Location playerLocation = player.getLocation().clone().add(0,1.2,0).add(player.getLocation().getDirection().clone().multiply(1.2));

//        int shootConeAngle = 90;
//        int projectiles = player.getProj
        int missileRange = 10;
        double projSpeed = 15; //Blocks/s (Scales with proj speed stat)
        int delta = 1; //In ticks (1/20s)
        double distStep = projSpeed * ((double) delta /20);
        Vector initialPosition = playerLocation.toVector().clone();
        Vector targetPosition;
        Vector currentPosition = initialPosition.clone();

        RayTraceResult result = player.rayTraceBlocks(missileRange);
        if (result != null && result.getHitBlock() != null){
            targetPosition = result.getHitPosition();
        } else {
            targetPosition = playerLocation.toVector().add(playerLocation.getDirection().clone().multiply(missileRange));
        }
        int iterations = (int) ((targetPosition.distance(initialPosition))/distStep);
        final Vector velocityVector = targetPosition.clone().subtract(initialPosition).normalize().multiply(distStep);

        int taskID = new BukkitRunnable(){

            final Set<Player> hitPlayers = new HashSet<>();
            int index = 0;

            @Override
            public void run() {
                List<Player> nearbyPlayers;
                ParticlePlotter.spawnParticleAt(targetPosition, playerWorld, Particle.END_ROD);
                if (index > iterations){
                    this.cancel();
                    return;
                }
                ParticlePlotter.spawnColorTransitionParticleAt(currentPosition, playerWorld, 72, 139, 207, 36, 182, 212, 2F, 2);
                ParticlePlotter.spawnParticleAt(currentPosition, playerWorld, Particle.ELECTRIC_SPARK);
                playerWorld.spawnParticle(Particle.GLOW, currentPosition.toLocation(playerWorld), 2, 0.1, 0.1, 0.1);

                nearbyPlayers = (List<Player>) currentPosition.toLocation(playerWorld).getNearbyPlayers(0.5);
                nearbyPlayers.remove(player);
                if (!nearbyPlayers.isEmpty()){
                    if (nearbyPlayers.size() == 1){
                        Player nearbyPlayer = nearbyPlayers.get(0);
                        if (!hitPlayers.contains(nearbyPlayer)){
                            DamageRouter.playerAttack(player, nearbyPlayer);
                            hitPlayers.add(nearbyPlayer);
                        }
                    } else {
                        for (Player p : nearbyPlayers){
                            if (hitPlayers.contains(p)){continue;}
                            DamageRouter.playerAttack(player, p);
                            hitPlayers.add(p);
                        }
                    }
                }

                currentPosition.add(velocityVector);
                index++;
            }
        }.runTaskTimer(Inscripted.getPlugin(), 0, delta).getTaskId();
    }

    public static void sceptreBasicAttackBy(Player player){
        World playerWorld = player.getWorld();

        //Get player location -> Access to direction vector later
        Location playerLocation = player.getLocation();
        Vector dir = playerLocation.getDirection().clone().setY(0).normalize();
        final Vector rightVec = dir.getCrossProduct(new Vector(0, 1, 0)).normalize();
        double firstStrikeRadius = 1.5;
        double firstStrikeRotation = (double) 30 /180*Math.PI;
        int strikeAnimationSpeed = 2;

        double slamRadius = 1.5;

        int firstStrikeTask = new BukkitRunnable(){

            double currAngle = 0;
            @Override
            public void run() {
                if (currAngle > Math.PI/2){
                    this.cancel();
                }
                for (int i = 0; i< strikeAnimationSpeed; i++){
                    double t = Math.sin(currAngle);
                    double currentDepth = firstStrikeRadius*t; //Multi by direction
                    double currentHeight = (1-(t*t*1.7))*firstStrikeRadius - 0.2;
                    Vector curPos = dir.clone().multiply(currentDepth).add(new Vector(0, currentHeight, 0)).add(playerLocation.toVector()).add(dir);
                    curPos.add(rightVec.clone().multiply((1-t)*firstStrikeRadius*Math.sin(firstStrikeRotation)));
                    ParticlePlotter.spawnColoredParticleAt(curPos, playerWorld, 160,160,160, 1.5F, 2);
                    if (curPos.toLocation(playerWorld).getBlock().isSolid()){//Check for blocks or player collisions
                        Vector groundCollisionPos = curPos.clone().add(new Vector(0, 0.3, 0));
                        ParticlePlotter.spawnParticleAt(groundCollisionPos, playerWorld, Particle.SWEEP_ATTACK);
                        ItemStack hitBlock = new ItemStack(curPos.toLocation(playerWorld).getBlock().getType());
                        playerWorld.spawnParticle(Particle.ITEM_CRACK, groundCollisionPos.toLocation(playerWorld),
                                20,
                                hitBlock);
                        //Ring attack
                        for (double rads = 0; rads < 2*Math.PI; rads += Math.PI/10){
                            double curX = Math.cos(rads)*slamRadius;
                            double curZ = Math.sin(rads)*slamRadius;
                            double addedY = 0.3;
                            ParticlePlotter.spawnParticleAt(groundCollisionPos.clone().add(new Vector(curX, addedY, curZ)), playerWorld, Particle.ELECTRIC_SPARK);
                            ParticlePlotter.spawnColoredParticleAt(groundCollisionPos.clone().add(new Vector(curX, addedY, curZ)),
                                    playerWorld,
                                    220,220,220, 1.4F, 1);
                        }
                        List<Player> nearbyPlayers = (List<Player>) groundCollisionPos.toLocation(playerWorld).getNearbyPlayers(slamRadius);
                        nearbyPlayers.remove(player);
                        for (Player p : nearbyPlayers){
                            DamageRouter.playerAttack(player,p);
                        }

                        this.cancel();
                    }
                    currAngle+=Math.PI/16;
                }
            }
        }.runTaskTimer(Inscripted.getPlugin(), 0, 1).getTaskId();
    }
    public static void marauderMovement(Player player){
        SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "entity.ravager.roar", 0.8F, 0.8F);
        int taskID = new BukkitRunnable(){

            int iteration = 0;
            final double vel = 0.9;
//            final Vector horizontalVel = player.getLocation().getDirection().clone().setY(0).normalize().multiply(vel);
            @Override
            public void run() {
                if (iteration>15){
                    this.cancel();
                }
                final Vector horizontalVel = player.getLocation().getDirection().clone().setY(0).normalize().multiply(vel);
                player.setVelocity(horizontalVel);
                iteration+=1;
            }
        }.runTaskTimer(Inscripted.getPlugin(), 0, 3).getTaskId();
    }
    public static void gladiatorMovement(Player player){
        SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "entity.ghast.shoot", 0.7f, 1.5f);
        double vel = 1.2;
        Vector dir = player.getLocation().getDirection().clone();
        dir.setY(Math.abs(dir.getY()));
        player.setVelocity(dir.multiply(vel));
    }
    public static void mercenaryMovement(Player player){
        SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "entity.ghast.shoot", 0.7f, 1.5f);
        SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "entity.arrow.hit_player", 0.6f, 0.9f);
        double vel = 1.7;
        Vector dir = player.getLocation().getDirection().clone();
        player.setVelocity(dir.multiply(-vel));
    }
    public static void rogueMovement(Player player){
        int duration = 50;
        new BukkitRunnable(){

            final int iterations = 2;
            int index = 0;
            @Override
            public void run() {
                if (index >= iterations){
                    this.cancel();
                }
                SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "entity.ghast.shoot", 0.7f, 0.9f + (0.3f*index));
                index++;
            }
        }.runTaskTimer(Inscripted.getPlugin(), 0, 2);

        for (Player p : Bukkit.getOnlinePlayers()){
            p.hidePlayer(Inscripted.getPlugin(), player);
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()){
                    p.showPlayer(Inscripted.getPlugin(), player);
                }
                this.cancel();
            }
        }.runTaskLater(Inscripted.getPlugin(), duration);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 1));
    }

    public static void sorcererMovement(Player player){
        SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "entity.enderman.teleport", 1.0F, 1.3F);

        World playerWorld = player.getWorld();
        Location playerLocation = player.getLocation().clone().add(0,1.2,0).add(player.getLocation().getDirection().clone().multiply(1.2));

        int blinkRange = 10;
        Vector initialPosition = playerLocation.toVector().clone();
        Vector targetPosition;
//        Vector currentPosition = initialPosition.clone();

        RayTraceResult result = player.rayTraceBlocks(blinkRange);
        if (result != null && result.getHitBlock() != null){
            targetPosition = result.getHitPosition();
        } else {
            targetPosition = playerLocation.toVector().add(playerLocation.getDirection().clone().multiply(blinkRange));
        }
        ParticlePlotter.coloredParticleLerp(initialPosition, targetPosition, 0.6F, playerWorld, 50, 129, 168, 1.4F);

        player.teleport(targetPosition.toLocation(playerWorld).setDirection(playerLocation.getDirection()));
    }

    public static void templarMovement(Player player){
        double radius = 4.7;
        Vector center = player.getLocation().toVector().clone();
        World playerWorld = player.getWorld();
        double maxVelocity = 1.6;

        SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "block.basalt.break", 0.8f, 0.5f);
        SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "entity.zombie.break_wooden_door", 0.1f, 0.2f);

        for (double rad = 0; rad <= 2*Math.PI; rad += Math.PI/16){
            double xPos = Math.sin(rad)*radius;
            double zPos = Math.cos(rad)*radius;
            ParticlePlotter.spawnParticleAt(center.clone().add(new Vector(xPos,0.4,zPos)), playerWorld, Particle.END_ROD);
        }

        List<Player> nearbyPlayers = (List<Player>) center.toLocation(playerWorld).getNearbyPlayers(radius);
        nearbyPlayers.remove(player);

        for (Player p : nearbyPlayers){
            double distToCenter = p.getLocation().toVector().clone().distance(center);
            Vector dirToCenter = p.getLocation().toVector().clone().subtract(center).normalize();
            double velScaling = distToCenter/radius;
            double velocity = 0.2F + velScaling*maxVelocity;
            p.setVelocity(dirToCenter.multiply(-velocity));
        }
    }
}
