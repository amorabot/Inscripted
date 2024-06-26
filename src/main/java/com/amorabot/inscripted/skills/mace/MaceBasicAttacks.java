package com.amorabot.inscripted.skills.mace;

import com.amorabot.inscripted.APIs.damageAPI.DamageRouter;
import com.amorabot.inscripted.APIs.damageAPI.DamageSource;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.skills.ParticlePlotter;
import com.amorabot.inscripted.skills.PlayerAbilities;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class MaceBasicAttacks {



    public static void maceBasicAttackBy(Player player, PlayerAbilities mappedAbility){
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
                            DamageRouter.playerAttack(player,p, DamageSource.HIT, mappedAbility);
                        }

                        this.cancel();
                    }
                    currAngle+=Math.PI/16;
                }
            }
        }.runTaskTimer(Inscripted.getPlugin(), 0, 1).getTaskId();
    }
}
