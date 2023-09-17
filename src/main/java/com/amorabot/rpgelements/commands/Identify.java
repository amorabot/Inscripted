package com.amorabot.rpgelements.commands;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.rpgelements.events.FunctionalItemAccessInterface;
import com.amorabot.rpgelements.components.Items.Weapon.Weapon;
import com.amorabot.rpgelements.utils.ColorUtils;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class Identify implements CommandExecutor {

    private RPGElements plugin;

    public Identify(RPGElements plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            return true; //Se não é um player usando
        }
        Player player = (Player) sender;
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR){
            return true; // Se o player não estiver segurando nada
        }
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (!heldItem.hasItemMeta()){
            return true; //Se ele estivar algo que não tenha ItemMeta (no caso, um persistentDataContainer)
        }
        ItemMeta heldItemMeta = heldItem.getItemMeta();
        PersistentDataContainer dataContainer = heldItemMeta.getPersistentDataContainer();

        if (FunctionalItemAccessInterface.isEquipableWeapon(dataContainer)){
            player.sendMessage(ColorUtils.translateColorCodes(DefenceTypes.DODGE.getTextColor()+ "&lThis weapon is already identified!"));
        }
        if (FunctionalItemAccessInterface.isEquipableArmor(dataContainer)){
            player.sendMessage(ColorUtils.translateColorCodes(DefenceTypes.DODGE.getTextColor()+ "&lThis armor is already identified!"));
        }

        FunctionalItemAccessInterface.identifyItem(heldItem);
//            //After the item's changed, equip it if in main hand     //////      Negate currency usage with items in main hand


        return true;
    }
}