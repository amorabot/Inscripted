package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.Inscripted;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class UpdateNBT implements CommandExecutor {

    private Inscripted plugin;

    public UpdateNBT(Inscripted plugin){
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

        //new containers implementation goes here

        return true;
    }
}
