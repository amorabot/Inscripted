package com.amorabot.inscripted.item.relic;

import com.amorabot.inscripted.item.structure.Armor.ArmorTypes;
import com.amorabot.inscripted.item.structure.EquipmentSlots;

import java.io.Serializable;

public record RelicArmorData(EquipmentSlots armorSlot, ArmorTypes type, int baseHealth, GenericRelicData data) implements Serializable {
}
