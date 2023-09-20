package com.amorabot.rpgelements.events;

import com.amorabot.rpgelements.components.Items.Armor.Armor;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemTypes;
import com.google.common.collect.Lists;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ArmorEquipEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private final Event rootEvent;
    private final ItemUsage armorUsage;
    private final ItemStack armorItem;
    private final ItemTypes armorSlot;
    private static final Set<ItemTypes> allowedItemTypes = Set.of(
            ItemTypes.HELMET,
            ItemTypes.CHESTPLATE,
            ItemTypes.LEGGINGS,
            ItemTypes.BOOTS);

    public ArmorEquipEvent(PlayerInteractEvent event, ItemStack armorItem, ItemTypes armorPiece, ItemUsage armorUsage){
        this.rootEvent = event;
        this.armorUsage = armorUsage;
        this.armorItem = armorItem;


        if (!allowedItemTypes.contains(armorPiece)){
            this.armorSlot = null;
            return;
        }
        this.armorSlot = armorPiece;
    }
    public ArmorEquipEvent(InventoryClickEvent event, ItemStack armorItem, ItemTypes armorPiece,  ItemUsage armorUsage){
        this.rootEvent = event;
        this.armorUsage = armorUsage;
        this.armorItem = armorItem;

        if (!allowedItemTypes.contains(armorPiece)){
            this.armorSlot = null;
            return;
        }
        this.armorSlot = armorPiece;
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
    public ItemUsage getArmorUsage() {
        return armorUsage;
    }
    public ItemTypes getArmorSlot() {
        return armorSlot;
    }
    public ItemStack getArmorItem(){
        return armorItem;
    }
    public Armor getArmorData(){
        return FunctionalItemAccessInterface.deserializeArmor(Objects.requireNonNull(
                getArmorItem().getItemMeta()).getPersistentDataContainer());
    }
}
