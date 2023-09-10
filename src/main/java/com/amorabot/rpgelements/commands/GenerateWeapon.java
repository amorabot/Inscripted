package com.amorabot.rpgelements.commands;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.RangeTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Modifier;
import com.amorabot.rpgelements.components.Items.Weapon.BasicWeaponGenerator;
import com.amorabot.rpgelements.components.Items.Weapon.Weapon;
import com.amorabot.rpgelements.components.Items.Weapon.WeaponModifiers;
import com.amorabot.rpgelements.components.Items.Weapon.WeaponTypes;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class GenerateWeapon implements CommandExecutor {

    private final RPGElements plugin;
    public GenerateWeapon(RPGElements plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;
        String rarityArg = args[1];
        String weaponTypeArg = args[2];
        int ilvlArg = 0;
        ItemRarities rarity;
        WeaponTypes weaponType;
        try { //Argument Validation
            rarity = ItemRarities.valueOf(rarityArg.toUpperCase());
            weaponType = WeaponTypes.valueOf(weaponTypeArg.toUpperCase());
            ilvlArg = Integer.parseInt(args[0]);
        } catch (IllegalArgumentException exception){
            player.sendMessage(Utils.color("&cCorrect syntax -> /weapon itemLevel rarity weaponType [-d]"));
            return false;
        }

        Weapon randomWeapon = BasicWeaponGenerator.createGenericWeapon(ilvlArg, rarity, weaponType, false, false);
        assert randomWeapon != null;
        player.getInventory().addItem(randomWeapon.getItemForm(plugin));
        try {
            if (args[3].equals("-d")){
                List<Modifier<WeaponModifiers>> mods = randomWeapon.getModifiers();
                for (Modifier<WeaponModifiers> mod : mods){
                    player.sendMessage(Utils.color("&8--------------------"));
                    player.sendMessage(mod.getModifier().toString());
                    player.sendMessage("Tier: " + mod.getTier());
                    RangeTypes rangeType = mod.getModifier().getRangeType();
                    switch (rangeType){
                        case SINGLE_VALUE -> {
                            //
                        }
                        case SINGLE_RANGE -> {
                            player.sendMessage("Chosen value: " + mod.getValue()[0]);
                        }
                        case DOUBLE_RANGE -> {
                            int[] values = mod.getValue();
                            player.sendMessage("Chosen value: " + values[0] + " / " + values[1]);
                        }
                    }
                }
                player.sendMessage(Utils.color("&7--------"+randomWeapon.getStarRating()+"★"+"---------"));
                return true;
            }
        } catch (IndexOutOfBoundsException exception){
            return true;
//            Utils.log("weapon generated without debug mode enabled");
//            exception.printStackTrace();
        }

        return true;
    }
}
