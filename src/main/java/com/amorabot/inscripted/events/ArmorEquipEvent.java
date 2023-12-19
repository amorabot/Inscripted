package com.amorabot.inscripted.events;

import com.amorabot.inscripted.components.Items.Armor.Armor;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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

    public ArmorEquipEvent(Event event, ItemStack armorItem, ItemTypes armorPiece, ItemUsage armorUsage){
        if (event instanceof PlayerInteractEvent || event instanceof InventoryClickEvent){
            this.rootEvent = event;
            this.armorItem = armorItem;
            this.armorUsage = armorUsage;
        } else {
            this.rootEvent = null;
            this.armorItem = null;
            this.armorUsage = null;
        }
        if (!allowedItemTypes.contains(armorPiece)){
            this.armorSlot = null;
            Utils.log("Invalid armor piece: " + armorPiece);
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
        return FunctionalItemAccessInterface.
                deserializeArmor(Objects.
                        requireNonNull(getArmorItem().getItemMeta()).getPersistentDataContainer());
    }
    public boolean isValid(){
        //Nullity check
        return !(
                (rootEvent == null) || (armorUsage == null) || (armorItem == null) || (armorSlot == null)
                );
    }
}
