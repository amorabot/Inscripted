package com.amorabot.inscripted.GUIs.components;

import com.amorabot.inscripted.GUIs.modules.GUIButton;
import com.amorabot.inscripted.item.structure.ItemRarities;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RaritySelectorButton extends GUIButton {
    
    private final ItemRarities rarity;
    private final Consumer<ItemRarities> onRaritySelected;
    private final boolean isSelected;

    public RaritySelectorButton(int slot, ItemRarities rarity, boolean isSelected, Consumer<ItemRarities> onRaritySelected) {
        super(slot, getMaterialForRarity(rarity), 1, isSelected, 
            (isSelected ? "§a§l" : getRarityColor(rarity) + "§l") + rarity.name(),
            createRarityDescription(rarity, isSelected));
        this.rarity = rarity;
        this.onRaritySelected = onRaritySelected;
        this.isSelected = isSelected;
    }

    private static Material getMaterialForRarity(ItemRarities rarity) {
        return switch (rarity) {
            case COMMON -> Material.WHITE_DYE;
            case AUGMENTED -> Material.BLUE_DYE;
            case RUNIC -> Material.YELLOW_DYE;
            case RELIC -> Material.RED_DYE;
        };
    }

    private static String getRarityColor(ItemRarities rarity) {
        return switch (rarity) {
            case COMMON -> "§f"; // White
            case AUGMENTED -> "§9"; // Blue
            case RUNIC -> "§e"; // Yellow
            case RELIC -> "§c"; // Red
        };
    }

    private static List<String> createRarityDescription(ItemRarities rarity, boolean isSelected) {
        List<String> lore = new ArrayList<>();
        lore.add("§7Max Affixes: §f" + rarity.getMaxAffixes());
        lore.add("");
        String description = switch (rarity) {
            case COMMON -> "§7Basic items with no modifiers";
            case AUGMENTED -> "§9Items with 1-2 modifiers";
            case RUNIC -> "§eItems with 3-6 modifiers";
            case RELIC -> "§cUnique items with special properties";
        };
        lore.add(description);
        lore.add("");
        if (isSelected) {
            lore.add("§a§l✓ SELECTED");
        } else {
            lore.add("§eClick to select this rarity");
        }
        return lore;
    }

    @Override
    public void leftClick(Player player) {
        onRaritySelected.accept(rarity);
    }

    @Override
    public void rightClick(Player player) {
        onRaritySelected.accept(rarity);
    }

    @Override
    public void shiftLeftClick(Player player) {
        onRaritySelected.accept(rarity);
    }

    @Override
    public void shiftRightClick(Player player) {
        onRaritySelected.accept(rarity);
    }
}