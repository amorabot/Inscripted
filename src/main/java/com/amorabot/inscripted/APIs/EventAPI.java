package com.amorabot.inscripted.APIs;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.events.ArmorEquipEvent;
import com.amorabot.inscripted.events.ItemUsage;
import com.amorabot.inscripted.events.WeaponEquipEvent;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

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
        Utils.log("weapon equip API call #DEBUG");
    }
}
