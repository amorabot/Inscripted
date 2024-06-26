package com.amorabot.inscripted.skills.bow;

import com.amorabot.inscripted.APIs.SoundAPI;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BowMovement {


    public static void mercenaryMovement(Player player){
        SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "entity.ghast.shoot", 0.7f, 1.5f);
        SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "entity.arrow.hit_player", 0.6f, 0.9f);
        double vel = 1.7;
        Vector dir = player.getLocation().getDirection().clone();
        player.setVelocity(dir.multiply(-vel));
    }
}
