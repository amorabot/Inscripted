package com.amorabot.inscripted.skills.bow;

import com.amorabot.inscripted.APIs.damageAPI.DamageRouter;
import com.amorabot.inscripted.APIs.damageAPI.DamageSource;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.skills.ParticlePlotter;
import com.amorabot.inscripted.skills.PlayerAbilities;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;

import static com.amorabot.inscripted.skills.AbilityRoutines.getLargeHitbox;

public class BowBasicAttacks {



    public static void bowBasicAttackBy(Player player, PlayerAbilities mappedAbility){
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
                                DamageRouter.playerAttack(player, p, DamageSource.HIT, mappedAbility);
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
}
