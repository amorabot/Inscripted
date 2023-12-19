package com.amorabot.inscripted.events;

import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class WeaponEquipEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;


    private final Event rootEvent;
    private final ItemUsage weaponUsage;
    private final ItemStack weaponItem;

    //WeaponEquipEvent calls assume valid weapons or null (signaling unequips)
    public WeaponEquipEvent(Event event, ItemStack weaponItem){
        if (event instanceof PlayerItemHeldEvent || event instanceof InventoryClickEvent){
            this.rootEvent = event;
            this.weaponItem = weaponItem;
            if (weaponItem != null){
                this.weaponUsage = ItemUsage.WEAPON_EQUIP;
            } else {
                this.weaponUsage = ItemUsage.WEAPON_UNEQUIP;
            }
        } else {
            this.rootEvent = null;
            this.weaponItem = null;
            this.weaponUsage = null;
        }
    }
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public Event getRootEvent() {
        return rootEvent;
    }

    public ItemUsage getWeaponUsage() {
        return weaponUsage;
    }

    public ItemStack getWeaponItem() {
        return weaponItem;
    }

    public Weapon getWeaponData(){
        return FunctionalItemAccessInterface.
                deserializeWeapon(
                        Objects.requireNonNull(getWeaponItem().getItemMeta()).getPersistentDataContainer());
    }

    public boolean isValid(){
        //Nullity check
        return !((rootEvent == null) || (weaponUsage == null));
    }
    public boolean isUnequip(){
        return getWeaponItem() == null;
    }
}
