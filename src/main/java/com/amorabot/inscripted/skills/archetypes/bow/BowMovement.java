package com.amorabot.inscripted.skills.archetypes.bow;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.skills.ParticlePlotter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BowMovement {


    public static void mercenaryMovement(Player player){
        Location playerLoc = player.getLocation();
        SoundAPI.playGenericSoundAtLocation(player, playerLoc, "entity.ghast.shoot", 0.7f, 1.5f);
        SoundAPI.playGenericSoundAtLocation(player, playerLoc, "entity.arrow.hit_player", 0.6f, 0.9f);
        double vel = 1.7;
        Vector dir = player.getLocation().getDirection().clone();
        player.setVelocity(dir.multiply(-vel));

        ParticlePlotter.plotColoredCircleAt(playerLoc.toVector(), playerLoc.getWorld(), 140, 255, 140, 1.2f, 1.2f, 30);
    }
}
