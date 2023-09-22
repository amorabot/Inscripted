package com.amorabot.rpgelements.handlers.Inventory;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.rpgelements.components.Items.Weapon.Weapon;
import com.amorabot.rpgelements.components.Player.Profile;
import com.amorabot.rpgelements.events.WeaponEquipEvent;
import com.amorabot.rpgelements.managers.JSONProfileManager;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;
import java.util.Objects;

import static com.amorabot.rpgelements.events.FunctionalItemAccessInterface.deserializeWeapon;

public class WeaponEquipListener implements Listener {

    public WeaponEquipListener(){
        RPGElements plugin = RPGElements.getPlugin();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onWeaponEquipAttempt(WeaponEquipEvent event){
        if (!event.isValid()){
            event.setCancelled(true);
            if (event.getRootEvent() != null){
                event.setCancelled(true);
            }
            return;
            //If the event is not valid for any reason, cancel it and, if the root event is valid cancel it too
        }

        //The 2 root causes should be handled:

        //Scrolling through hotbar/holding a new item
        if (event.getRootEvent() instanceof PlayerItemHeldEvent){
            PlayerItemHeldEvent rootEvent = (PlayerItemHeldEvent) event.getRootEvent();

            Player player = rootEvent.getPlayer();
            if (event.isUnequip()){
                Profile playerProfile = JSONProfileManager.getProfile(player.getUniqueId()); // Get profile from cache
                if (!playerProfile.hasWeaponEquipped()){
                    return;
                }
                playerProfile.updateMainHand(null);
                return;
            }

            //May be null!
            ItemStack heldItem = event.getWeaponItem();
            PersistentDataContainer weaponDataContainer =
                    Objects.requireNonNull(heldItem.getItemMeta()).getPersistentDataContainer();

            Weapon weapon = deserializeWeapon(weaponDataContainer);
            if (weapon != null){
                Profile playerProfile = JSONProfileManager.getProfile(player.getUniqueId());
                playerProfile.updateMainHand(weapon);
                renderWeaponEquipToPlayer(player,heldItem,weapon);
            } else {
                Utils.log("Null weapon (WeaponEquipEvent)");
                event.setCancelled(true);
            }
        }

        //Clicks and substitutions
        if (event.getRootEvent() instanceof InventoryClickEvent){
            InventoryClickEvent rootEvent = (InventoryClickEvent) event.getRootEvent();
            Player player = (Player) rootEvent.getWhoClicked();

            if (event.isUnequip()){
                Profile playerProfile = JSONProfileManager.getProfile(player.getUniqueId()); // Get profile from cache
                if (!playerProfile.hasWeaponEquipped()){
                    return;
                }
                playerProfile.updateMainHand(null);
                return;
            }

            //May be null!
            ItemStack eventWeaponItem = event.getWeaponItem();
            PersistentDataContainer weaponDataContainer =
                    Objects.requireNonNull(eventWeaponItem.getItemMeta()).getPersistentDataContainer();

            Weapon weapon = deserializeWeapon(weaponDataContainer);
            if (weapon != null){
                Profile playerProfile = JSONProfileManager.getProfile(player.getUniqueId());
                playerProfile.updateMainHand(weapon);
                renderWeaponEquipToPlayer(player,eventWeaponItem,weapon);
            } else {
                Utils.log("Null weapon (WeaponEquipEvent)");
                event.setCancelled(true);
            }
        }
    }

    public void renderWeaponEquipToPlayer(Player player, ItemStack weaponItem, Weapon weaponData){
        List<String> weaponLore = weaponItem.getItemMeta().getLore();
        ItemRarities weaponRarity = weaponData.getRarity();

        Utils.msgPlayer(player,"");
        if (weaponRarity == ItemRarities.COMMON){
            String equipString = "&f------->> "+ weaponData.getName();
            Utils.msgPlayer(player, equipString, "&f" + "-".repeat(Utils.decolor(equipString).length()-4));
        } else {
            String equipString;
            String closingString = "";
            if (weaponRarity == ItemRarities.MAGIC){
                equipString = "&9------->> "+ weaponData.getName();
                closingString = "&9" + "-".repeat(Utils.decolor(equipString).length()-4);
                Utils.msgPlayer(player, equipString);
            } else if (weaponRarity == ItemRarities.RARE){
                equipString = "&e------->> "+ weaponData.getName();
                closingString = "&e" + "-".repeat(Utils.decolor(equipString).length()-4);
                Utils.msgPlayer(player, equipString);
            }

            assert weaponLore != null;
            String div;
            int acc = 0;
            while (!weaponLore.get(acc).contains("---")){
                acc++;
            }
            div = weaponLore.get(acc);

            int modStart = weaponLore.indexOf(div);
            int modEnd = weaponLore.lastIndexOf(div);
            if (modStart != -1 && modEnd != -1){
                for (int i = modStart+1; i<modEnd; i++){
                    String statLine = weaponLore.get(i);
                    Utils.msgPlayer(player, "  "+statLine);
                }
            }
            Utils.msgPlayer(player, closingString);
        }
        Utils.msgPlayer(player,"");
    }
}
