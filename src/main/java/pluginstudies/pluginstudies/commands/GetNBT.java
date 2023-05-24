package pluginstudies.pluginstudies.commands;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import pluginstudies.pluginstudies.CustomDataTypes.ItemInfo.ItemInfoDataType;
import pluginstudies.pluginstudies.CustomDataTypes.ItemInfo.ItemInformation;
import pluginstudies.pluginstudies.RPGElements;

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

        ItemInformation itemInfo = dataContainer.get(new NamespacedKey(plugin, "data"), new ItemInfoDataType());
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
