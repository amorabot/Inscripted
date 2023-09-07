package com.amorabot.rpgelements.handlers;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.FunctionalItems.FunctionalItemHandler;
import com.amorabot.rpgelements.components.Items.Weapon.Weapon;
import com.amorabot.rpgelements.components.PlayerComponents.Profile;
import com.amorabot.rpgelements.managers.JSONProfileManager;
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
        Player player = event.getPlayer();

        Inventory inventory = player.getInventory();
        ItemStack heldItem = inventory.getItem(event.getNewSlot());
//        ItemStack previousItem = inventory.getItem(event.getPreviousSlot());
        if (isNotFunctional(heldItem)){
            unequipWeaponSlot(player);
            return;
        }
        if (isEquipableWeapon(heldItem)){
            equipWeapon(heldItem,player); // Equip new weapon
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
            if (click == ClickType.DOUBLE_CLICK && (isEquipableWeapon(event.getCurrentItem()) || isEquipableWeapon(player.getItemOnCursor()))) {
                //Checking for double clicks into main hand
                if (event.getSlot() != player.getInventory().getHeldItemSlot()) {
                    return;
                }
                event.setCancelled(true);
                return;
            }
            if (event.isShiftClick()){ //------------------------------ CHECKING FOR SHIFT-CLICKS ------------------------------ //TODO: consider adding weapon shift-swap mechanic?
                ItemStack currentMainHandItem = player.getInventory().getItemInMainHand();
                ItemStack shiftClickedItem = event.getCurrentItem();
                //The clicked item is equipable and held
                if (isEquipableWeapon(currentMainHandItem)){
                    if (event.getSlot() == player.getInventory().getHeldItemSlot()) {
                        event.setCancelled(true);
                        return;
                    }
                }
                //If the player is holding something or the clicked item is not equipable, ignore. It should not result in a equip anyway
                if (!(currentMainHandItem.getType().isAir() && isEquipableWeapon(shiftClickedItem))){
                    return;
                }
                if (currentMainHandItem.getType().isAir()){
                    new DelayedTask(new BukkitRunnable() {
                        @Override
                        public void run() {
                            ItemStack newlyCheckedMainHandItem = player.getInventory().getItemInMainHand();
                            if (isEquipableWeapon(newlyCheckedMainHandItem)) {
                                equipWeapon(newlyCheckedMainHandItem, player);
                            }
                        }
                    }, 5L);
                }
                return;
            }
            //-----------------------------------------------------------------------------------------------------------------------
            if (attemptedAction == InventoryAction.SWAP_WITH_CURSOR){
                if (event.getSlot() != player.getInventory().getHeldItemSlot()){ //se a tentativa não for no slot da mão, ignore
                    return;
                }
//                ItemStack clickedItem = event.getCurrentItem();
                ItemStack itemOnCursor = player.getItemOnCursor();
                if (isEquipableWeapon(itemOnCursor)){
                    equipWeapon(itemOnCursor, player);
                    return;
                }else {
                    unequipWeaponSlot(player);
                }
            }
            if ((event.getSlot() == player.getInventory().getHeldItemSlot())
                    && isEquipableWeapon(player.getInventory().getItemInMainHand())
                    && player.getItemOnCursor().getType().isAir()){
                unequipWeaponSlot(player);
                return;
            }
            if ((event.getSlot() == player.getInventory().getHeldItemSlot()) && isEquipableWeapon(player.getItemOnCursor())){
                equipWeapon(player.getItemOnCursor(), player);
                return;
            }
        } else {
            if (event.isShiftClick()){ //------------------------------ CHECKING FOR SHIFT-CLICKS OUTSIDE INVENTORY ------------------------------
                if (!player.getInventory().getItemInMainHand().getType().isAir()){
                    return;
                }
                if (isEquipableWeapon(event.getCurrentItem())){
                    new DelayedTask(new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (isEquipableWeapon(player.getInventory().getItemInMainHand())) {
                                equipWeapon(player.getInventory().getItemInMainHand(), player);
                            }
                        }
                    }, 5L);
                    return;
                }
                return; //If the player is holding something and its not equipable, just return
            }
        }
        if (attemptedAction == InventoryAction.HOTBAR_SWAP){
            event.setCancelled(true); //No hotbar swapping
        }
    }

    private boolean isEquipableWeapon(ItemStack heldItem){
        if (isNotFunctional(heldItem)){return false;}
        ItemMeta heldItemMeta = heldItem.getItemMeta();
        PersistentDataContainer dataContainer = heldItemMeta.getPersistentDataContainer();
        return FunctionalItemHandler.isEquipableWeapon(dataContainer);
    }
    private void equipWeapon(ItemStack heldItem, Player player){
        ItemMeta heldItemMeta = heldItem.getItemMeta();
        PersistentDataContainer dataContainer = heldItemMeta.getPersistentDataContainer();

        Weapon weapon = FunctionalItemHandler.deserializeWeapon(dataContainer);
        Profile playerProfile = JSONProfileManager.getProfile(player.getUniqueId());
        playerProfile.updateMainHand(weapon);
        JSONProfileManager.setProfile(player.getUniqueId().toString(),playerProfile);
        Utils.msgPlayer(player, "Equipped: " + weapon.getName() + " ("+playerProfile.getDamageComponent().getDPS()+")");
    }
    private void unequipWeaponSlot(Player player){
        Profile playerProfile = JSONProfileManager.getProfile(player.getUniqueId()); // Get profile from cache
        if (!playerProfile.hasWeaponEquipped()){
            return;
        }
        playerProfile.updateMainHand(null);
        JSONProfileManager.setProfile(player.getUniqueId().toString(),playerProfile);
    }
    private boolean isNotFunctional(ItemStack item){
        return (item == null || !item.hasItemMeta() || item.getType().isAir());
    }
}
