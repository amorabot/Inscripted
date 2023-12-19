package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Armor.Armor;
import com.amorabot.inscripted.components.Items.Armor.ArmorTypes;
import com.amorabot.inscripted.components.Items.ItemGenerator;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Tiers;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;
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

        int ilvl = Integer.parseInt(args[0]);
        ItemRarities rarity = ItemRarities.valueOf(args[1]);

        if (args[2].equals(ItemTypes.WEAPON.toString())){

            WeaponTypes weaponType = WeaponTypes.valueOf(args[3]);
            Weapon weapon = ( Weapon ) ItemGenerator.randomItem(ItemTypes.WEAPON, weaponType, ilvl, rarity,  true, false);

            if (weapon == null){
                Utils.msgPlayer(player, "Invalid weapon stats...");
                return false;
            }
            giveGeneratedItem(weapon, player);
        } else { //Its a armor

            ItemTypes armorPiece = ItemTypes.valueOf(args[2]);
            ArmorTypes armorType = ArmorTypes.valueOf(args[3]);
            Armor armor = ( Armor ) ItemGenerator.randomItem(armorPiece,armorType, ilvl, rarity, true, false);

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
        } else if (strings.length == 3) { //Item type
            for (ItemTypes type : ItemTypes.values()){
                options.add(type.toString());
            }
            return options;
        } else if (strings.length == 4) {
            //If weapon is selected, show weapon options, else
            if (strings[2].equals(ItemTypes.WEAPON.toString())){
                for (WeaponTypes weaponType : WeaponTypes.values()){
                    options.add(weaponType.toString());
                }
                return options;
            }
            //Not a weapon, show its options (For now, if its not a weapon, its a armor piece
            for (ArmorTypes armorType : ArmorTypes.values()){
                options.add(armorType.toString());
            }
            return options;

        }
        return null;
    }

    private void giveGeneratedItem(Item item, Player player){
        player.getInventory().addItem(item.getItemForm(plugin));
    }
}
