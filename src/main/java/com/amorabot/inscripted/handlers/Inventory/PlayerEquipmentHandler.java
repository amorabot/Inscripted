package com.amorabot.inscripted.handlers.Inventory;

import com.amorabot.inscripted.APIs.EventAPI;
import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Armor.Armor;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Items.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.events.FunctionalItemAccessInterface;
import com.amorabot.inscripted.events.ItemUsage;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.skills.AbilityTypes;
import com.amorabot.inscripted.skills.AbilityRoutines;
import com.amorabot.inscripted.utils.DelayedTask;
import com.amorabot.inscripted.utils.Utils;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.amorabot.inscripted.events.FunctionalItemAccessInterface.*;

//TODO: fragment this class in multiple event handlers
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
    public PlayerEquipmentHandler(Inscripted plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onArmorChange(PlayerArmorChangeEvent event){
        //HEAD, CHEST, LEGS, FEET -> HELMET, CHESTPLATE, LEGGINGS, BOOTS

        Player player = event.getPlayer();

        UUID playerUUID = player.getUniqueId();
        Profile profile = JSONProfileManager.getProfile(playerUUID);
        ItemStack newItem = event.getNewItem();
        ItemStack oldItem = event.getOldItem();
        ItemTypes changedSlot = mapArmorSlot(event.getSlotType());
        if (isNotFunctional(newItem)){ // Unequip
            SoundAPI.playArmorUnequipFor(player);
            profile.updateEquipmentSlot(changedSlot, null, playerUUID);
            return;
        }
        // New item is a armor
        Armor armorData = getArmorData(newItem); //No problem if null (Doesnt have a valid PDC for Armor)
        profile.updateEquipmentSlot(changedSlot, armorData, playerUUID);
        if (armorData == null){
            player.sendMessage(Utils.color("&cInvalid armor, no stats will be gained."));
        } else {
            SoundAPI.playArmorEquipFor(player);
            player.sendMessage("Equipped: " + armorData.getName());
        }
    }
    private Armor getArmorData(ItemStack armorItem){
        return FunctionalItemAccessInterface.
                deserializeArmorData(armorItem.getItemMeta().getPersistentDataContainer());
    }
    private ItemTypes mapArmorSlot(PlayerArmorChangeEvent.SlotType eventSlot){
        switch (eventSlot){
            case HEAD -> {
                return ItemTypes.HELMET;
            }
            case CHEST -> {
                return ItemTypes.CHESTPLATE;
            }
            case LEGS -> {
                return ItemTypes.LEGGINGS;
            }
            case FEET -> {
                return ItemTypes.BOOTS;
            }
        }
        return null;
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
//            unequipWeaponSlot(player);
            EventAPI.callWeaponEquipEvent(event, null);
            return;
        }
        PersistentDataContainer heldItemDataContainer = heldItem.getItemMeta().getPersistentDataContainer();
        if (isIdentified(WEAPON_TAG,heldItemDataContainer)){
            player.setCooldown(heldItem.getType(), 20*1);
            EventAPI.callWeaponEquipEvent(event, heldItem);
        } else {
            EventAPI.callWeaponEquipEvent(event, null);
        }
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
        PersistentDataContainer dataContainer = usedItem.getItemMeta().getPersistentDataContainer();
        ItemUsage itemUsage = mapPlayerInteractAction(dataContainer, event.getAction());
        switch (itemUsage){
            case NONE -> player.sendMessage("Non functional item usage");
            case ARMOR_RIGHT_CLICK_AIR -> player.sendMessage("Equiping armor!!");
            case ARMOR_LEFT_CLICK_AIR -> player.sendMessage("Punching with armor");
            case WEAPON_LEFT_CLICK_AIR, WEAPON_LEFT_CLICK_BLOCK -> {
                Weapon weaponData = FunctionalItemAccessInterface.deserializeWeaponData(dataContainer);
                if (weaponData == null){
                    player.sendMessage("Invalid weapon attack...");
                    return;
                }
                basicAttackBy(player, usedItem, weaponData.getSubtype());
            }
            case WEAPON_RIGHT_CLICK_AIR, WEAPON_RIGHT_CLICK_BLOCK -> {
//                player.sendMessage("Mobility skill!");
                Weapon weaponData = FunctionalItemAccessInterface.deserializeWeaponData(dataContainer);
                if (weaponData == null){
                    player.sendMessage("Invalid weapon attack...");
                    return;
                }
                AbilityRoutines.playerBaseAbilityCast(player, AbilityTypes.MOVEMENT, weaponData.getSubtype());
            }
            case UNIDED_WEAPON -> player.sendMessage(Utils.color("&l&cThis weapon is not identified!"));
        }
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent event){
        if (event.isCancelled()){return;}

        if (!(event.getWhoClicked() instanceof Player)){return;}
        if (event.getClickedInventory() == null){return;}
        if (event.getCurrentItem() == null){return;}

        InventoryAction attemptedAction = event.getAction();
        Player player = (Player) event.getWhoClicked();
        PlayerInventory inventory = player.getInventory();
        //InventoryHandler should handle all inventory interactions that are not happening exclusively on the players inventory alone
        //Lets assume it from now on


//        if (event.getClickedInventory() != player.getInventory()){
//            player.sendMessage("Ignoring external invs");//                    # DEBUG MESSAGE
//            return;
//        }

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
                player.sendMessage("Dropping: " + event.getCurrentItem().getType());
                //Decide what to do to functional items (in this case, cursor items should be ignored)
                if (isNotFunctional(clickedItem)){
                    return; //Ignore drops for non functional items
                }
                PersistentDataContainer clickedItemDataContainer = Objects.requireNonNull(clickedItem.getItemMeta()).getPersistentDataContainer();
                //The clicked item is functional, lets check if it was a equiped armor
                if (isArmorSlotClick(event)){ //It was a armor slot drop attempt
                    if (isEquipableArmor(clickedItemDataContainer)){
                        player.sendMessage("No equipped armor dropping");
                        event.setCancelled(true);
                        return;
                    }
                }
                if (isIdentified(WEAPON_TAG,clickedItemDataContainer) && event.getSlot() == player.getInventory().getHeldItemSlot()){
                    //Main hand dropping with equiped weapon
                    //Todo: change this based on player preferences
                    player.sendMessage("No main hand dropping");
                    event.setCancelled(true);
                    return;
                }
            }
            case SWAP_OFFHAND -> { //F
                //F-key with open inventory is a different trigger!
                player.sendMessage(Utils.color("&9&lTeleportation scroll/rune!!"));
                event.setCancelled(true);
                return;
            }
            case LEFT -> {
                if (event.getSlot() != player.getInventory().getHeldItemSlot()){ //The click was not in the main hand
                    player.sendMessage("standard click");
                    int clickedSlot = event.getSlot();
                    //Since it was not in the main hand, lets check for armor slot clicks:
                    // REMOVED
                    //If its not a armor slot click, ignore for now
                    return;
                }


                //Left clicks on main hand

                //The clicked item doesnt matter, the item that is going to the main hand is not equipable anyway
                if (isNotFunctional(cursorItem)){
                    EventAPI.callWeaponEquipEvent(event, null);
                    return;
                }
                //Now its time to decide what to do with the functional items (1 of them or both)
                if (isNotFunctional(clickedItem)){
                    //clickedItem is not functional, cursorItem is
                    PersistentDataContainer dataContainer = Objects.requireNonNull(cursorItem.getItemMeta()).getPersistentDataContainer();
                    if (isIdentified(WEAPON_TAG,dataContainer)){
                        EventAPI.callWeaponEquipEvent(event, cursorItem);
                        return;
                    }
                    //cursorItem is functional but its not a weapon, so ingnore
                    return;
                }
                if (isNotFunctional(cursorItem)){
                    //cursorItem is not functional, clickedItem is
                    //If the clicked item (main hand) is functional, lets check if its a weapon
                    PersistentDataContainer dataContainer = Objects.requireNonNull(clickedItem.getItemMeta()).getPersistentDataContainer();
                    if (isIdentified(WEAPON_TAG,dataContainer)){
                        EventAPI.callWeaponEquipEvent(event, null);
                        return;
                    }
                    //cursorItem is not functional, and clickedItem is not a weapon, do any shenanigans here
                    //...
                }
                //Both are functional (not necessarily weapons, must be checked)
                if (attemptedAction == InventoryAction.SWAP_WITH_CURSOR){ //Main hand swapping
                    player.sendMessage("swap!");
                    //TODO: orb usage events

                    PersistentDataContainer clickedDataContainer = Objects.requireNonNull(clickedItem.getItemMeta()).getPersistentDataContainer();
                    PersistentDataContainer cursorDataContainer = Objects.requireNonNull(cursorItem.getItemMeta()).getPersistentDataContainer();
                    if (isIdentified(WEAPON_TAG,clickedDataContainer) && !isIdentified(WEAPON_TAG,cursorDataContainer)){
                        EventAPI.callWeaponEquipEvent(event, null);
                        return;
                    }
                    if (!isIdentified(WEAPON_TAG,clickedDataContainer) && isIdentified(WEAPON_TAG,cursorDataContainer)){
                        EventAPI.callWeaponEquipEvent(event, cursorItem);
                        return;
                    }
                    //Both are weapons
                    EventAPI.callWeaponEquipEvent(event, cursorItem);
                    return;
                }
            }
            case RIGHT -> {
                player.sendMessage("opening something");
                event.setCancelled(true);
            }
        }
        //After all this granular click events, lets check for more general clicks (swaps and shift-clicks)

        //------------------------------------------------ SHIFTING TESTS BEGIN ----------------------------------------------------
        if (event.isShiftClick()){
            //Decide what to do to functional items (in this case, cursor items should be ignored)
            if (isNotFunctional(clickedItem)){
                return; //Ignore shift-clicks for non functional items
            }
            //Checking clicks in armor slots
            //NOT NEEDED ANYMORE


            //Other shift-clicks
            PersistentDataContainer clickedDataContainer = Objects.requireNonNull(clickedItem.getItemMeta()).getPersistentDataContainer();

            //Its not armor-shifting, lets check for weapon-shifting
            //Lets first check for main-hand clicks to filter unequip attempts with shift
            if (event.getSlot() == inventory.getHeldItemSlot()){
                //It necessarily is a functional item, lets check if its a equipable weapon
                PersistentDataContainer mainHandDataContainer = Objects.requireNonNull(inventory.getItemInMainHand().getItemMeta()).getPersistentDataContainer();
                if (isIdentified(WEAPON_TAG,mainHandDataContainer)){
                    player.sendMessage(Utils.color("&cNo main hand shift-clicking"));
                    event.setCancelled(true);
                    return;
                }
                //If not on main hand, ignore
                return;
            }


            //From now on, the clicks are functional items not on the main hand
            //Lets check for a late-equip when shifting INTO main hand, not FROM like earlier
            if (isIdentified(WEAPON_TAG,clickedDataContainer)){
                if (inventory.getItemInMainHand().getType().isAir()){
                    new DelayedTask(new BukkitRunnable() {
                        @Override
                        public void run() {
                            ItemStack newlyCheckedMainHandItem = inventory.getItemInMainHand();
                            if (isNotFunctional(newlyCheckedMainHandItem)){
                                return;
                            }
                            PersistentDataContainer newMainHandDataContainer = Objects.requireNonNull(newlyCheckedMainHandItem.getItemMeta()).getPersistentDataContainer();
                            if (isIdentified(WEAPON_TAG,newMainHandDataContainer)) {
                                player.sendMessage("late-equip");
                                EventAPI.callWeaponEquipEvent(event, newlyCheckedMainHandItem);
                            }
                        }
                    }, 5L);
                }
            }
            //Its not a weapon, nor a armor piece, ignore
            return;
        }
        //------------------------------------------------ SHIFTING TESTS END ----------------------------------------------------

        //TODO: Consider a shift-equip mechanic for weapons?
        //Check for swaps within LEFT or RIGHT?
        if (attemptedAction == InventoryAction.HOTBAR_SWAP){
            event.setCancelled(true); //No hotbar swapping
        }
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
        boolean equipableWeapon = isIdentified(WEAPON_TAG,heldItemData);

        switch (interactionType){
            case LEFT_CLICK_AIR -> {
                if (equipableArmor){
                    return ItemUsage.ARMOR_LEFT_CLICK_AIR;
                }

                if (equipableWeapon){
                    return ItemUsage.WEAPON_LEFT_CLICK_AIR;
                }

                if (isArmor(heldItemData)){ return ItemUsage.UNIDED_ARMOR; }//Both can be further specified
                if (isItemType(WEAPON_TAG,heldItemData)){ return ItemUsage.UNIDED_WEAPON;}
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

    private boolean isArmorSlotClick(InventoryClickEvent event){
        InventoryType.SlotType clickedSlotType = event.getSlotType();
        return clickedSlotType.equals(InventoryType.SlotType.ARMOR);
    }


    private void leftClickArmorSwapping(InventoryClickEvent event, ItemStack testedArmor, ItemTypes slotType,
                                        ItemStack cursorItem, ItemStack clickedItem){
        //Armor slot was clicked: (armor-slot match done prior)
        if (testedArmor == null){
            EventAPI.callArmorEquipEvent(event, cursorItem, slotType, ItemUsage.ARMOR_SWAP);
            return;
        }
        //Functional item checks??????????

        PersistentDataContainer clickedDataContainer = Objects.requireNonNull(clickedItem.getItemMeta()).getPersistentDataContainer();
        PersistentDataContainer cursorDataContainer = Objects.requireNonNull(cursorItem.getItemMeta()).getPersistentDataContainer();

        if (!(isEquipableArmor(clickedDataContainer) && isEquipableArmor(cursorDataContainer))){
            //If they're not both equipable armor items, cancel the attemp to swap them
            event.setCancelled(true);
            return;
        }

        //The helmet slot was clicked and it contains a armor piece, lets check if the cursor item is compatible (helmet-to-helmet swap)
        ItemTypes cursorArmorCategory = Objects.requireNonNull(deserializeArmorData(cursorDataContainer)).getCategory();
        if (cursorArmorCategory.equals(slotType)){
//            Utils.log("armorswap!!!!");
            EventAPI.callArmorEquipEvent(event, cursorItem, slotType, ItemUsage.ARMOR_SWAP);
            return;
        }
        //The cursor item is not a helmet, cancel the equip attempt
        event.setCancelled(true);
    }

    public static void basicAttackBy(Player player, ItemStack usedItem, WeaponTypes weaponType){
        //This is triggered when dropping a equiped weapon from inv

        if (!player.hasCooldown(usedItem.getType())){
            WeaponAttackSpeeds atkSpeed = weaponType.getBaseAttackSpeed();

            //Apply the swing speed modifier
            PotionEffect swingSpeedModifier = atkSpeed.getSwingAnimationBuff();
            if (swingSpeedModifier!= null){
                swingSpeedModifier.apply(player);
            }

            //Cast attack
            AbilityRoutines.playerBaseAbilityCast(player, AbilityTypes.BASIC_ATTACK, weaponType);
            SoundAPI.playAttackSoundFor(player, player.getLocation(), weaponType);

            //Apply the item usage cooldown
            player.setCooldown(weaponType.getRange().getItem(), (int) (weaponType.getBaseAttackSpeed().getItemUsageCooldown()*20));
        }
    }
}
