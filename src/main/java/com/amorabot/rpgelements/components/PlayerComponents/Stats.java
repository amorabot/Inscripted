package com.amorabot.rpgelements.components.PlayerComponents;

import com.amorabot.rpgelements.Crafting.Weapons.Modifiers.WeaponModifiers;

import java.util.HashMap;
import java.util.Map;

public class Stats {
//    Map<ArmorModifier, int[]> armorStats = new HashMap<>();
    Map<WeaponModifiers, int[]> weaponSlot = new HashMap<>();

    public Map<WeaponModifiers, int[]> getWeaponSlotData(){
        return weaponSlot;
    }
}
