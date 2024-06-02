package com.amorabot.inscripted.components.Items.Files;

import com.amorabot.inscripted.components.Items.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;

import java.util.List;

public record RelicWeaponDAO(String name, int ilvl, WeaponTypes type, WeaponAttackSpeeds atkSpeed, int[] baseDmg, List<InscriptionID> inscriptions) {
}
