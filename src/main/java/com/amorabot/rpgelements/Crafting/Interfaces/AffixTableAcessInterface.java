package com.amorabot.rpgelements.Crafting.Interfaces;

import com.amorabot.rpgelements.Crafting.Weapons.Modifiers.WeaponModifiers;

import java.util.List;
import java.util.Map;

public interface AffixTableAcessInterface {
    default Map<Integer, int[]> getModTable(WeaponModifiers mod){
        return null;
    }
    default List<WeaponModifiers> getAllModNames(){
        return null;
    }
}
