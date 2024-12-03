package com.amorabot.inscripted.components.Items.relic;

import com.amorabot.inscripted.components.Items.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;

import java.util.List;

public record RelicWeaponDAO(WeaponTypes type, WeaponAttackSpeeds atkSpeed, int[] baseDmg, GenericRelicData genericData) {
}
