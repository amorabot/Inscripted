package com.amorabot.inscripted.components.Items.Files;

import com.amorabot.inscripted.components.Items.Armor.ArmorTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;

import java.util.List;

public record RelicArmorDAO(String name, int ilvl, ItemTypes slot, ArmorTypes type, int baseHealth, List<InscriptionID> inscriptions) {
}
