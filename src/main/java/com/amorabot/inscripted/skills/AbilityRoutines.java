package com.amorabot.inscripted.skills;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;
import com.amorabot.inscripted.components.Items.modifiers.unique.Effects;
import com.amorabot.inscripted.components.Items.modifiers.unique.TriggerTimes;
import com.amorabot.inscripted.components.Items.modifiers.unique.TriggerTypes;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.skills.casting.GlobalCooldownManager;
import com.amorabot.inscripted.skills.math.LinalgMath;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

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

    public static void newVerticalSwipeAnimationBy(Player player,
                                                   double arc, int steps, int durationInTicks, double distanceFromPlayer, double attackCenterOffset,
                                                   double swipeMinSize, double swipeMaxSize,
                                                   double initialOffsetFromCenter, double finalOffsetFromCenter,
                                                   int r, int g, int b,
                                                   Particle additionalParticle, double tipParticlePercentage, double baseParticleSize, double finalParticleSize,
                                                   boolean isMirrored, double... Q1Angles){

        double angleStep = (arc/steps)/180*Math.PI;
        double statingPhase = -90*Math.PI;
        int animationSpeed = (int) Math.max(Math.ceil((double) steps /durationInTicks), 1D);




        World playerWorld = player.getWorld();
        Location playerLocation = player.getLocation();
        final Vector playerPos = playerLocation.toVector().clone();

        Vector initialDirection = playerLocation.clone().getDirection().setY(0).normalize();

        Vector perpendicularAxis = initialDirection.clone().crossProduct(new Vector(0,1,0)).normalize();

        //Original normal vec. to the initial dir., lets make the "right/left" handed effect
        Vector handOffsetVec = perpendicularAxis.clone().multiply(attackCenterOffset);
        double fixedHandRotation = Q1Angles[0]; //30deg
        Vector baseAttackCenter = playerPos.clone();
        if (!isMirrored){
            handOffsetVec.multiply(-1);
            fixedHandRotation = -fixedHandRotation;
        }
        initialDirection = LinalgMath.rotateAroundY(initialDirection.clone(), fixedHandRotation/180*Math.PI);
        baseAttackCenter = baseAttackCenter.add(handOffsetVec);

        if (Math.abs(playerLocation.getPitch()+90) <= 2){ //2 degree "facing up" threshold
            return;
        }

        Vector finalInitialDirection = initialDirection;
        Vector finalBaseAttackCenter = baseAttackCenter;
        int taskID = new BukkitRunnable(){

            int frameCount = 0;
            double t = statingPhase;

            final Vector attackCenter = finalBaseAttackCenter.add(finalInitialDirection.clone().multiply(attackCenterOffset));

            @Override
            public void run() {
                if (frameCount>=durationInTicks){
                    this.cancel();
                    return;
                }

                for (int i = 0; i < animationSpeed; i++){
                    double animationProgress = ((double) (frameCount * animationSpeed) + i )/steps;


                    double swipeSize = swipeMinSize + (swipeMaxSize-swipeMinSize)*animationProgress;
                    double handleOffset = initialOffsetFromCenter + (finalOffsetFromCenter-initialOffsetFromCenter)*animationProgress;

                    Vector tipPoint =
                            attackCenter.clone()
                                    .add( new Vector(0, 1, 0).multiply((distanceFromPlayer+handleOffset)*Math.cos(t)) )
                                    .add( finalInitialDirection.clone().multiply((distanceFromPlayer+handleOffset)*Math.sin(t)) );
                    Vector radialDirection = attackCenter.clone().subtract(tipPoint.clone()).normalize(); //Border -> Center

                    Vector handlePoint = tipPoint.clone().add(radialDirection.multiply(swipeSize));

                    t += angleStep;

                    float particleSize = (float) (baseParticleSize + (finalParticleSize-baseParticleSize)*animationProgress);

                    //Particle plotting
                    ParticlePlotter.coloredParticleLerp(handlePoint, tipPoint,0.3f,playerWorld,r, g, b, particleSize);
                    if (additionalParticle != null){
                        ParticlePlotter.lerpParticlesBetween(handlePoint, tipPoint, 0.6F, additionalParticle, playerWorld);
                    }
                    Vector swingMidpoint = tipPoint.clone().subtract(tipPoint.clone().subtract(handlePoint).multiply(tipParticlePercentage));
                    ParticlePlotter.coloredParticleLerp(swingMidpoint, tipPoint,0.1f,playerWorld,247, 242, 198, particleSize);
                    ParticlePlotter.spawnParticleAt(tipPoint,playerWorld, Particle.ELECTRIC_SPARK);

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
