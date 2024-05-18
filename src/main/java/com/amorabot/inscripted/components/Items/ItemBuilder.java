package com.amorabot.inscripted.components.Items;

import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Armor.Armor;
import com.amorabot.inscripted.components.Items.Armor.ArmorTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.DataStructures.ModifierManager;
import com.amorabot.inscripted.components.Items.Interfaces.ItemSubtype;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;
import com.amorabot.inscripted.components.Items.modifiers.Inscription;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;
import com.amorabot.inscripted.utils.CraftingUtils;

import java.util.*;

public class ItemBuilder {

    public static <SubType extends Enum<SubType> & ItemSubtype> Item randomItem(ItemTypes type, SubType subType, int itemLevel, ItemRarities rarity, boolean identified, boolean corrupted){
        //Initial item setup
        Item blankItem;

        Map<InscriptionID, Map<Integer, Integer>> prefixes;
        Map<InscriptionID, Map<Integer, Integer>> suffixes;

        if (subType instanceof WeaponTypes){
            blankItem = new Weapon(itemLevel, (WeaponTypes) subType, rarity, identified, corrupted);
        } else if (subType instanceof ArmorTypes) {
            blankItem = new Armor(type, itemLevel, (ArmorTypes) subType, rarity, identified, corrupted);
        } else {
            blankItem = null;
        }

        prefixes = getAffixTableOf(Affix.PREFIX, type, subType);
        suffixes = getAffixTableOf(Affix.SUFFIX, type, subType);
        assert prefixes != null;
        assert suffixes != null;

        Set<InscriptionID> illegalMods = new HashSet<>();
        //Lets add the mods blocked by the item's level
        illegalMods.addAll(ModifierManager.checkForIllegalMods(prefixes, itemLevel));
        illegalMods.addAll(ModifierManager.checkForIllegalMods(suffixes, itemLevel));

        if (blankItem != null){
            fillModsFor(blankItem, prefixes, suffixes, illegalMods);
        }

        return blankItem;
    }

    public static boolean isPrefix(){
        //50%-50% of being a prefix
        double random = Math.random();
        return random >= 0.5;
    }

    public static void addModTo(Item item, Map<InscriptionID, Map<Integer, Integer>> affixMap, Set<InscriptionID> illegalMods){
        //Lets first block all the current mods already inside that item (Alternative: add the selected mod's ID to the illegal mods Set)
        Set<InscriptionID> blockedMods = item.getInscriptions();
        blockedMods.addAll(illegalMods);
        //Blocked mods are: the item's previous mods and previously illegal mods
        Inscription mod = ModifierManager.selectAvailableMod(item.getIlvl(), affixMap, blockedMods);
        item.addInscription(mod);
    }

    public static void fillModsFor(Item item, Map<InscriptionID, Map<Integer, Integer>> prefixes, Map<InscriptionID, Map<Integer, Integer>> suffixes, Set<InscriptionID> illegalMods){
        //Generating the modifiers to fill the item
        ItemRarities rarity = item.getRarity();
        switch (rarity){
            case COMMON -> {
                //No mods should be added
            }
            case MAGIC -> addNewMagicModSet(item, prefixes, suffixes, illegalMods);
            case RARE -> addNewRareModSet(item, prefixes, suffixes, illegalMods);
            case RELIC -> {
            }
        }
    }

    public static <SubType extends Enum<SubType> & ItemSubtype> Map<InscriptionID, Map<Integer, Integer>> getAffixTableOf(Affix affix,
                                                                                                                        ItemTypes type,
                                                                                                                        SubType subType){
        Map<InscriptionID, Map<Integer, Integer>> affixTable;

        if (subType instanceof WeaponTypes){
            Map<String, Map<String, Map<Integer, Integer>>> allWeaponAffixes = ((WeaponTypes)subType).getAffixes();
            affixTable = subType.castModMap(allWeaponAffixes.get(affix.toString()));
        } else if(subType instanceof ArmorTypes) {
            switch (type){
                case HELMET -> {
                    Map<String, Map<String, Map<Integer, Integer>>> helmetAffixes = ((ArmorTypes)subType).getHelmetAffixes();
                    affixTable = subType.castModMap(helmetAffixes.get(affix.toString()));
                }
                case CHESTPLATE -> {
                    Map<String, Map<String, Map<Integer, Integer>>> chestplateAffixes = ((ArmorTypes)subType).getChestplateAffixes();
                    affixTable = subType.castModMap(chestplateAffixes.get(affix.toString()));
                }
                case LEGGINGS -> {
                    Map<String, Map<String, Map<Integer, Integer>>> leggingsAffixes = ((ArmorTypes)subType).getLeggingsAffixes();
                    affixTable = subType.castModMap(leggingsAffixes.get(affix.toString()));
                }
                case BOOTS -> {
                    Map<String, Map<String, Map<Integer, Integer>>> bootsAffixes = ((ArmorTypes)subType).getBootsAffixes();
                    affixTable = subType.castModMap(bootsAffixes.get(affix.toString()));
                }
                default -> {
                    return new HashMap<>();
                }
            }
        } else {
            return new HashMap<>();
        }

        return affixTable;
    }

    public static <SubType extends Enum<SubType> & ItemSubtype> void fillAllPrerequisiteTablesFor(ItemTypes type,
                                                                                                  SubType subType,
                                                                                                  int ilvl,
                                                                                                  Map<InscriptionID, Map<Integer, Integer>> prefixes,
                                                                                                  Map<InscriptionID, Map<Integer, Integer>> suffixes,
                                                                                                  Set<InscriptionID> illegalMods
    ){
        prefixes.putAll(ItemBuilder.getAffixTableOf(Affix.PREFIX, type, subType));
        suffixes.putAll(ItemBuilder.getAffixTableOf(Affix.SUFFIX, type, subType));

        illegalMods.addAll(ModifierManager.checkForIllegalMods(prefixes, ilvl));
        illegalMods.addAll(ModifierManager.checkForIllegalMods(suffixes, ilvl));
    }


    public static void addNewMagicModSet(Item item,
                                         Map<InscriptionID, Map<Integer, Integer>> prefixes,
                                         Map<InscriptionID, Map<Integer, Integer>> suffixes,
                                         Set<InscriptionID> illegalMods)
    {
        boolean hasImbuedMod = false;
        Inscription imbuedMod = null;
        for (Inscription mod : item.getInscriptionList()){
            if (mod.isImbued()){
                hasImbuedMod = true;
                imbuedMod = mod;
            }
        }
        List<Inscription> modList = item.getInscriptionList();
        modList.clear();
        int numberOfMods = CraftingUtils.getRandomNumber(1, ItemRarities.MAGIC.getMaxMods());
        if (hasImbuedMod){
            modList.add(imbuedMod);
            numberOfMods = 1;
        }
        for (int i = 0; i<numberOfMods; i++){
            if (isPrefix()){
                addModTo(item, prefixes, illegalMods);
            } else { //Its a suffix
                addModTo(item, suffixes, illegalMods);
            }
        }
    }
    public static void addNewRareModSet(Item item,
                                         Map<InscriptionID, Map<Integer, Integer>> prefixes,
                                         Map<InscriptionID, Map<Integer, Integer>> suffixes,
                                         Set<InscriptionID> illegalMods)
    {
        boolean hasImbuedMod = false;
        Inscription imbuedMod = null;
        for (Inscription mod : item.getInscriptionList()){
            if (mod.isImbued()){
                hasImbuedMod = true;
                imbuedMod = mod;
            }
        }
        List<Inscription> modList = item.getInscriptionList();
        modList.clear();

        int numberOfMods = CraftingUtils.getRandomNumber(3, ItemRarities.RARE.getMaxMods());
        int maxPrefixes = 3;
        int currPrefixes = 0;
        int maxSuffixes = 3;
        int currSuffixes = 0;

        if (hasImbuedMod){
            modList.add(imbuedMod);
            numberOfMods--;
            if (imbuedMod.getInscription().getData().getAffixType().equals(Affix.PREFIX)){
                currPrefixes++;
            } else {
                currSuffixes++;
            }
        }

        for (int i = 0; i<numberOfMods; i++){
            if (isPrefix()){
                if (currPrefixes < maxPrefixes){ //Open prefix
                    addModTo(item, prefixes, illegalMods);
                    currPrefixes++;
                } else { //Generate a suffix
                    addModTo(item, suffixes, illegalMods);
                    currSuffixes++;
                }
            } else { //Suffix
                if (currSuffixes < maxSuffixes){ //Open suffix
                    addModTo(item, suffixes, illegalMods);
                    currSuffixes++;
                } else { //Generate a prefix
                    addModTo(item, prefixes, illegalMods);
                    currPrefixes++;
                }
            }
        }
    }
}
