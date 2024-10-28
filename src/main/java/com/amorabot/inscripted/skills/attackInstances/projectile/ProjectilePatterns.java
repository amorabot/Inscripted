package com.amorabot.inscripted.skills.attackInstances.projectile;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.skills.math.LinalgMath;
import com.amorabot.inscripted.skills.PlayerAbilities;
import com.amorabot.inscripted.skills.SteeringBehaviors;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public enum ProjectilePatterns {

    CONE {
        @Override
        public void instantiate(Player player, PlayerAbilities sourceAbility, int projectiles, int maxRange, double projSpeed, double detectionRange,
                                SteeringBehaviors behavior, boolean gravity, boolean ignoreBlocks, boolean destroyOnContact,
                                Consumer<Projectile> trailRenderer, Consumer<Projectile> collisionRoutine, double extraData, Vector... targets) {
            Location origin = player.getLocation().clone().add(0,1.2,0);
//            int shootCone = 90;
            int shootCone = (int) extraData;
            double radius = 2;

            Vector lookDir = origin.getDirection().clone();
            Vector perpendicularAxis = lookDir.clone().crossProduct(new Vector(lookDir.getX(), 0, lookDir.getZ())).normalize();
            double angleStep = (double) shootCone /(projectiles+1);
            double initialOffset = -((double) shootCone /2) + angleStep;
            List<Vector> directions = new ArrayList<>();
            for (int i = 0; i<projectiles; i++){
                double rad = (initialOffset + (i * angleStep))/180*Math.PI;
                Vector currentDir = origin.toVector().clone()
                        .add(
                                perpendicularAxis.clone().multiply((radius)*Math.sin(rad))
                        ).add(
                                lookDir.clone().multiply((radius)*Math.cos(rad))
                        );
                currentDir.subtract(origin.toVector()).normalize();
                directions.add(currentDir);
            }
            for (Vector dir : directions){
                Vector currentTarget = Projectile.getRaytracedMaxDistance(origin, dir, maxRange);
//                    Vector currentTarget = dir.clone().multiply(maxRange).add(origin.toVector());
                Vector initialVelocity = dir.clone().multiply(projSpeed);
                Projectile currProj = new Projectile(player, sourceAbility, origin.toVector().clone(), initialVelocity, new Vector(), currentTarget,
                        gravity, ignoreBlocks, destroyOnContact, projSpeed, 0.12, Math.min(currentTarget.distance(origin.toVector()), maxRange),detectionRange,
                        behavior, trailRenderer, collisionRoutine
                );
                //If the behavior dictates a need for a different type of target, prioritize it
                updateTargets(currProj, targets);
                currProj.execute();
            }
        }
    },
    SHOTGUN {
        @Override
        public void instantiate(Player player, PlayerAbilities sourceAbility, int projectiles, int maxRange, double projSpeed, double detectionRange,
                                SteeringBehaviors behavior, boolean gravity, boolean ignoreBlocks, boolean destroyOnContact,
                                Consumer<Projectile> trailRenderer, Consumer<Projectile> collisionRoutine, double extraData, Vector... targets) {
            Location origin = player.getLocation().clone().add(0,1.2,0);
//            double coneRadius = 12;
            double coneRadius = extraData;

            Vector lookDir = origin.getDirection().clone();
            Vector spreadCenter = Projectile.getRaytracedMaxDistance(origin, lookDir, maxRange);

            Vector[] spreadPoints = LinalgMath.plotPointsInsideNonAlignedCircle(spreadCenter,lookDir, coneRadius, projectiles);
            for (Vector target : spreadPoints){
                Vector currentDir = target.clone().subtract(origin.toVector()).normalize();
                Vector initialVel = currentDir.clone().multiply(Utils.getRandomInclusiveValue(0.5*projSpeed, projSpeed));
                Projectile currProj = new Projectile(player, sourceAbility, origin.toVector().clone(), initialVel, new Vector(), target,
                        gravity, ignoreBlocks, destroyOnContact, projSpeed, 0.15, maxRange, detectionRange,
                        behavior, trailRenderer, collisionRoutine
                );
                updateTargets(currProj,targets);
                currProj.execute();
            }
        }
    },
    BARRAGE {
        @Override
        public void instantiate(Player player, PlayerAbilities sourceAbility, int projectiles, int maxRange, double projSpeed, double detectionRange,
                                SteeringBehaviors behavior, boolean gravity, boolean ignoreBlocks, boolean destroyOnContact,
                                Consumer<Projectile> trailRenderer, Consumer<Projectile> collisionRoutine, double extraData, Vector... targets) {
            int period = (int) extraData;
            int taskID = new BukkitRunnable(){
                int shotProj = 0;
                @Override
                public void run() {
                    if (shotProj >= projectiles || !player.isOnline()){
                        this.cancel();
                        return;
                    }

                    Location origin = player.getLocation().clone().add(0,1.2,0);
                    Vector currentDir = origin.getDirection().clone();
                    Vector currentTarget = Projectile.getRaytracedMaxDistance(origin, currentDir, maxRange);
                    Projectile currProj = new Projectile(player, sourceAbility, origin.toVector().clone(), currentDir.clone().multiply(projSpeed), new Vector(), currentTarget,
                            gravity, ignoreBlocks, destroyOnContact, projSpeed, 0.10, maxRange, detectionRange,
                            behavior, trailRenderer, collisionRoutine
                    );
                    updateTargets(currProj,targets);
                    currProj.execute();
                    shotProj++;
                }
            }.runTaskTimer(Inscripted.getPlugin(),0, period).getTaskId();
        }
    };

    ProjectilePatterns(){

    }

    public abstract void instantiate(Player player, PlayerAbilities sourceAbility, int projectiles, int maxRange, double projSpeed, double detectionRange,
                                     SteeringBehaviors behavior, boolean gravity, boolean ignoreBlocks, boolean destroyOnContact,
                                     Consumer<Projectile> trailRenderer, Consumer<Projectile> collisionRoutine, double extraData, Vector... targets);

    public void updateTargets(Projectile proj, Vector[] targets){
        if (proj.getBehavior().isSingleTarget() && targets != null){
            if (targets[0] != null){proj.changeTarget(targets[0]);}
        }
    }
}
