package com.amorabot.inscripted.skills.archetypes.sword;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.skills.ParticlePlotter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SwordMovement {

    public static void gladiatorMovement(Player player){
        Location playerLoc = player.getLocation();
        SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "entity.ghast.shoot", 0.7f, 1.5f);
        double vel = 1.2;
        Vector dir = playerLoc.getDirection().clone();
        dir.setY(Math.abs(dir.getY()));
        player.setVelocity(dir.multiply(vel));

        ParticlePlotter.plotColoredCircleAt(playerLoc.toVector(), playerLoc.getWorld(), 255, 255, 50, 1.2f, 1.2f, 30);
    }
}
