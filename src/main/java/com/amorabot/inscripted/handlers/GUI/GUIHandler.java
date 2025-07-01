package com.amorabot.inscripted.handlers.GUI;

import com.amorabot.inscripted.GUIs.ItemCommandGUI;
import com.amorabot.inscripted.GUIs.OrbGUI;
import com.amorabot.inscripted.GUIs.RelicsGUI;
import com.amorabot.inscripted.Inscripted;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class GUIHandler implements Listener {

    private Inscripted plugin;

    public GUIHandler(Inscripted p){
        plugin = p;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMenuClick(InventoryClickEvent event){
        Inventory inventory = event.getInventory();

        Player player = (Player) event.getWhoClicked();
        int clickedSlot = event.getSlot();
        ClickType clickType = event.getClick();

        if (inventory.getHolder(false) instanceof ItemCommandGUI itemCommandGUI){
            if (itemCommandGUI.getInventory() != event.getClickedInventory()){
                return;
            }
            event.setCancelled(!itemCommandGUI.isEditable());
            itemCommandGUI.click(player, clickedSlot, clickType);
            return;
        }
        if (inventory.getHolder(false) instanceof OrbGUI orbGUI){
            if (orbGUI.getInventory() != event.getClickedInventory()){
                return;
            }
            event.setCancelled(!orbGUI.isEditable());
            orbGUI.click(player, clickedSlot, clickType);
            return;
        }

        if (inventory.getHolder(false) instanceof RelicsGUI relicsGUI){
            if (relicsGUI.getInventory() != event.getClickedInventory()){
                return;
            }
            event.setCancelled(!relicsGUI.isEditable());
            relicsGUI.click(player, clickedSlot, clickType);
            return;
        }
    }
}
