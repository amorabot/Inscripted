package com.amorabot.inscripted.handlers.Inventory;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.item.structure.Armor.Armor;
import com.amorabot.inscripted.item.structure.EquipmentSlots;
import com.amorabot.inscripted.item.structure.Weapon.Weapon;
import com.amorabot.inscripted.events.ItemUsage;
import com.amorabot.inscripted.item.structure.io.InscriptedItem;
import com.amorabot.inscripted.item.structure.io.ItemDeserializer;
import com.amorabot.inscripted.managers.CasterStateManager;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;
import java.util.UUID;

import static com.amorabot.inscripted.APIs.SoundAPI.playAttackSoundFor;
import static com.amorabot.inscripted.item.structure.io.ItemDeserializer.isNotFunctional;

//TODO: fragment this class in multiple event handlers
public class PlayerEquipmentHandler implements Listener {
    private static final boolean DEBUG_MODE = false; //TODO: implement debug mode on all logs

    public PlayerEquipmentHandler(Inscripted plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    //TODO: InventoryEvent - Represents an inventory-related event
    //TODO: InventoryOpenEvent - Called when inventory is opened

    @EventHandler
    public void onInvEvent(PlayerSwapHandItemsEvent event){
        if (DEBUG_MODE) Utils.log("Toggling spellcast mode");
        CasterStateManager.alternateSpellcastingTriggerFor(event.getPlayer(), ItemUsage.NONE);
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
        Optional<ItemStack> interactedItem = Optional.ofNullable(event.getItem());
        if (interactedItem.isEmpty()){
            //Empty hand set of actions
            if (event.getAction().equals(Action.LEFT_CLICK_AIR)){
                if (DEBUG_MODE) Utils.log("Fisting whatever the fuck is in front of you");
                return;
            }
            return;
        }
        ItemStack usedItem = interactedItem.get();
        ItemUsage itemUsage = mapPlayerInteractAction(usedItem, event.getAction());
        switch (itemUsage){
            case NONE -> {
                if (DEBUG_MODE) player.sendMessage("Non functional item usage");
            }
            case ARMOR_RIGHT_CLICK_AIR -> {
                if (DEBUG_MODE) player.sendMessage("Armor equipped!");
            }
            case ARMOR_LEFT_CLICK_AIR -> {
                if (DEBUG_MODE) Utils.log("Punching w/ armor");
            }
            case WEAPON_LEFT_CLICK_AIR, WEAPON_LEFT_CLICK_BLOCK -> {
                Weapon weaponData = ItemDeserializer.deserializeWeaponData(usedItem);
                if (CasterStateManager.getCastingStateFor(player).isAlternateCasting()){
                    CasterStateManager.alternateSpellcastingTriggerFor(player,itemUsage); //Serves only as a notification/update to the CastingState
                    weaponCast(player,usedItem,CastType.SPECIAL_ATTACK,69);
                    return;
                }
                if (weaponData!=null){
                    if (!player.hasCooldown(usedItem.getType())){
                        playAttackSoundFor(player,player.getLocation(),weaponData.getWeaponType());
                    }
                }
                weaponCast(player,usedItem,CastType.BASIC_ATTACK,69);
            }
            case WEAPON_RIGHT_CLICK_AIR, WEAPON_RIGHT_CLICK_BLOCK -> {
                if (CasterStateManager.getCastingStateFor(player).isAlternateCasting()){
                    CasterStateManager.alternateSpellcastingTriggerFor(player,itemUsage);
                    weaponCast(player,usedItem,CastType.UTILITY,69);
                    return;
                }
                weaponCast(player,usedItem,CastType.MOVEMENT,69);
            }
            case UNIDED_WEAPON -> player.sendMessage(Utils.color("&l&cThis weapon is not identified!"));
        }
    }
    public static void weaponCast(Player player, ItemStack heldItem, CastType castType, int variant){
        // Assumes a valid weapon item
        Weapon weaponData = ItemDeserializer.deserializeWeaponData(heldItem);
        if (weaponData==null){return;}
        Skills basicAttack = getSkillVariant(weaponData,castType,variant);
        basicAttack.cast(player.getUniqueId(), CastSource.PLAYER,weaponData.getAtkSpeed());
    }
    public static Skills getSkillVariant(Weapon weaponData, CastType castType, int variant){
        Skills mappedSkill = Skills.mapSkillcast(weaponData.getWeaponType(), castType, variant);
        if (mappedSkill == null){
            if (DEBUG_MODE) Utils.error("Invalid basic attack..., Variant: " + variant);
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

        if (event.getClick().equals(ClickType.SWAP_OFFHAND)){
            if (DEBUG_MODE) player.sendMessage("Opening cool F-Key thingy");
            event.setCancelled(true);
            return;
        }

        ClickType clickType = event.getClick();
        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();
        boolean validClickedWeapon = isValidItem(clickedItem) & ItemDeserializer.isWeapon(clickedItem);
        boolean validCursorWeapon = isValidItem(cursorItem) & ItemDeserializer.isWeapon(cursorItem);

        //When, for instance, the player has a item in the cursor and they click a armorSlot, it comes as air/null
        if (isNotFunctional(clickedItem) && isNotFunctional(cursorItem)){ //If they are both non functional, ignore the event
            if (DEBUG_MODE) player.sendMessage("Ignoring: non functional items (clicked item and cursor item");//                    # DEBUG MESSAGE
            return;
        }

        boolean mainHandClick = event.getSlot() == player.getInventory().getHeldItemSlot();
        //From now on, clickedItem OR cursorItem may be null, test if needed
        switch (clickType){
            case DROP -> { //Q
                if (DEBUG_MODE) Utils.log("Drop");
                //Decide what to do to functional items (in this case, cursor items should be ignored)
                if (isNotFunctional(clickedItem)){
                    return; //Ignore drops for non functional items
                }
                //The clicked item is functional, lets check if it was a equiped armor
                if (isArmorSlotClick(event)){ //It was a armor armorSlot drop attempt
                    if (isValidItem(clickedItem)){
                        player.sendMessage("STOP TRYING TO DROP EQUIPPED ARMOR");
                        event.setCancelled(true);
                        return;
                    }
                }
                if (mainHandClick && validClickedWeapon){
                    player.sendMessage("Sorry, you cant drop your equipped weapon like that");
                    event.setCancelled(true);
                    return;
                }
            }
            case SWAP_OFFHAND -> { //F
                //F-key with open inventory is a different trigger!
                if (DEBUG_MODE) player.sendMessage(Utils.color("&9&lTeleportation scroll/rune!!"));
                event.setCancelled(true);
                return;
            }
            case LEFT -> {
                if (!mainHandClick){ //The click was not in the main hand
                    if (DEBUG_MODE) player.sendMessage("\uE000 click \uE000");
                    int clickedSlot = event.getSlot();
                    //Since it was not in the main hand, lets check for armor armorSlot clicks:
                    if (isArmorSlotClick(event)){
                        return;
                    }
                    //If its not a armor armorSlot click, ignore for now
                    return;
                }

                //Left clicks on main hand
                //The clicked item doesnt matter, the item that is going to the main hand is not equipable anyway
                if (isNotFunctional(cursorItem)){
                    weaponEquip(player,null);
                    return;
                }
                //Now its time to decide what to do with the functional items (1 of them or both)
                if (isNotFunctional(clickedItem)){
                    if (validCursorWeapon){
                        weaponEquip(player,cursorItem);
                    }
                    return;
                }
                //Both are functional (not necessarily weapons, must be checked)
                if (attemptedAction == InventoryAction.SWAP_WITH_CURSOR){ //Main hand swapping
                    if (validClickedWeapon & !validCursorWeapon){
                        weaponEquip(player,null);
                        return;
                    }
                    // Cursor item is valid, so it must me swapped
                    if (validCursorWeapon){
                        weaponEquip(player,cursorItem);
                        return;
                    }
                }
            }
            case RIGHT -> event.setCancelled(true);
        }
        //After all this granular click events, lets check for more general clicks (swaps and shift-clicks)

        //------------------------------------------------ SHIFTING TESTS BEGIN ----------------------------------------------------
        if (event.isShiftClick()){
            //Decide what to do to functional items (in this case, cursor items should be ignored)
            if (isNotFunctional(clickedItem)){
                return; //Ignore shift-clicks for non functional items
            }

            //Its not armor-shifting, lets check for weapon-shifting
            //Lets first check for main-hand clicks to filter unequip attempts with shift
            if (mainHandClick){
                //It necessarily is a functional item, lets check if its a equipable weapon
                ItemStack mainHandItem = inventory.getItemInMainHand();
                if ((isValidItem(mainHandItem) && ItemDeserializer.isWeapon(mainHandItem))){
                    //Check if shifting-out resulted in a unequip
                    delayedEquipOnMainHand(player);
                    return;
                }
                //If not on main hand, ignore
                return;
            }


            //From now on, the clicks are functional items not on the main hand
            //Lets check for a late-equip when shifting INTO main hand, not FROM like earlier
            if (validClickedWeapon){
                delayedEquipOnMainHand(player);
            }
            //Its not a weapon, nor a armor piece, ignore
            return;
        }
        //------------------------------------------------ SHIFTING TESTS END ----------------------------------------------------

        if (attemptedAction == InventoryAction.HOTBAR_SWAP){
            event.setCancelled(true); //No hotbar swapping
        }
    }
    private ItemUsage mapPlayerInteractAction(ItemStack heldItem, Action interactionType){
        if (heldItem == null || heldItem.getPersistentDataContainer().isEmpty()){
            return ItemUsage.NONE;
        }
        // Valid item check
        boolean validItem = isValidItem(heldItem);
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
    public static boolean isValidItem(ItemStack item){
        if (isNotFunctional(item)){return false;}
        return (InscriptedItem.hasInscriptedTag(item) && ItemDeserializer.isIdentified(item));
    }

    public static boolean isArmorSlotClick(InventoryClickEvent event){
        InventoryType.SlotType clickedSlotType = event.getSlotType();
        return clickedSlotType.equals(InventoryType.SlotType.ARMOR);
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
        }, 1L);
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
//            player.sendMessage(armorData.getSlot().name()); SLOT EQUIP DEBUG
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
