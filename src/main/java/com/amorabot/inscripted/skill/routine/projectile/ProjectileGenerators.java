package com.amorabot.inscripted.skill.routine.projectile;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.math.LinalgMath;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.tasks.base.Skillcast;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public enum ProjectileGenerators {
    CONE {
        @Override
        public void instantiate(Skillcast skillcast, AttackData attackData, ProjectileConfig config, Location origin, Vector target,
                                int projectiles, double maxDistance, double extraData) {
            Vector dir = origin.getDirection().clone();
            int shootCone = (int) extraData;
            double radius = 2;
            Vector perpendicularAxis = dir.clone().crossProduct(new Vector(dir.getX(), 0, dir.getZ())).normalize();
            double angleStep = (double) shootCone /(projectiles+1);
            double initialOffset = -((double) shootCone /2) + angleStep;
            List<Vector> directions = new ArrayList<>();
            for (int i = 0; i<projectiles; i++){
                double rad = (initialOffset + (i * angleStep))/180*Math.PI;
                Vector currentConeDir = origin.toVector().clone()
                        .add(
                                perpendicularAxis.clone().multiply((radius)*Math.sin(rad))
                        ).add(
                                dir.clone().multiply((radius)*Math.cos(rad))
                        );
                currentConeDir.subtract(origin.toVector()).normalize();
                directions.add(currentConeDir);
            }
            for (Vector currentDir : directions){
                Vector currentTarget = Projectile.getRaytracedMaxDistance(origin, currentDir, maxDistance);
                Vector initialVelocity = currentDir.clone().multiply(config.maxSpeed());
                if (target!=null){
                    new Projectile(skillcast,attackData,origin.toVector().clone(),initialVelocity,new Vector(),target,maxDistance,config);
                } else {
                    new Projectile(skillcast,attackData,origin.toVector().clone(),initialVelocity,new Vector(),currentTarget,maxDistance,config);
                }
            }
        }
    },
    SHOTGUN {
        @Override
        public void instantiate(Skillcast skillcast, AttackData attackData, ProjectileConfig config, Location origin, Vector target,
                                int projectiles, double maxDistance, double extraData) {
            Vector lookDir = origin.getDirection().clone();
            Vector spreadCenter = Projectile.getRaytracedMaxDistance(origin, lookDir, maxDistance);

            Vector[] spreadPoints = LinalgMath.plotPointsInsideNonAlignedCircle(spreadCenter,lookDir, extraData, projectiles);
            for (Vector circleTarget : spreadPoints){
                Vector currentDir = circleTarget.clone().subtract(origin.toVector()).normalize();
                double maxSpeed = config.maxSpeed();
                Vector initialVel = currentDir.clone().multiply(Utils.getRandomInclusiveValue(0.5*maxSpeed, maxSpeed));
                if (target!=null){
                    new Projectile(skillcast,attackData,origin.toVector().clone(),initialVel,new Vector(),target,maxDistance,config);
                } else {
                    new Projectile(skillcast,attackData,origin.toVector().clone(),initialVel,new Vector(),circleTarget,maxDistance,config);
                }
            }
        }
    },
    RADIAL {
        @Override
        public void instantiate(Skillcast skillcast, AttackData attackData, ProjectileConfig config, Location origin, Vector target,
                                int projectiles, double maxDistance, double extraData) {

        }
    },
    BARRAGE {
        @Override
        public void instantiate(Skillcast skillcast, AttackData attackData, ProjectileConfig config, Location origin, Vector target,
                                int projectiles, double maxDistance, double extraData) {
            int taskID = new BukkitRunnable(){
                int shotProj = 0;
                @Override
                public void run() {
                    if (shotProj >= projectiles || skillcast.isCancelled()){
                        this.cancel();
                        return;
                    }

                    Vector currentDir = origin.getDirection().clone();
                    Vector currentTarget = Projectile.getRaytracedMaxDistance(origin, currentDir, maxDistance);
                    new Projectile(skillcast,attackData,origin.toVector().clone(),
                            currentDir.clone().multiply(config.maxSpeed()), new Vector(),currentTarget,maxDistance,config);
                    shotProj++;
                }
            }.runTaskTimer(Inscripted.getPlugin(),0, (int) extraData).getTaskId();
        }
    };

    public abstract void instantiate(Skillcast skillcast, AttackData attackData, ProjectileConfig config,
                                     Location origin, Vector target, int projectiles, double maxDistance, double extraData);
}
