package com.amorabot.inscripted.skill.archetypes.mace;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.math.LinalgMath;
import com.amorabot.inscripted.particle.ParticlePlotter;
import com.amorabot.inscripted.tasks.base.Skillcast;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;

public class MaceMovement {
    public static void pull(Skillcast skillcastInstance){
        Player player = skillcastInstance.getPlayer();
        double radius = 4.7;
        Vector center = player.getLocation().toVector().clone();
        World playerWorld = player.getWorld();
        double maxVelocity = 1.6;

        SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "block.basalt.break", 0.8f, 0.5f);
        SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "entity.zombie.break_wooden_door", 0.1f, 0.2f);

        Vector[] points = LinalgMath.plotPointsInsideHorizontalCircle(center.clone(),radius,30);
        for (Vector point : points){
            ParticlePlotter.spawnColoredParticleAt(point,playerWorld,166, 91, 75,1.2f,1);
        }
        ParticlePlotter.plotColoredCircleAt(center.clone(), playerWorld, 184, 37, 217, 2f, (float) radius, 60,true);

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
