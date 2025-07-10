package com.amorabot.inscripted.skill.archetypes.sword;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.particle.ParticlePlotter;
import com.amorabot.inscripted.tasks.base.Skillcast;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SwordMovement {

    public static void gladiatorMovement(Player player){

    }
    public static void leap(Skillcast skillcastInstance){
        Player player = skillcastInstance.getPlayer();
        Location playerLoc = player.getLocation();
        SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "entity.ghast.shoot", 0.7f, 1.5f);
        double vel = 1.2;
        Vector dir = playerLoc.getDirection().clone();
        dir.setY(Math.abs(dir.getY()));
        player.setVelocity(dir.multiply(vel));

        ParticlePlotter.plotColoredCircleAt(playerLoc.toVector(), playerLoc.getWorld(), 255, 255, 50, 1.2f, 1.2f, 30);
    }
}
