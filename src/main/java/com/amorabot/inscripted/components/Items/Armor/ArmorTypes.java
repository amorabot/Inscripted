package com.amorabot.inscripted.components.Items.Armor;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Tiers;
import com.amorabot.inscripted.components.Items.Files.ResourcesJSONReader;
import com.amorabot.inscripted.components.Items.Interfaces.AffixTableSelector;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public enum ArmorTypes implements AffixTableSelector {

    HEAVY_PLATING,
    CARVED_PLATING,
    LIGHT_CLOTH,
    RUNIC_LEATHER,
    ENCHANTED_SILK,
    RUNIC_STEEL;

    private Map<String, Map<String, Map<Integer, Integer>>> helmetAffixes;
    private Map<String, Map<String, Map<Integer, Integer>>> chestplateAffixes;
    private Map<String, Map<String, Map<Integer, Integer>>> leggingsAffixes;
    private Map<String, Map<String, Map<Integer, Integer>>> bootsAffixes;


    private static final double HELMET_MAIN_STAT_WEIGHT = 0.8;
    private static final double CHESTPLATE_MAIN_STAT_WEIGHT = 1.3;
    private static final double LEGGINGS_MAIN_STAT_WEIGHT = 1.2;
    private static final double BOOTS_MAIN_STAT_WEIGHT = 0.7;

    //TODO: Make a map: Tier -> Name
    private final String tier1Name;
    private final String tier2Name;
    private final String tier3Name;
    private final String tier4Name;
    private final String tier5Name;

    ArmorTypes(){
        this.tier1Name = loadTierName(Tiers.T1);
        this.tier2Name = loadTierName(Tiers.T2);
        this.tier3Name = loadTierName(Tiers.T3);
        this.tier4Name = loadTierName(Tiers.T4);
        this.tier5Name = loadTierName(Tiers.T5);

        loadAllAffixes();
    }
    public Material mapArmorBase(Tiers tier, ItemTypes armorBase){
        switch (armorBase){
            case HELMET -> {
                return mapHelmetMaterial(tier);
            }
            case CHESTPLATE -> {
                return mapChestplateMaterial(tier);
            }
            case LEGGINGS -> {
                return mapLeggingsMaterial(tier);
            }
            case BOOTS -> {
                return mapBootsMaterial(tier);
            }
            default -> {
                Utils.error("Invalid argument for armor mapping." + armorBase + " is not a armor type.");
                return null;
            }
        }
    }
    private Material mapHelmetMaterial(Tiers tier){
        switch (tier){
            case T1 -> {
                return Material.LEATHER_HELMET;
            }
            case T2 -> {
                return Material.CHAINMAIL_HELMET;
            }
            case T3 -> {
                return Material.IRON_HELMET;
            }
            case T4 -> {
                return Material.DIAMOND_HELMET;
            }
            case T5 -> {
                return Material.GOLDEN_HELMET;
            }
            default -> {
                return Material.NETHERITE_HELMET;
            }
        }
    }
    private Material mapChestplateMaterial(Tiers tier){
        switch (tier){
            case T1 -> {
                return Material.LEATHER_CHESTPLATE;
            }
            case T2 -> {
                return Material.CHAINMAIL_CHESTPLATE;
            }
            case T3 -> {
                return Material.IRON_CHESTPLATE;
            }
            case T4 -> {
                return Material.DIAMOND_CHESTPLATE;
            }
            case T5 -> {
                return Material.GOLDEN_CHESTPLATE;
            }
            default -> {
                return Material.NETHERITE_CHESTPLATE;
            }
        }
    }
    private Material mapLeggingsMaterial(Tiers tier){
        switch (tier){
            case T1 -> {
                return Material.LEATHER_LEGGINGS;
            }
            case T2 -> {
                return Material.CHAINMAIL_LEGGINGS;
            }
            case T3 -> {
                return Material.IRON_LEGGINGS;
            }
            case T4 -> {
                return Material.DIAMOND_LEGGINGS;
            }
            case T5 -> {
                return Material.GOLDEN_LEGGINGS;
            }
            default -> {
                return Material.NETHERITE_LEGGINGS;
            }
        }
    }
    private Material mapBootsMaterial(Tiers tier){
        switch (tier){
            case T1 -> {
                return Material.LEATHER_BOOTS;
            }
            case T2 -> {
                return Material.CHAINMAIL_BOOTS;
            }
            case T3 -> {
                return Material.IRON_BOOTS;
            }
            case T4 -> {
                return Material.DIAMOND_BOOTS;
            }
            case T5 -> {
                return Material.GOLDEN_BOOTS;
            }
            default -> {
                return Material.NETHERITE_BOOTS;
            }
        }
    }
    private void loadAllAffixes(){
        loadBasicAffixes();
    }
    private void loadBasicAffixes(){
        this.helmetAffixes = ResourcesJSONReader.getModifierTableFor(ItemTypes.HELMET, this);
        this.chestplateAffixes = ResourcesJSONReader.getModifierTableFor(ItemTypes.CHESTPLATE, this);
        this.leggingsAffixes = ResourcesJSONReader.getModifierTableFor(ItemTypes.LEGGINGS, this);
        this.bootsAffixes = ResourcesJSONReader.getModifierTableFor(ItemTypes.BOOTS, this);

        Utils.log("Modifiers loaded successfully! (" + this + ")");
    }


    public Map<String, Map<String, Map<Integer, Integer>>> getHelmetAffixes(){
        return this.helmetAffixes;
    }
    public Map<String, Map<String, Map<Integer, Integer>>> getChestplateAffixes(){
        return this.chestplateAffixes;
    }
    public Map<String, Map<String, Map<Integer, Integer>>> getLeggingsAffixes(){
        return this.leggingsAffixes;
    }
    public Map<String, Map<String, Map<Integer, Integer>>> getBootsAffixes(){
        return this.bootsAffixes;
    }

    public String loadTierName(Tiers tier){
        String namePath = ArmorTypes.class.getSimpleName() + "." + this + "." + tier + "." + "NAME";
        return Inscripted.getPlugin().getConfig().getString(namePath);
    }

    public String getTierName(Tiers tier){
        switch (tier){
            case T1 -> {
                return tier1Name;
            }
            case T2 -> {
                return tier2Name;
            }
            case T3 -> {
                return tier3Name;
            }
            case T4 -> {
                return tier4Name;
            }
            case T5 -> {
                return tier5Name;
            }
        }
        return "INVALID NAME :(";
    }

/*
This functions maps all the defences for a item, given its item level, category and subtype
Since the life value can be retrieved from this call, the return value reflects final health value chosen
for this item. The map-stats and map-health functions were previously called one after the other, always together.
This way, one entire routine can be skipped altogether.

@Params
    armorData - a Armor object that has its internal data already mapped (at least the ones used in this routine)
    returns: the health value associated with armorData parameters
*/
    public int mapBaseStats(Armor armorData){
        //This routine is called everytime a armor generated -> TODO: Rework with a cache solution
        FileConfiguration config = Inscripted.getPlugin().getConfig();
        Map<DefenceTypes, Integer> defenceMap = armorData.getDefencesMap();

        ItemTypes slot = armorData.getCategory();
        ArmorTypes subtype = this;
        Tiers tier = armorData.getTier();

        String subtypePath = ArmorTypes.class.getSimpleName() + "." + subtype + ".";

        List<String> subtypeDefences = config.getStringList(subtypePath+DefenceTypes.class.getSimpleName());
        List<DefenceTypes> mappedSubtypeDefences = new ArrayList<>();
        for (String defString : subtypeDefences){
            Utils.log(defString);
            try {
                DefenceTypes mappedDef = DefenceTypes.valueOf(defString);
                mappedSubtypeDefences.add(mappedDef);
            } catch (IllegalArgumentException exception){
                Utils.error("Invalid argument for armor mapping.");
                return 0;
            }
        }

        for (DefenceTypes def : mappedSubtypeDefences){
            String currDefencePath = subtypePath + "." + tier + "." + def;

            int ilvl = armorData.getIlvl();
            int tierMaxLevel = tier.getMaxLevel();
            Optional<Tiers> prevTier = tier.getPreviousTier();

            if (ilvl <= 0){
                defenceMap.put(def, 0);
                return 1;
            }

            int mappedDefenceValue;

            if (prevTier.isPresent()){
                Tiers previousTier = prevTier.get();
                int prevTierMaxLvl = previousTier.getMaxLevel();

                int v1 = ilvl - prevTierMaxLvl;
                int v2 = tierMaxLevel - prevTierMaxLvl;
                float t = ((float) (v1)) /v2;

                String prevTierDefencePath = subtypePath + "." + previousTier + "." + def;

                int currMaxDef = config.getInt(currDefencePath);
                int prevMaxDef = config.getInt(prevTierDefencePath);

                mappedDefenceValue = ( int ) Utils.getParametricValue(prevMaxDef, currMaxDef, t);
            } else {//Means its a T1 (has no previous tier)
                float t = ((float)(ilvl)) / tierMaxLevel ;

                int currMaxDef = config.getInt(currDefencePath);

                mappedDefenceValue = ( int ) Utils.getParametricValue(0, currMaxDef, t);
            }

            //Scale the base value based on the armor piece
            switch (slot){
                case HELMET -> mappedDefenceValue = (int) (mappedDefenceValue * HELMET_MAIN_STAT_WEIGHT);
                case CHESTPLATE -> mappedDefenceValue = (int) (mappedDefenceValue * CHESTPLATE_MAIN_STAT_WEIGHT);
                case LEGGINGS -> mappedDefenceValue = (int) (mappedDefenceValue * LEGGINGS_MAIN_STAT_WEIGHT);
                case BOOTS -> mappedDefenceValue = (int) (mappedDefenceValue * BOOTS_MAIN_STAT_WEIGHT);
                default -> {
                    mappedDefenceValue = 0;
                    Utils.error("Cant map base armor stats for " + slot);
                }
            }

            if (mappedDefenceValue != 0){
                //Actually putting the mapped value in the item's defence map
                defenceMap.put(def, mappedDefenceValue);
            }
        }

        String healthPath = subtypePath + tier + "." + slot;
        return getMappedHealth(healthPath);
    }
    private int getMappedHealth(String path){
        int baseHealth = Inscripted.getPlugin().getConfig().getInt(path);
        float hpVariance = 0.1F; //10% more or less
        int HPOffset = (int) (Utils.getRandomOffset()*(hpVariance*baseHealth));
        return baseHealth + HPOffset;
    }
}