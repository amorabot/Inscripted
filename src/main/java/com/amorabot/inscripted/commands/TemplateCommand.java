package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.Inscripted;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

public class TemplateCommand implements CommandExecutor {

    /*
    entity.wither_skeleton.ambient
    wither.break_block
    minecraft:entity.arrow.hit_player
    entity.player.attack.knockback - dish dish

     */

    public static Skeleton testDummy = null;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (!(commandSender instanceof Player)){
            return false;
        }
        Player player = (Player) commandSender;
        World playerWorld = player.getWorld();

        if (strings.length == 1){
            String action = strings[0];
            switch (action){
                case "atk":
                    break;
                case "food":
                    player.sendMessage("FL " + player.getFoodLevel());
                    player.sendMessage("SAT " + player.getSaturation());
                    player.sendMessage("EXHA " + player.getExhaustion());
                    player.sendMessage("STARV " + player.getStarvationRate());
                    player.setFoodLevel(10);
                    break;
                case "toggle":
                    //Not persistent (ideal for temporary tags/ownership/toggles that are not essential in combat) -> if persistance is needed: scoreboard tags
                    if (!player.hasMetadata("Player")){
                        player.sendMessage("you are not a player!, turning you into one");
                        player.setMetadata("Player", new FixedMetadataValue(Inscripted.getPlugin(), "im a player!"));
                    } else {
                        player.sendMessage("you are a player! not anymore");
                        player.removeMetadata("Player", Inscripted.getPlugin());
                    }
                    return true;
                case "createTeam":
                    return true;
            }
        }

        return true;
    }
}
