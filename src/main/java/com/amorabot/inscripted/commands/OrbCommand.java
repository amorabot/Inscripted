package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.components.Items.currency.Currencies;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class OrbCommand implements TabExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)){
            return true;
        }
        Player player = (Player) commandSender;

        if (strings.length == 0){
            player.getInventory().addItem(Currencies.SCROLL_OF_WISDOM.get(5));
            player.getInventory().addItem(Currencies.AUGMENT.get(2));
            player.getInventory().addItem(Currencies.REGAL.get(2));
            player.getInventory().addItem(Currencies.MAGIC_CHISEL.get(2));
            player.getInventory().addItem(Currencies.PRISTINE_CHISEL.get(2));
            player.getInventory().addItem(Currencies.ALTERATION.get(10));
            player.getInventory().addItem(Currencies.CHAOS.get(10));
            player.getInventory().addItem(Currencies.NULLIFYING.get(3));
            player.getInventory().addItem(Currencies.IMBUIMENT_SEAL.get(3));
            player.getInventory().addItem(Currencies.PROFANE_TENDRILS.get(3));

            player.getInventory().addItem(Currencies.SHARPENING_WHETSTONE.get(5));
            player.getInventory().addItem(Currencies.ARMOR_SCRAP.get(5));
            return true;
        } else if (strings.length == 2){
            try {
                int amount = Integer.parseInt(strings[0]);
                Currencies currency = Currencies.valueOf(strings[1]);

                player.getInventory().addItem(currency.get(amount));

            } catch (IllegalArgumentException exception){
                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        List<String> options = new ArrayList<>();
        if (strings.length == 1){
            options.add("1");
            options.add("3");
            options.add("10");
            options.add("64");
            return options;
        }
        if (strings.length == 2){
            for (Currencies curr : Currencies.values()){
                options.add(curr.toString());
            }
            return options;
        }
        return null;
    }
}
