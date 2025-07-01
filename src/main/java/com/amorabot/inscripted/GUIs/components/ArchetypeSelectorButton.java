package com.amorabot.inscripted.GUIs.components;

import com.amorabot.inscripted.GUIs.modules.GUIButton;
import com.amorabot.inscripted.player.Archetypes;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ArchetypeSelectorButton extends GUIButton {
    
    private final Archetypes archetype;
    private final Consumer<Archetypes> onArchetypeSelected;
    private final boolean isSelected;

    public ArchetypeSelectorButton(int slot, Archetypes archetype, boolean isSelected, Consumer<Archetypes> onArchetypeSelected) {
        super(slot, getMaterialForArchetype(archetype), 1, isSelected, 
            (isSelected ? "§a§l" : getArchetypeColor(archetype) + "§l") + archetype.name(),
            createArchetypeDescription(archetype, isSelected));
        this.archetype = archetype;
        this.onArchetypeSelected = onArchetypeSelected;
        this.isSelected = isSelected;
    }

    private static Material getMaterialForArchetype(Archetypes archetype) {
        return switch (archetype) {
            case MARAUDER -> Material.IRON_AXE;
            case GLADIATOR -> Material.IRON_SWORD;
            case MERCENARY -> Material.BOW;
            case ROGUE -> Material.STONE_SWORD;
            case SORCERER -> Material.STICK;
            case TEMPLAR -> Material.STONE_AXE;
            case NONE -> Material.BARRIER;
        };
    }

    private static String getArchetypeColor(Archetypes archetype) {
        return switch (archetype) {
            case MARAUDER -> "§c"; // Red
            case GLADIATOR -> "§6"; // Gold  
            case MERCENARY -> "§a"; // Green
            case ROGUE -> "§3"; // Dark Aqua
            case SORCERER -> "§9"; // Blue
            case TEMPLAR -> "§5"; // Dark Purple
            case NONE -> "§7"; // Gray
        };
    }

    private static List<String> createArchetypeDescription(Archetypes archetype, boolean isSelected) {
        List<String> lore = new ArrayList<>();
        
        String weaponType = switch (archetype) {
            case MARAUDER -> "§7Weapon: §fAxe";
            case GLADIATOR -> "§7Weapon: §fSword";
            case MERCENARY -> "§7Weapon: §fBow";
            case ROGUE -> "§7Weapon: §fDagger";
            case SORCERER -> "§7Weapon: §fWand";
            case TEMPLAR -> "§7Weapon: §fMace";
            case NONE -> "§7No archetype";
        };
        
        String armorType = switch (archetype) {
            case MARAUDER -> "§7Armor: §fArmored";
            case GLADIATOR -> "§7Armor: §fOrnate";
            case MERCENARY -> "§7Armor: §fCloth";
            case ROGUE -> "§7Armor: §fPelt";
            case SORCERER -> "§7Armor: §fSilk";
            case TEMPLAR -> "§7Armor: §fRunisteel";
            case NONE -> "";
        };

        lore.add(weaponType);
        if (!armorType.isEmpty()) {
            lore.add(armorType);
        }
        lore.add("");
        
        if (isSelected) {
            lore.add("§a§l✓ SELECTED");
        } else {
            lore.add("§eClick to select this archetype");
        }
        return lore;
    }

    @Override
    public void leftClick(Player player) {
        onArchetypeSelected.accept(archetype);
    }

    @Override
    public void rightClick(Player player) {
        onArchetypeSelected.accept(archetype);
    }

    @Override
    public void shiftLeftClick(Player player) {
        onArchetypeSelected.accept(archetype);
    }

    @Override
    public void shiftRightClick(Player player) {
        onArchetypeSelected.accept(archetype);
    }
}