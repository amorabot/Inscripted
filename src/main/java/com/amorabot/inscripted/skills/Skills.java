package com.amorabot.inscripted.skills;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.APIs.damageAPI.DamageRouter;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;
import com.amorabot.inscripted.utils.Utils;
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
//                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 300, 5));

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
//                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 300, 2));
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
//                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20, 3));
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
//                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20, 3));
                        daggerBasicAttackBy(player);
                    }
                    case MOVEMENT -> {
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
//                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20, 3));
                        wandBasicAttackBy(player);
                    }
                    case MOVEMENT -> {
                        if (GlobalCooldownManager.skillcastBy(player.getUniqueId(),skillType,5)){
                            sorcererMovement(player);
                        } else {
                            SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "block.note_block.basedrum", 0.9F, 1.0F);
                        }
                    }
                }
            }
            case MACE -> {
                switch (skillType){
                    case BASIC_ATTACK -> {
//                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20, 4));
                        sceptreBasicAttackBy(player);
                    }
                    case MOVEMENT -> {
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
        Location playerLocation = player.getLocation();
        final Vector playerPos = playerLocation.toVector().clone().add(new Vector(0, 1.4, 0));

        final Vector initialDirection = playerLocation.clone().getDirection().normalize();
        final Vector horizontalVec = new Vector(initialDirection.getX(), 0, initialDirection.getZ());

        //Slash randomization when view angle is low
        Vector perpendicularAxis = initialDirection.clone().crossProduct(horizontalVec).normalize();
        if (playerLocation.getPitch() <=15 && playerLocation.getPitch() >= -15){ //30deg cone, the effect activates
            double randomAngle = Utils.getRandomInclusiveValue(10,40); //10-40deg
            perpendicularAxis = LinalgMath.rotateAroundGenericAxis(initialDirection.clone(),perpendicularAxis, randomAngle/180*Math.PI);
        }
        Vector finalPerpendicularAxis = perpendicularAxis;

        double arc = 120;

        int steps = 16;
        double angleStep = (arc/steps)/180*Math.PI;

        int duration = 4; //In ticks
        int animationSpeed = (int) Math.max(Math.ceil((double) steps /duration), 1D);

        double offset = 0.3;
        if (player.isSprinting()){
            offset+=1.4;
        }
        double finalOffset = offset;
        double handleRadius = 0.8;
        double tipRadius = 2.2;

        if (Math.abs(playerLocation.getPitch()+90) <= 2){ //2 degree "facing up" threshold
            return;
        }
        int taskID = new BukkitRunnable(){

            int frameCount = 0;
            double t = (arc/2)/180*Math.PI;

            final Set<Player> hitPlayers = new HashSet<>();
            final List<Player> nearbyPlayers = (List<Player>) playerPos.toLocation(playerWorld).getNearbyPlayers(finalOffset+tipRadius+3);

            @Override
            public void run() {
                if (frameCount>=duration){
                    this.cancel();
                    return;
                }
                nearbyPlayers.remove(player);

                Vector attackCenter = playerPos.clone().add(initialDirection.clone().multiply(finalOffset)); //Moving the circle's center slightly forward
                Vector tipCenter = playerPos.clone().add(initialDirection.clone().multiply(finalOffset -1.1));

                for (int i = 0; i < animationSpeed; i++){
                    Vector handlePoint =
                            attackCenter.clone()
                            .add( initialDirection.clone().multiply(handleRadius*Math.cos(t)) )
                            .add( finalPerpendicularAxis.clone().multiply( handleRadius*Math.sin(t)) );
                    Vector tipPoint =
                            tipCenter.clone()
                            .add( initialDirection.clone().multiply(tipRadius*Math.cos(t)*1.5D) )
                            .add( finalPerpendicularAxis.clone().multiply( tipRadius*Math.sin(t)) );

                    t -= angleStep;
                    //Particle plotting
                    ParticlePlotter.coloredParticleLerp(handlePoint, tipPoint,0.3f,playerWorld,173, 143, 130, 1f);
                    Vector swingMidpoint = handlePoint.clone().add(tipPoint.clone().subtract(handlePoint).multiply(0.65));
                    ParticlePlotter.coloredParticleLerp(swingMidpoint, tipPoint,0.1f,playerWorld,247, 242, 198, 1.2f);
                    ParticlePlotter.spawnParticleAt(swingMidpoint,playerWorld, Particle.ELECTRIC_SPARK);

                    //Collision detec.
                    for (Player p : nearbyPlayers){
                        if (!hitPlayers.contains(p)){

                            BoundingBox playerAABB = getLargeHitbox(p);

                            Vector dir = tipPoint.clone().subtract(handlePoint);
                            RayTraceResult collisionResult = playerAABB.rayTrace(handlePoint.clone(), dir.clone().normalize(), dir.length()+0.1);
                            if (collisionResult != null){
                                DamageRouter.playerAttack(player, p);
                                hitPlayers.add(p);
                            }
                        }
                    }

                }
                frameCount++;
            }
        }.runTaskTimer(Inscripted.getPlugin(),0, 1).getTaskId();

    }
    private static void newSwordBasicAttackBy(Player player){
        //Experiment attack
        World playerWorld = player.getWorld();
        Location playerLocation = player.getLocation();

        //Slash setup variables
        double arc = 70;
        int steps = 12;
        int duration = 4; //In ticks
        int animationSpeed = (int) Math.max(Math.ceil((double) steps /duration), 1D);
        double angleStep = (arc/steps)/180*Math.PI;
        double initialAngleOffset = (arc/2)/180*Math.PI;

        double offset = 0.8;
        double sprintOffset = 2.6;
        double length = 1.6;

        final Vector playerPos = playerLocation.toVector().clone().add(new Vector(0, 1.4, 0));
        if (player.isSprinting()){
            playerPos.add(playerLocation.clone().getDirection().setY(0).normalize().multiply(sprintOffset));
        }


        final Vector initialDirection = playerLocation.clone().getDirection().normalize();
        final Vector horizontalVec = new Vector(initialDirection.getX(), 0, initialDirection.getZ());

        Vector perpendicularAxis = initialDirection.clone().crossProduct(horizontalVec).normalize();
        if (playerLocation.getPitch() >= 0){
            perpendicularAxis.multiply(-1);
        }
        final Vector planeNormal = initialDirection.clone().getCrossProduct(perpendicularAxis).normalize();


        int taskID = new BukkitRunnable(){

            int frameCount = 0;
            int stepsConcluded = 0;
            @Override
            public void run() {
                if (frameCount>=duration){
                    this.cancel();
                    return;
                }

                for (int i = 0; i < animationSpeed; i++){
                    double currentAngle = initialAngleOffset - (stepsConcluded*angleStep);
                    Vector dir = LinalgMath.rotateAroundGenericAxis(planeNormal.clone(), initialDirection.clone(), currentAngle);
                    Vector handle = playerPos.clone().add(dir.clone().multiply(offset));
                    Vector tip = playerPos.clone().add(dir.clone().multiply(offset+length));

                    ParticlePlotter.lerpParticlesBetween(handle, tip,0.5f,Particle.END_ROD, playerWorld);
                    stepsConcluded++;
                }
                frameCount++;
            }
        }.runTaskTimer(Inscripted.getPlugin(), 0, 1).getTaskId();
    }

    private static void axeBasicAttackBy(Player player){

        World playerWorld = player.getWorld();

        //Get player location -> Access to direction vector later
        Location playerLocation = player.getLocation();

        //Slash setup variables
        double arc = 60;
        int steps = 10;
        int duration = 10; //In ticks
        int durationPerStep = Math.max(Math.round((float) duration /steps), 1);
        int animationSpeed = 4; //Each runnable iteration equates to "animationSpeed" number of frames
        double angleStep = (arc/steps)/180*Math.PI;
        double initialAngleOffset = (arc/2)/180*Math.PI;
        if (Math.random() > 0.5){
            angleStep = -angleStep;
            initialAngleOffset = -initialAngleOffset;
        }

        double offset = 0.8;
        double length = 2.5;

        double finalAngleStep = angleStep;
        double finalInitialAngleOffset = initialAngleOffset;

        final Vector playerPos = playerLocation.toVector().clone().add(new Vector(0, 1.1, 0));

        if (player.isSprinting()){
            playerPos.add(playerLocation.clone().getDirection().setY(0).normalize().multiply(2.6));
        }
        int taskID = new BukkitRunnable(){

            int frameCount = 0;
            Vector dir = LinalgMath.rotateAroundY(playerLocation.clone().getDirection().setY(0).normalize(), finalInitialAngleOffset);

            final Set<Player> hitPlayers = new HashSet<>();
            final List<Player> nearbyPlayers = (List<Player>) playerPos.toLocation(playerWorld).getNearbyPlayers(offset+length+3);
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
//                    t = t*t;
                    double endHeight = Utils.getParametricValue(1.1, -1.4, t);
                    double handleHeight = endHeight*offset / (offset + length);
                    Vector begin = dir.clone().multiply(offset).add(playerPos).add(new Vector(0, handleHeight, 0));
                    Vector end = dir.clone().multiply(offset+length).add(playerPos).add(new Vector(0, endHeight, 0));
                    ParticlePlotter.lerpParticlesBetween(begin, end, 0.25F, Particle.CRIT, playerWorld);
                    ParticlePlotter.lerpParticlesBetween(begin, end, 0.6F, Particle.SMOKE, playerWorld);

                    for (Player p : nearbyPlayers){
                        if (!hitPlayers.contains(p)){

                            BoundingBox playerAABB = getLargeHitbox(p);

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

    private static void bowBasicAttackBy(Player player){
        World playerWorld = player.getWorld();
        Location playerLocation = player.getLocation().clone().add(0,1.5,0);

//        int shootConeAngle = 90;
//        int projectiles = player.getProj
        int arrowRange = 25;
        double projSpeed = 50; //Blocks/s (Scales with proj speed stat)
        double collisionDetectionRadius = 0.8;
        int delta = 1; //In ticks (1/20s)
        double distStep = projSpeed * ((double) delta /20);
        int substeps = 3; //TODO: substitute this metric with minDist, implement the shoot cone
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
        final Vector velocityVector = targetPosition.clone().subtract(initialPosition).normalize().multiply(distStep/substeps);
        ParticlePlotter.spawnParticleAt(targetPosition, playerWorld, Particle.END_ROD);

        int taskID = new BukkitRunnable(){
            int index = 0;
            @Override
            public void run() {
                if (index > iterations){
                    this.cancel();
                    return;
                }
                for (int i = 0; i < substeps; i++){
                    ParticlePlotter.spawnColoredParticleAt(currentPosition, playerWorld, 91, 245, 56, 1.2F,1);
                    ParticlePlotter.spawnDifuseParticleAt(currentPosition, playerWorld, Particle.CRIT, 0.3D);

                    //TODO: Add shotgunning and collision for multiple proj
                    List<Player> nearbyPlayers = (List<Player>) currentPosition.toLocation(playerWorld).getNearbyPlayers(collisionDetectionRadius);
                    nearbyPlayers.remove(player);

                    if (!nearbyPlayers.isEmpty()){
                        for (Player p : nearbyPlayers){
                            BoundingBox playerAABB = getLargeHitbox(p);

                            //TODO: Encapsulate arrow collision
                            BoundingBox arrowAABB = new BoundingBox(currentPosition.getX(), currentPosition.getY(), currentPosition.getZ(),
                                    currentPosition.getX(), currentPosition.getY(), currentPosition.getZ());
                            arrowAABB.expand(collisionDetectionRadius/4);

                            if (playerAABB.overlaps(arrowAABB)){
                                DamageRouter.playerAttack(player, p);
                                this.cancel();
                                return;
                            }
                        }

                    }

                    currentPosition.add(velocityVector);
                }
                index++;
            }
        }.runTaskTimer(Inscripted.getPlugin(), 0, delta).getTaskId();
    }

    private static void daggerBasicAttackBy(Player player){
        World playerWorld = player.getWorld();

        //Get player location -> Access to direction vector later
        Location playerLocation = player.getLocation();
        //Slash setup variables
        double arc = 40;
        int steps = 6;
        int duration = 5; //In ticks
        int durationPerStep = Math.max(Math.round((float) duration /steps), 1);
        int animationSpeed = 4; //Each runnable iteration equates to "animationSpeed" number of frames
        double angleStep = (arc/steps)/180*Math.PI;
        double initialAngleOffset = (arc/2)/180*Math.PI;
//        if (Math.random() > 0.5){
//            angleStep = -angleStep;
//            initialAngleOffset = -initialAngleOffset;
//        }

        double offset = 0.9;
        double length = 0.9;

        final Vector startingHandPos = playerLocation.toVector().clone().add(new Vector(0, 0.8, 0));

        if (player.isSprinting()){
            startingHandPos.add(playerLocation.clone().getDirection().setY(0).normalize().multiply(2.6));
        }
//        double finalInitialAngleOffset = initialAngleOffset;
//        double finalAngleStep = angleStep;
        int taskID = new BukkitRunnable(){

            int frameCount = 0;
            final Vector facing = playerLocation.clone().getDirection().setY(0).normalize();
            final Vector rightVec = facing.getCrossProduct(new Vector(0, 1, 0)).normalize();
            Vector dir = LinalgMath.rotateAroundGenericAxis(rightVec.clone(), (LinalgMath.rotateAroundGenericAxis(rightVec.clone(), facing.clone(), initialAngleOffset)), Math.PI/6);

            final Set<Player> hitPlayers = new HashSet<>();
            final List<Player> nearbyPlayers = (List<Player>) startingHandPos.toLocation(playerWorld).getNearbyPlayers(offset+length+3);

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
                    double bladeTipOffset = Utils.getParametricValue(0.3, -0.2, t);
                    double handleEndOffset = bladeTipOffset*offset / (offset + length);
                    Vector handleEnd = dir.clone().multiply(offset).add(startingHandPos).add(rightVec.clone().multiply(handleEndOffset));
                    Vector bladeTip = dir.clone().multiply(offset+length).add(startingHandPos).add(rightVec.clone().multiply(bladeTipOffset));
                    ParticlePlotter.coloredParticleLerp(handleEnd, bladeTip, 0.2F, playerWorld, 160,160,160, 1.1F);

                    for (Player p : nearbyPlayers){
                        if (!hitPlayers.contains(p)){
                            BoundingBox playerAABB = getLargeHitbox(p);

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
        int missileRange = 13;
        double projSpeed = 30; //Blocks/s (Scales with proj speed stat)
        int delta = 1; //In ticks (1/20s)
        double distStep = projSpeed * ((double) delta /20);
        int substeps = 2; //additional checks between the interpolated points to prevent tunelling
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
//        final Vector velocityVector = targetPosition.clone().subtract(initialPosition).normalize().multiply(distStep);
        final Vector velocityVector = targetPosition.clone().subtract(initialPosition).normalize().multiply(distStep/substeps);
        ParticlePlotter.spawnParticleAt(targetPosition, playerWorld, Particle.END_ROD);

        int taskID = new BukkitRunnable(){

            final Set<Player> hitPlayers = new HashSet<>();
            int index = 0;

            @Override
            public void run() {
                List<Player> nearbyPlayers;
                if (index > iterations){
                    this.cancel();
                    return;
                }

                for (int i = 0; i < substeps; i++){
                    ParticlePlotter.spawnColorTransitionParticleAt(currentPosition, playerWorld, 72, 139, 207, 36, 182, 212, 2F, 2);
                    ParticlePlotter.spawnParticleAt(currentPosition, playerWorld, Particle.ELECTRIC_SPARK);
                    playerWorld.spawnParticle(Particle.GLOW, currentPosition.toLocation(playerWorld), 2, 0.1, 0.1, 0.1);

                    nearbyPlayers = (List<Player>) currentPosition.toLocation(playerWorld).getNearbyPlayers(0.5);
                    nearbyPlayers.remove(player);

                    if (!nearbyPlayers.isEmpty()){
                        if (nearbyPlayers.size() == 1){
                            Player nearbyPlayer = nearbyPlayers.get(0);
                            if (wandBoltCollision(nearbyPlayer, currentPosition, hitPlayers)){
                                DamageRouter.playerAttack(player, nearbyPlayer);
                                hitPlayers.add(nearbyPlayer);
                            }
                        } else {
                            for (Player p : nearbyPlayers){
                                if (wandBoltCollision(p, currentPosition, hitPlayers)){
                                    DamageRouter.playerAttack(player, p);
                                    hitPlayers.add(p);
                                }
                            }
                        }
                    }

                    currentPosition.add(velocityVector);
                }

                index++;
            }
        }.runTaskTimer(Inscripted.getPlugin(), 0, delta).getTaskId();
    }
    private static boolean wandBoltCollision(Player target, Vector boltPosition, Set<Player> hitPlayers){
        if (hitPlayers.contains(target)){return false;}

        BoundingBox playerAABB = getLargeHitbox(target);


        BoundingBox magicBoltAABB = new BoundingBox(
                boltPosition.getX(), boltPosition.getY(), boltPosition.getZ(),
                boltPosition.getX(), boltPosition.getY(), boltPosition.getZ()
        );
        magicBoltAABB.expand(0.2);
        return playerAABB.overlaps(magicBoltAABB);
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

        double slamRadius = 1.7;

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
                        Vector groundCollisionPos = curPos.clone().add(new Vector(0, 0.4, 0));
                        ParticlePlotter.spawnParticleAt(groundCollisionPos, playerWorld, Particle.SWEEP_ATTACK);
                        ParticlePlotter.spawnParticleAt(groundCollisionPos, playerWorld, Particle.EXPLOSION);
                        ItemStack hitBlock = new ItemStack(curPos.toLocation(playerWorld).getBlock().getType());
//                        playerWorld.spawnParticle(Particle.BLOCK, groundCollisionPos.toLocation(playerWorld),
//                                20,
//                                hitBlock);
                        ParticlePlotter.spawnBlockCrackPartileAt(groundCollisionPos, playerWorld, hitBlock.getType(),10,0.9D);
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


    private static BoundingBox getLargeHitbox(Player player){
        BoundingBox playerAABB = player.getBoundingBox();

        playerAABB.expand(0.25, 0.0, 0.25);
        playerAABB.expand(new Vector(0, 1, 0), 0.25);

        return playerAABB;
    }
    private static void showHitbox(BoundingBox playerAABB, World playerWorld){
        Vector BBMax = playerAABB.getMax();
        Vector BBMin = playerAABB.getMin();
        ParticlePlotter.spawnParticleAt(BBMax, playerWorld, Particle.END_ROD);
        ParticlePlotter.spawnParticleAt(BBMin, playerWorld, Particle.END_ROD);
    }
    private static void showDebugHitbox(BoundingBox playerAABB, World playerWorld, int r, int g, int b){
        Vector BBMax = playerAABB.getMax();
        Vector BBMin = playerAABB.getMin();
        ParticlePlotter.spawnColoredParticleAt(BBMax, playerWorld, r, g, b, 3f, 3);
        ParticlePlotter.spawnColoredParticleAt(BBMin, playerWorld, r, g, b, 3f, 3);

        //Clockwise points
        Vector maxAdj1 = new Vector(BBMin.getX(), BBMax.getY(), BBMax.getZ());
        Vector maxAdj2 = new Vector(BBMin.getX(), BBMax.getY(), BBMin.getZ());
        Vector maxAdj3 = new Vector(BBMax.getX(), BBMax.getY(), BBMin.getZ());

        Vector minAdj1 = new Vector(BBMax.getX(), BBMin.getY(), BBMin.getZ());
        Vector minAdj2 = new Vector(BBMax.getX(), BBMin.getY(), BBMax.getZ());
        Vector minAdj3 = new Vector(BBMin.getX(), BBMin.getY(), BBMax.getZ());


        Vector[] topVertices = new Vector[] {BBMax , maxAdj1 , maxAdj2, maxAdj3};
        Vector[] botVertices = new Vector[] {minAdj2 , minAdj3 , BBMin, minAdj1};

        for (int i = 0; i < topVertices.length; i++){
            ParticlePlotter.lerpParticlesBetween(topVertices[i], topVertices[(i + 1) % topVertices.length], 0.2f, Particle.END_ROD, playerWorld);
            ParticlePlotter.lerpParticlesBetween(botVertices[i], botVertices[(i + 1) % botVertices.length], 0.2f, Particle.END_ROD, playerWorld);
            ParticlePlotter.lerpParticlesBetween(topVertices[i], botVertices[i], 0.2f, Particle.END_ROD, playerWorld);
        }
    }
}
