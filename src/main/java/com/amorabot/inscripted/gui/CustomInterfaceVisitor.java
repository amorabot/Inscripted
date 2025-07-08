package com.amorabot.inscripted.gui;

import com.amorabot.inscripted.gui.instances.RelicSelection;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface CustomInterfaceVisitor {
    void visitRelicSelection(RelicSelection relicSelectionGUI, InventoryClickEvent event);
}
