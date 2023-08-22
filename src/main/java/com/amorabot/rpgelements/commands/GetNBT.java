package com.amorabot.rpgelements.commands;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.Items.DataStructures.GenericItemContainerDataType;
import com.amorabot.rpgelements.components.Items.Weapon.Weapon;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class GetNBT implements CommandExecutor {

    private RPGElements plugin;

    public GetNBT(RPGElements plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            return true;
        }

        Player player = (Player) sender;

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
//        if (dataContainer.has(new NamespacedKey(plugin, "item-data"), new GenericItemContainerDataType<>(Weapon.class))){
//            player.sendMessage(":D");
//        }
        player.sendMessage(meta.getAsString());
        player.sendMessage(dataContainer.toString());
        player.sendMessage("datacontainerStatus: " + dataContainer.isEmpty());
        return false;
    }
}
