package com.amorabot.inscripted.handlers.GUI;

import com.amorabot.inscripted.GUIs.ItemCommandGUI;
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

        //Item command GUI handling
//        if (event.getView().getTitle().equals(ItemCommandGUI.TITLE)){
//          OLD METHOD
//        }
        if (inventory.getHolder(false) instanceof ItemCommandGUI itemCommandGUI){
            event.getView().setTitle(ItemCommandGUI.TITLE);


            if (itemCommandGUI.getInventory() != event.getClickedInventory()){
//                player.sendMessage("DEBUG: Inventory click @" + ItemCommandGUI.class.getSimpleName()); //Debug message
                //Canceling the event due to client-side visual bugs (Cursor items vanishing)
                event.setCancelled(true);
                return;
            }


            event.setCancelled(!itemCommandGUI.isEditable());
            itemCommandGUI.click(player, clickedSlot, clickType);

            //Debug/test methods
//            player.getOpenInventory().getInventory()
//            event.getClick().isKeyboardClick();
//            player.closeInventory();
//            player.updateInventory();
//            player.openInventory(inventory);
        }
    }
}
