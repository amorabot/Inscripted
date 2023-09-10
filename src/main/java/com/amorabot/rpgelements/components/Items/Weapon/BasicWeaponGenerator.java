package com.amorabot.rpgelements.components.Items.Weapon;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.rpgelements.components.Items.DataStructures.Modifier;
import com.amorabot.rpgelements.utils.CraftingUtils;

import java.util.Map;

public class BasicWeaponGenerator { //Modularize the generation steps for re-use

    public static Weapon createGenericWeapon(int itemLevel, ItemRarities rarity, WeaponTypes type, boolean identified, boolean corrupted){

        //Initial item setup
        Weapon blankWeapon = new Weapon(itemLevel, type, rarity, identified, corrupted); //Generic blank weapon constructor
        //Generating the modifiers to fill the item
        int maxMods = rarity.getMaxMods();
        int numberOfMods = 0;
        Map<WeaponModifiers, Map<Integer, int[]>> prefixes = type.castTo(WeaponModifiers.class, type.getBasicPrefixes());
        Map<WeaponModifiers, Map<Integer, int[]>> suffixes = type.castTo(WeaponModifiers.class, type.getBasicSuffixes());
        switch (rarity){
            case COMMON -> {
                //No mods should be added
                return blankWeapon;
            }
            case MAGIC -> {
                numberOfMods = CraftingUtils.getRandomNumber(1, maxMods);
                for (int i = 0; i<numberOfMods; i++){
                    double random = Math.random();
                    if (random >= 0.5){ //Its a prefix
                        blankWeapon.addModifier(Modifier.getRandomModifier(prefixes, blankWeapon.getIlvl(), blankWeapon.getModifiers()));
                    } else { //Its a suffix
                        blankWeapon.addModifier(Modifier.getRandomModifier(suffixes, blankWeapon.getIlvl(), blankWeapon.getModifiers()));
                    }
                }
                blankWeapon.updateBaseDamage();
                return blankWeapon;
            }
            case RARE -> {
                numberOfMods = CraftingUtils.getRandomNumber(3, maxMods);
                int maxPrefixes = 3;
                int currPrefixes = 0;
                int maxSuffixes = 3;
                int currSuffixes = 0;
                for (int i = 0; i<numberOfMods; i++){
                    double random = Math.random();
                    if (random >= 0.5){ //Its a prefix
                        if (currPrefixes < maxPrefixes){ //Open prefix
                            blankWeapon.addModifier(Modifier.getRandomModifier(prefixes, blankWeapon.getIlvl(), blankWeapon.getModifiers()));
                            currPrefixes++;
                        } else { //Generate a suffix
                            blankWeapon.addModifier(Modifier.getRandomModifier(suffixes, blankWeapon.getIlvl(), blankWeapon.getModifiers()));
                            currSuffixes++;
                        }
                    } else { //Its a suffix
                        if (currSuffixes < maxSuffixes){ //Open suffix
                            blankWeapon.addModifier(Modifier.getRandomModifier(suffixes, blankWeapon.getIlvl(), blankWeapon.getModifiers()));
                            currSuffixes++;
                        } else { //Generate a prefix
                            blankWeapon.addModifier(Modifier.getRandomModifier(prefixes, blankWeapon.getIlvl(), blankWeapon.getModifiers()));
                            currPrefixes++;
                        }
                    }
                }
                blankWeapon.updateBaseDamage();
                return blankWeapon;
            }
            case UNIQUE -> {
                return null; //Should not happen
            }
        }
        return null;
    }
}
