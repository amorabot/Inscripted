package com.amorabot.rpgelements.components.Items.DataStructures.Enums;

import com.amorabot.rpgelements.utils.CraftingUtils;

import java.util.Arrays;
import java.util.List;

public enum WeaponNames {

    AXE("Training Hatchet", "War-axe", "Cleaver", "Battleaxe"),
    SHORTSWORD("Training Sword", "Elegant sword", "Foil", "Carved sword"),
    BOW("Basic bow"),
    SCYTHE("Basic Scythe"),
    WAND("Basic wand");

    private final List<String> namesList;
    WeaponNames(String... names){
        namesList = Arrays.asList(names);
    }

    public String getRandomName(){
        int i = CraftingUtils.getRandomNumber(0, namesList.size()-1);
        return namesList.get(i);
    }
}
