package com.amorabot.inscripted.skills.wand;

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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.amorabot.inscripted.skills.AbilityRoutines.getLargeHitbox;

public class WandBasicAttacks {

    public static void wandBasicAttackBy(Player player, PlayerAbilities mappedAbility){
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
                                DamageRouter.playerAttack(player, nearbyPlayer, DamageSource.HIT, mappedAbility);
                                hitPlayers.add(nearbyPlayer);
                            }
                        } else {
                            for (Player p : nearbyPlayers){
                                if (wandBoltCollision(p, currentPosition, hitPlayers)){
                                    DamageRouter.playerAttack(player, p,DamageSource.HIT, mappedAbility);
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
}
