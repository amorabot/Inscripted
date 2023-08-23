package com.amorabot.rpgelements.components.Items.Weapon;

import com.amorabot.rpgelements.utils.CraftingUtils;

import java.util.Arrays;
import java.util.List;

public enum WeaponNames {//TODO UNIFICAR COM WEAPONTYPES

    AXE("Training Hatchet", "War-axe", "Cleaver", "Battleaxe"),
    SWORD("Training Sword", "Elegant sword", "Foil", "Carved sword"),
    BOW("Basic bow"),
    DAGGER("Basic dagger"),
    WAND("Basic wand"),
    SCEPTRE("Basic sceptre");

    private final List<String> namesList;
    WeaponNames(String... names){
        namesList = Arrays.asList(names);
    }

    public String getRandomName(){
        int i = CraftingUtils.getRandomNumber(0, namesList.size()-1);
        return namesList.get(i);
    }
}
