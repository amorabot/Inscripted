package com.amorabot.inscripted.skills.mace;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.skills.ParticlePlotter;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class MaceMovement {


    public static void templarMovement(Player player){
        double radius = 4.7;
        Vector center = player.getLocation().toVector().clone();
        World playerWorld = player.getWorld();
        double maxVelocity = 1.6;

        SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "block.basalt.break", 0.8f, 0.5f);
        SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "entity.zombie.break_wooden_door", 0.1f, 0.2f);

        for (double rad = 0; rad <= 2*Math.PI; rad += Math.PI/16){
            double xPos = Math.sin(rad)*radius;
            double zPos = Math.cos(rad)*radius;
            ParticlePlotter.spawnParticleAt(center.clone().add(new Vector(xPos,0.4,zPos)), playerWorld, Particle.END_ROD);
        }

        List<Player> nearbyPlayers = (List<Player>) center.toLocation(playerWorld).getNearbyPlayers(radius);
        nearbyPlayers.remove(player);

        for (Player p : nearbyPlayers){
            double distToCenter = p.getLocation().toVector().clone().distance(center);
            Vector dirToCenter = p.getLocation().toVector().clone().subtract(center).normalize();
            double velScaling = distToCenter/radius;
            double velocity = 0.2F + velScaling*maxVelocity;
            p.setVelocity(dirToCenter.multiply(-velocity));
        }
    }
}
