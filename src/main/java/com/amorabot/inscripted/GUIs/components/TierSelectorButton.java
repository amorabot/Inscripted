package com.amorabot.inscripted.GUIs.components;

import com.amorabot.inscripted.GUIs.modules.GUIButton;
import com.amorabot.inscripted.item.structure.Tiers;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TierSelectorButton extends GUIButton {
    
    private final Tiers tier;
    private final Consumer<Tiers> onTierSelected;
    private final boolean isSelected;

    public TierSelectorButton(int slot, Tiers tier, boolean isSelected, Consumer<Tiers> onTierSelected) {
        super(slot, getMaterialForTier(tier), 1, isSelected, 
            (isSelected ? "§a§l" : getTierColor(tier)) + "§l" + tier.name(),
            createTierDescription(tier, isSelected));
        this.tier = tier;
        this.onTierSelected = onTierSelected;
        this.isSelected = isSelected;
    }

    private static Material getMaterialForTier(Tiers tier) {
        return switch (tier) {
            case T1 -> Material.LEATHER_HELMET;
            case T2 -> Material.CHAINMAIL_HELMET;
            case T3 -> Material.IRON_HELMET;
            case T4 -> Material.DIAMOND_HELMET;
            case T5 -> Material.GOLDEN_HELMET;
        };
    }

    private static String getTierColor(Tiers tier) {
        return switch (tier) {
            case T1 -> "§f"; // White
            case T2 -> "§7"; // Gray
            case T3 -> "§8"; // Dark Gray
            case T4 -> "§b"; // Aqua
            case T5 -> "§6"; // Gold
        };
    }

    private static List<String> createTierDescription(Tiers tier, boolean isSelected) {
        List<String> lore = new ArrayList<>();
        lore.add("§7Max Level: §f" + tier.getMaxLevel());
        lore.add("");
        if (isSelected) {
            lore.add("§a§l✓ SELECTED");
        } else {
            lore.add("§eClick to select this tier");
        }
        return lore;
    }

    @Override
    public void leftClick(Player player) {
        onTierSelected.accept(tier);
    }

    @Override
    public void rightClick(Player player) {
        onTierSelected.accept(tier);
    }

    @Override
    public void shiftLeftClick(Player player) {
        onTierSelected.accept(tier);
    }

    @Override
    public void shiftRightClick(Player player) {
        onTierSelected.accept(tier);
    }
}