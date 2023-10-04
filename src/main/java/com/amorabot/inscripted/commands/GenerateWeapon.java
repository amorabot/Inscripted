package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemRarities;
//import com.amorabot.rpgelements.components.Items.DataStructures.Modifier;
import com.amorabot.inscripted.components.Items.Weapon.BasicWeaponGenerator;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GenerateWeapon implements CommandExecutor {
    //Todo: implement tabExecutor and commandmanagers to merge Generate armor    (Generate command with both functionalities)

    private final Inscripted plugin;
    public GenerateWeapon(Inscripted plugin){
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
                //Insert debug functionality
                player.sendMessage(Utils.color("&7--------"+randomWeapon.getStarRating()+"★"+"---------"));
                return true;
            }
        } catch (IndexOutOfBoundsException exception){
            return true;
        }

        return true;
    }
}
