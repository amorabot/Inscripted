package com.amorabot.inscripted.GUIs.components;

import com.amorabot.inscripted.GUIs.modules.GUIButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class GenerateButton extends GUIButton {
    
    private final Runnable onGenerate;
    private final boolean isEnabled;

    public GenerateButton(int slot, boolean isEnabled, Runnable onGenerate) {
        super(slot, Material.EMERALD_BLOCK, 1, true, 
            isEnabled ? "§a§lGenerate Items" : "§c§lSelect Options First",
            createDescription(isEnabled));
        this.onGenerate = onGenerate;
        this.isEnabled = isEnabled;
    }

    private static List<String> createDescription(boolean isEnabled) {
        if (isEnabled) {
            return List.of(
                "§7Click to generate items",
                "§7with selected options",
                "",
                "§aReady to generate!"
            );
        } else {
            return List.of(
                "§7Select tier, rarity, archetype",
                "§7and item type first",
                "",
                "§cMissing selections"
            );
        }
    }

    @Override
    public void leftClick(Player player) {
        if (isEnabled && onGenerate != null) {
            onGenerate.run();
        }
    }

    @Override
    public void rightClick(Player player) {
        if (isEnabled && onGenerate != null) {
            onGenerate.run();
        }
    }

    @Override
    public void shiftLeftClick(Player player) {
        if (isEnabled && onGenerate != null) {
            onGenerate.run();
        }
    }

    @Override
    public void shiftRightClick(Player player) {
        if (isEnabled && onGenerate != null) {
            onGenerate.run();
        }
    }
}