package pluginstudies.pluginstudies.managers;

import org.bukkit.inventory.ItemStack;
import pluginstudies.pluginstudies.Crafting.Weapons.AxeAffixes;
import pluginstudies.pluginstudies.PluginStudies;
import pluginstudies.pluginstudies.components.CraftableWeapon;
import pluginstudies.pluginstudies.utils.Utils;

public class CraftingManager {

    private PluginStudies plugin;
    public CraftingManager(PluginStudies plugin){
        this.plugin = plugin;
    }

    public ItemStack generateWeapon(int ilvl, int rarity){
        return new CraftableWeapon(ilvl, "AXE").generate(plugin, rarity);
    }
}
