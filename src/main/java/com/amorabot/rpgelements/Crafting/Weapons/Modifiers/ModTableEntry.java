package com.amorabot.rpgelements.Crafting.Weapons.Modifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.amorabot.rpgelements.utils.Utils.log;

public class ModTableEntry {

    private final WeaponModifiers mod;
    private final TierData[] tiers;

    public ModTableEntry(WeaponModifiers mod, TierData... ranges){
        this.mod = mod;

        if(ranges.length != mod.getNumberOfTiers()){
            log("error(modtable serialization): allowed and given number of tiers do not match");
        }

        tiers = ranges;
    }

    public WeaponModifiers getMod(){
        return mod;
    }
    public TierData[] getTiers(){
        return tiers;
    }
}
