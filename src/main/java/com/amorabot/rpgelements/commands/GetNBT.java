package com.amorabot.rpgelements.commands;

import com.amorabot.rpgelements.RPGElements;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import com.amorabot.rpgelements.components.CraftingComponents.Items.BaseItem;
import com.amorabot.rpgelements.CustomDataTypes.RPGElementsContainerDataType;

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

        BaseItem itemInfo = dataContainer.get(new NamespacedKey(plugin, "data"), new RPGElementsContainerDataType<>(BaseItem.class));
        for (Enum<?> prefix : itemInfo.getPrefixes()){
            player.sendMessage(prefix.toString());
        }
        player.sendMessage("----------------------");
        for (Enum<?> suffix : itemInfo.getSuffixes()){
            player.sendMessage(suffix.toString());
        }

        return false;
    }
}
