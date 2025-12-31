package com.amorabot.inscripted.skill.archetypes.dagger;

import com.amorabot.inscripted.math.LinalgMath;
import com.amorabot.inscripted.particle.ParticlePlotter;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.skill.annotations.ProjectileSkill;
import com.amorabot.inscripted.skill.routine.projectile.Projectile;
import com.amorabot.inscripted.skill.routine.projectile.ProjectileCollision;
import com.amorabot.inscripted.skill.routine.projectile.ProjectileConfig;
import com.amorabot.inscripted.skill.routine.projectile.ProjectileTrail;
import com.amorabot.inscripted.skill.type.subroutines.DurationSubroutine;
import com.amorabot.inscripted.tasks.base.Skillcast;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class DaggerUtility {

    public static void smokeBomb(Skillcast skillcastInstance){
        Skills sourceSkill = skillcastInstance.getCastedSkill();
        ProjectileSkill projData = sourceSkill.getProjectileSkilLData();
        AttackData throwAttack = new AttackData(skillcastInstance.getPlayerID(),sourceSkill,PlayerDataContainer.getDataContainerFor(skillcastInstance.getPlayerID()).getGlobalStats());

        int maxRange = 30;
        ProjectileConfig projectileConfig = new ProjectileConfig(true,false,true,
                15D / 20, 0.04, 0.8,
                ProjectileTrail::smokeBombTrail, ProjectileCollision::standardSingleProjDetection, ProjectileCollision::smokeBombCollision);
        Location playerLocation = skillcastInstance.getPlayer().getLocation().clone().add(0,1.2,0);
        Vector target = null;
        boolean uniqueTarget = skillcastInstance.getCastedSkill().getProjectileSkilLData().uniqueTarget();
        if (uniqueTarget){
            target = Projectile.getRaytracedMaxDistance(playerLocation, playerLocation.getDirection().clone(), maxRange);
        }

        projData.spread().instantiate(skillcastInstance,throwAttack,projectileConfig,playerLocation,target, 1, maxRange, 90D);
    }
    public static void smokeBombCloud(Skillcast skillcastInstance){
        DurationSubroutine smokebombRoutine = new DurationSubroutine(skillcastInstance);
        World playerWorld = skillcastInstance.getPlayer().getWorld();
        Location collisionBlockLoc = skillcastInstance.getCastData().getCastingContext().getOrigin().toLocation(playerWorld)
                .toBlockLocation().toCenterLocation();
        collisionBlockLoc.add(0,0.6,0);

        final float radius = 3.2f;
        final int points = 30;

        // 200, 220, 220 cool prismatic color theme?

        smokebombRoutine.setRoutine(new BukkitRunnable() {
            @Override
            public void run() {
                if (this.isCancelled()) return;
                smokebombRoutine.cancelIfInvalid();
                //Routine
                ParticlePlotter.plotDirectionalCircleAt(collisionBlockLoc.toVector().add(new Vector(0,0.1,0)), playerWorld,
                        Particle.CAMPFIRE_COSY_SMOKE,1.7f,15,true,0.05f,false);
                ParticlePlotter.plotColoredCircleAt(collisionBlockLoc.toVector(),playerWorld,125,125,125,1.3f, radius,points,false);

                List<Vector> rings = LinalgMath.plotCircleBorder(collisionBlockLoc.clone().add(0,0.8,0).toVector(),radius-0.8, points-5);
                rings.addAll(LinalgMath.plotCircleBorder(collisionBlockLoc.clone().add(0,1.6,0).toVector(),radius-1.6,points-15));
                for (Vector ringVector : rings){
                    ParticlePlotter.spawnParticleAt(ringVector,playerWorld,Particle.SMOKE);
                }


                PotionEffect slow = new PotionEffect(PotionEffectType.BLINDNESS, 35, 1, true, false, false);
                List<Player> affectedPlayers = (List<Player>) collisionBlockLoc.getNearbyPlayers(radius-0.1);
                for (Player p : affectedPlayers){
                    slow.apply(p);
                }

                smokebombRoutine.addElapsedTime();
            }
        });
        smokebombRoutine.startSubroutine(0);
    }
}
