package com.amorabot.inscripted.APIs;

import com.amorabot.inscripted.APIs.damageAPI.EntityStateManager;
import com.amorabot.inscripted.events.CurrencyUsageEvent;
import com.amorabot.inscripted.events.death.InscriptedPlayerDeathEvent;
import com.amorabot.inscripted.events.WeaponEquipEvent;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public class EventAPI {

    public static void entityDeath(LivingEntity deadEntity){
        Utils.log("Calling death event");
        EntityStateManager.setDead(deadEntity,true);
        if (deadEntity instanceof Player p){
            Bukkit.getServer().getPluginManager().callEvent(new InscriptedPlayerDeathEvent(p));
        }
    }

    public static void callWeaponEquipEvent(Event event, ItemStack weaponItem){
        Bukkit.getServer().getPluginManager().callEvent(new WeaponEquipEvent(event, weaponItem));
    }


    public static void currencyUsage(Player player, ItemStack targetItem, ItemStack currency){
        Bukkit.getServer().getPluginManager().callEvent(new CurrencyUsageEvent(player, targetItem, currency));
    }
}
