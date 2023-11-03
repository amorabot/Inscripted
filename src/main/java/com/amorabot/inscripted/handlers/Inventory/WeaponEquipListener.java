package com.amorabot.inscripted.handlers.Inventory;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.events.WeaponEquipEvent;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Objects;

import static com.amorabot.inscripted.events.FunctionalItemAccessInterface.deserializeWeapon;

public class WeaponEquipListener implements Listener {

    public WeaponEquipListener(){
        Inscripted plugin = Inscripted.getPlugin();
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
    }
}
