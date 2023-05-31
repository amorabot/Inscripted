package com.amorabot.rpgelements.Crafting.Interfaces;

import com.amorabot.rpgelements.Crafting.Weapons.Modifiers.TierData;
import com.amorabot.rpgelements.Crafting.Weapons.Modifiers.WeaponModifiers;

import java.util.List;

public interface TableDataAccess {
    default List<WeaponModifiers> getAllModNames(){
        return null;
    }
    default TierData[] getModTiers(WeaponModifiers mod){
      return null;
    }
    default List<Integer> getSortedTierIlvls(WeaponModifiers mod){
        return null;
    }
}
