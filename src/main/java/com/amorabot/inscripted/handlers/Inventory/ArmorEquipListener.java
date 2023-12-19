package com.amorabot.inscripted.handlers.Inventory;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Armor.Armor;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.events.ArmorEquipEvent;
import com.amorabot.inscripted.events.FunctionalItemAccessInterface;
import com.amorabot.inscripted.events.ItemUsage;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ArmorEquipListener implements Listener {

    public ArmorEquipListener(){
        Inscripted plugin = Inscripted.getPlugin();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onArmorEquipAttempt(ArmorEquipEvent event){
        //TODO: unequip all at death (death listener)
        if (!event.isValid()){
            event.setCancelled(true);
            if (event.getRootEvent() != null){
                event.setCancelled(true);
            }
            return;
            //If the event is not valid for any reason, cancel it and, if the root event is valid cancel it too
        }

        if (event.getRootEvent() instanceof PlayerInteractEvent){
            PlayerInteractEvent rootEvent = (PlayerInteractEvent) event.getRootEvent();
            //TODO: click interactions (right, left...)
            return;
        }

        if (event.getRootEvent() instanceof InventoryClickEvent){
            InventoryClickEvent rootEvent = (InventoryClickEvent) event.getRootEvent();
            //TODO: drop checks
            if (!(rootEvent.getWhoClicked() instanceof Player) || event.getArmorSlot() == null){
                event.setCancelled(true);
                Utils.log("Invalid parameter for ArmorEquipEvent");
                return;
            }
            Player player = (Player) rootEvent.getWhoClicked();
            PlayerInventory inventory = player.getInventory();

//            ItemStack[] playerArmorSet = inventory.getArmorContents();
            ItemStack armorToEquip = event.getArmorItem();

            ItemUsage usage = event.getArmorUsage();
            switch (usage){
                case ARMOR_UNEQUIP -> {
                    if (event.isValid()){
                        Profile playerProfile = JSONProfileManager.getProfile(player.getUniqueId());
                        if (playerProfile.getStats().setArmorPiece(null, event.getArmorSlot())){
                            player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 0.5f, 1.7f);
                            //Call for a stat recompilation
                            playerProfile.updateArmorSlot();
                            player.setHealth(playerProfile.getHealthComponent().getMappedHealth(20));
                            return;
                        }
                        //If the armor couldn't be set
                        rootEvent.setCancelled(true);
                    }
                }
                case ARMOR_SHIFTING_TO_INV -> {

                }
                case ARMOR_SHIFTING_FROM_INV -> clickEquipArmorPiece(event, rootEvent, player, inventory, armorToEquip);
//                case ARMOR_SWAP -> equipArmor(event, player, 0.9f,1.0f);
                case ARMOR_SWAP -> {
                    if (event.isValid()){
                        Profile playerProfile = JSONProfileManager.getProfile(player.getUniqueId());
                        if (playerProfile.getStats().setArmorPiece(event.getArmorData(), event.getArmorSlot())){
                            player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1.0f, 0.2f);
                            //Call for a stat recompilation
                            playerProfile.updateArmorSlot();
                            player.setHealth(playerProfile.getHealthComponent().getMappedHealth(20));
                            return;
                        }
                        //If the armor couldn't be set
                        rootEvent.setCancelled(true);
                    }
                }
            }
        }
    }

    private void clickEquipArmorPiece(ArmorEquipEvent event, InventoryClickEvent rootEvent, Player player, PlayerInventory inventory, ItemStack armorToEquip){
        //Since the armor slot is free, lets check beforehand if the player can equip it
        if (!FunctionalItemAccessInterface.isEquipableArmorByPlayer(
                event.getArmorData(),
                JSONProfileManager.getProfile(player.getUniqueId()))){
            //The slot is free but the armor is not equipable by this player
            //The root event should be canceled (shift-clicking armor) and we should move the
            //Item to the first available slot (simulating "already equipped helmet")
            rootEvent.setCancelled(true);
            int firstEmptySlot = inventory.firstEmpty();
            if (firstEmptySlot == -1){ //No empty slots
                return;
            }
            inventory.remove(armorToEquip); //"move" the item to the first emptySlot (addItem)
            inventory.addItem(armorToEquip);
            event.setCancelled(true);// Just move the item around as if it was shift clicked and do not equip the armor
            return;
        }
        //Slot is free and the item is equipable
        rootEvent.setCancelled(true);
        inventory.remove(armorToEquip); //Removing from inventory. Adding it again in the correct spot is decided later
//        inventory.setHelmet(armorToEquip);
        Profile playerProfile = JSONProfileManager.getProfile(player.getUniqueId());
        Armor[] playerArmorSet = playerProfile.getStats().getArmorSet();
        ItemTypes armorSlot = event.getArmorSlot();
        Utils.log("clickEquip call");
        switch (armorSlot){
            case HELMET -> {
                if (playerArmorSet[3] != null){ //If a helmet is being equipped and there is something in the slot,
                    inventory.addItem(armorToEquip);
                    return;
                }
                //If the helmet slot is clear, set it and update the profile afterward
                inventory.setHelmet(armorToEquip);
            }
            case CHESTPLATE -> {
                if (playerArmorSet[2] != null){
                    inventory.addItem(armorToEquip);
                    return;
                }
                inventory.setChestplate(armorToEquip);
            }
            case LEGGINGS -> {
                if (playerArmorSet[1] != null){
                    inventory.addItem(armorToEquip);
                    return;
                }
                inventory.setLeggings(armorToEquip);
            }
            case BOOTS -> {
                if (playerArmorSet[0] != null){
                    inventory.addItem(armorToEquip);
                    return;
                }
                inventory.setBoots(armorToEquip);
            }
        }
        Armor armorData = event.getArmorData();
        playerProfile.getStats().setArmorPiece(armorData, armorSlot);
        player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_NETHERITE, 0.5f, 1.3f);

        //Call for a stat recompilation
        playerProfile.updateArmorSlot();
        player.setHealth(playerProfile.getHealthComponent().getMappedHealth(20));
    }

    private boolean equipArmor(ArmorEquipEvent event, Player player, float volume, float pitch){
        return true;
    }
}