package pluginstudies.pluginstudies.commands;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pluginstudies.pluginstudies.PluginStudies;

import java.util.Arrays;

import static pluginstudies.pluginstudies.utils.Utils.color;

public class Identify implements CommandExecutor {

    private PluginStudies plugin;

    public Identify(PluginStudies plugin){
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
            return true; //Se ele estivar algo que não tenha ItemMeta (no caso, um persistentDataContainer
        }
        ItemMeta heldItemMeta = heldItem.getItemMeta();
        PersistentDataContainer dataContainer = heldItemMeta.getPersistentDataContainer();
        if (dataContainer.has(new NamespacedKey(plugin, "state"), PersistentDataType.STRING)){
            if (!dataContainer.get(new NamespacedKey(plugin, "state"), PersistentDataType.STRING).equalsIgnoreCase("UNIDED")){
                return true;
            }
            String ilvl = dataContainer.get(new NamespacedKey(plugin, "ilvl"), PersistentDataType.INTEGER).toString();
            switch (dataContainer.get(new NamespacedKey(plugin, "rarity"), PersistentDataType.INTEGER)){
                case 0:
                    heldItemMeta.setDisplayName(color("&f&lCommon item"));
                    heldItemMeta.setLore(Arrays.asList(color("&7Item level: " + "&6&l" + ilvl)));
                    break;
                case 1:
                    heldItemMeta.setDisplayName(color("&9&lMagic item"));
                    heldItemMeta.setLore(Arrays.asList(color("&7Item level: " + "&6&l" + ilvl)));
                    break;
                case 2:
                    heldItemMeta.setDisplayName(color("&e&lRare item"));
                    heldItemMeta.setLore(Arrays.asList(
                            color("&7Item level: " + "&6&l" + ilvl),
                            color("&7"),
                            color("&4" + dataContainer.get(new NamespacedKey(plugin, "stat1"), PersistentDataType.INTEGER_ARRAY)[1] + "&c% Increased Physical Damage")));
                    break;
            }
            dataContainer.set(new NamespacedKey(plugin, "state"), PersistentDataType.STRING, "IDED");
        }
        heldItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        heldItem.setItemMeta(heldItemMeta);

        return false;
    }
}
