package com.amorabot.inscripted.components.Items.Armor;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Tiers;
import com.amorabot.inscripted.components.Items.Files.ResourcesJSONReader;
import com.amorabot.inscripted.components.Items.Interfaces.ItemSubtype;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public enum ArmorTypes implements ItemSubtype {

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

    private final Map<Tiers, String> names = new HashMap<>();
    private final Map<Tiers, Map<ItemTypes, Map<DefenceTypes, Integer>>> baseStats = new HashMap<>();
    public static final int percentHealthVariance = 10;

    ArmorTypes(){
        for (Tiers tier : Tiers.values()){
            this.names.put(tier, loadTierName(tier));
            this.baseStats.put(tier, loadBaseStats(tier));
        }
        loadAffixes();
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
    private void loadAffixes(){
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

    @Override
    public String loadTierName(Tiers tier) {
        String namePath = ArmorTypes.class.getSimpleName() + "." + this + "." + tier + "." + "NAME";
        return Inscripted.getPlugin().getConfig().getString(namePath);
    }
    @Override
    public String getTierName(Tiers tier){
        return this.names.getOrDefault(tier, "INVALID ARMOR");
    }

    public Map<ItemTypes, Map<DefenceTypes, Integer>> loadBaseStats(Tiers tier){
        FileConfiguration config = Inscripted.getPlugin().getConfig();
        Map<ItemTypes, Map<DefenceTypes, Integer>> baseStatData = new HashMap<>();

        for (ItemTypes armorSlot : ItemTypes.values()){
            if (armorSlot.equals(ItemTypes.WEAPON)){continue;}

            //Get to the armor subtype
            String subtypePath = ArmorTypes.class.getSimpleName() + "." + this + ".";

            //Get all the defence types that armor subtype has
            List<String> subtypeDefences = config.getStringList(subtypePath+DefenceTypes.class.getSimpleName());
            List<DefenceTypes> mappedSubtypeDefences = new ArrayList<>();
            for (String defString : subtypeDefences){
                try {
                    DefenceTypes mappedDef = DefenceTypes.valueOf(defString);
                    mappedSubtypeDefences.add(mappedDef);
                } catch (IllegalArgumentException exception){
                    Utils.error("Invalid argument for armor mapping.");
                    return new HashMap<>();
                }
            }

            Map<DefenceTypes, Integer> defMap = new HashMap<>();
            //Once the def.Types are known, lets fetch them individually and build the defMap for that subtype
            for (DefenceTypes def : mappedSubtypeDefences){
                String currDefencePath = subtypePath + "." + tier + "." + def;

                int mappedDefenceValue = config.getInt(currDefencePath);
                //Scale the base value based on the armor piece
                switch (armorSlot){
                    case HELMET -> mappedDefenceValue = (int) (mappedDefenceValue * HELMET_MAIN_STAT_WEIGHT);
                    case CHESTPLATE -> mappedDefenceValue = (int) (mappedDefenceValue * CHESTPLATE_MAIN_STAT_WEIGHT);
                    case LEGGINGS -> mappedDefenceValue = (int) (mappedDefenceValue * LEGGINGS_MAIN_STAT_WEIGHT);
                    case BOOTS -> mappedDefenceValue = (int) (mappedDefenceValue * BOOTS_MAIN_STAT_WEIGHT);
                    default -> {
                        mappedDefenceValue = 0;
                        Utils.error("Cant map base armor stats for " + armorSlot);
                    }
                }
                defMap.put(def, mappedDefenceValue);
            }
            //Adding the health value for that armor piece, for that given tier
            String healthPath = subtypePath + tier + "." + armorSlot;
            defMap.put(DefenceTypes.HEALTH, loadHealthValue(healthPath));

            //Now, the defMap needs to be associated with its armorSlot
            baseStatData.put(armorSlot,defMap);
        }
        return baseStatData;
    }

    public Map<DefenceTypes, Integer> mapBaseStats(Armor armorData){
        FileConfiguration config = Inscripted.getPlugin().getConfig();
        Map<DefenceTypes, Integer> defenceMap = new HashMap<>();

        ItemTypes slot = armorData.getCategory();
        ArmorTypes subtype = this;
        Tiers tier = armorData.getTier();

        String subtypePath = ArmorTypes.class.getSimpleName() + "." + subtype + ".";
        List<String> subtypeDefences = config.getStringList(subtypePath+DefenceTypes.class.getSimpleName());
        List<DefenceTypes> mappedSubtypeDefences = new ArrayList<>();
        for (String defString : subtypeDefences){
            try {
                DefenceTypes mappedDef = DefenceTypes.valueOf(defString);
                mappedSubtypeDefences.add(mappedDef);
            } catch (IllegalArgumentException exception){
                Utils.error("Invalid argument for armor mapping.");
                return defenceMap;
            }
        }

        for (DefenceTypes def : mappedSubtypeDefences){
            int ilvl = armorData.getIlvl();
            int tierMaxLevel = tier.getMaxLevel();
            Optional<Tiers> prevTier = tier.getPreviousTier();

            if (ilvl <= 0){
                defenceMap.put(def, 0);
                continue;
            }

            int mappedDefenceValue;

            if (prevTier.isPresent()){
                Tiers previousTier = prevTier.get();
                int prevTierMaxLvl = previousTier.getMaxLevel();

                int v1 = ilvl - prevTierMaxLvl;
                int v2 = tierMaxLevel - prevTierMaxLvl;
                float t = ((float) (v1)) /v2;

                int currMaxDef = baseStats.get(tier).get(slot).get(def);
                int prevMaxDef = baseStats.get(previousTier).get(slot).get(def);

                mappedDefenceValue = ( int ) Utils.getParametricValue(prevMaxDef, currMaxDef, t);
            } else {//Means its a T1 (has no previous tier)
                float t = ((float)(ilvl)) / tierMaxLevel ;

                int currMaxDef = baseStats.get(tier).get(slot).get(def);

                mappedDefenceValue = ( int ) Utils.getParametricValue(0, currMaxDef, t);
            }
            defenceMap.put(def, mappedDefenceValue);
        }
        defenceMap.put(DefenceTypes.HEALTH, armorData.getBaseHealth());
        return defenceMap;
    }
    public int mapHealthValue(Armor armorData){
        Map<DefenceTypes, Integer> cacheDefMap = baseStats.get(armorData.getTier()).get(armorData.getCategory());
        return cacheDefMap.get(DefenceTypes.HEALTH);
    }


    private int loadHealthValue(String path){
        return Inscripted.getPlugin().getConfig().getInt(path);
    }
}