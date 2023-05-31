package com.amorabot.rpgelements.commands;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class UpdateNBT implements CommandExecutor {

    private RPGElements plugin;

    public UpdateNBT(RPGElements plugin){
        this.plugin = plugin;

    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){

        //Filter for player senders only
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;
        //If the player is not holding anything, return
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR){
            return true;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();



        return true;
    }
}
