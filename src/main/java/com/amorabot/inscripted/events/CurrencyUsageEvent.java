package com.amorabot.inscripted.events;

import com.amorabot.inscripted.components.Items.currency.Currencies;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class CurrencyUsageEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;


    private final Player player;
    private final ItemStack targetItem;
    private final ItemStack currencyItem;
    private final Currencies mappedCurrency;

    public CurrencyUsageEvent(Player player, ItemStack targetItem, ItemStack currency){
        this.player = player;
        this.targetItem = targetItem;
        this.currencyItem = currency;
        if (Currencies.isCurrency(currency)){
            ItemMeta currencyMeta = currency.getItemMeta();
            String rawCurrName =currencyMeta.getPersistentDataContainer().get(Currencies.PDC_ID, PersistentDataType.STRING);
            this.mappedCurrency = Currencies.valueOf(rawCurrName);
        } else {
            this.mappedCurrency = null;
            setCancelled(true);
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

    public Currencies getMappedCurrency() {
        return mappedCurrency;
    }

    public ItemStack getCurrencyItem() {
        return currencyItem;
    }

    public ItemStack getTargetItem() {
        return targetItem;
    }

    public Player getPlayer() {
        return player;
    }
}
