package com.amorabot.inscripted.item.relic;

import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.item.structure.Weapon.WeaponTypes;

import java.io.Serializable;

public record RelicWeaponData(WeaponTypes type, WeaponAttackSpeeds atkSpeed, int[] baseDmg, GenericRelicData data) implements Serializable {
}
