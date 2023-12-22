package com.amorabot.inscripted.components.Items;

import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Armor.Armor;
import com.amorabot.inscripted.components.Items.Armor.ArmorTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.DataStructures.ModifierIDs;
import com.amorabot.inscripted.components.Items.DataStructures.Modifier;
import com.amorabot.inscripted.components.Items.DataStructures.ModifierManager;
import com.amorabot.inscripted.components.Items.Interfaces.AffixTableSelector;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;
import com.amorabot.inscripted.utils.CraftingUtils;

import java.util.*;

public class ItemGenerator {

    public static <SubType extends Enum<SubType> & AffixTableSelector> Item randomItem(ItemTypes type, SubType subType, int itemLevel, ItemRarities rarity, boolean identified, boolean corrupted){
        //Initial item setup
        Item blankItem;

        Map<ModifierIDs, Map<Integer, Integer>> prefixes;
        Map<ModifierIDs, Map<Integer, Integer>> suffixes;


        if (subType instanceof WeaponTypes){
            blankItem = new Weapon(itemLevel, (WeaponTypes) subType, rarity, identified, corrupted);

            Map<String, Map<String, Map<Integer, Integer>>> weaponAffixes = ((WeaponTypes)subType).getAffixes();
            prefixes = subType.castModMap(weaponAffixes.get(Affix.PREFIX.toString()));
            suffixes = subType.castModMap(weaponAffixes.get(Affix.SUFFIX.toString()));

        } else {
            blankItem = new Armor(type, itemLevel, (ArmorTypes) subType, rarity, identified, corrupted);

            switch (type){
                case HELMET -> {
                    Map<String, Map<String, Map<Integer, Integer>>> helmetAffixes = ((ArmorTypes)subType).getHelmetAffixes();
                    prefixes = subType.castModMap(helmetAffixes.get(Affix.PREFIX.toString()));
                    suffixes = subType.castModMap(helmetAffixes.get(Affix.SUFFIX.toString()));
                }
                case CHESTPLATE -> {
                    Map<String, Map<String, Map<Integer, Integer>>> chestplateAffixes = ((ArmorTypes)subType).getChestplateAffixes();
                    prefixes = subType.castModMap(chestplateAffixes.get(Affix.PREFIX.toString()));
                    suffixes = subType.castModMap(chestplateAffixes.get(Affix.SUFFIX.toString()));
                }
                case LEGGINGS -> {
                    Map<String, Map<String, Map<Integer, Integer>>> leggingsAffixes = ((ArmorTypes)subType).getLeggingsAffixes();
                    prefixes = subType.castModMap(leggingsAffixes.get(Affix.PREFIX.toString()));
                    suffixes = subType.castModMap(leggingsAffixes.get(Affix.SUFFIX.toString()));
                }
                case BOOTS -> {
                    Map<String, Map<String, Map<Integer, Integer>>> bootsAffixes = ((ArmorTypes)subType).getBootsAffixes();
                    prefixes = subType.castModMap(bootsAffixes.get(Affix.PREFIX.toString()));
                    suffixes = subType.castModMap(bootsAffixes.get(Affix.SUFFIX.toString()));
                }
                default -> {
                    return null;
                }
            }
        }

        //Generating the modifiers to fill the item
        int maxMods = rarity.getMaxMods();
        int numberOfMods;

        Set<ModifierIDs> illegalMods = new HashSet<>();
        illegalMods.addAll(ModifierManager.checkForIllegalMods(prefixes, itemLevel));
        illegalMods.addAll(ModifierManager.checkForIllegalMods(suffixes, itemLevel));

        switch (rarity){
            case COMMON -> {
                //No mods should be added
                return blankItem;
            }
            case MAGIC -> {
                numberOfMods = CraftingUtils.getRandomNumber(1, maxMods);
                for (int i = 0; i<numberOfMods; i++){
                    if (isPrefix()){
                        fillItemMod(blankItem, prefixes, illegalMods);
                    } else { //Its a suffix
                        fillItemMod(blankItem, suffixes, illegalMods);
                    }
                }
                return blankItem;
            }
            case RARE -> {
                numberOfMods = CraftingUtils.getRandomNumber(3, maxMods);
                int maxPrefixes = 3;
                int currPrefixes = 0;
                int maxSuffixes = 3;
                int currSuffixes = 0;
                for (int i = 0; i<numberOfMods; i++){
                    if (isPrefix()){
                        if (currPrefixes < maxPrefixes){ //Open prefix
                            fillItemMod(blankItem, prefixes, illegalMods);
                            currPrefixes++;
                        } else { //Generate a suffix
                            fillItemMod(blankItem, suffixes, illegalMods);
                            currSuffixes++;
                        }
                    } else { //Suffix
                        if (currSuffixes < maxSuffixes){ //Open suffix
                            fillItemMod(blankItem, suffixes, illegalMods);
                            currSuffixes++;
                        } else { //Generate a prefix
                            fillItemMod(blankItem, prefixes, illegalMods);
                            currPrefixes++;
                        }
                    }
                }
                return blankItem;
            }
            case UNIQUE -> {
                return null; //Should not happen
            }
        }
        return null;
    }

    public static boolean isPrefix(){
        //50%-50% of being a prefix
        double random = Math.random();
        return random >= 0.5;
    }

    private static void fillItemMod(Item item, Map<ModifierIDs, Map<Integer, Integer>> affixMap, Set<ModifierIDs> illegalMods){
        Set<ModifierIDs> blockedMods = item.getModIDs();
        blockedMods.addAll(illegalMods);
        //Blocked mods are now updated with both already generated mods and illegal mods
        Modifier mod = ModifierManager.selectAvailableMod(item.getIlvl(), affixMap, blockedMods);
        item.addModifier(mod);
    }
}
