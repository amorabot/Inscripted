package com.amorabot.rpgelements.components.Items.Armor;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Modifier;
import com.amorabot.rpgelements.utils.CraftingUtils;

import java.util.HashMap;
import java.util.Map;

public class BasicArmorGenerator {

    public static Armor createGenericArmor(ItemTypes armorPiece, ArmorTypes type, int itemLevel, ItemRarities rarity, boolean identified, boolean corrupted){
        //ItemTypes armorPiece, int ilvl, ItemRarities rarity, boolean identified, boolean corrupted
        //Initial item setup
        Armor blankArmor = new Armor(armorPiece, itemLevel, type, rarity, identified, corrupted); //Generic blank weapon constructor
        //Generating the modifiers to fill the item
        int maxMods = rarity.getMaxMods();
        int numberOfMods = 0;
        Map<ArmorModifiers, Map<Integer, int[]>> prefixes = new HashMap<>();
        Map<ArmorModifiers, Map<Integer, int[]>> suffixes = new HashMap<>();
        switch (armorPiece){
            case HELMET -> {
                prefixes = type.castTo(ArmorModifiers.class, type.getBasicHelmetPrefixes());
                suffixes = type.castTo(ArmorModifiers.class, type.getBasicHelmetSuffixes());
            }
            case CHESTPLATE -> {
                //
            }
            case LEGGINGS -> {
                //a
            }
            case BOOTS -> {
                //ab
            }
            default -> {
                return null;
            }
        }
        switch (rarity){
            case COMMON -> {
                //No mods should be added
                return blankArmor;
            }
            case MAGIC -> {
                numberOfMods = CraftingUtils.getRandomNumber(1, maxMods);
                for (int i = 0; i<numberOfMods; i++){
                    double random = Math.random();
                    if (random >= 0.5){ //Its a prefix
                        blankArmor.addModifier(Modifier.getRandomModifier(prefixes, blankArmor.getIlvl(), blankArmor.getModifiers()));
                    } else { //Its a suffix
                        blankArmor.addModifier(Modifier.getRandomModifier(suffixes, blankArmor.getIlvl(), blankArmor.getModifiers()));
                    }
                }
                return blankArmor;
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
                            blankArmor.addModifier(Modifier.getRandomModifier(prefixes, blankArmor.getIlvl(), blankArmor.getModifiers()));
                            currPrefixes++;
                        } else { //Generate a suffix
                            blankArmor.addModifier(Modifier.getRandomModifier(suffixes, blankArmor.getIlvl(), blankArmor.getModifiers()));
                            currSuffixes++;
                        }
                    } else { //Its a suffix
                        if (currSuffixes < maxSuffixes){ //Open suffix
                            blankArmor.addModifier(Modifier.getRandomModifier(suffixes, blankArmor.getIlvl(), blankArmor.getModifiers()));
                            currSuffixes++;
                        } else { //Generate a prefix
                            blankArmor.addModifier(Modifier.getRandomModifier(prefixes, blankArmor.getIlvl(), blankArmor.getModifiers()));
                            currPrefixes++;
                        }
                    }
                }
                return blankArmor;
            }
            case UNIQUE -> {
                return null; //Should not happen
            }
        }
        return null;
    }
}
