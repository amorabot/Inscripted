package com.amorabot.inscripted.APIs;

import com.amorabot.inscripted.events.CurrencyUsageEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EventAPI {
    public static void currencyUsage(Player player, ItemStack targetItem, ItemStack currency){
        Bukkit.getServer().getPluginManager().callEvent(new CurrencyUsageEvent(player, targetItem, currency));
    }
}
