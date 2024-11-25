package com.amorabot.inscripted.components.Items;

import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Armor.Armor;
import com.amorabot.inscripted.components.Items.Armor.ArmorTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.Interfaces.ItemSubtype;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;
import com.amorabot.inscripted.components.Items.modifiers.Inscription;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;
import com.amorabot.inscripted.inscriptions.InscriptionTable;
import com.amorabot.inscripted.utils.CraftingUtils;

import java.util.*;

public class ItemBuilder {

    public static <SubType extends Enum<SubType> & ItemSubtype> Item randomItem(
            ItemTypes type, SubType subType, int itemLevel, ItemRarities rarity, boolean identified, boolean corrupted){
        //Initial item setup
        Item blankItem;

        InscriptionTable itemInscriptionsTable;
        Set<InscriptionID> illegalMods = new HashSet<>();

        if (subType instanceof WeaponTypes weaponType){
            blankItem = new Weapon(itemLevel, weaponType, rarity, identified, corrupted);
            itemInscriptionsTable = weaponType.getTableData();
        } else if (subType instanceof ArmorTypes armorType) {
            blankItem = new Armor(type, itemLevel, armorType, rarity, identified, corrupted);
            itemInscriptionsTable = armorType.getTableData();
        } else {
            blankItem = null;
            itemInscriptionsTable = null;
        }

        assert blankItem != null;
        switch (rarity){
            case AUGMENTED -> {
                generateMagicModSet(blankItem, itemInscriptionsTable, illegalMods);
            }
            case RUNIC -> {
                generateRareModSet(blankItem,itemInscriptionsTable, illegalMods);
            }
        }
        return blankItem;
    }

    public static boolean isPrefix(){
        //50%-50% of being a prefix
        double random = Math.random();
        return random >= 0.5;
    }

    public static void generateRareModSet(Item itemData, InscriptionTable itemInscriptionsTable, Set<InscriptionID> illegalMods){
        List<Inscription> newItemInscriptions = new ArrayList<>();
        int inscriptionsToGenerate = CraftingUtils.getRandomNumber(3, ItemRarities.RUNIC.getMaxMods());
        final int maxPrefixes = 3;
        final int maxSuffixes = 3;

        int currentPrefixes = 0;
        int currentSuffixes = 0;

        //Isolate the imbued inscription
        for (Inscription mod : itemData.getInscriptionList()){
            if (mod.isImbued()){
                inscriptionsToGenerate--; //One is already predefined
                newItemInscriptions.add(mod);
                if (mod.getInscription().getData().getAffixType().equals(Affix.PREFIX)){
                    currentPrefixes++;
                } else {currentSuffixes++;}
            }
        }
        //Clear existing Inscriptions
        itemData.getInscriptionList().clear();

        //Generating mods
        for (int i = 0; i<inscriptionsToGenerate; i++){
            //Attempting to generate a prefix
            if (isPrefix()){
                //Prefixes are open
                if (currentPrefixes < maxPrefixes){
                    newItemInscriptions.add(itemInscriptionsTable.getRandomInscription(Affix.PREFIX, itemData.getIlvl(),illegalMods));
                    currentPrefixes++;
                } else { //If a prefix attempt was unsuccessful, generate a suffix instead
                    newItemInscriptions.add(itemInscriptionsTable.getRandomInscription(Affix.SUFFIX, itemData.getIlvl(),illegalMods));
                    currentSuffixes++;
                }
            }else {
                //Suffixes are open
                if (currentSuffixes < maxSuffixes){
                    newItemInscriptions.add(itemInscriptionsTable.getRandomInscription(Affix.SUFFIX, itemData.getIlvl(),illegalMods));
                    currentSuffixes++;
                } else { //If a suffix attempt was unsuccessful, generate a prefix instead
                    newItemInscriptions.add(itemInscriptionsTable.getRandomInscription(Affix.PREFIX, itemData.getIlvl(),illegalMods));
                    currentPrefixes++;
                }
            }
        }

        itemData.getInscriptionList().addAll(newItemInscriptions);
    }
    public static void generateMagicModSet(Item itemData, InscriptionTable itemInscriptionsTable, Set<InscriptionID> illegalMods){
        List<Inscription> newItemInscriptions = new ArrayList<>();
        int inscriptionsToGenerate = CraftingUtils.getRandomNumber(1, ItemRarities.AUGMENTED.getMaxMods());
        boolean hasImbuedMod = false;
        //Isolate the imbued inscription
        for (Inscription mod : itemData.getInscriptionList()){
            if (mod.isImbued()){
                //When generating a new mod set with a imbued mod, just add 1 extra random Inscription
                hasImbuedMod = true;
                newItemInscriptions.add(mod);
                if (isPrefix()){
                    newItemInscriptions.add(itemInscriptionsTable.getRandomInscription(Affix.PREFIX, itemData.getIlvl(),illegalMods));
                } else {
                    newItemInscriptions.add(itemInscriptionsTable.getRandomInscription(Affix.SUFFIX, itemData.getIlvl(),illegalMods));
                }
            }
        }
        //Clear existing Inscriptions
        itemData.getInscriptionList().clear();
        if (hasImbuedMod){
            itemData.getInscriptionList().addAll(newItemInscriptions);
            return;
        }
        //In case it doesnt have a imbued mod, lets generate them normally
        for (int i = 0; i<inscriptionsToGenerate; i++){
            if (isPrefix()){
                newItemInscriptions.add(itemInscriptionsTable.getRandomInscription(Affix.PREFIX, itemData.getIlvl(),illegalMods));
            } else { //Its a suffix
                newItemInscriptions.add(itemInscriptionsTable.getRandomInscription(Affix.SUFFIX, itemData.getIlvl(),illegalMods));
            }
        }

        itemData.getInscriptionList().addAll(newItemInscriptions);
    }
}
