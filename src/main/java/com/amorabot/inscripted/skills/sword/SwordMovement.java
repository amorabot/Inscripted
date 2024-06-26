package com.amorabot.inscripted.skills.sword;

import com.amorabot.inscripted.APIs.SoundAPI;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SwordMovement {

    public static void gladiatorMovement(Player player){
        SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "entity.ghast.shoot", 0.7f, 1.5f);
        double vel = 1.2;
        Vector dir = player.getLocation().getDirection().clone();
        dir.setY(Math.abs(dir.getY()));
        player.setVelocity(dir.multiply(vel));
    }
}
