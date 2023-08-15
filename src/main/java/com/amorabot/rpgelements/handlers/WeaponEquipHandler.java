package com.amorabot.rpgelements.handlers;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.utils.DelayedTask;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
        Player player = event.getPlayer();

        Inventory inventory = player.getInventory();
        ItemStack heldItem = inventory.getItem(event.getNewSlot());
        ItemStack previousItem = inventory.getItem(event.getPreviousSlot());

        //Let's check fist if we are changing FROM a weapon, so we can deduce what stats to change
        if (isEquipable(heldItem)){
            unequipWeaponSlot(player);
            equip(heldItem,player); // Equip new weapon
        } else { // If it's not equipable, do:
            if (isEquipable(previousItem)){
                unequipWeaponSlot(player);
            }
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
        if (event.getCurrentItem() == null){
            return;
        }

        InventoryAction attemptedAction = event.getAction();
        Player player = (Player) event.getWhoClicked();
        Inventory playerInventory = player.getInventory();

        if (event.getClickedInventory().equals(playerInventory)) {
            ClickType click = event.getClick();
            //-----------------------------------------------------------------------------------------------------------------------
            if (click == ClickType.DOUBLE_CLICK && (isEquipable(event.getCurrentItem()) || isEquipable(player.getItemOnCursor()))) {
                //Checking for double clicks into main hand
                if (!(event.getSlot() == player.getInventory().getHeldItemSlot())) {
                    return;
                }
                event.setCancelled(true);
                return;
            }
            if (event.isShiftClick()){ //------------------------------ CHECKING FOR SHIFT-CLICKS ------------------------------
                //IGNORING WHEN HOLDING GENERIC ITEMS
                if (!player.getInventory().getItemInMainHand().getType().isAir() && !isEquipable(player.getInventory().getItemInMainHand())){
                    return;
                }
                if (isEquipable(event.getCurrentItem())){
                    if (event.getSlot() == player.getInventory().getHeldItemSlot()) { //Cancelling shift-clicks at main hand
                        event.setCancelled(true);
                        return;
                    }
                    if (player.getInventory().getItemInMainHand().getType().isAir()){
                        new DelayedTask(new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (isEquipable(player.getInventory().getItemInMainHand())) {
                                    equip(player.getInventory().getItemInMainHand(), player);
//                                    Utils.log("shift equip");
                                }
                            }
                        }, 5L);
                    }
                } else {
                    return;
                }
            }
            //-----------------------------------------------------------------------------------------------------------------------
            if (attemptedAction == InventoryAction.SWAP_WITH_CURSOR){ //TODO: orb and currency usage
                if (event.getSlot() != player.getInventory().getHeldItemSlot()){ //se a tentativa não for no slot da mão, ignore
                    return;
                }
                Utils.log("testando swap");
                //The swap is attempted at the main hand slot and its a equipable item. Let's apply the changes.
                ItemStack clickedItem = event.getCurrentItem();
                ItemStack itemOnCursor = player.getItemOnCursor();
                if (clickedItem.getType().isAir()){ //If the player isnt holding anything and the cursor is equipable, equip;
                    if (!isEquipable(itemOnCursor)){
                        return;
                    }
                    equip(itemOnCursor, player);
                    return;
                }
                if (isEquipable(clickedItem) && isEquipable(itemOnCursor)){
                    unequipWeaponSlot(player);
                    equip(itemOnCursor, player);
                    return;
                }
                if (!isEquipable(clickedItem) && isEquipable(itemOnCursor)){
                    equip(itemOnCursor, player);
                    return;
                }
                if (isEquipable(clickedItem) && !isEquipable(itemOnCursor)){
                    unequipWeaponSlot(player);
                    return;
                }
            }
            if ((event.getSlot() == player.getInventory().getHeldItemSlot()) && isEquipable(player.getInventory().getItemInMainHand())){
                unequipWeaponSlot(player);
                return;
            }
            if ((event.getSlot() == player.getInventory().getHeldItemSlot()) && isEquipable(player.getItemOnCursor())){
                unequipWeaponSlot(player);
                equip(player.getItemOnCursor(), player);
                return;
            }
        } else {
            if (event.isShiftClick()){ //------------------------------ CHECKING FOR SHIFT-CLICKS ------------------------------
                if (!player.getInventory().getItemInMainHand().getType().isAir()){
                    return;
                }
                if (isEquipable(event.getCurrentItem())){
                    new DelayedTask(new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (isEquipable(player.getInventory().getItemInMainHand())) {
                                equip(player.getInventory().getItemInMainHand(), player);
                            }
                        }
                    }, 5L);
                }

            }
        }
        if (attemptedAction == InventoryAction.HOTBAR_SWAP){
            event.setCancelled(true);
        }
    }

    private boolean isEquipable(ItemStack heldItem){
//        if (heldItem == null || !heldItem.hasItemMeta()){ //If its not custom or is null
//            return false;
//        }
//        ItemMeta heldItemMeta = heldItem.getItemMeta();
//        PersistentDataContainer dataContainer = heldItemMeta.getPersistentDataContainer();
//
//        if (dataContainer.has(new NamespacedKey(plugin, "data"), new RPGElementsContainerDataType<>(BaseItem.class))){
//            BaseItem itemData = dataContainer.get(new NamespacedKey(plugin, "data"), new RPGElementsContainerDataType<>(BaseItem.class));
//            return itemData.getItemType() == ItemTypes.WEAPON;
//        } else {
//            return false;
//        }
        return false;
    }
    private void equip(ItemStack heldItem, Player player){
//        ItemMeta heldItemMeta = heldItem.getItemMeta();
//        PersistentDataContainer dataContainer = heldItemMeta.getPersistentDataContainer();
//
//        Weapooon weaponStats = dataContainer.get(new NamespacedKey(plugin, "stats"), new RPGElementsContainerDataType<>(Weapooon.class));
//        if (weaponStats == null){
//            Utils.error("de-serialization error: weapon equip method");
//            return;
//        }
//        Profile playerProfile = JSONProfileManager.getProfile(player.getUniqueId().toString());
//        DamageComponent damageComponent = playerProfile.getDamage();
//        damageComponent.setDamage(weaponStats);
//        Utils.msgPlayer(player, "Equipped: " + weaponStats.getName() + " ("+damageComponent.getDPS()+")");
    }
    private void unequipWeaponSlot(Player player){
//        Profile playerProfile = JSONProfileManager.getProfile(player.getUniqueId().toString()); // Get profile
//        playerProfile.getStats().getWeaponSlotData().clear();
//        playerProfile.getDamage().setDamage(null);
    }
}
