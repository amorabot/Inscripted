package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.GUIs.ItemCommandGUI;
import com.amorabot.inscripted.GUIs.RelicsGUI;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Armor.Armor;
import com.amorabot.inscripted.components.Items.ItemBuilder;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Tiers;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Player.archetypes.Archetypes;
import com.amorabot.inscripted.components.renderers.InscriptedPalette;
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
            player.sendMessage("Usage: /item itemTier itemRarity itemArchetype");
            player.sendMessage("");

            Utils.msgPlayer(player, "Example: "+"&c&l/item" + " T4" + " " + ItemRarities.AUGMENTED + " " + Archetypes.MERCENARY);
            Utils.msgPlayer(player, "&c  ->Would open the item generation menu for Tier 4 &lAUGMENTED&c(have up to 2 modifiers) Mercenary items");
            Utils.msgPlayer(player, "Item tiers: T1 T2 T3 T4 T5 & RELIC");
            Utils.msgPlayer(player, "Item Rarities: COMMON (0 modifiers),   AUGMENTED (up to 2),     RUNIC (up to 6)");
            Utils.msgPlayer(player, "Item Archetypes:");
            player.sendMessage(Component.text(""+Archetypes.MARAUDER).color(Archetypes.MARAUDER.getColor().getColor()).decorate(TextDecoration.BOLD));
            player.sendMessage(Component.text(""+Archetypes.GLADIATOR).color(Archetypes.GLADIATOR.getColor().getColor()).decorate(TextDecoration.BOLD));
            player.sendMessage(Component.text(""+Archetypes.MERCENARY).color(Archetypes.MERCENARY.getColor().getColor()).decorate(TextDecoration.BOLD));
            player.sendMessage(Component.text(""+Archetypes.ROGUE).color(Archetypes.ROGUE.getColor().getColor()).decorate(TextDecoration.BOLD));
            player.sendMessage(Component.text(""+Archetypes.SORCERER).color(Archetypes.SORCERER.getColor().getColor()).decorate(TextDecoration.BOLD));
            player.sendMessage(Component.text(""+Archetypes.TEMPLAR).color(Archetypes.TEMPLAR.getColor().getColor()).decorate(TextDecoration.BOLD));
            Utils.msgPlayer(player, "");
            Utils.msgPlayer(player, "You can also modify your non-Relic items with currencies from /orb!");
            return true;
        }
        if (args.length == 1){//manually getting Relics
            if (Objects.equals(args[0], "RELIC")){
                RelicsGUI relicsGUI = new RelicsGUI();
                player.openInventory(relicsGUI.getInventory());
                return true;
            } else {
                Utils.error("Not a valid argument for item generation");
            }
            return true;
        }

        
//        int ilvl = Integer.parseInt(args[0]);
        Tiers tier = Tiers.valueOf(args[0]);
        int ilvl = tier.getMaxLevel();
        ItemRarities rarity = ItemRarities.valueOf(args[1]);
        Archetypes archetype = Archetypes.valueOf(args[2]);

        if (args.length == 3){

            ItemCommandGUI itemGUI = new ItemCommandGUI(player, archetype, ilvl, rarity, 6, false, true);
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

    private void giveGeneratedItem(Item item, Player player){
        player.getInventory().addItem(item.getItemForm());
    }
}
