package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.gui.instances.ItemGeneration;
import com.amorabot.inscripted.item.relic.Relics;
import com.amorabot.inscripted.item.structure.ItemRarities;
import com.amorabot.inscripted.item.structure.Tiers;
import com.amorabot.inscripted.player.Archetypes;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GenerateItem implements TabExecutor {

    private final Inscripted plugin;
    public GenerateItem(Inscripted plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;

        new ItemGeneration(player).open();
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        List<String> options = new ArrayList<>();
        if (strings.length == 1){ //Level argument
            for (Tiers tier : Tiers.values()){
                options.add(tier.toString());
//                int tierMaxLevel = tier.getMaxLevel();
//                options.add(String.valueOf(t));
            }
            options.add("RELIC");
            return options;
        } else if (strings.length == 2) { // Rarity
            if (strings[1].equals("RELIC")){
                for (Relics relic : Relics.values()){
                    options.add(relic.name());
                }
                return options;
            }
            for (ItemRarities rarity : ItemRarities.values()){
                options.add(rarity.toString());
            }
            options.remove(ItemRarities.RELIC.toString());
            return options;
        } else if (strings.length == 3) { //Archetype
            for (Archetypes archetype : Archetypes.values()){
                options.add(archetype.toString());
            }
            return options;
        }
        return null;
    }
}
