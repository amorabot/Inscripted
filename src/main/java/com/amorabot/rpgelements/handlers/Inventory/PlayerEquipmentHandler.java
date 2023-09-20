package com.amorabot.rpgelements.handlers.Inventory;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.events.ArmorEquipEvent;
import com.amorabot.rpgelements.events.FunctionalItemAccessInterface;
import com.amorabot.rpgelements.components.Items.Weapon.Weapon;
import com.amorabot.rpgelements.components.Player.Profile;
import com.amorabot.rpgelements.events.ItemUsage;
import com.amorabot.rpgelements.managers.JSONProfileManager;
import com.amorabot.rpgelements.utils.DelayedTask;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.amorabot.rpgelements.events.FunctionalItemAccessInterface.*;

public class PlayerEquipmentHandler implements Listener {
//    private static final Set<InventoryAction> CHECKED_CLICKS = Set.of(
//            InventoryAction.SWAP_WITH_CURSOR,
//            InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_SOME, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ONE,
//            InventoryAction.PLACE_ALL, InventoryAction.PLACE_SOME, InventoryAction.PLACE_ONE,
//            InventoryAction.MOVE_TO_OTHER_INVENTORY,
//            InventoryAction.HOTBAR_MOVE_AND_READD,
//            InventoryAction.HOTBAR_SWAP);
//    player.hasPotionEffect()
//    player.getAttackCooldown()

    //TODO: hook equip/unequips to events
    //TODO: shift click detection and mapping via ItemUsage
    public PlayerEquipmentHandler(RPGElements plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onSlotChange(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
//        player.sendEquipmentChange();
//        player.sendHealthUpdate();
//        player.damage();
//        player.getAbsorptionAmount()
//        player.eject()
//        player.getAttackCooldown()
//        player.getBoundingBox();
//        player.getExhaustion();
//        player.getExpToLevel();
//        player.getLastDeathLocation();
//        player.getNearbyEntities();
//        player.hasCooldown()
//        player.rayTraceBlocks();
//        player.setAbsorptionAmount();
//        player.setCooldown();
//        player.getInventory().getArmorContents()

        PlayerInventory inventory = player.getInventory();
        //There is just the Inventory interface, wich PlayerInventory extends from.
//        inventory.getArmorContents();
//        inventory.firstEmpty();
//        inventory.first();
//        inventory.contains();
//        inventory.iterator();

        ItemStack heldItem = inventory.getItem(event.getNewSlot());
//        ItemStack previousItem = inventory.getItem(event.getPreviousSlot());
        if (isNotFunctional(heldItem)){
            unequipWeaponSlot(player);
            return;
        }
        PersistentDataContainer heldItemDataContainer = heldItem.getItemMeta().getPersistentDataContainer();
        if (isEquipableWeapon(heldItemDataContainer)){
            player.setCooldown(heldItem.getType(), 20*1);
            equipWeapon(heldItemDataContainer, player);
        } else {
            unequipWeaponSlot(player);
        }
        return;
//        InventoryType.SlotType

    }
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Optional<ItemStack> usedItemOptional = Optional.ofNullable(event.getItem());
        if (usedItemOptional.isEmpty()){
            //Empty hand set of actions
            Utils.log("empty hand");
            return;
        }
        ItemStack usedItem = usedItemOptional.get();
        ItemUsage itemUsage = mapPlayerInteractAction(usedItem.getItemMeta().getPersistentDataContainer(), event.getAction());
        switch (itemUsage){
            case NONE -> player.sendMessage("Non functional item usage");
            case ARMOR_RIGHT_CLICK_AIR -> {
                player.sendMessage("Equiping armor!!");
            }
            case ARMOR_LEFT_CLICK_AIR -> {
                player.sendMessage("Punching with armor");
            }
            case WEAPON_LEFT_CLICK_AIR -> {
                //This is triggered when dropping a equiped weapon from inv
                if (player.hasCooldown(usedItem.getType())){
                    player.sendMessage(Utils.color("&c&lYou cannot attack right now!"));
                } else {
                    player.sendMessage(Utils.color("&c&lAttack!"));
                    player.setCooldown(usedItem.getType(), (int)(20*0.5));
                }
            }
            case WEAPON_RIGHT_CLICK_AIR -> {
                player.sendMessage("Weapon special!");
            }
            case UNIDED_WEAPON -> {
                player.sendMessage(Utils.color("&l&cThis weapon is not identified!"));
            }
        }
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClick(InventoryClickEvent event){
        if (!(event.getWhoClicked() instanceof Player)){return;}
        if (event.getClickedInventory() == null){return;}
        if (event.getCurrentItem() == null){return;}

        InventoryAction attemptedAction = event.getAction();
        Player player = (Player) event.getWhoClicked();
        //InventoryHandler should handle all inventory interactions that are not happening exclusively on the players inventory alone
        //Lets assume it from now on
        if (event.getClickedInventory() != player.getInventory()){
            player.sendMessage("Ignoring external invs");//                    # DEBUG MESSAGE
            return;
        }

        ClickType clickType = event.getClick();
        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();
        //When, for instance, the player has a item in the cursor and they click a slot, it comes as air/null
        if (isNotFunctional(clickedItem) && isNotFunctional(cursorItem)){ //If they are both non functional, ignore the event
            player.sendMessage("Ignoring: non functional items (clicked item and cursor item");//                    # DEBUG MESSAGE
            return;
        }

        //From now on, clickedItem OR cursorItem may be null, test if needed
        switch (clickType){
            case DROP -> { //Q
                //Decide what to do to functional items (in this case, cursor items should be ignored)
                if (isNotFunctional(clickedItem)){
                    return; //Ignore drops for non functional items
                }
                if (isEquipableWeapon(Objects.requireNonNull(clickedItem.getItemMeta()).getPersistentDataContainer())
                        && event.getSlot() == player.getInventory().getHeldItemSlot()){
                    //Main hand dropping with equiped weapon
                    player.sendMessage("No main hand dropping");
                    event.setCancelled(true);
                }
            }
            case SWAP_OFFHAND -> { //F
                //F-key with open inventory is a different trigger!
                player.sendMessage(Utils.color("&9&lTeleportation scroll/rune!!"));
                event.setCancelled(true);
            }
            case LEFT -> {
                if (event.getSlot() != player.getInventory().getHeldItemSlot()){
                    player.sendMessage("standard click");
                    return;
                }
                //Left clicks on main hand

                //The clicked item doesnt matter, the item that is going to the main hand is not equipable anyway
                if (isNotFunctional(cursorItem)){
                    player.sendMessage("unequiping");
                    unequipWeaponSlot(player);
                    return;
                }
                //Now its time to decide what to do with the functional items (1 of them or both)
                if (isNotFunctional(clickedItem)){
                    //clickedItem is not functional, cursorItem is
                    PersistentDataContainer dataContainer = Objects.requireNonNull(cursorItem.getItemMeta()).getPersistentDataContainer();
                    if (isEquipableWeapon(dataContainer)){
                        player.sendMessage("equiping, none->weapon");
                        equipWeapon(dataContainer, player);
                        return;
                    }
                    //cursorItem is functional but its not a weapon, so ingnore
                    return;
                }
                if (isNotFunctional(cursorItem)){
                    //cursorItem is not functional, clickedItem is
                    //If the clicked item (main hand) is functional, lets check if its a weapon
                    PersistentDataContainer dataContainer = Objects.requireNonNull(clickedItem.getItemMeta()).getPersistentDataContainer();
                    if (isEquipableWeapon(dataContainer)){
                        player.sendMessage("unequiping 2");
                        unequipWeaponSlot(player);
                        return;
                    }
                    //cursorItem is not functional, and clickedItem is not a weapon, do any shenanigans here
                    //...
                }
                //Both are functional (not necessarily weapons, must be checked)
                if (attemptedAction == InventoryAction.SWAP_WITH_CURSOR){ //Main hand swapping
                    player.sendMessage("swap!");
                    PersistentDataContainer clickedDataContainer = Objects.requireNonNull(clickedItem.getItemMeta()).getPersistentDataContainer();
                    PersistentDataContainer cursorDataContainer = Objects.requireNonNull(cursorItem.getItemMeta()).getPersistentDataContainer();
                    if (isEquipableWeapon(clickedDataContainer) && !isEquipableWeapon(cursorDataContainer)){
                        unequipWeaponSlot(player);
                        return;
                    }
                    if (!isEquipableWeapon(clickedDataContainer) && isEquipableWeapon(cursorDataContainer)){
                        equipWeapon(cursorDataContainer, player);
                        return;
                    }
                    //Both are weapons
                    equipWeapon(cursorDataContainer, player);
                    return;
                }
            }
            case RIGHT -> {
                player.sendMessage("opening something");
                event.setCancelled(true);
            }
            case SHIFT_LEFT -> {
                player.sendMessage("shift-clicking!!!");
                //Decide what to do to functional items (in this case, cursor items should be ignored)
                if (isNotFunctional(clickedItem)){
                    return; //Ignore shift-clicks for non functional items
                }
                PersistentDataContainer clickedDataContainer = Objects.requireNonNull(clickedItem.getItemMeta()).getPersistentDataContainer();
                if (isEquipableArmor(clickedDataContainer)){
                    player.sendMessage("chamando armorequipevent");
                    Bukkit.getServer().getPluginManager().callEvent(new ArmorEquipEvent(
                            event,
                            clickedItem,
                            Objects.requireNonNull(deserializeArmor(clickedDataContainer)).getArmorPiece(),
                            ItemUsage.ARMOR_SHIFTING_TO_INV));
                } else {
                    event.setCancelled(true);
                }
            }
        }
//        if (event.getClickedInventory().equals(playerInventory)) {
//            ClickType click = event.getClick();
//            //-----------------------------------------------------------------------------------------------------------------------
//            if (click == ClickType.DOUBLE_CLICK && (isEquipableWeapon(event.getCurrentItem()) || isEquipableWeapon(player.getItemOnCursor()))) {
//                //Checking for double clicks into main hand
//                if (event.getSlot() != player.getInventory().getHeldItemSlot()) {
//                    return;
//                }
//                event.setCancelled(true);
//                return;
//            }
//            if (event.isShiftClick()){ //------------------------------ CHECKING FOR SHIFT-CLICKS ------------------------------ //TODO: consider adding weapon shift-swap mechanic?
//                ItemStack currentMainHandItem = player.getInventory().getItemInMainHand();
//                ItemStack shiftClickedItem = event.getCurrentItem();
//                //The clicked item is equipable and held
//                if (isEquipableWeapon(currentMainHandItem)){
//                    if (event.getSlot() == player.getInventory().getHeldItemSlot()) {
//                        event.setCancelled(true);
//                        return;
//                    }
//                }
//                //If the player is holding something or the clicked item is not equipable, ignore. It should not result in a equip anyway
//                if (!(currentMainHandItem.getType().isAir() && isEquipableWeapon(shiftClickedItem))){
//                    return;
//                }
//                if (currentMainHandItem.getType().isAir()){
//                    new DelayedTask(new BukkitRunnable() {
//                        @Override
//                        public void run() {
//                            ItemStack newlyCheckedMainHandItem = player.getInventory().getItemInMainHand();
//                            if (isEquipableWeapon(newlyCheckedMainHandItem)) {
//                                equipWeapon(newlyCheckedMainHandItem.getItemMeta().getPersistentDataContainer(), player);
//                            }
//                        }
//                    }, 5L);
//                }
//                return;
//            }
//            //-----------------------------------------------------------------------------------------------------------------------
//            if (attemptedAction == InventoryAction.SWAP_WITH_CURSOR){
//                if (event.getSlot() != player.getInventory().getHeldItemSlot()){ //se a tentativa não for no slot da mão, ignore
//                    return;
//                }
////                ItemStack clickedItem = event.getCurrentItem();
//                ItemStack itemOnCursor = player.getItemOnCursor();
//                if (isEquipableWeapon(itemOnCursor)){
//                    equipWeapon(itemOnCursor.getItemMeta().getPersistentDataContainer(), player);
//                    return;
//                }else {
//                    unequipWeaponSlot(player);
//                }
//            }
//            if ((event.getSlot() == player.getInventory().getHeldItemSlot())
//                    && isEquipableWeapon(player.getInventory().getItemInMainHand())
//                    && player.getItemOnCursor().getType().isAir()){
//                unequipWeaponSlot(player);
//                return;
//            }
//            if ((event.getSlot() == player.getInventory().getHeldItemSlot()) && isEquipableWeapon(player.getItemOnCursor())){
//                equipWeapon(player.getItemOnCursor().getItemMeta().getPersistentDataContainer(), player);
//                return;
//            }
//        } else {
//            if (event.isShiftClick()){ //------------------------------ CHECKING FOR SHIFT-CLICKS OUTSIDE INVENTORY ------------------------------
//                if (!player.getInventory().getItemInMainHand().getType().isAir()){
//                    return;
//                }
//                if (isEquipableWeapon(event.getCurrentItem())){
//                    new DelayedTask(new BukkitRunnable() {
//                        @Override
//                        public void run() {
//                            ItemStack mainHandItem = player.getInventory().getItemInMainHand();
//                            if (isEquipableWeapon(mainHandItem)) {
//                                equipWeapon(mainHandItem.getItemMeta().getPersistentDataContainer(), player);
//                            }
//                        }
//                    }, 5L);
//                    return;
//                }
//                return; //If the player is holding something and its not equipable, just return
//            }
//        }
        if (attemptedAction == InventoryAction.HOTBAR_SWAP){
            event.setCancelled(true); //No hotbar swapping
        }
    }
    private void equipWeapon(PersistentDataContainer weaponDataContainer, Player player){
        Weapon weapon = FunctionalItemAccessInterface.deserializeWeapon(weaponDataContainer);
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
    /* Checks wether the item has any custom data at all
    *  */
    private boolean isNotFunctional(ItemStack item){
        return (item == null || !item.hasItemMeta() || item.getType().isAir());
    }
    private ItemUsage mapPlayerInteractAction(PersistentDataContainer heldItemData, Action interactionType){
        if (heldItemData == null || heldItemData.isEmpty()){
            return ItemUsage.NONE;
        }
        boolean equipableArmor = isEquipableArmor(heldItemData);
        boolean equipableWeapon = isEquipableWeapon(heldItemData);

        switch (interactionType){
            case LEFT_CLICK_AIR -> {
                if (equipableArmor){
                    return ItemUsage.ARMOR_LEFT_CLICK_AIR;
                }

                if (equipableWeapon){
                    return ItemUsage.WEAPON_LEFT_CLICK_AIR;
                }

                if (isArmor(heldItemData)){ return ItemUsage.UNIDED_ARMOR; }//Both can be further specified
                if (isWeapon(heldItemData)){ return ItemUsage.UNIDED_WEAPON;}
                return ItemUsage.NONE;
            }
            case LEFT_CLICK_BLOCK -> {
                if (equipableArmor){
                    return ItemUsage.ARMOR_LEFT_CLICK_BLOCK;
                }

                if (equipableWeapon){
                    return ItemUsage.WEAPON_LEFT_CLICK_BLOCK;
                }
                return ItemUsage.NONE;
            }
            case RIGHT_CLICK_AIR -> {
                if (equipableArmor){
                    return ItemUsage.ARMOR_RIGHT_CLICK_AIR;
                }

                if (equipableWeapon){
                    return ItemUsage.WEAPON_RIGHT_CLICK_AIR;
                }
                return ItemUsage.NONE;
            }
            case RIGHT_CLICK_BLOCK -> {
                if (equipableArmor){
                    return ItemUsage.ARMOR_RIGHT_CLICK_BLOCK;
                }

                if (equipableWeapon){
                    return ItemUsage.WEAPON_RIGHT_CLICK_BLOCK;
                }
                return ItemUsage.NONE;
            }
        }
        return ItemUsage.NONE;
    }
    private ItemUsage mapPlayerInteractAction(PersistentDataContainer heldItemData, ClickType interactionType){
        return null;
    }
}
