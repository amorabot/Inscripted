package pluginstudies.pluginstudies.deprecated;

import org.bukkit.inventory.ItemStack;
import pluginstudies.pluginstudies.RPGElements;
import pluginstudies.pluginstudies.deprecated.CraftableWeapon;
@Deprecated
public class CraftingManager {

    private RPGElements plugin;
    public CraftingManager(RPGElements plugin){
        this.plugin = plugin;
    }

    public ItemStack generateWeapon(int ilvl, int rarity, String weaponType){
        return new CraftableWeapon(ilvl, weaponType).generate(plugin, rarity);
    }
}
