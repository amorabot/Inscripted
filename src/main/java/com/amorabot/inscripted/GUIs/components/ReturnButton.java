package com.amorabot.inscripted.GUIs.components;

import com.amorabot.inscripted.GUIs.modules.GUIButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class ReturnButton extends GUIButton {

    public ReturnButton(int slot) {
        super(slot, Material.BARRIER, 1, false, "§c§lReturn", 
            List.of("§7Click to go back"));
    }

    @Override
    public void leftClick(Player player) {
        player.closeInventory();
    }

    @Override
    public void rightClick(Player player) {
        player.closeInventory();
    }

    @Override
    public void shiftLeftClick(Player player) {
        player.closeInventory();
    }

    @Override
    public void shiftRightClick(Player player) {
        player.closeInventory();
    }
}