package com.amorabot.inscripted.components.Items.relic;

import com.amorabot.inscripted.components.Items.Armor.ArmorTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;

import java.util.List;

public record RelicArmorDAO(ItemTypes slot, ArmorTypes type, int baseHealth, GenericRelicData genericData) {
}
