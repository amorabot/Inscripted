package com.amorabot.inscripted.gui;

import com.amorabot.inscripted.gui.instances.RelicSelection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIRouter implements CustomInterfaceVisitor{
    @Override
    public void visitRelicSelection(RelicSelection relicSelectionGUI, InventoryClickEvent event) {
        defaultClickAttempt(relicSelectionGUI,event);
    }

    private void defaultClickAttempt(GUI clickedGUI, InventoryClickEvent event){
        Player player = clickedGUI.getOwner();
        int clickedSlot = event.getSlot();
        ClickType clickType = event.getClick();

        clickedGUI.click(player, clickedSlot, clickType);
        return;
    }
}
