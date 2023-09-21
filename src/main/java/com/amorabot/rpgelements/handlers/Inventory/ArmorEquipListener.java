package com.amorabot.rpgelements.handlers.Inventory;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.Items.Armor.Armor;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.rpgelements.components.Player.Profile;
import com.amorabot.rpgelements.events.ArmorEquipEvent;
import com.amorabot.rpgelements.events.FunctionalItemAccessInterface;
import com.amorabot.rpgelements.events.ItemUsage;
import com.amorabot.rpgelements.managers.JSONProfileManager;
import com.amorabot.rpgelements.utils.Utils;
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
        RPGElements plugin = RPGElements.getPlugin();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onArmorEquipAttempt(ArmorEquipEvent event){
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

            return;
        }

        if (event.getRootEvent() instanceof InventoryClickEvent){
            InventoryClickEvent rootEvent = (InventoryClickEvent) event.getRootEvent();
            if (!(rootEvent.getWhoClicked() instanceof Player) || event.getArmorSlot() == null){
                event.setCancelled(true);
                Utils.log("Invalid parameter for ArmorEquipEvent");
                return;
            }
            Player player = (Player) rootEvent.getWhoClicked();
            PlayerInventory inventory = player.getInventory();

            ItemStack[] playerArmorSet = inventory.getArmorContents();
            ItemStack armorToEquip = event.getArmorItem();

            ItemUsage usage = event.getArmorUsage();
            switch (usage){
                case ARMOR_UNEQUIP -> {
                    if (event.isValid()){
                        Profile playerProfile = JSONProfileManager.getProfile(player.getUniqueId());
                        playerProfile.getStats().setArmorPiece(null, event.getArmorSlot());
                        player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 0.5f, 1.7f);

                        //Call for a stat recompilation
                        playerProfile.updateArmorSlot();
                        return;
                    }
                }
                case ARMOR_SHIFTING_TO_INV -> {

                }
                case ARMOR_SHIFTING_FROM_INV -> { //From inventory to slot
                    switch (event.getArmorSlot()){
                        case HELMET -> {
                            if (playerArmorSet[3] == null){
                                //Since the helmet slot is free, lets check beforehand if the player can equip it
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
                                inventory.remove(armorToEquip);
                                inventory.setHelmet(armorToEquip);
                                Profile playerProfile = JSONProfileManager.getProfile(player.getUniqueId());
                                Armor armorData = event.getArmorData();
                                ItemTypes armorSlot = event.getArmorSlot();
                                playerProfile.getStats().setArmorPiece(armorData, armorSlot);
                                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_NETHERITE, 0.5f, 1.3f);

                                //Call for a stat recompilation
                                playerProfile.updateArmorSlot();
                            }
                        }
                        case CHESTPLATE -> {

                        }
                        case LEGGINGS -> {

                        }
                        case BOOTS -> {

                        }
                    }
                }
            }

            return;
        }
    }
}
