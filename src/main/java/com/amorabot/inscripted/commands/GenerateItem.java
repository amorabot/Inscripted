package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.GUIs.ItemCommandGUI;
import com.amorabot.inscripted.GUIs.RelicsGUI;
import com.amorabot.inscripted.Inscripted;
//import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.item.structure.ItemRarities;
import com.amorabot.inscripted.item.structure.Tiers;
import com.amorabot.inscripted.player.Archetypes;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        if (args.length == 0){
            // Open the item generation GUI with default values
            ItemCommandGUI itemGUI = new ItemCommandGUI(player, Archetypes.MARAUDER, 75, ItemRarities.AUGMENTED);
            player.openInventory(itemGUI.getInventory());
            return true;
        }
        if (args.length == 1){//manually getting Relics
            if (Objects.equals(args[0].toUpperCase(), "RELIC")){
                RelicsGUI relicsGUI = new RelicsGUI();
                player.openInventory(relicsGUI.getInventory());
                return true;
            } else if (Objects.equals(args[0].toUpperCase(), "HELP")) {
                player.sendMessage("§e§lItem Generation Commands:");
                player.sendMessage("§f/item §7- Opens the item creation interface");
                player.sendMessage("§f/item relic §7- Opens the relic browser");
                player.sendMessage("§f/item <tier> <rarity> <archetype> §7- Opens interface with presets");
                player.sendMessage("");
                player.sendMessage("§7Available options:");
                player.sendMessage("§7Tiers: §fT1, T2, T3, T4, T5");
                player.sendMessage("§7Rarities: §fCOMMON, AUGMENTED, RUNIC");
                player.sendMessage("§7Archetypes: §fMARAUDER, GLADIATOR, MERCENARY, ROGUE, SORCERER, TEMPLAR");
            } else {
                Utils.msgPlayer(player, "§cInvalid argument. Use §f/item help §cfor usage information.");
            }
            return true;
        }

        
//        int ilvl = Integer.parseInt(args[0]);
        Tiers tier = Tiers.valueOf(args[0].toUpperCase());
        int ilvl = tier.getMaxLevel();
        ItemRarities rarity = ItemRarities.valueOf(args[1].toUpperCase());
        Archetypes archetype = Archetypes.valueOf(args[2].toUpperCase());

        if (args.length == 3){

            ItemCommandGUI itemGUI = new ItemCommandGUI(player, archetype, ilvl, rarity);
            player.openInventory(itemGUI.getInventory());
            return true;
        }
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
            options.add("HELP");
            return options;
        } else if (strings.length == 2) { // Rarity
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
