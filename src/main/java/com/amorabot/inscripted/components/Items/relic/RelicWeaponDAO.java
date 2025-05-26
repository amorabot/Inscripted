package com.amorabot.inscripted.components.Items.relic;

import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.item.structure.Weapon.WeaponTypes;

public record RelicWeaponDAO(WeaponTypes type, WeaponAttackSpeeds atkSpeed, int[] baseDmg, GenericRelicData genericData) {
}
