package com.amorabot.inscripted.handlers.Inventory;

import com.amorabot.inscripted.APIs.EventAPI;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.currency.Currencies;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryHandler implements Listener {

    public InventoryHandler(){
        Bukkit.getPluginManager().registerEvents(this, Inscripted.getPlugin());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (event.getClickedInventory() == null) {
            return;
        }
        if (event.getCurrentItem() == null) {
            return;
        }

        InventoryAction attemptedAction = event.getAction();
        Player player = (Player) event.getWhoClicked();
        PlayerInventory inventory = player.getInventory();

        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        if (attemptedAction == InventoryAction.SWAP_WITH_CURSOR){
            if (event.getSlot() == inventory.getHeldItemSlot()){
                player.sendMessage("No currency usage on main hand....");
                event.setCancelled(true);
                return;
            }

            if (event.isRightClick()){
                if (Currencies.isCurrency(cursorItem)){
                    if (isNotFunctional(clickedItem)){
                        return;
                    }
                    EventAPI.currencyUsage(player,clickedItem,cursorItem);
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean isNotFunctional(ItemStack item){
        return (item == null || !item.hasItemMeta() || item.getType().isAir());
    }
}
