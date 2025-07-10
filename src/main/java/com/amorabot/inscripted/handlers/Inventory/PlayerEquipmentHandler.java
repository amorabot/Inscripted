package com.amorabot.inscripted.handlers.Inventory;

import com.amorabot.inscripted.APIs.EventAPI;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.item.structure.Armor.Armor;
import com.amorabot.inscripted.item.structure.EquipmentSlots;
import com.amorabot.inscripted.item.structure.Weapon.Weapon;
import com.amorabot.inscripted.item.structure.Weapon.WeaponTypes;
import com.amorabot.inscripted.events.ItemUsage;
import com.amorabot.inscripted.item.structure.io.InscriptedItem;
import com.amorabot.inscripted.item.structure.io.ItemDeserializer;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.equipment.PlayerEquipment;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.skill.casting.CastSource;
import com.amorabot.inscripted.skill.casting.CastType;
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
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

//TODO: fragment this class in multiple event handlers
public class PlayerEquipmentHandler implements Listener {

    public PlayerEquipmentHandler(Inscripted plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    //TODO: InventoryEvent - Represents an inventory-related event
    //TODO: InventoryOpenEvent - Called when inventory is opened

    @EventHandler
    public void onInvEvent(PlayerSwapHandItemsEvent event){
        Utils.log("Toggling spellcast mode");
        event.setCancelled(true);
    }

    @EventHandler
    public void onArmorChange(PlayerArmorChangeEvent event){
        //HEAD, CHEST, LEGS, FEET -> HELMET, CHESTPLATE, LEGGINGS, BOOTS

        Player player = event.getPlayer();
        ItemStack newItem = event.getNewItem();
        ItemStack oldItem = event.getOldItem();

        armorEquip(player,newItem,mapArmorSlot(event.getSlotType()));
    }

    @EventHandler
    public void onSlotChange(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack heldItem = inventory.getItem(event.getNewSlot());
        ItemStack prevItem = inventory.getItem(event.getPreviousSlot());
        if (heldItem==null && prevItem==null){return;} //Nothing to nothing, no state change

        weaponEquip(player,heldItem);
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Optional<ItemStack> usedItemOptional = Optional.ofNullable(event.getItem());
        if (usedItemOptional.isEmpty()){
            //Empty hand set of actions
            Utils.log("Fisting whatever the fuck is in front of you");
            return;
        }
        ItemStack usedItem = usedItemOptional.get();
        ItemUsage itemUsage = mapPlayerInteractAction(usedItem, event.getAction());
        switch (itemUsage){
            case NONE -> player.sendMessage("Non functional item usage");
            case ARMOR_RIGHT_CLICK_AIR -> player.sendMessage("Equiping armor!!");
            case ARMOR_LEFT_CLICK_AIR -> player.sendMessage("Punching with armor");
            case WEAPON_LEFT_CLICK_AIR, WEAPON_LEFT_CLICK_BLOCK -> {
                weaponCast(player,usedItem,CastType.BASIC_ATTACK,69);
//                if (!player.hasCooldown(usedItem.getType())){
//                }
            }
            case WEAPON_RIGHT_CLICK_AIR -> weaponCast(player,usedItem,CastType.MOVEMENT,69);
            case WEAPON_RIGHT_CLICK_BLOCK -> {
                Utils.log("Nah, ignoring movement cast on blocks");
            }
            case UNIDED_WEAPON -> player.sendMessage(Utils.color("&l&cThis weapon is not identified!"));
        }
    }
    private void weaponCast(Player player, ItemStack heldItem, CastType castType, int variant){
        // Assumes a valid weapon item
        Weapon weaponData = ItemDeserializer.deserializeWeaponData(heldItem);
        Skills basicAttack = getSkillVariant(weaponData,castType,variant);
        basicAttack.cast(player.getUniqueId(), CastSource.PLAYER,weaponData.getAtkSpeed());
    }
    private Skills getSkillVariant(Weapon weaponData, CastType castType, int variant){
        Skills mappedSkill = Skills.mapSkillcast(weaponData.getWeaponType(), castType, variant);
        if (mappedSkill == null){
            Utils.error("Invalid basic attack..., Variant: " + variant);
            return Skills.FIST;
        }
        return mappedSkill;
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

//        if (event.)

        if (event.getClick().equals(ClickType.SWAP_OFFHAND)){
            player.sendMessage("Opening Orb menu!");
//            OrbGUI orbGUI = new OrbGUI();
//            player.openInventory(orbGUI.getInventory());
//            event.setCancelled(true);
            return;
        }

        //InventoryHandler should handle all inventory interactions that are not happening exclusively on the players inventory alone
        //Lets assume it from now on


//        if (event.getClickedInventory() != player.getInventory()){
//            player.sendMessage("Ignoring external invs");//                    # DEBUG MESSAGE
//            return;
//        }

        ClickType clickType = event.getClick();
        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        //When, for instance, the player has a item in the cursor and they click a armorSlot, it comes as air/null
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
                if (isArmorSlotClick(event)){ //It was a armor armorSlot drop attempt
//                    if (isEquipableArmor(clickedItemDataContainer)){
//                        player.sendMessage("No equipped armor dropping");
//                        event.setCancelled(true);
//                        return;
//                    }
                }
//                if (isIdentified(WEAPON_TAG,clickedItemDataContainer) && event.getSlot() == player.getInventory().getHeldItemSlot()){
//                    //Main hand dropping with equiped weapon
//                    //Todo: change this based on player preferences
//                    player.sendMessage("No main hand dropping");
//                    event.setCancelled(true);
//                    return;
//                }
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
                    //Since it was not in the main hand, lets check for armor armorSlot clicks:
                    // REMOVED
                    //If its not a armor armorSlot click, ignore for now
                    return;
                }


                //Left clicks on main hand

                //The clicked item doesnt matter, the item that is going to the main hand is not equipable anyway
                if (isNotFunctional(cursorItem)){
                    EventAPI.callWeaponEquipEvent(event, null);
                    return;
                }
                //Now its time to decide what to do with the functional items (1 of them or both)
//                if (isNotFunctional(clickedItem)){
//                    //clickedItem is not functional, cursorItem is
//                    PersistentDataContainer dataContainer = Objects.requireNonNull(cursorItem.getItemMeta()).getPersistentDataContainer();
//                    if (isIdentified(WEAPON_TAG,dataContainer)){
//                        EventAPI.callWeaponEquipEvent(event, cursorItem);
//                        return;
//                    }
//                    //cursorItem is functional but its not a weapon, so ingnore
//                    return;
//                }
//                if (isNotFunctional(cursorItem)){
//                    //cursorItem is not functional, clickedItem is
//                    //If the clicked item (main hand) is functional, lets check if its a weapon
//                    PersistentDataContainer dataContainer = Objects.requireNonNull(clickedItem.getItemMeta()).getPersistentDataContainer();
//                    if (isIdentified(WEAPON_TAG,dataContainer)){
//                        EventAPI.callWeaponEquipEvent(event, null);
//                        return;
//                    }
//                    //cursorItem is not functional, and clickedItem is not a weapon, do any shenanigans here
//                    //...
//                }
                //Both are functional (not necessarily weapons, must be checked)
                if (attemptedAction == InventoryAction.SWAP_WITH_CURSOR){ //Main hand swapping
//                    player.sendMessage("swap!");
//                    //TODO: orb usage events
//
//                    PersistentDataContainer clickedDataContainer = Objects.requireNonNull(clickedItem.getItemMeta()).getPersistentDataContainer();
//                    PersistentDataContainer cursorDataContainer = Objects.requireNonNull(cursorItem.getItemMeta()).getPersistentDataContainer();
//                    if (isIdentified(WEAPON_TAG,clickedDataContainer) && !isIdentified(WEAPON_TAG,cursorDataContainer)){
//                        EventAPI.callWeaponEquipEvent(event, null);
//                        return;
//                    }
//                    if (!isIdentified(WEAPON_TAG,clickedDataContainer) && isIdentified(WEAPON_TAG,cursorDataContainer)){
//                        EventAPI.callWeaponEquipEvent(event, cursorItem);
//                        return;
//                    }
//                    //Both are weapons
//                    EventAPI.callWeaponEquipEvent(event, cursorItem);
//                    return;
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
//                //It necessarily is a functional item, lets check if its a equipable weapon
//                PersistentDataContainer mainHandDataContainer = Objects.requireNonNull(inventory.getItemInMainHand().getItemMeta()).getPersistentDataContainer();
//                if (isIdentified(WEAPON_TAG,mainHandDataContainer)){
//                    player.sendMessage(Utils.color("&cNo main hand shift-clicking"));
//                    event.setCancelled(true);
//                    return;
//                }
//                //If not on main hand, ignore
//                return;
            }


//            //From now on, the clicks are functional items not on the main hand
//            //Lets check for a late-equip when shifting INTO main hand, not FROM like earlier
//            if (isIdentified(WEAPON_TAG,clickedDataContainer)){
//                if (inventory.getItemInMainHand().getType().isAir()){
//                    new DelayedTask(new BukkitRunnable() {
//                        @Override
//                        public void run() {
//                            ItemStack newlyCheckedMainHandItem = inventory.getItemInMainHand();
//                            if (isNotFunctional(newlyCheckedMainHandItem)){
//                                return;
//                            }
//                            PersistentDataContainer newMainHandDataContainer = Objects.requireNonNull(newlyCheckedMainHandItem.getItemMeta()).getPersistentDataContainer();
//                            if (isIdentified(WEAPON_TAG,newMainHandDataContainer)) {
//                                player.sendMessage("late-equip");
//                                EventAPI.callWeaponEquipEvent(event, newlyCheckedMainHandItem);
//                            }
//                        }
//                    }, 5L);
//                }
//            }
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
    private ItemUsage mapPlayerInteractAction(ItemStack heldItem, Action interactionType){
        if (heldItem == null || heldItem.getPersistentDataContainer().isEmpty()){
            return ItemUsage.NONE;
        }
        // Valid item check
        boolean validItem = (InscriptedItem.hasInscriptedTag(heldItem) && ItemDeserializer.isIdentified(heldItem));
        boolean validWeapon = (validItem && ItemDeserializer.isWeapon(heldItem));
        boolean validArmor = (validItem && ItemDeserializer.isArmor(heldItem));

        switch (interactionType){
            case LEFT_CLICK_AIR -> {
                if (validArmor){return ItemUsage.ARMOR_LEFT_CLICK_AIR;}
                if (validWeapon){return ItemUsage.WEAPON_LEFT_CLICK_AIR;}
                return ItemUsage.NONE;
            }
            case LEFT_CLICK_BLOCK -> {
                if (validArmor){return ItemUsage.ARMOR_LEFT_CLICK_BLOCK;}
                if (validWeapon){return ItemUsage.WEAPON_LEFT_CLICK_BLOCK;}
                return ItemUsage.NONE;
            }
            case RIGHT_CLICK_AIR -> {
                if (validArmor){return ItemUsage.ARMOR_RIGHT_CLICK_AIR;}
                if (validWeapon){return ItemUsage.WEAPON_RIGHT_CLICK_AIR;}
                return ItemUsage.NONE;
            }
            case RIGHT_CLICK_BLOCK -> {
                if (validArmor){return ItemUsage.ARMOR_RIGHT_CLICK_BLOCK;}
                if (validWeapon){return ItemUsage.WEAPON_RIGHT_CLICK_BLOCK;}
                return ItemUsage.NONE;
            }
        }
        return ItemUsage.NONE;
    }

    private boolean isArmorSlotClick(InventoryClickEvent event){
        InventoryType.SlotType clickedSlotType = event.getSlotType();
        return clickedSlotType.equals(InventoryType.SlotType.ARMOR);
    }

    public static void basicAttackBy(Player player, ItemStack usedItem, WeaponTypes weaponType){
        //This is triggered when dropping a equiped weapon from inv

        if (!player.hasCooldown(usedItem.getType())){
//            Weapon usedWeapon = FunctionalItemAccessInterface.deserializeWeaponData(Objects.requireNonNull(usedItem.getItemMeta()).getPersistentDataContainer());
//            assert usedWeapon != null;
//            WeaponAttackSpeeds atkSpeed = usedWeapon.getAtkSpeed();
//
//            //Apply the swing speed modifier
//            PotionEffect swingSpeedModifier = atkSpeed.getSwingAnimationBuff();
//            if (swingSpeedModifier!= null){
//                swingSpeedModifier.apply(player);
//            }
//
//            //Cast attack
//            AbilityRoutines.playerBaseAbilityCast(player, AbilityTypes.BASIC_ATTACK, weaponType,usedWeapon.getAtkSpeed());
//            SoundAPI.playAttackSoundFor(player, player.getLocation(), weaponType);
//
//            double APS = atkSpeed.getItemUsageCooldown();
//            int attackCD = (int) (APS*20);
//            //Apply the item usage cooldown
//            player.setCooldown(weaponType.getRange().getItem(), attackCD);
        }
    }

    public static void reEquipAllSlots(Player player){
        delayedEquipOnMainHand(player);
        EntityEquipment playerEquipments = player.getEquipment();
        //Equip each armorSlot individually, mapping the SlotType
        ItemStack helmetItem = playerEquipments.getHelmet();
        if (helmetItem!=null){ //No need for triggers when its null since the internal data is already cleared/invalid
            armorEquip(player,helmetItem,EquipmentSlots.HELMET);
        }
        ItemStack chestplateItem = playerEquipments.getChestplate();
        if (chestplateItem!=null){
            armorEquip(player,chestplateItem,EquipmentSlots.CHESTPLATE);
        }
        ItemStack leggingsItem = playerEquipments.getLeggings();
        if (leggingsItem!=null){
            armorEquip(player,leggingsItem,EquipmentSlots.LEGGINGS);
        }
        ItemStack bootsItem = playerEquipments.getBoots();
        if (bootsItem!=null){
            armorEquip(player,bootsItem,EquipmentSlots.BOOTS);
        }
    }
    private static void delayedEquipOnMainHand(Player player){
        new DelayedTask(new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack mainHandItem = player.getInventory().getItemInMainHand();
                weaponEquip(player,mainHandItem);
            }
        }, 2L);
    }
    public static void weaponEquip(Player player, ItemStack weaponItem){
        PlayerEquipment playerEquipment = PlayerDataContainer.getPlayerEquipment(player.getUniqueId());
        if (weaponItem == null || weaponItem.getType().isAir()){
            //Prematurely unequip weapon
            playerEquipment.updateEquimentSlot(EquipmentSlots.WEAPON, null);
            return;
        }
        // Valid item check
        boolean validWeapon = InscriptedItem.hasInscriptedTag(weaponItem) && ItemDeserializer.isWeapon(weaponItem);
        if (validWeapon && ItemDeserializer.isIdentified(weaponItem)){
            Weapon weaponData = ItemDeserializer.deserializeWeaponData(weaponItem);
            playerEquipment.updateEquimentSlot(EquipmentSlots.WEAPON, weaponData);
            return;
        }
        //Unequip weapon
        playerEquipment.updateEquimentSlot(EquipmentSlots.WEAPON, null);
    }

    private static void armorEquip(Player player, ItemStack armorItem, EquipmentSlots armorSlot){
        UUID playerID = player.getUniqueId();
        PlayerEquipment playerEquipment = PlayerDataContainer.getPlayerEquipment(playerID);
        // If the new item on that armorSlot is air, prematurely unequip that armorSlot
        if (armorItem.getType().isAir()){
            playerEquipment.updateEquimentSlot(armorSlot, null);
            return;
        }
        // Valid item check
        boolean validArmor = InscriptedItem.hasInscriptedTag(armorItem) && ItemDeserializer.isArmor(armorItem);
        if (validArmor && ItemDeserializer.isIdentified(armorItem)){
            Armor armorData = ItemDeserializer.deserializeArmorData(armorItem);
            player.sendMessage(armorData.getSlot().name());
            playerEquipment.updateEquimentSlot(armorData.getSlot(), armorData);
            return;
        }
        //Invalid armor && not Air -> Unequip that armorSlot
        playerEquipment.updateEquimentSlot(armorSlot, null);
    }
    private static EquipmentSlots mapArmorSlot(PlayerArmorChangeEvent.SlotType eventSlot){
        switch (eventSlot){
            case HEAD -> {
                return EquipmentSlots.HELMET;
            }
            case CHEST -> {
                return EquipmentSlots.CHESTPLATE;
            }
            case LEGS -> {
                return EquipmentSlots.LEGGINGS;
            }
            case FEET -> {
                return EquipmentSlots.BOOTS;
            }
        }
        return null;
    }
}
