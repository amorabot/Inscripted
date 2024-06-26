package com.amorabot.inscripted.skills;

import com.amorabot.inscripted.APIs.damageAPI.DamageRouter;
import com.amorabot.inscripted.APIs.damageAPI.DamageSource;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;
import com.amorabot.inscripted.components.Items.modifiers.unique.Effects;
import com.amorabot.inscripted.components.Items.modifiers.unique.TriggerTimes;
import com.amorabot.inscripted.components.Items.modifiers.unique.TriggerTypes;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AbilityRoutines {
    public static void playerBaseAbilityCast(Player player, AbilityTypes skillType, WeaponTypes weapon, WeaponAttackSpeeds atkSpeed){
        PlayerAbilities ability = PlayerAbilities.mapBaseAbility(weapon, skillType, 0);
        if (ability == null){
            Utils.error("Invalid skill mapping: " + weapon + " and " + skillType);
            return;
        }
        Set<Effects> playerEffects = JSONProfileManager.getProfile(player.getUniqueId()).getEffects();

        //Early cast effects


        abilityTriggers(skillType, player, playerEffects);
        ability.cast(player, atkSpeed); //TODO: Should support specific targets
        //LATE cast effects
        for (Effects effects : playerEffects){
            if (!effects.getTiming().equals(TriggerTimes.LATE)){continue;}
            if (effects.getTrigger().equals(TriggerTypes.ON_CAST)){
                effects.check(player, player, null);
            }
        }


    }


    public static void newSwipeAttackBy(Player player, PlayerAbilities mappedAbility,
                                        double arc, int steps, int durationInTicks, double distanceFromPlayer,
                                        double swipeMinSize, double swipeMaxSize,
                                        double initialOffsetFromCenter, double finalOffsetFromCenter,
                                        int r, int g, int b,
                                        Particle additionalParticle, double tipParticlePercentage, double baseParticleSize, double finalParticleSize,
                                        boolean isMirrored, boolean isInverted, boolean isRandomized, double... Q1Angles){

        double angleStep = (arc/steps)/180*Math.PI;
        double statingPhase = (arc/2)/180*Math.PI;
        int animationSpeed = (int) Math.max(Math.ceil((double) steps /durationInTicks), 1D);


        if (isMirrored){
            statingPhase = -statingPhase;
        }


        World playerWorld = player.getWorld();
        Location playerLocation = player.getLocation();
        final Vector playerPos = playerLocation.toVector().clone().add(new Vector(0, 1.4, 0));

        final Vector initialDirection = playerLocation.clone().getDirection().normalize();
        final Vector horizontalVec = new Vector(initialDirection.getX(), 0, initialDirection.getZ());

        //Slash randomization when view angle is low
        Vector perpendicularAxis = initialDirection.clone().crossProduct(horizontalVec).normalize();
        if (playerLocation.getPitch() <=15 && playerLocation.getPitch() >= -15){ //When inside the 30deg view angle, apply the plane rotation

            double rotationAngle;
            if (isRandomized){
                rotationAngle = Utils.getRandomInclusiveValue(Q1Angles[0],Q1Angles[1]);
            } else {
                rotationAngle = Q1Angles[0];
            }
            if (isMirrored){rotationAngle = -rotationAngle;}
            perpendicularAxis = LinalgMath.rotateAroundGenericAxis(initialDirection.clone(),perpendicularAxis, rotationAngle/180*Math.PI);

        }
        if (isInverted){
            perpendicularAxis.multiply(-1);
        }
        Vector finalPerpendicularAxis = perpendicularAxis;


        double offset = 0.3;
        if (player.isSprinting()){
            offset+=1.4;
        }
        double finalOffset = offset;

        if (Math.abs(playerLocation.getPitch()+90) <= 2){ //2 degree "facing up" threshold
            return;
        }
        double finalStatingPhase = statingPhase;
        int taskID = new BukkitRunnable(){

            int frameCount = 0;
            double t = finalStatingPhase;

            final Set<Player> hitPlayers = new HashSet<>();
            final List<Player> nearbyPlayers = (List<Player>) playerPos.toLocation(playerWorld).getNearbyPlayers(finalOffset+distanceFromPlayer+2);
            final Vector attackCenter = playerPos.clone().add(initialDirection.clone().multiply(finalOffset));

            @Override
            public void run() {
                if (frameCount>=durationInTicks){
                    this.cancel();
                    return;
                }
                nearbyPlayers.remove(player);

                for (int i = 0; i < animationSpeed; i++){
                    double animationProgress = ((double) (frameCount * animationSpeed) + i )/steps;


                    double swipeSize = swipeMinSize + (swipeMaxSize-swipeMinSize)*animationProgress;
                    double handleOffset = initialOffsetFromCenter + (finalOffsetFromCenter-initialOffsetFromCenter)*animationProgress;

                    Vector tipPoint =
                            attackCenter.clone()
                                    .add( initialDirection.clone().multiply((distanceFromPlayer+handleOffset)*Math.cos(t)) )
                                    .add( finalPerpendicularAxis.clone().multiply((distanceFromPlayer+handleOffset)*Math.sin(t)) );
                    Vector radialDirection = attackCenter.clone().subtract(tipPoint.clone()).normalize(); //Border -> Center

                    Vector handlePoint = tipPoint.clone().add(radialDirection.multiply(swipeSize));

                    if (isMirrored){
                        t += angleStep;
                    } else {
                        t -= angleStep;
                    }

                    float particleSize = (float) (baseParticleSize + (finalParticleSize-baseParticleSize)*animationProgress);

                    //Particle plotting
                    ParticlePlotter.coloredParticleLerp(handlePoint, tipPoint,0.3f,playerWorld,r, g, b, particleSize);
                    if (additionalParticle != null){
                        ParticlePlotter.lerpParticlesBetween(handlePoint, tipPoint, 0.6F, additionalParticle, playerWorld);
                    }
                    Vector swingMidpoint = tipPoint.clone().subtract(tipPoint.clone().subtract(handlePoint).multiply(tipParticlePercentage));
                    ParticlePlotter.coloredParticleLerp(swingMidpoint, tipPoint,0.1f,playerWorld,247, 242, 198, particleSize);
                    ParticlePlotter.spawnParticleAt(tipPoint,playerWorld, Particle.ELECTRIC_SPARK);

                    //Collision detec.
                    for (Player p : nearbyPlayers){
                        if (!hitPlayers.contains(p)){

                            BoundingBox playerAABB = getLargeHitbox(p);

                            Vector dir = tipPoint.clone().subtract(handlePoint);
                            RayTraceResult collisionResult = playerAABB.rayTrace(handlePoint.clone(), dir.clone().normalize(), dir.length()+0.1);
                            if (collisionResult != null){
                                DamageRouter. playerAttack(player, p, DamageSource.HIT, mappedAbility);
                                hitPlayers.add(p);
                            }
                        }
                    }

                }
                frameCount++;
            }
        }.runTaskTimer(Inscripted.getPlugin(),0, 1).getTaskId();

    }
    





    public static BoundingBox getLargeHitbox(Player player){
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
    public static void showDebugHitbox(BoundingBox playerAABB, World playerWorld, int r, int g, int b){
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

    private static void abilityTriggers(AbilityTypes skillType, Player player, Set<Effects> playerEffects){
        //Effect triggers for specific skill-types only
        if (GlobalCooldownManager.fetchAbilityRemainingCooldown(player.getUniqueId(),skillType)>0){return;}
        switch (skillType){
            case MOVEMENT -> {
                for (Effects effect : playerEffects){
                    if (!effect.getTrigger().equals(TriggerTypes.ON_MOVEMENT)){continue;}
                    effect.check(player,player, null);
                }
            }
            case UTILITY -> {
                break;
            }
            case SPECIAL -> {
                break;
            }
        }
    }
}
