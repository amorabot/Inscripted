package com.amorabot.inscripted.handlers.Inventory;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.item.currency.Currencies;
import com.amorabot.inscripted.item.structure.io.ItemDeserializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static com.amorabot.inscripted.handlers.Inventory.PlayerEquipmentHandler.isValidItem;
import static com.amorabot.inscripted.item.structure.io.ItemDeserializer.isNotFunctional;

public class InventoryHandler implements Listener {

    public InventoryHandler(){
        Bukkit.getPluginManager().registerEvents(this, Inscripted.getPlugin());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClick(InventoryClickEvent event) { //Prioritary click-handling
        if(event.isCancelled()){
            return;
        }
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
        PlayerInventory playerInventory = player.getInventory();

        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        if (attemptedAction == InventoryAction.SWAP_WITH_CURSOR){
            if (event.getSlot() == playerInventory.getHeldItemSlot()){
                if (isValidItem(cursorItem) && ItemDeserializer.isWeapon(cursorItem)){
                    //Let the less-prioritary PlayerEqyipmentHandler handle this event
                    return;
                }
                player.sendMessage("No currency usage on main hand...."); //Allow and attempt recompilation everytime? Might be too expensive
                event.setCancelled(true);
                return;
            }

            if (event.isRightClick()){
                if (Currencies.isCurrency(cursorItem)){
                    if (isNotFunctional(clickedItem)){
                        return;
                    }
//                    EventAPI.currencyUsage(player,clickedItem,cursorItem);
                    event.setCancelled(true);
                }
            }
        }
    }

//    private boolean isNotFunctional(ItemStack item){
//        return (item == null || !item.hasItemMeta() || item.getType().isAir());
//    }
}
