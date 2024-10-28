package com.amorabot.inscripted.skills.archetypes.mace;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.skills.ParticlePlotter;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;

public class MaceMovement {


    public static void templarMovement(Player player){
        double radius = 4.7;
        double innerRadius = 3.7;
        Vector center = player.getLocation().toVector().clone();
        World playerWorld = player.getWorld();
        double maxVelocity = 1.6;

        SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "block.basalt.break", 0.8f, 0.5f);
        SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "entity.zombie.break_wooden_door", 0.1f, 0.2f);

        double fullRotation = 2*Math.PI;
        for (double rad = 0; rad <= fullRotation; rad += Math.PI/16){
            double xPos = Math.sin(rad)*innerRadius;
            double zPos = Math.cos(rad)*innerRadius;
            ParticlePlotter.plotColoredCircleAt(center.clone().add(new Vector(xPos,0.2,zPos)), playerWorld, 166, 91, 75, 1.2f, 1, 10);
        }
        ParticlePlotter.plotColoredCircleAt(center.clone(), playerWorld, 200,160,200, 1.2f, (float) radius, 60);

        List<Player> nearbyPlayers = (List<Player>) center.toLocation(playerWorld).getNearbyPlayers(radius);
        nearbyPlayers.remove(player);

        for (Player p : nearbyPlayers){
            double distToCenter = p.getLocation().toVector().clone().distance(center);
            Vector dirToCenter = p.getLocation().toVector().clone().subtract(center).normalize();
            double velScaling = distToCenter/radius;
            double velocity = 0.2F + velScaling*maxVelocity;
            p.setVelocity(dirToCenter.multiply(-velocity));
            PotionEffect slow = new PotionEffect(PotionEffectType.SLOWNESS, 60, 1, true, false, false);
            slow.apply(p);
        }
    }
}
