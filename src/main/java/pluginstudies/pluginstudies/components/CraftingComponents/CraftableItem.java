package pluginstudies.pluginstudies.components.CraftingComponents;

import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;

public interface CraftableItem {
    ItemStack getItemForm();
    void serializeContainers(PersistentDataContainer itemContainer);
    void render(List<String> lore, List<Enum<?>> modifiers);
}
