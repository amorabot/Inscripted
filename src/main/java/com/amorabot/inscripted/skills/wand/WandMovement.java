package com.amorabot.inscripted.skills.wand;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.skills.ParticlePlotter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class WandMovement {


    public static void sorcererMovement(Player player){
        SoundAPI.playGenericSoundAtLocation(player, player.getLocation(), "entity.enderman.teleport", 1.0F, 1.3F);

        World playerWorld = player.getWorld();
        Location playerLocation = player.getLocation().clone().add(0,1.2,0).add(player.getLocation().getDirection().clone().multiply(1.2));

        int blinkRange = 10;
        Vector initialPosition = playerLocation.toVector().clone();
        Vector targetPosition;
//        Vector currentPosition = initialPosition.clone();

        RayTraceResult result = player.rayTraceBlocks(blinkRange);
        if (result != null && result.getHitBlock() != null){
            targetPosition = result.getHitPosition();
        } else {
            targetPosition = playerLocation.toVector().add(playerLocation.getDirection().clone().multiply(blinkRange));
        }
        ParticlePlotter.coloredParticleLerp(initialPosition, targetPosition, 0.6F, playerWorld, 50, 129, 168, 1.4F);

        player.teleport(targetPosition.toLocation(playerWorld).setDirection(playerLocation.getDirection()));
    }
}
