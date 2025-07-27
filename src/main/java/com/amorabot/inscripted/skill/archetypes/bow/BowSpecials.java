package com.amorabot.inscripted.skill.archetypes.bow;

import com.amorabot.inscripted.math.LinalgMath;
import com.amorabot.inscripted.particle.ParticlePlotter;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.skill.annotations.DurationSkill;
import com.amorabot.inscripted.skill.annotations.ProjectileSkill;
import com.amorabot.inscripted.skill.routine.projectile.Projectile;
import com.amorabot.inscripted.skill.routine.projectile.ProjectileCollision;
import com.amorabot.inscripted.skill.routine.projectile.ProjectileConfig;
import com.amorabot.inscripted.skill.routine.projectile.ProjectileTrail;
import com.amorabot.inscripted.skill.type.Attack;
import com.amorabot.inscripted.skill.type.PersistentAttack;
import com.amorabot.inscripted.skill.type.subroutines.DurationSubroutine;
import com.amorabot.inscripted.tasks.base.Skillcast;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;


public class BowSpecials {
    public static void rainOfArrows(Skillcast skillcastInstance){
        if (!(skillcastInstance instanceof PersistentAttack persistentAttack)){return;}
        Utils.error("CAST");
        AttackData attackData = persistentAttack.getAttackData();
        DurationSkill durationData = persistentAttack.getDurationData();

        DurationSubroutine rainOfArrowsRoutine = new DurationSubroutine(skillcastInstance);
        World world = skillcastInstance.getPlayer().getWorld();
        Vector center = LinalgMath.projectHorizontalPlayerRaycast(14,skillcastInstance.getPlayer());
        final float radius = 3.5f;
        final int points = 20;
        Vector[] targets = LinalgMath.plotPointsInsideHorizontalCircle(center,radius,30);
        Vector projDir = LinalgMath.rotateAroundX(new Vector(0,1,0),Math.toRadians(Math.random()*10));
        projDir = LinalgMath.rotateAroundX(projDir,Math.toRadians(Math.random()*10));

        //Projectile setup
        final int baseProjectilesPerIteration = 2;
        int distance = 15;
        ProjectileConfig projectileConfig = new ProjectileConfig(false,true,true,
                (30D/20), 0.1, 1,
                ProjectileTrail::rainOfArrowsTrail, ProjectileCollision::standardDetection, ProjectileCollision::testCollisionExecution);
        final Vector finalProjDir = projDir;
        rainOfArrowsRoutine.setRoutine(new BukkitRunnable() {
            @Override
            public void run() {
                if (this.isCancelled()) return;
                rainOfArrowsRoutine.cancelIfInvalid();
                ParticlePlotter.plotColoredCircleAt(
                        center,world,94, 156, 53,1.7f,radius,points,true
                );
                //Routine
                for (int i = 0; i < baseProjectilesPerIteration; i++) {
                    int randomPoint = Utils.getRandomIntBetween(targets.length-1,0);
                    Vector origin = Projectile.getRaytracedMaxDistance(targets[randomPoint].toLocation(world), finalProjDir.clone(), distance);
                    Vector target = targets[randomPoint];
                    new Projectile(skillcastInstance,attackData,origin,
                            finalProjDir.clone().multiply(-1*( (projectileConfig.maxSpeed()+ (Math.random()*5/20) ) )  )
                            ,new Vector(0,0,0),target,distance,projectileConfig);
                }


                rainOfArrowsRoutine.addElapsedTime();
                if (rainOfArrowsRoutine.isExpired()){

                }
            }
        });
        rainOfArrowsRoutine.startSubroutine(0);
    }
}
