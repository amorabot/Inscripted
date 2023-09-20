package com.amorabot.rpgelements.APIs;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.rpgelements.events.ArmorEquipEvent;
import com.amorabot.rpgelements.events.ItemUsage;
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
}
