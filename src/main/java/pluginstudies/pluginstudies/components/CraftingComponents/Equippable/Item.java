package pluginstudies.pluginstudies.components.CraftingComponents.Equippable;

import org.bukkit.inventory.meta.ItemMeta;
import pluginstudies.pluginstudies.CustomDataTypes.ItemInfo.ItemInformation;

public abstract class Item {
    protected String tag;
    protected ItemInformation itemInfo;

    public Item(String coloredTag){
        tag = coloredTag;
    }

    protected abstract void generateMods(ItemMeta itemMeta);
}
