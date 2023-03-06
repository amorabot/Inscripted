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
import pluginstudies.pluginstudies.components.CraftableWeapon;
import pluginstudies.pluginstudies.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static pluginstudies.pluginstudies.utils.Utils.color;
import static pluginstudies.pluginstudies.utils.Utils.log;

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
            return true; //Se ele estivar algo que não tenha ItemMeta (no caso, um persistentDataContainer)
        }
        ItemMeta heldItemMeta = heldItem.getItemMeta();
        PersistentDataContainer dataContainer = heldItemMeta.getPersistentDataContainer();
        if (dataContainer.has(new NamespacedKey(plugin, "state"), PersistentDataType.STRING)){
            if (!dataContainer.get(new NamespacedKey(plugin, "state"), PersistentDataType.STRING).equalsIgnoreCase("UNIDED")){
                player.sendMessage(color("&cThis item is already identified."));
                return true;
            }
            String ilvl = dataContainer.get(new NamespacedKey(plugin, "ilvl"), PersistentDataType.INTEGER).toString();
            List<String> lore = new ArrayList<>();
            int modifiers = dataContainer.get(new NamespacedKey(plugin,"modifiers"), PersistentDataType.INTEGER);

            switch (dataContainer.get(new NamespacedKey(plugin, "rarity"), PersistentDataType.INTEGER)){
                case 0:
                    player.sendMessage("Common items cannot be identified.");
                    break;
                case 1:
                    heldItemMeta.setDisplayName(color("&9&lMagic item"));
                    if (dataContainer.get(new NamespacedKey(plugin, "type"), PersistentDataType.STRING).equalsIgnoreCase("WEAPON")){
                        // Garantimos que é uma weapon magica
                        int[] dmgRange = dataContainer.get(new NamespacedKey(plugin, "baseDMG"), PersistentDataType.INTEGER_ARRAY);
                        lore.add(color("&c DMG: " + dmgRange[0] + "-" + dmgRange[1]));
                        lore.add(color("&7"));
                    } else {
                        //se não é arma, é armadura
                    }
                    lore.add(color("&7Item level: " + "&6&l" + ilvl));
                    lore.add(color("&7"));
                    for (int i = 0; i < modifiers; i++){
                        String genericKey = String.format("stat%d", i);
                        String genericValue = dataContainer.get(new NamespacedKey(plugin, genericKey), PersistentDataType.INTEGER).toString();
                        lore.add(color("&1Stat: " + genericValue));
                    }
                    heldItemMeta.setLore(lore);
                    break;
                case 2:
                    heldItemMeta.setDisplayName(color("&e&lRare item"));
                    if (dataContainer.get(new NamespacedKey(plugin, "type"), PersistentDataType.STRING).equalsIgnoreCase("WEAPON")){
                        // Garantimos que é uma weapon magica
                        int[] dmgRange = dataContainer.get(new NamespacedKey(plugin, "baseDMG"), PersistentDataType.INTEGER_ARRAY);
                        lore.add(color("&c DMG: " + dmgRange[0] + "-" + dmgRange[1]));
                        lore.add(color("&7"));
                    } else {
                        //se não é arma, é armadura
                    }
                    lore.add(color("&7Item level: " + "&6&l" + ilvl));
                    lore.add(color("&7"));
                    for (int i = 0; i < modifiers; i++){
                        String genericKey = String.format("stat%d", i);
                        String genericValue = dataContainer.get(new NamespacedKey(plugin, genericKey), PersistentDataType.INTEGER).toString();
                        lore.add(color("&7Stat: " + "&e" + genericValue));
                    }
                    heldItemMeta.setLore(lore);
                    break;
            }
            dataContainer.set(new NamespacedKey(plugin, "state"), PersistentDataType.STRING, "IDED");
        }
        heldItem.setItemMeta(heldItemMeta);

        return true;
    }
}
