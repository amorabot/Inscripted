package com.amorabot.inscripted.APIs;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.currency.Currencies;
import com.amorabot.inscripted.events.ArmorEquipEvent;
import com.amorabot.inscripted.events.CurrencyUsageEvent;
import com.amorabot.inscripted.events.ItemUsage;
import com.amorabot.inscripted.events.WeaponEquipEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class EventAPI {

    public static void callArmorEquipEvent(Event rootEvent, ItemStack armorItem, ItemTypes armorPiece, ItemUsage armorUsage){
        //If invalid parameters are passed in, the event will be invalid and can be checked via isValid()
        Bukkit.getServer().getPluginManager().callEvent(new ArmorEquipEvent(
                rootEvent,
                armorItem,
                armorPiece,
                armorUsage));
    }

    public static void callWeaponEquipEvent(Event event, ItemStack weaponItem){
        Bukkit.getServer().getPluginManager().callEvent(new WeaponEquipEvent(event, weaponItem));
    }


    public static void currencyUsage(Player player, ItemStack targetItem, ItemStack currency){
        Bukkit.getServer().getPluginManager().callEvent(new CurrencyUsageEvent(player, targetItem, currency));
    }
}
