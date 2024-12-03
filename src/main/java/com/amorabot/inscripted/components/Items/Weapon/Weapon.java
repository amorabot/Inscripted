package com.amorabot.inscripted.components.Items.Weapon;

import com.amorabot.inscripted.components.Items.Abstract.ItemCategory;
import com.amorabot.inscripted.components.Items.modifiers.Inscription;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;
import com.amorabot.inscripted.components.Items.modifiers.data.HybridInscriptionData;
import com.amorabot.inscripted.components.Items.modifiers.data.InscriptionData;
import com.amorabot.inscripted.components.Items.modifiers.data.ModifierData;
import com.amorabot.inscripted.components.Items.modifiers.data.StatDefinition;
import com.amorabot.inscripted.components.Items.relic.RelicWeaponDAO;
import com.amorabot.inscripted.components.Player.archetypes.Archetypes;
import com.amorabot.inscripted.components.Player.stats.PlayerStats;
import com.amorabot.inscripted.events.FunctionalItemAccessInterface;
import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.*;
import com.amorabot.inscripted.utils.CraftingUtils;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Weapon extends Item implements ItemCategory {
    private final WeaponTypes type;
    private final int[] baseDamage;
    @Getter
    private final WeaponAttackSpeeds atkSpeed;
    @Getter
    private final RangeCategory range;
    private final int percentDamageVariance;

    public Weapon(int ilvl, WeaponTypes type, ItemRarities rarity, boolean identified, boolean corrupted){
        super(ilvl, rarity, identified, corrupted, ItemTypes.WEAPON);
        this.type = type;
        setup();
        baseDamage = type.mapBaseDamage(getTier());
        this.percentDamageVariance = CraftingUtils.getRandomNumber(-WeaponTypes.weaponDamageVariance, WeaponTypes.weaponDamageVariance);
        this.atkSpeed = getSubtype().getBaseAttackSpeed();
        this.range = getSubtype().getRange();
    }
    public Weapon(int ilvl, ItemRarities rarity, boolean identified, boolean corrupted){ //Random generic weapon constructor
        super(ilvl, rarity, identified, corrupted, ItemTypes.WEAPON);
        //Do the rest...
        WeaponTypes[] weapons = WeaponTypes.values();
        int weaponIndex = CraftingUtils.getRandomNumber(0, weapons.length-1);
        this.type = weapons[weaponIndex];
        setup();
        baseDamage = type.mapBaseDamage(getTier());
        this.percentDamageVariance = CraftingUtils.getRandomNumber(-WeaponTypes.weaponDamageVariance, WeaponTypes.weaponDamageVariance);
        this.atkSpeed = getSubtype().getBaseAttackSpeed();
        this.range = getSubtype().getRange();
    }
    public Weapon(RelicWeaponDAO relicWeaponData,List<Inscription> inscriptions){ // Relic constructor
        super(relicWeaponData.genericData().itemLevel(), ItemRarities.RELIC, true, false, ItemTypes.WEAPON);
        this.type = relicWeaponData.type();
        this.name = relicWeaponData.genericData().name();
        setTier(Tiers.mapItemLevel(getIlvl()));
        getInscriptionList().addAll(inscriptions);
        setImplicit(Archetypes.mapImplicitFor(getSubtype(), getTier(), isCorrupted()));
        this.baseDamage = relicWeaponData.baseDmg();
        this.percentDamageVariance = 0;
        this.atkSpeed = relicWeaponData.atkSpeed();
        this.range = getSubtype().getRange();
        mapBase();
    }

    @Override
    protected void setup() {
        setTier(Tiers.mapItemLevel(getIlvl()));
        setImplicit(Archetypes.mapImplicitFor(getSubtype(), getTier(), isCorrupted()));
        this.name = getSubtype().getTierName(getTier());
        mapBase();
    }

    @Override
    protected void mapBase(){
        vanillaMaterial = type.mapWeaponBase();
    }

    //-------------------------------------------------------------------------
    public Map<DamageTypes, int[]> getLocalDamage(){ //Once a weapon is created, the damage map needs to be updated to contain any possible new damages
        Map<DamageTypes, int[]> baseDmg = new HashMap<>();
        Map<DamageTypes, Integer> incPecentages = new HashMap<>();

        int[] basePhys = getBaseDamage();
        baseDmg.put(DamageTypes.PHYSICAL, basePhys);

        int qualityIncrease = 5*getQuality();
        if (qualityIncrease>0){
            for (DamageTypes dmg : DamageTypes.values()){
                incPecentages.put(dmg, qualityIncrease);
            }
        }

        for (Inscription mod : getInscriptionList()){
            //Local mod mapping
            Utils.error("Local mod mapping for " + mod.getInscription());
            InscriptionID weaponModifier = mod.getInscription();
            if (weaponModifier.isGlobal()){continue;}
            ModifierData modData = weaponModifier.getData();
            if (modData instanceof InscriptionData inscriptionData){
                int[] mappedValues = mod.getMappedFinalValue();
                StatDefinition statDef = inscriptionData.getDefinitionData();
                mapLocalMods(baseDmg, incPecentages, statDef, mappedValues);
            } else if (modData instanceof HybridInscriptionData hybridInscriptionData) {
                StatDefinition[] defs = hybridInscriptionData.getStatDefinitions();
                for (int d = 0; d < defs.length; d++){
                    int[] currentMappedVal = mod.getMappedFinalValue(d);
                    mapLocalMods(baseDmg, incPecentages, defs[d], currentMappedVal);
                }
            } else {
                continue;
            }
        }

        for (DamageTypes dmg : DamageTypes.values()){
            int[] currFlatDMG = baseDmg.getOrDefault(dmg,new int[2]).clone();
            int currIncValue = incPecentages.getOrDefault(dmg,0);
            int[] updatedFlatDMGs = Arrays.stream(currFlatDMG).map( currDMG -> (int) Utils.applyPercentageTo(currDMG, currIncValue)).toArray();
            if (Arrays.equals(updatedFlatDMGs, new int[2])){continue;}
            baseDmg.put(dmg, updatedFlatDMGs);
        }

        return baseDmg;
    }
    private void mapLocalMods(Map<DamageTypes, int[]> flatDMG, Map<DamageTypes, Integer> percentages, StatDefinition statDef, int[] mappedValues){
        PlayerStats targetStat = statDef.stat();
        ValueTypes valueType = statDef.valueType();
        switch (targetStat){
            case PHYSICAL_DAMAGE -> redirectValue(valueType, DamageTypes.PHYSICAL, flatDMG, percentages, mappedValues);
            case FIRE_DAMAGE -> redirectValue(valueType, DamageTypes.FIRE, flatDMG, percentages, mappedValues);
            case COLD_DAMAGE -> redirectValue(valueType, DamageTypes.COLD, flatDMG, percentages, mappedValues);
            case LIGHTNING_DAMAGE -> redirectValue(valueType, DamageTypes.LIGHTNING, flatDMG, percentages, mappedValues);
            case ABYSSAL_DAMAGE -> redirectValue(valueType, DamageTypes.ABYSSAL, flatDMG, percentages, mappedValues);
            case ELEMENTAL_DAMAGE -> {
                redirectValue(valueType, DamageTypes.FIRE, flatDMG, percentages, mappedValues);
                redirectValue(valueType, DamageTypes.LIGHTNING, flatDMG, percentages, mappedValues);
                redirectValue(valueType, DamageTypes.COLD, flatDMG, percentages, mappedValues);
            }
        }
    }
    private void redirectValue
            (
            ValueTypes valueType,
            DamageTypes damageType,
            Map<DamageTypes, int[]> flatDMG,
            Map<DamageTypes, Integer> percentages,
            int[] mappedValues
            )
    {
        switch (valueType){
            case FLAT -> {
                if (!flatDMG.containsKey(damageType)){
                    flatDMG.put(damageType, mappedValues);
                    return;
                }
                int[] selectedFlatDMG = flatDMG.get(damageType).clone();
                flatDMG.put(damageType,Utils.vectorSum(mappedValues, selectedFlatDMG));
            }
            case INCREASED -> {
                if (!percentages.containsKey(damageType)){
                    percentages.put(damageType, mappedValues[0]);
                    return;
                }
                int selectedPercentage = percentages.get(damageType);
                percentages.put(damageType, (selectedPercentage + mappedValues[0]));
            }
        }
    }
    public WeaponTypes getSubtype() {
        return type;
    }
    //-------------------------------------------------------------------------
    @Override
    public ItemStack getItemForm() {
        ItemStack weaponItem = new ItemStack(this.vanillaMaterial);
        imprint(weaponItem,type);

        serializeContainers(this, weaponItem);

        setWeaponModel(weaponItem);
        return weaponItem;
    }
    @Override
    public void serializeContainers(Item itemData, ItemStack item) {
        FunctionalItemAccessInterface.serializeItem(item, this);
    }

    private void setWeaponModel(ItemStack item){
        int modelID = getSubtype().mapWeaponTierModel(getTier());
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setCustomModelData(modelID);
        item.setItemMeta(itemMeta);
    }

    public int[] getBaseDamage() {
        int[] basePhys = baseDamage.clone();
        return Arrays.stream(basePhys).map(currValue -> (int) ((1+((float)percentDamageVariance/100))*currValue)).toArray();
    }
}
