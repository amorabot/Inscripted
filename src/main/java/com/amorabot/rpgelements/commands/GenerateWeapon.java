package com.amorabot.rpgelements.commands;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.RangeTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.WeaponModifiers;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.WeaponTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Modifier;
import com.amorabot.rpgelements.components.Items.Weapon.BasicWeaponGenerator;
import com.amorabot.rpgelements.components.Items.Weapon.BasicWeaponRenderer;
import com.amorabot.rpgelements.components.Items.Weapon.Weapon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

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
        int ilvlArg = Integer.parseInt(args[0]);
        String rarityArg = args[1];
        String weaponType = args[2];

        Weapon randomWeapon = BasicWeaponGenerator
                .createGenericWeapon(ilvlArg, ItemRarities.valueOf(rarityArg), WeaponTypes.valueOf(weaponType), new BasicWeaponRenderer());
        assert randomWeapon != null;
        player.getInventory().addItem(randomWeapon.getItemForm(plugin, randomWeapon.getRenderer()));
        if (args[3].equals("debug")){

            List<Modifier<WeaponModifiers>> mods = randomWeapon.getModifiers();
            for (Modifier<WeaponModifiers> mod : mods){
                player.sendMessage("-----------------");
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
            player.sendMessage("-----------------");
            return true;
        }

        return true;
    }
}
