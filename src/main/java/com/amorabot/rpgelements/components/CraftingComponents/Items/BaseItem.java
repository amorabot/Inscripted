package com.amorabot.rpgelements.components.CraftingComponents.Items;

import com.amorabot.rpgelements.Crafting.Affix;
import com.amorabot.rpgelements.Crafting.CraftingUtils;
import com.amorabot.rpgelements.Crafting.Interfaces.SelectableAffixTable;
import com.amorabot.rpgelements.Crafting.ItemRarities;
import com.amorabot.rpgelements.Crafting.ItemTypes;
import com.amorabot.rpgelements.Crafting.Weapons.Modifiers.AxeMods;
import com.amorabot.rpgelements.Crafting.Weapons.Modifiers.WeaponModifiers;
import com.amorabot.rpgelements.Crafting.Weapons.Enums.WeaponTypes;
import com.amorabot.rpgelements.CustomDataTypes.RPGElementsContainer;

import java.io.Serializable;
import java.util.*;

import static com.amorabot.rpgelements.utils.Utils.log;

public class BaseItem implements Serializable, RPGElementsContainer {

    private static final long serialversionUID = 1000101L;

    private boolean identified;
    private final int ilvl;
    private ItemRarities rarity;
    private String implicit;
    private final Enum<?> itemType; //WEAPON, ARMOR
    private final Enum<?> category; //AXE, SWORD -//- HELMET, CHESTPLATE
    private List<Enum<?>> selectedPrefixes = new ArrayList<>();
    private List<Enum<?>> selectedSuffixes = new ArrayList<>();

    public <T extends Enum<ItemTypes>, C extends Enum<C> & SelectableAffixTable> BaseItem(T itemType, C itemCategory, boolean id, int ilvl, ItemRarities rarity, String implicit){
        this.itemType = itemType;
        this.category = itemCategory;

        this.identified = id;
        this.ilvl = ilvl;
        this.rarity = rarity;
        this.implicit = implicit;

        Enum<?> prefixTable = itemCategory.getPrefixTable();
        Enum<?> suffixTable = itemCategory.getSuffixTable();

        if (itemType == ItemTypes.WEAPON){
            WeaponTypes weaponCategory = (WeaponTypes) itemCategory;
            switch (weaponCategory){
                case AXE:
                    AxeMods axePrefixTable = (AxeMods) prefixTable;
                    List<WeaponModifiers> allPrefixes = axePrefixTable.getAllModNames();

                    AxeMods axeSuffixTable = (AxeMods) suffixTable;
                    List<WeaponModifiers> allSuffixes = axeSuffixTable.getAllModNames();

                    generateMods(allPrefixes, allSuffixes);
                    break;
                case SHORTSWORD:
                    //TODO: geração para shortsword
                    break;
            }
        }
    }

    private <A extends Enum<A>> void generateMods(List<A> allPrefixes, List<A> allSuffixes){
        int mods;
        switch (rarity){
            case COMMON:
                mods = 0;
                break;
            case MAGIC:
                mods = CraftingUtils.getRandomNumber(1, 2);

                for (int i = 0; i<mods; i++){
                    //gere um affixo aleatorio "mods" vezes
                    getRandomAffix(allPrefixes, allSuffixes);
                }
                break;
            case RARE:
                mods = CraftingUtils.getRandomNumber(3, 6);

                for (int i = 0; i<mods; i++){
                    //gere um affixo aleatorio "mods" vezes
                    getRandomAffix(allPrefixes, allSuffixes);
                }
                break;
        }
    }

    private <A extends Enum<A>> void getRandomAffix(List<A> prefixTable, List<A> suffixTable){
        int rand = CraftingUtils.getRandomNumber(1, 10);
        // Get blocked mods list
        List<A> blockedPrefixes = new ArrayList<>();
        List<A> blockedSuffixes = new ArrayList<>();

        for (A prefix : prefixTable){
            if (!selectableAffix(prefix)) {
                blockedPrefixes.add(prefix);
            }
        }
        for (A suffix : suffixTable){
            if (!selectableAffix(suffix)) {
                blockedSuffixes.add(suffix);
            }
        }

        if (rand <= 5){ // Suffix
            if (hasOpenSuffix()){ // Se puder gerar suffix, gere
//                log("SUFFIX S");
                generateSuffix(suffixTable, blockedSuffixes);
            } else { // Senão, gere um prefix
//                log("PREFIX S");
                generatePrefix(prefixTable, blockedPrefixes);
            }
        } else { //Prefix
            if (hasOpenPrefix()){ //Se puder gerar prefix, gere
//                log("PREFIX P");
                generatePrefix(prefixTable, blockedPrefixes);
            } else { //Senão, gere um suffix
//                log("SUFFIX P");
                generateSuffix(suffixTable, blockedSuffixes);
            }
        }
    }
    public <A extends Enum<A>> boolean selectableAffix(A affix){
        if (category instanceof WeaponTypes){
            WeaponModifiers weaponMod = (WeaponModifiers)  affix;
            WeaponTypes weaponCategory = (WeaponTypes) category;
            switch (weaponCategory){
                case AXE:
                    AxeMods modTable;
                    if (weaponMod.getAffixType() == Affix.PREFIX){
                        modTable = (AxeMods) weaponCategory.getPrefixTable();
                    } else {
                        modTable = (AxeMods) weaponCategory.getSuffixTable();
                    }
                    List<Integer> tierIlvlData = modTable.getSortedTierIlvls(weaponMod);
                    int minimumIlvl = tierIlvlData.get(0);
                    return (ilvl >= minimumIlvl);
                case SHORTSWORD:
                    break;
            }
        } else {
            return false;
            //TODO: implementação para armor
        }
        log("invalid item category");
        return false;
    }
    //---------------------------------------PREFIX METHODS------------------------------------
    private boolean hasOpenPrefix(){
        int prefixes = selectedPrefixes.size();
        switch (rarity){
            case MAGIC:
                return (prefixes < 2); //se tiver menos que 2, retorne ok
            case RARE:
                return (prefixes < 3);
        }
        log("Unable to check for open prefix");
        return false;
    }
    private <P extends Enum<P>, B extends Enum<B>> void generatePrefix(List<P> prefixList, List<B> blockedModList) {
        List<P> availablePrefixes = new ArrayList<>(prefixList);
        availablePrefixes.removeAll(selectedPrefixes);

        availablePrefixes.removeAll(blockedModList);

        int index = CraftingUtils.getRandomNumber(0, availablePrefixes.size()-1); //pegar algum prefixo na lista

//        log("adding prefix");
        selectedPrefixes.add(availablePrefixes.get(index));
    }
    //---------------------------------------SUFFIX METHODS------------------------------------
    private boolean hasOpenSuffix(){
        int suffixes = selectedSuffixes.size();
        switch (rarity){
            case MAGIC:
                return (suffixes < 2);
            case RARE:
                return (suffixes < 3);
        }
        log("Unable to check for open suffix");
        return false;
    }
    private <S extends Enum<S>, B extends Enum<B>> void generateSuffix(List<S> suffixList, List<B> blockedModList) {
        List<S> availableSuffixes = new ArrayList<>(suffixList);
        availableSuffixes.removeAll(selectedSuffixes);

        availableSuffixes.removeAll(blockedModList);

        int index = CraftingUtils.getRandomNumber(0, availableSuffixes.size()-1);

//        log("adding suffix");
        selectedSuffixes.add(availableSuffixes.get(index));
    }
    //------------------------------------------------------------------------------------------
    public boolean isIdentified() {
        return identified;
    }

    public int getIlvl() {
        return ilvl;
    }

    public ItemRarities getRarity() {
        return rarity;
    }

    public String getImplicit() {
        return implicit;
    }

    public Enum<?> getItemType() {
        return itemType;
    }

    public Enum<?> getCategory() {
        return category;
    }

    public void setIdentified(boolean identified) {
        this.identified = identified;
    }

    public void setImplicit(String implicit) {
        this.implicit = implicit;
    }

    public List<Enum<?>> getPrefixes(){
        return selectedPrefixes;
    }
    public List<Enum<?>> getSuffixes(){
        return selectedSuffixes;
    }
}
