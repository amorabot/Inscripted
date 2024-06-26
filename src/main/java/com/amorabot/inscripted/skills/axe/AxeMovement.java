package com.amorabot.inscripted.skills.axe;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.Inscripted;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class AxeMovement {


    public static void marauderMovement(Player player){
        SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "entity.ravager.roar", 0.8F, 0.8F);
        int taskID = new BukkitRunnable(){

            int iteration = 0;
            final double vel = 0.9;
            //            final Vector horizontalVel = player.getLocation().getDirection().clone().setY(0).normalize().multiply(vel);
            @Override
            public void run() {
                if (iteration>15){
                    this.cancel();
                }
                final Vector horizontalVel = player.getLocation().getDirection().clone().setY(0).normalize().multiply(vel);
                player.setVelocity(horizontalVel);
                iteration+=1;
            }
        }.runTaskTimer(Inscripted.getPlugin(), 0, 3).getTaskId();
    }
}
