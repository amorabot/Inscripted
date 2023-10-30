package com.amorabot.inscripted.components.Items.Armor;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
//import com.amorabot.rpgelements.components.Items.DataStructures.Modifier;
import com.amorabot.inscripted.components.Items.DataStructures.ModifierIDs;
import com.amorabot.inscripted.components.Items.DataStructures.Modifier;
import com.amorabot.inscripted.utils.CraftingUtils;

import java.util.Map;

public class BasicArmorGenerator {

    public static Armor createGenericArmor(ItemTypes armorPiece, ArmorTypes type, int itemLevel, ItemRarities rarity, boolean identified, boolean corrupted){
        //ItemTypes armorPiece, int ilvl, ItemRarities rarity, boolean identified, boolean corrupted
        //Initial item setup
        Armor blankArmor = new Armor(armorPiece, itemLevel, type, rarity, identified, corrupted); //Generic blank weapon constructor
        //Generating the modifiers to fill the item
        int maxMods = rarity.getMaxMods();
        int numberOfMods = 0;
//        Map<ArmorModifiers, Map<Integer, int[]>> prefixes;
//        Map<ArmorModifiers, Map<Integer, int[]>> suffixes;
        Map<ModifierIDs, Map<Integer, int[]>> prefixes;
        Map<ModifierIDs, Map<Integer, int[]>> suffixes;
        switch (armorPiece){
            case HELMET -> {
//                prefixes = type.castTo(ArmorModifiers.class, type.getBasicHelmetPrefixes());
//                suffixes = type.castTo(ArmorModifiers.class, type.getBasicHelmetSuffixes());
                prefixes = type.castToModEnum(type.getBasicHelmetPrefixes());
                suffixes = type.castToModEnum(type.getBasicHelmetSuffixes());
            }
            case CHESTPLATE -> {
//                prefixes = type.castTo(ArmorModifiers.class, type.getBasicChestlatePrefixes());
//                suffixes = type.castTo(ArmorModifiers.class, type.getBasicChesplateSuffixes());
                prefixes = type.castToModEnum(type.getBasicChestlatePrefixes());
                suffixes = type.castToModEnum(type.getBasicChesplateSuffixes());
            }
            case LEGGINGS -> {
//                prefixes = type.castTo(ArmorModifiers.class, type.getBasicLeggingsPrefixes());
//                suffixes = type.castTo(ArmorModifiers.class, type.getBasicLeggingsSuffixes());
                prefixes = type.castToModEnum(type.getBasicLeggingsPrefixes());
                suffixes = type.castToModEnum(type.getBasicLeggingsSuffixes());
            }
            case BOOTS -> {
//                prefixes = type.castTo(ArmorModifiers.class, type.getBasicBootsPrefixes());
//                suffixes = type.castTo(ArmorModifiers.class, type.getBasicBootsSuffixes());
                prefixes = type.castToModEnum(type.getBasicBootsPrefixes());
                suffixes = type.castToModEnum(type.getBasicBootsSuffixes());
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
//                        blankArmor.addModifier(Modifier.getRandomModifier(prefixes, blankArmor.getIlvl(), blankArmor.getModifiers()));
                    } else { //Its a suffix
//                        blankArmor.addModifier(Modifier.getRandomModifier(suffixes, blankArmor.getIlvl(), blankArmor.getModifiers()));
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
//                            blankArmor.addModifier(Modifier.getRandomModifier(prefixes, blankArmor.getIlvl(), blankArmor.getModifiers()));
                            blankArmor.addModifier(Modifier.getRandomModifier(prefixes, blankArmor.getIlvl(), blankArmor.getModifiers()));
                            currPrefixes++;
                        } else { //Generate a suffix
//                            blankArmor.addModifier(Modifier.getRandomModifier(suffixes, blankArmor.getIlvl(), blankArmor.getModifiers()));
                            blankArmor.addModifier(Modifier.getRandomModifier(suffixes, blankArmor.getIlvl(), blankArmor.getModifiers()));
                            currSuffixes++;
                        }
                    } else { //Its a suffix
                        if (currSuffixes < maxSuffixes){ //Open suffix
//                            blankArmor.addModifier(Modifier.getRandomModifier(suffixes, blankArmor.getIlvl(), blankArmor.getModifiers()));
                            blankArmor.addModifier(Modifier.getRandomModifier(suffixes, blankArmor.getIlvl(), blankArmor.getModifiers()));
                            currSuffixes++;
                        } else { //Generate a prefix
//                            blankArmor.addModifier(Modifier.getRandomModifier(prefixes, blankArmor.getIlvl(), blankArmor.getModifiers()));
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
