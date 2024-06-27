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
import com.amorabot.inscripted.components.Items.modifiers.unique.Relics;
import com.amorabot.inscripted.components.Player.archetypes.Archetypes;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
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
            player.sendMessage("Usage: /item ItemLevel Rarity Archetype");
            player.sendMessage("Alternate usage: /item RELIC (Opens the Relic list menu)");
            player.sendMessage("");

            Utils.msgPlayer(player, "Example: "+"&c&l/item" + " 89" + " " + ItemRarities.MAGIC + " " + Archetypes.MERCENARY);
            Utils.msgPlayer(player, "&c  ->Would open the item generation menu for &9&lMAGIC &cMercenary items (of ilvl 89)");
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

        int ilvl = Integer.parseInt(args[0]);
        ItemRarities rarity = ItemRarities.valueOf(args[1]);
        Archetypes archetype = Archetypes.valueOf(args[2]);

        if (args.length == 3){

            ItemCommandGUI itemGUI = new ItemCommandGUI(player, archetype, ilvl, rarity, 6, false, true);
            player.openInventory(itemGUI.getInventory());
            return true;
        }

        if (args[3].equals("SET")){
            Weapon weapon = ( Weapon ) ItemBuilder.randomItem(ItemTypes.WEAPON, archetype.getWeaponType(), ilvl, rarity,  true, false);
            Armor helmet = ( Armor ) ItemBuilder.randomItem(ItemTypes.HELMET,archetype.getArmorType(), ilvl, rarity, true, false);
            Armor chestplate = ( Armor ) ItemBuilder.randomItem(ItemTypes.CHESTPLATE,archetype.getArmorType(), ilvl, rarity, true, false);
            Armor leggings = ( Armor ) ItemBuilder.randomItem(ItemTypes.LEGGINGS,archetype.getArmorType(), ilvl, rarity, true, false);
            Armor boots = ( Armor ) ItemBuilder.randomItem(ItemTypes.BOOTS,archetype.getArmorType(), ilvl, rarity, true, false);
            giveGeneratedItem(weapon, player);
            giveGeneratedItem(helmet, player);
            giveGeneratedItem(chestplate, player);
            giveGeneratedItem(leggings, player);
            giveGeneratedItem(boots, player);
            return true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        List<String> options = new ArrayList<>();
        if (strings.length == 1){ //Level argument
            options.add("RELIC");
            for (Tiers tier : Tiers.values()){
                int tierMaxLevel = tier.getMaxLevel();
                options.add(String.valueOf(tierMaxLevel));
            }
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
        } else if (strings.length == 4) { //Equipment type (ItemTypes)
            options.add("SET");
            for (ItemTypes type : ItemTypes.values()){
                options.add(type.toString());
            }
            return options;
        }
        return null;
    }

    private void giveGeneratedItem(Item item, Player player){
        player.getInventory().addItem(item.getItemForm(plugin));
    }
}
