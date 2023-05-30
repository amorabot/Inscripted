package com.amorabot.rpgelements.handlers;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.utils.DelayedTask;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitRunnable;

public class WeaponEquipHandler implements Listener {
    private final RPGElements plugin;
    public WeaponEquipHandler(RPGElements plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    //Consider using event priority!!
    @EventHandler
    public void onSlotChange(PlayerItemHeldEvent event){
        Inventory inventory = event.getPlayer().getInventory();
        ItemStack heldItem = inventory.getItem(event.getNewSlot());
        ItemStack previousItem = inventory.getItem(event.getPreviousSlot());
        Utils.log("held item changed!");

        //Let's check fist if we are changing FROM a weapon, so we can deduce what stats to change
        if (isEquipable(previousItem)){ //If the previous item was a equipable weapon, let's remove all it's stats from the player before checking anything

            applyStats(previousItem, event.getPlayer(), false); //Applying the previous held item stat's (subtracting)
        }

        if (isEquipable(heldItem)){

            applyStats(heldItem, event.getPlayer(), true); //Then the new item stat's are added in (if its equipable)
        }
    }
    @EventHandler
    public void onForcedSwap(InventoryClickEvent event){
        if (!(event.getWhoClicked() instanceof Player)){
            return;
        }
        if (event.getClickedInventory() == null){
            return;
        }
        InventoryAction attemptedAction = event.getAction();
        Player player = (Player) event.getWhoClicked();
        Inventory playerInventory = player.getInventory();

        if (event.getClickedInventory().equals(playerInventory)){ //TODO simplificar os if/elses E CHECAR SE O ITEM TA VINDO DE OUTROS INVENTÁRIOS
            ClickType click = event.getClick();

            if (click == ClickType.DOUBLE_CLICK && (isEquipable(event.getCurrentItem()) || isEquipable(player.getItemOnCursor()))){
                //Checking for double clicks into main hand
                if (!(event.getSlot() == player.getInventory().getHeldItemSlot())){
                    return;
                }
                event.setCancelled(true);
                return;
            }

            if (click.isShiftClick()){
//                ItemStack clickedItem = event.getCurrentItem();
                Material mainHandMaterial = player.getInventory().getItemInMainHand().getType();
                if (event.getSlot() == player.getInventory().getHeldItemSlot()){ //Se é um shift-Click no slot da main hand, cancele
//                    log("no shift-clicking main hand items...");
                    event.setCancelled(true);
                    return;
                }
                if (mainHandMaterial == Material.AIR){ //If hand was empty, check after 5 ticks if there's a item there after the shift-click
                    new DelayedTask(new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (isEquipable(player.getInventory().getItemInMainHand())){
                                applyStats(player.getInventory().getItemInMainHand(), player, true);
                            }
                        }
                    }, 5L);
                }
            }

            if (attemptedAction == InventoryAction.HOTBAR_SWAP){
                event.setCancelled(true);
                return;
            }
            if (attemptedAction == InventoryAction.SWAP_WITH_CURSOR){ //TODO: orb and currency usage
                if (!(event.getSlot() == player.getInventory().getHeldItemSlot())){ //se a tentativa não for no slot da mão, ignore
                    return;
                }

                //The swap is attempted at the main hand slot and its a equipable item. Let's apply the changes.
                // If the clicked item is equipable, sub its stats then add the new item stats. Else,  just add them.
                ItemStack clickedItem = player.getInventory().getItemInMainHand();
                ItemStack itemOnCursor = player.getItemOnCursor();

                //Check if item on cursor is even equipable:
                if (!isEquipable(itemOnCursor)){
                    //If not equipable, just sub the clicked item stats from player
//                    player.sendMessage("item on hand is equipable.. overriding with a normal item");
                    applyStats(clickedItem, player, false);
                    return;
                }

                if (isEquipable(clickedItem)){
//                    player.sendMessage("item on hand is equipable.. changing items normally");
                    applyStats(clickedItem, player, false);
                    applyStats(itemOnCursor, player, true);
                    return;
                } else {
//                    player.sendMessage("item on hand is not equipable. applying cursor item stats");
                    applyStats(itemOnCursor, player, true);
                    return;
                }
            }
            // The attemted interaction is not a forced hotbar swap nor a swap with cursor, lets check if its to grab item from main hand

            // se ITEM CLICAVEL É EQUIPAVEL e ESTÁ NA MAIN HAND e NÃO TEM NADA NO CURSOR
            if (isEquipable(event.getCurrentItem()) && event.getSlot() == player.getInventory().getHeldItemSlot() && player.getItemOnCursor().getType() == Material.AIR){
                player.sendMessage("item-> no item ok");
                applyStats(event.getCurrentItem(), player, false);
                return;
            }
            if (isEquipable(player.getItemOnCursor()) && event.getSlot() == player.getInventory().getHeldItemSlot() && event.getCurrentItem().getType() == Material.AIR){
                player.sendMessage("no item ->item ok");
                applyStats(player.getItemOnCursor(), player, true);
                return;
            }
        }
    }

    private boolean isEquipable(ItemStack heldItem){
        if (!(heldItem != null && heldItem.hasItemMeta())){ //If its not custom or is null
            return false;
        }
        ItemMeta heldItemMeta = heldItem.getItemMeta();
        PersistentDataContainer dataContainer = heldItemMeta.getPersistentDataContainer();



        return true;
    }
    private void applyStats(ItemStack heldItem, Player player, boolean adding){
        ItemMeta heldItemMeta = heldItem.getItemMeta();
        PersistentDataContainer dataContainer = heldItemMeta.getPersistentDataContainer();


    }
}
