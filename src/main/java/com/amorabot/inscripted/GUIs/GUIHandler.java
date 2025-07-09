package com.amorabot.inscripted.GUIs;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.gui.GUI;
import com.amorabot.inscripted.gui.GUIRouter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GUIHandler implements Listener {

    private Inscripted plugin;

    public GUIHandler(Inscripted p){
        plugin = p;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMenuClick(InventoryClickEvent event){
        Inventory inventory = event.getInventory();

        InventoryHolder holder = inventory.getHolder(false);
        if (!(holder instanceof GUI customGUI)){
            return;
        }
        // Is a custom GUI
        event.setCancelled(!customGUI.isEditable());
        customGUI.accept(new GUIRouter(),event);
    }
}
