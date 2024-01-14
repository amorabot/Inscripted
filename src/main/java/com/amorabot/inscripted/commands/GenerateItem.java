package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Armor.Armor;
import com.amorabot.inscripted.components.Items.ItemGenerator;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Tiers;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Player.archetypes.Archetypes;
import com.amorabot.inscripted.utils.Utils;
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

        if (args.length == 0){
            player.sendMessage("Usage: /item ItemLevel Rarity Archetype EquipmentSlot");
            player.sendMessage("Example: " +
                    Utils.color("&c&l/item" + " 120" + " " + ItemRarities.MAGIC + " " + Archetypes.MERCENARY + " " + ItemTypes.HELMET));
            return true;
        }

        int ilvl = Integer.parseInt(args[0]);
        ItemRarities rarity = ItemRarities.valueOf(args[1]);
        Archetypes archetype = Archetypes.valueOf(args[2]);

        if (args[3].equals(ItemTypes.WEAPON.toString())){
            Weapon weapon = ( Weapon ) ItemGenerator.randomItem(ItemTypes.WEAPON, archetype.getWeaponType(), ilvl, rarity,  true, false);
            if (weapon == null){
                Utils.msgPlayer(player, "Invalid weapon stats...");
                return false;
            }
            giveGeneratedItem(weapon, player);
        } else {
            ItemTypes armorPiece = ItemTypes.valueOf(args[3]);
            Armor armor = ( Armor ) ItemGenerator.randomItem(armorPiece,archetype.getArmorType(), ilvl, rarity, true, false);
            if (armor == null){
                Utils.msgPlayer(player, "Invalid armor stats...");
                return false;
            }
            giveGeneratedItem(armor, player);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        List<String> options = new ArrayList<>();
        if (strings.length == 1){ //Level argument
            options.add("1");
            for (Tiers tier : Tiers.values()){
                int tierMaxLevel = tier.getMaxLevel();
                options.add(String.valueOf(tierMaxLevel));
            }
            return options;
        } else if (strings.length == 2) { // Rarity
            for (ItemRarities rarity : ItemRarities.values()){
                options.add(rarity.toString());
            }
            return options;
        } else if (strings.length == 3) { //Archetype
            for (Archetypes archetype : Archetypes.values()){
                options.add(archetype.toString());
            }
            return options;
        } else if (strings.length == 4) { //Equipment type (ItemTypes)
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
