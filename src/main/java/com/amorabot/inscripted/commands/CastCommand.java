package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.skills.PlayerAbilities;
import com.amorabot.inscripted.skills.attackInstances.slash.Slash;
import com.amorabot.inscripted.skills.attackInstances.slash.SlashConfigDTO;
import com.amorabot.inscripted.skills.attackInstances.slash.SlashSegment;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CastCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)){
            return true;
        }
        Player player = (Player) commandSender;
        if (strings == null){return false;}
        try{
            String selectorArgument = strings[0];

            switch (selectorArgument){
                case "slash":
                    boolean isMirrored = Math.random() > 0.5;
                    boolean isInverted = Math.random() > 0.5;
                    SlashConfigDTO slashConfig = new SlashConfigDTO(
                            20,100,2,-0.2, 0.1,
                            0.3,1.2, new int[]{173, 143, 130}, 0.7F, 0.2
                            );

                    Slash slash = new Slash(player, PlayerAbilities.BASIC_SWORD_SLASH,slashConfig,
                            isMirrored,isInverted,false, SlashSegment::standardSword, 30);

                    slash.execute();
                    return true;
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            player.sendMessage("Invalid Call");
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        //Add tab completion to spell casts
        return List.of();
    }
}
