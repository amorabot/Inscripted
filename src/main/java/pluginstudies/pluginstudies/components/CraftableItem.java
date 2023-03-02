package pluginstudies.pluginstudies.components;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pluginstudies.pluginstudies.Crafting.Weapons.AxeAffixes;
import pluginstudies.pluginstudies.PluginStudies;

import static pluginstudies.pluginstudies.utils.Utils.color;

public abstract class CraftableItem {

    protected boolean identified;
    protected int ilvl;
    protected int rarity; // 0 = Common, 1 = Magic, 2 = Rare, TODO: 3 = Unique
    protected int affixLimit;
    protected Material itemType;
    protected String implicit;
    protected String[] prefixes;
    protected String[] suffixes;
    protected String[][] affixes = {prefixes, suffixes};

    public ItemStack generateItem(PluginStudies plugin){
        ItemStack item = new ItemStack(itemType);
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        int[] physRange = AxeAffixes.PREFIXES.getAffix("INCREASED_PHYS").get(ilvl).get(0);

        dataContainer.set(new NamespacedKey(plugin, "state"), PersistentDataType.STRING, "UNIDED");
        dataContainer.set(new NamespacedKey(plugin, "ilvl"), PersistentDataType.INTEGER, ilvl);
        dataContainer.set(new NamespacedKey(plugin, "rarity"), PersistentDataType.INTEGER, rarity);
        dataContainer.set(new NamespacedKey(plugin, "stat1"), PersistentDataType.INTEGER_ARRAY, physRange);

        itemMeta.setDisplayName(color("&4&lUnidentified item"));

        item.setItemMeta(itemMeta);
        return item;
    }
}
