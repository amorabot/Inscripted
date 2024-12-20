package com.amorabot.inscripted.skills.archetypes.dagger;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.skills.ParticlePlotter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DaggerMovement {


    public static void rogueMovement(Player player){
        int duration = 50;
        new BukkitRunnable(){

            final int iterations = 2;
            int index = 0;
            @Override
            public void run() {
                if (index >= iterations){
                    this.cancel();
                }
                Location playerLoc = player.getLocation();
                double progress = (double) index /iterations;
                double circleHeight = 0.2 + progress;
                SoundAPI.playGenericSoundAtLocation(player, playerLoc, "entity.ghast.shoot", 0.7f, 0.9f + (0.3f*index));
                ParticlePlotter.plotCircleAt(playerLoc.toVector().clone().add(new Vector(0, circleHeight, 0)), playerLoc.getWorld(), Particle.SMOKE,0.5F, 20);
                index++;
            }
        }.runTaskTimer(Inscripted.getPlugin(), 0, 2);

        for (Player p : Bukkit.getOnlinePlayers()){
            p.hidePlayer(Inscripted.getPlugin(), player);
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()){
                    p.showPlayer(Inscripted.getPlugin(), player);
                }
                PotionEffect exhaustionEffect = new PotionEffect(PotionEffectType.SLOWNESS, 40, 1, true, false, false);
                exhaustionEffect.apply(player);
                this.cancel();
            }
        }.runTaskLater(Inscripted.getPlugin(), duration);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 1));
    }
}
