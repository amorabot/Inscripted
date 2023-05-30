package com.amorabot.rpgelements.commands;

import com.amorabot.rpgelements.Crafting.ItemRarities;
import com.amorabot.rpgelements.Crafting.ItemTypes;
import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.CraftingComponents.Builders.WeaponBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.amorabot.rpgelements.Crafting.ItemBaseImplicits;
import com.amorabot.rpgelements.Crafting.Weapons.Enums.WeaponTypes;

public class GenerateWeapon implements CommandExecutor {

    private RPGElements plugin;
//    private CraftingManager craftingManager;
    public GenerateWeapon(RPGElements plugin){
        this.plugin = plugin;
//        this.craftingManager = new CraftingManager(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;
        //TODO check dos tipos de arma
        int ilvlArg = Integer.parseInt(args[0]);
        String rarityArg = args[1];
        String weaponType = args[2];

        WeaponBuilder weapon =
                new WeaponBuilder(plugin, ItemTypes.WEAPON, WeaponTypes.valueOf(weaponType),
                        false, ilvlArg, ItemRarities.valueOf(rarityArg), ItemBaseImplicits.valueOf(weaponType).getBasicImplicit(), rarityArg);

        ItemStack item = weapon.getItemForm();
        player.getInventory().addItem(item);
        return true;
    }
}
