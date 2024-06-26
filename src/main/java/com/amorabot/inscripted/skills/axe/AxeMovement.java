package com.amorabot.inscripted.skills.axe;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.skills.ParticlePlotter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class AxeMovement {


    public static void marauderMovement(Player player){
        Location playerLoc = player.getLocation();
        SoundAPI.playGenericSoundAtLocation(player, playerLoc, "entity.ravager.roar", 0.8F, 0.8F);
        int taskID = new BukkitRunnable(){

            int iteration = 0;
            final double vel = 0.9;
            @Override
            public void run() {
                if (iteration>15){
                    this.cancel();
                }
                final Vector horizontalVel = player.getLocation().getDirection().clone().setY(0).normalize().multiply(vel);
                player.setVelocity(horizontalVel);
                Vector trailLocation = player.getLocation().toVector().clone().add(new Vector(0,1.2,0));
                ParticlePlotter.spawnColoredParticleAt(trailLocation, playerLoc.getWorld(), 255, 40, 50, 1.2f, 2);
                iteration+=1;
            }
        }.runTaskTimer(Inscripted.getPlugin(), 0, 3).getTaskId();

        ParticlePlotter.plotColoredCircleAt(playerLoc.toVector(), playerLoc.getWorld(), 255, 40, 50, 1.2f, 1.2f, 30);
    }
}
