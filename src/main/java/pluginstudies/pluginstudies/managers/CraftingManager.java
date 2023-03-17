package pluginstudies.pluginstudies.managers;

import org.bukkit.inventory.ItemStack;
import pluginstudies.pluginstudies.PluginStudies;
import pluginstudies.pluginstudies.components.CraftingComponents.CraftableWeapon;

public class CraftingManager {

    private PluginStudies plugin;
    public CraftingManager(PluginStudies plugin){
        this.plugin = plugin;
    }

    public ItemStack generateWeapon(int ilvl, int rarity, String weaponType){
        return new CraftableWeapon(ilvl, weaponType).generate(plugin, rarity);
    }
}
