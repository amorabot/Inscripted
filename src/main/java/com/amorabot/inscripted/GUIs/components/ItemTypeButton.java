package com.amorabot.inscripted.GUIs.components;

import com.amorabot.inscripted.GUIs.modules.GUIButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ItemTypeButton extends GUIButton {
    
    public enum ItemType {
        WEAPON("§6§lWeapons", Material.IRON_SWORD, List.of("§7Generate weapons", "§7based on selected archetype")),
        ARMOR("§b§lArmor", Material.IRON_CHESTPLATE, List.of("§7Generate armor pieces", "§7based on selected archetype")),
        ALL("§a§lAll Items", Material.CHEST, List.of("§7Generate a full set", "§7of weapon and armor"));
        
        private final String displayName;
        private final Material material;
        private final List<String> description;
        
        ItemType(String displayName, Material material, List<String> description) {
            this.displayName = displayName;
            this.material = material;
            this.description = description;
        }
    }
    
    private final ItemType itemType;
    private final Consumer<ItemType> onTypeSelected;
    private final boolean isSelected;

    public ItemTypeButton(int slot, ItemType itemType, boolean isSelected, Consumer<ItemType> onTypeSelected) {
        super(slot, itemType.material, 1, isSelected, 
            (isSelected ? "§a§l" : "") + itemType.displayName,
            createDescription(itemType, isSelected));
        this.itemType = itemType;
        this.onTypeSelected = onTypeSelected;
        this.isSelected = isSelected;
    }

    private static List<String> createDescription(ItemType itemType, boolean isSelected) {
        List<String> lore = new ArrayList<>(itemType.description);
        if (isSelected) {
            lore.add("");
            lore.add("§a§l✓ SELECTED");
        } else {
            lore.add("");
            lore.add("§eClick to select");
        }
        return lore;
    }

    @Override
    public void leftClick(Player player) {
        onTypeSelected.accept(itemType);
    }

    @Override
    public void rightClick(Player player) {
        onTypeSelected.accept(itemType);
    }

    @Override
    public void shiftLeftClick(Player player) {
        onTypeSelected.accept(itemType);
    }

    @Override
    public void shiftRightClick(Player player) {
        onTypeSelected.accept(itemType);
    }
}