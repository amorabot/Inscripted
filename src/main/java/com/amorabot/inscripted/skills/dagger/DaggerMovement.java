package com.amorabot.inscripted.skills.dagger;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.Inscripted;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

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
                SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "entity.ghast.shoot", 0.7f, 0.9f + (0.3f*index));
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
                this.cancel();
            }
        }.runTaskLater(Inscripted.getPlugin(), duration);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 1));
    }
}
