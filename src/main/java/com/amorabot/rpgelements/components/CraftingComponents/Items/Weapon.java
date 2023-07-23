package com.amorabot.rpgelements.components.CraftingComponents.Items;

import com.amorabot.rpgelements.Crafting.CraftingUtils;
import com.amorabot.rpgelements.Crafting.Weapons.Modifiers.AxeMods;
import com.amorabot.rpgelements.Crafting.Weapons.Modifiers.TierData;
import com.amorabot.rpgelements.Crafting.Weapons.Modifiers.TierValuePair;
import com.amorabot.rpgelements.Crafting.Weapons.Modifiers.WeaponModifiers;
import org.bukkit.Material;
import com.amorabot.rpgelements.Crafting.Interfaces.TableDataAccess;
import com.amorabot.rpgelements.Crafting.Weapons.Enums.DamageTypes;
import com.amorabot.rpgelements.Crafting.Weapons.Enums.WeaponNames;
import com.amorabot.rpgelements.Crafting.Weapons.Enums.WeaponTypes;
import com.amorabot.rpgelements.CustomDataTypes.RPGElementsContainer;

import java.io.Serializable;
import java.util.*;

import static com.amorabot.rpgelements.utils.Utils.log;

public class Weapon implements Serializable, RPGElementsContainer {

    private static final long serialversionUID = 100069L;

    private String name;
//    private String nameColor;
    private Material material;
    private Map<DamageTypes, int[]> baseDmg = new HashMap<>();
    private Map<WeaponModifiers, TierValuePair> modifierTierMap = new HashMap<>();

    public Weapon(WeaponTypes type, List<WeaponModifiers> prefixes, List<WeaponModifiers> suffixes, int ilvl){
        switch (type){
            case AXE:
                generateTiers(AxeMods.PREFIXES, prefixes, AxeMods.SUFFIXES, suffixes, ilvl);
                getName(type);
                defineItemBase(type, ilvl);
//                this.nameColor = "#e8885f"; //#d6431e
                break;
            case SHORTSWORD:
                break;
        }
    }

    public Weapon(String name, Material material, Map<DamageTypes, int[]> baseDmg, Map<WeaponModifiers, TierValuePair> mappedModifiers){
        this.name = name;
        this.material = material;
        this.baseDmg = baseDmg;
        this.modifierTierMap = mappedModifiers;
    }
    private <E extends Enum<E> & TableDataAccess> void generateTiers(E prefixTable, List<WeaponModifiers> prefixes, E suffixTable, List<WeaponModifiers> suffixes, int ilvl){

        generateAffixTiers(prefixTable, prefixes, ilvl);
        generateAffixTiers(suffixTable, suffixes, ilvl);

    }
    private <E extends Enum<E> & TableDataAccess> void generateAffixTiers(E modTable, List<WeaponModifiers> affixes, int ilvl){

        for (WeaponModifiers affix : affixes){
            TierData[] tierData = modTable.getModTiers(affix);

            //--------------SORTING TIERS--------------------
            List<Integer> sortedTierIlvls = modTable.getSortedTierIlvls(affix);
            int highestTier = getMaximumTier(sortedTierIlvls, ilvl);
            int chosenTier = CraftingUtils.getRandomNumber(0, highestTier);

            TierData chosenTierData = tierData[chosenTier];
            int[] chosenValues = chosenTierData.getRandomValue();

            log(affix.toString());
            log(""+chosenTier);
            modifierTierMap.put(affix, new TierValuePair(chosenTier, chosenValues));
        }
    }

    private int getMaximumTier(List<Integer> sortedIlvlList, int ilvl){
        if (sortedIlvlList.get(0) > ilvl){ //Se o ilvl mais baixo para o mod for maior que ilvl do item
            log("Invalid affix at current item level.");
            return -1;
        }
        List<Integer> aux = new ArrayList<>(sortedIlvlList); //aux pode ser alterado

        if (!(aux.contains(ilvl))){ //Se a lista não tiver o item level desejado, adicione
            aux.add(ilvl);
            aux.sort(Comparator.naturalOrder());
        } else { //Senão, já temos o número desejado
            return aux.indexOf(ilvl);
        }

        if (aux.indexOf(ilvl) != 0){ //Se o ilvl não for o primeiro da lista
            return aux.indexOf(ilvl)-1; //retorne o item antes dele (maior tier alcançável)
        }
        return 0; //Se for o ultimo retorne o tier mais basico
    }

    public void getName(WeaponTypes type){ // TODO: relocate some functions to weaponbuilder Class
        WeaponNames names = WeaponNames.valueOf(type.toString());
        this.name = names.getRandomName();
    }
    private void defineItemBase(WeaponTypes type, int ilvl){
        switch (type){
            case AXE:
                mapAxeDmg(ilvl);
                mapAxeBase(ilvl);
                break;
            case SHORTSWORD:
                break;
        }
    }
    // -------------AXE-----------------------
    private void mapAxeDmg(int ilvl){
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
        mapBonusDamages();
    }
    private void mapAxeBase(int ilvl){
        if (ilvl <= 10){
            this.material = Material.WOODEN_AXE;
        } else if (ilvl <= 25) {
            this.material = Material.STONE_AXE;
        } else if (ilvl <= 45) {
            this.material = Material.IRON_AXE;
        } else if (ilvl <= 75) {
            this.material = Material.DIAMOND_AXE;
        } else {
            this.material = Material.GOLDEN_AXE;
        }
    }
    // ---------------------------------------
    private void mapBonusDamages(){
        Set<WeaponModifiers> mods = modifierTierMap.keySet();
        if (mods.contains(WeaponModifiers.ADDED_PHYSICAL)){
            int[] physDmg = baseDmg.get(DamageTypes.PHYSICAL);
            physDmg[0] = physDmg[0] + modifierTierMap.get(WeaponModifiers.ADDED_PHYSICAL).value()[0];
            physDmg[1] = physDmg[1] + modifierTierMap.get(WeaponModifiers.ADDED_PHYSICAL).value()[1];
            baseDmg.put(DamageTypes.PHYSICAL, physDmg);
        }
        if (mods.contains(WeaponModifiers.ADDED_FIRE)){
            baseDmg.put(DamageTypes.FIRE, modifierTierMap.get(WeaponModifiers.ADDED_FIRE).value());
            //CHECK POR PERCENT ELEMENTAL
        }
        if (mods.contains(WeaponModifiers.ADDED_ABYSSAL)){
            baseDmg.put(DamageTypes.ABYSSAL, modifierTierMap.get(WeaponModifiers.ADDED_ABYSSAL).value());
        }
        if (mods.contains(WeaponModifiers.PERCENT_PHYSICAL)){
            int percentPhys = modifierTierMap.get(WeaponModifiers.PERCENT_PHYSICAL).value()[0];
            int[] physDmg = baseDmg.get(DamageTypes.PHYSICAL);
            float phys1 = physDmg[0] * (1 + (float) percentPhys/100);
            float phys2 = physDmg[1] * (1 + (float) percentPhys/100);
            physDmg[0] = (int)phys1;
            physDmg[1] = (int)phys2;
            baseDmg.put(DamageTypes.PHYSICAL, physDmg);
        }
    }
    public Material getItemMaterial(){
        return this.material;
    }
    public String getName(){
        return name;
    }
    public String getNameColor(WeaponTypes type){
//        return nameColor;
        return type.getDefaulNameColor();
    }
    public Map<DamageTypes, int[]> getBaseDmg(){
        return baseDmg;
    }
    public Map<WeaponModifiers, TierValuePair> getModifierTierMap() {
        return modifierTierMap;
    }
}
