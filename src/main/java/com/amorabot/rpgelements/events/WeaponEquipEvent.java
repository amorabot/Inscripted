package com.amorabot.rpgelements.events;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class WeaponEquipEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;


    private final Event rootEvent;
    private final ItemUsage weaponUsage;
    private final ItemStack weaponItem;

    public WeaponEquipEvent(Event event, ItemStack weaponItem, ItemUsage weaponUsage){
        if (event instanceof PlayerInteractEvent || event instanceof InventoryClickEvent){
            this.rootEvent = event;
            this.weaponItem = weaponItem;
            this.weaponUsage = weaponUsage;
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
}
