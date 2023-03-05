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
//        StringBuilder stringBuilder = new StringBuilder();
//        String itemPool = "Item Pool: ";
//        for (String affixName : AxeAffixes.PREFIXES.getAffixList()){
//            itemPool += (affixName + ", ");
//        }
//        Utils.log(itemPool);
        return new CraftableWeapon(ilvl).generateItem(plugin, rarity);
    }
}
