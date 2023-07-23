package com.amorabot.rpgelements.components.CraftingComponents.Builders;

import com.amorabot.rpgelements.Crafting.CraftingUtils;
import com.amorabot.rpgelements.Crafting.Interfaces.TableDataAccess;
import com.amorabot.rpgelements.Crafting.Weapons.Enums.DamageTypes;
import com.amorabot.rpgelements.Crafting.Weapons.Enums.WeaponNames;
import com.amorabot.rpgelements.Crafting.Weapons.Enums.WeaponTypes;
import com.amorabot.rpgelements.Crafting.Weapons.Modifiers.*;
import com.amorabot.rpgelements.components.CraftingComponents.Items.BaseItem;
import com.amorabot.rpgelements.components.CraftingComponents.Items.Weapon;
import org.bukkit.Material;

import java.util.*;

import static com.amorabot.rpgelements.utils.Utils.log;

public class WeaponAssembler {

    private final WeaponTypes weaponType;
    private final List<WeaponModifiers> prefixes;
    private final List<WeaponModifiers> suffixes;
    private final int ilvl;

    private final String name;
    private Material material;
    private final Map<DamageTypes, int[]> baseDmg = new HashMap<>();
    private final Map<WeaponModifiers, TierValuePair> mappedModifiers = new HashMap<>();

    public WeaponAssembler(BaseItem baseItemData) {
        WeaponTypes weaponType = (WeaponTypes) baseItemData.getCategory();
        int ilvl = baseItemData.getIlvl();


        List<WeaponModifiers> weaponPrefixes = new ArrayList<>(); //Casting the Prefixes and Suffixes to Weapon so that their methods are available
        for (Enum<?> prefix : baseItemData.getPrefixes()){
            weaponPrefixes.add((WeaponModifiers) prefix);
        }
        List<WeaponModifiers> weaponSuffixes = new ArrayList<>();
        for (Enum<?> suffix : baseItemData.getSuffixes()){
            weaponSuffixes.add((WeaponModifiers) suffix);
        }

        this.weaponType = weaponType;
        this.prefixes = weaponPrefixes;
        this.suffixes = weaponSuffixes;
        this.ilvl = ilvl;
        this.name = WeaponNames.valueOf(weaponType.toString()).getRandomName();
    }

    public Weapon assemble() {
        generateTiers();
        mapBase();
        return new Weapon(name, material, baseDmg, mappedModifiers);
    }

    private <E extends Enum<E> & TableDataAccess> void generateTiers() {

        switch (weaponType) {
            case AXE:
                //        prefix source // chosen prefixes // suffix source // chosen suffixes // ilvl
                generateTiers(AxeMods.PREFIXES, prefixes, AxeMods.SUFFIXES, suffixes, ilvl);
                break;
            case SHORTSWORD:
                generateTiers(SwordMods.PREFIXES, prefixes, SwordMods.SUFFIXES, suffixes, ilvl);
                break;
        }
    }

    private <E extends Enum<E> & TableDataAccess> void generateTiers(E prefixTable, List<WeaponModifiers> prefixes, E suffixTable, List<WeaponModifiers> suffixes, int ilvl) {
        generateAffixTiers(prefixTable, prefixes, ilvl);
        generateAffixTiers(suffixTable, suffixes, ilvl);
    }

    private <E extends Enum<E> & TableDataAccess> void generateAffixTiers(E modTable, List<WeaponModifiers> affixes, int ilvl) {

        for (WeaponModifiers affix : affixes) {
            TierData[] tierData = modTable.getModTiers(affix);

            //--------------SORTING TIERS--------------------
            List<Integer> sortedTierIlvls = modTable.getSortedTierIlvls(affix);
            int highestTier = getMaximumTier(sortedTierIlvls, ilvl);
            int chosenTier = CraftingUtils.getRandomNumber(0, highestTier);

            TierData chosenTierData = tierData[chosenTier];
            int[] chosenValues = chosenTierData.getRandomValue();

            log(affix.toString());
            log("" + chosenTier);
            mappedModifiers.put(affix, new TierValuePair(chosenTier, chosenValues));
        }
    }

    private int getMaximumTier(List<Integer> sortedIlvlList, int ilvl) {
        if (sortedIlvlList.get(0) > ilvl) { //Se o ilvl mais baixo para o mod for maior que ilvl do item
            log("Invalid affix at current item level.");
            return -1;
        }
        List<Integer> aux = new ArrayList<>(sortedIlvlList); //aux pode ser alterado

        if (!(aux.contains(ilvl))) { //Se a lista não tiver o item level desejado, adicione
            aux.add(ilvl);
            aux.sort(Comparator.naturalOrder());
        } else { //Senão, já temos o número desejado
            return aux.indexOf(ilvl);
        }

        if (aux.indexOf(ilvl) != 0) { //Se o ilvl não for o primeiro da lista
            return aux.indexOf(ilvl) - 1; //retorne o item antes dele (maior tier alcançável)
        }
        return 0; //Se for o ultimo retorne o tier mais basico
    }
    public void mapBase(){
        switch (weaponType){
            case AXE:
                if (ilvl > 0 && ilvl <= 11){
                    baseDmg.put(DamageTypes.PHYSICAL, new int[]{6,11});
                } else if (ilvl <= 30){
                    baseDmg.put(DamageTypes.PHYSICAL, new int[]{11,21});
                } else if (ilvl <= 45){
                    baseDmg.put(DamageTypes.PHYSICAL, new int[]{27,50});
                } else if (ilvl <= 60){
                    baseDmg.put(DamageTypes.PHYSICAL, new int[]{35,65});
                } else if (ilvl <= 80){
                    baseDmg.put(DamageTypes.PHYSICAL, new int[]{38,114});
                } else {
                    baseDmg.put(DamageTypes.PHYSICAL, new int[]{60,150});
                }

                if (ilvl <= 10){
                    material = Material.WOODEN_AXE;
                } else if (ilvl <= 25) {
                    material = Material.STONE_AXE;
                } else if (ilvl <= 45) {
                    material = Material.IRON_AXE;
                } else if (ilvl <= 75) {
                    material = Material.DIAMOND_AXE;
                } else {
                    material = Material.GOLDEN_AXE;
                }
                break;
            case SHORTSWORD:
                if (ilvl > 0 && ilvl <= 11){
                    baseDmg.put(DamageTypes.PHYSICAL, new int[]{3,9});
                } else if (ilvl <= 30){
                    baseDmg.put(DamageTypes.PHYSICAL, new int[]{10,17});
                } else if (ilvl <= 45){
                    baseDmg.put(DamageTypes.PHYSICAL, new int[]{25,32});
                } else if (ilvl <= 60){
                    baseDmg.put(DamageTypes.PHYSICAL, new int[]{30,55});
                } else if (ilvl <= 80){
                    baseDmg.put(DamageTypes.PHYSICAL, new int[]{33,90});
                } else {
                    baseDmg.put(DamageTypes.PHYSICAL, new int[]{45,120});
                }

                if (ilvl <= 10){
                    this.material = Material.WOODEN_SWORD;
                } else if (ilvl <= 25) {
                    this.material = Material.STONE_SWORD;
                } else if (ilvl <= 45) {
                    this.material = Material.IRON_SWORD;
                } else if (ilvl <= 75) {
                    this.material = Material.DIAMOND_SWORD;
                } else {
                    this.material = Material.GOLDEN_SWORD;
                }
                break;
            default:
                this.material = Material.BONE;
                break;
        }
        mapBonusDamages();
    }

    public void mapBonusDamages(){
        Set<WeaponModifiers> mods = mappedModifiers.keySet();
        if (mods.contains(WeaponModifiers.ADDED_PHYSICAL)){
            int[] physDmg = baseDmg.get(DamageTypes.PHYSICAL);
            physDmg[0] = physDmg[0] + mappedModifiers.get(WeaponModifiers.ADDED_PHYSICAL).value()[0];
            physDmg[1] = physDmg[1] + mappedModifiers.get(WeaponModifiers.ADDED_PHYSICAL).value()[1];
            baseDmg.put(DamageTypes.PHYSICAL, physDmg);
        }
        if (mods.contains(WeaponModifiers.ADDED_FIRE)){
            baseDmg.put(DamageTypes.FIRE, mappedModifiers.get(WeaponModifiers.ADDED_FIRE).value());
            //CHECK POR PERCENT ELEMENTAL
        }
        if (mods.contains(WeaponModifiers.ADDED_ABYSSAL)){
            baseDmg.put(DamageTypes.ABYSSAL, mappedModifiers.get(WeaponModifiers.ADDED_ABYSSAL).value());
        }
        if (mods.contains(WeaponModifiers.PERCENT_PHYSICAL)){
            int percentPhys = mappedModifiers.get(WeaponModifiers.PERCENT_PHYSICAL).value()[0];
            int[] physDmg = baseDmg.get(DamageTypes.PHYSICAL);
            float phys1 = physDmg[0] * (1 + (float) percentPhys/100);
            float phys2 = physDmg[1] * (1 + (float) percentPhys/100);
            physDmg[0] = (int)phys1;
            physDmg[1] = (int)phys2;
            baseDmg.put(DamageTypes.PHYSICAL, physDmg);
        }
    }
}
