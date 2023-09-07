package com.amorabot.rpgelements.components.Items.Armor;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.rpgelements.components.Items.Files.ModifiersJSON;
import com.amorabot.rpgelements.components.Items.Interfaces.AffixTableSelector;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.Material;

import java.util.Map;

public enum ArmorTypes implements AffixTableSelector {

    HEAVY_PLATING(){
        @Override
        public void mapBaseStats(int ilvl, ItemTypes armorBase, Map<DefenceTypes, Integer> defences) {
            float maxItemLevel = 120;
            switch (armorBase){
                case HELMET -> {
                    int maximumHelmetArmor = 400;
                    putArmor(ilvl, maxItemLevel, maximumHelmetArmor, defences);
                }
                case CHESTPLATE -> {
                    int maximumChestplateArmor = 800;
                    putArmor(ilvl, maxItemLevel, maximumChestplateArmor, defences);
                }
                case LEGGINGS -> {
                    int maximumLeggingsArmor = 600;
                    putArmor(ilvl, maxItemLevel, maximumLeggingsArmor, defences);
                }
                case BOOTS -> {
                    int maximumBootsArmor = 300;
                    putArmor(ilvl, maxItemLevel, maximumBootsArmor, defences);
                }
                default -> Utils.error("Invalid argument for armor mapping." + armorBase + " is not a armor type.");
            }
        }
    },
    CARVED_PLATING() {
        @Override
        public void mapBaseStats(int ilvl, ItemTypes armorBase, Map<DefenceTypes, Integer> defences) {
            float maxItemLevel = 120;
            switch (armorBase){
                case HELMET -> {
                    int maximumHelmetArmor = 250;
                    int maxDodge = 1;
                    putArmor(ilvl, maxItemLevel, maximumHelmetArmor, defences);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case CHESTPLATE -> {
                    int maximumChestplateArmor = 600;
                    int maxDodge = 4;
                    putArmor(ilvl, maxItemLevel, maximumChestplateArmor, defences);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case LEGGINGS -> {
                    int maximumLeggingsArmor = 400;
                    int maxDodge = 3;
                    putArmor(ilvl, maxItemLevel, maximumLeggingsArmor, defences);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case BOOTS -> {
                    int maximumBootsArmor = 150;
                    int maxDodge = 2;
                    putArmor(ilvl, maxItemLevel, maximumBootsArmor, defences);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                default -> Utils.error("Invalid argument for armor mapping." + armorBase + " is not a armor type.");
            }
        }
    },
    LIGHT_CLOTH() {
        @Override
        public void mapBaseStats(int ilvl, ItemTypes armorBase, Map<DefenceTypes, Integer> defences) {
            float maxItemLevel = 120;
            switch (armorBase){
                case HELMET -> {
                    int maxDodge = 2;
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case CHESTPLATE -> {
                    int maxDodge = 8;
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case LEGGINGS -> {
                    int maxDodge = 6;
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case BOOTS -> {
                    int maxDodge = 4;
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                default -> Utils.error("Invalid argument for armor mapping." + armorBase + " is not a armor type.");
            }
        }
    },
    RUNIC_LEATHER() {
        @Override
        public void mapBaseStats(int ilvl, ItemTypes armorBase, Map<DefenceTypes, Integer> defences) {
            float maxItemLevel = 120;
            switch (armorBase){
                case HELMET -> {
                    int maximumWard = 50;
                    int maxDodge = 1;
                    putWard(ilvl, maxItemLevel, maximumWard, defences);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case CHESTPLATE -> {
                    int maximumWard = 120;
                    int maxDodge = 4;
                    putWard(ilvl, maxItemLevel, maximumWard, defences);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case LEGGINGS -> {
                    int maximumWard = 90;
                    int maxDodge = 3;
                    putWard(ilvl, maxItemLevel, maximumWard, defences);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case BOOTS -> {
                    int maximumWard = 40;
                    int maxDodge = 2;
                    putWard(ilvl, maxItemLevel, maximumWard, defences);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                default -> Utils.error("Invalid argument for armor mapping." + armorBase + " is not a armor type.");
            }
        }
    },
    ENCHANTED_SILK() {
        @Override
        public void mapBaseStats(int ilvl, ItemTypes armorBase, Map<DefenceTypes, Integer> defences) {
            float maxItemLevel = 120;
            switch (armorBase){
                case HELMET -> {
                    int maxWard = 90;
                    putWard(ilvl, maxItemLevel, maxWard, defences);
                }
                case CHESTPLATE -> {
                    int maxWard = 200;
                    putWard(ilvl, maxItemLevel, maxWard, defences);
                }
                case LEGGINGS -> {
                    int maxWard = 150;
                    putWard(ilvl, maxItemLevel, maxWard, defences);
                }
                case BOOTS -> {
                    int maxWard = 70;
                    putWard(ilvl, maxItemLevel, maxWard, defences);
                }
                default -> Utils.error("Invalid argument for armor mapping." + armorBase + " is not a armor type.");
            }
        }
    },
    RUNIC_STEEL() {
        @Override
        public void mapBaseStats(int ilvl, ItemTypes armorBase, Map<DefenceTypes, Integer> defences) {
            float maxItemLevel = 120;
            switch (armorBase){
                case HELMET -> {
                    int maximumHelmetArmor = 250;
                    int maximumWard = 50;
                    putArmor(ilvl, maxItemLevel, maximumHelmetArmor, defences);
                    putWard(ilvl, maxItemLevel, maximumWard, defences);
                }
                case CHESTPLATE -> {
                    int maximumChestplateArmor = 600;
                    int maximumWard = 120;
                    putArmor(ilvl, maxItemLevel, maximumChestplateArmor, defences);
                    putWard(ilvl, maxItemLevel, maximumWard, defences);
                }
                case LEGGINGS -> {
                    int maximumLeggingsArmor = 400;
                    int maximumWard = 90;
                    putArmor(ilvl, maxItemLevel, maximumLeggingsArmor, defences);
                    putWard(ilvl, maxItemLevel, maximumWard, defences);
                }
                case BOOTS -> {
                    int maximumBootsArmor = 150;
                    int maximumWard = 40;
                    putArmor(ilvl, maxItemLevel, maximumBootsArmor, defences);
                    putWard(ilvl, maxItemLevel, maximumWard, defences);
                }
                default -> Utils.error("Invalid argument for armor mapping." + armorBase + " is not a armor type.");
            }
        }
    };

    private Map<String, Map<Integer, int[]>> basicHelmetPrefixes;
    private Map<String, Map<Integer, int[]>> basicHelmetSuffixes;

    private Map<String, Map<Integer, int[]>> basicChestlatePrefixes;
    private Map<String, Map<Integer, int[]>> basicChesplateSuffixes;

    private Map<String, Map<Integer, int[]>> basicLeggingsPrefixes;
    private Map<String, Map<Integer, int[]>> basicLeggingsSuffixes;

    private Map<String, Map<Integer, int[]>> basicBootsPrefixes;
    private Map<String, Map<Integer, int[]>> basicBootsSuffixes;

    ArmorTypes(){
        loadAllAffixes();
    }
    public abstract void mapBaseStats(int ilvl, ItemTypes armorBase, Map<DefenceTypes, Integer> defences);
    private Integer linearScaleStat(int givenItemLevel, float maxItemLevel, int maximumBaseStat){
        float itemLevelPercentile = givenItemLevel / maxItemLevel; // normalization
        return (int) (maximumBaseStat * itemLevelPercentile);
    }
//    DecimalFormat decimalFormat = new DecimalFormat("#.##");
//    float twoDigitsF = Float.valueOf(decimalFormat.format(f));
    public void putArmor(int givenItemLevel, float maxItemLevel, int maximumBaseStat, Map<DefenceTypes, Integer> defences){
        if (givenItemLevel > 0 && givenItemLevel <= maxItemLevel){
            int armorValue = linearScaleStat(givenItemLevel, maxItemLevel, maximumBaseStat);
            if (armorValue == 0){ return; }
            defences.put(DefenceTypes.ARMOR, armorValue);
        } else if (givenItemLevel > maxItemLevel){
            defences.put(DefenceTypes.ARMOR, maximumBaseStat);
        } else {
            defences.put(DefenceTypes.ARMOR, 0);
        }
    }
    public void putWard(int givenItemLevel, float maxItemLevel, int maximumBaseStat, Map<DefenceTypes, Integer> defences){
        if (givenItemLevel > 0 && givenItemLevel <= maxItemLevel){
            int wardValue = linearScaleStat(givenItemLevel, maxItemLevel, maximumBaseStat);
            if (wardValue == 0){ return; }
            defences.put(DefenceTypes.WARD, wardValue);
        } else if (givenItemLevel > maxItemLevel){
            defences.put(DefenceTypes.WARD, maximumBaseStat);
        } else {
            defences.put(DefenceTypes.WARD, 0);
        }
    }
    public void putDodge(int givenItemLevel, float maxItemLevel, int maximumBaseStat, Map<DefenceTypes, Integer> defences){ //Will return -1 if invalid maxIlvl is given
        //Dodge will cap below the max item level given
        int softCap = 20;
        if (maxItemLevel <= softCap){
            defences.put(DefenceTypes.DODGE, -1);
            return;
        }
        if (givenItemLevel > 0 && givenItemLevel <= maxItemLevel){
            int dodgeValue = linearScaleStat(givenItemLevel,maxItemLevel-softCap, maximumBaseStat);
            if (dodgeValue == 0){ return; } // If its truncated to 0 during int conversion, don't add
            defences.put(DefenceTypes.DODGE, dodgeValue);
        } else if (givenItemLevel > maxItemLevel-softCap){
            defences.put(DefenceTypes.DODGE, maximumBaseStat);
        } else {
            defences.put(DefenceTypes.DODGE, 0);
        }
    }
    public Material mapArmorBase(int ilvl, ItemTypes armorBase){
        switch (armorBase){
            case HELMET -> {
                return mapHelmetMaterial(ilvl);
            }
            case CHESTPLATE -> {
                return mapChestplateMaterial(ilvl);
            }
            case LEGGINGS -> {
                return mapLeggingsMaterial(ilvl);
            }
            case BOOTS -> {
                return mapBootsMaterial(ilvl);
            }
            default -> {
                Utils.error("Invalid argument for armor mapping." + armorBase + " is not a armor type.");
                return null;
            }
        }
    }
    private Material mapHelmetMaterial(int ilvl){
        if (ilvl <= 10){
            return Material.LEATHER_HELMET;
        } else if (ilvl <= 25) {
            return Material.CHAINMAIL_HELMET;
        } else if (ilvl <= 45) {
            return Material.IRON_HELMET;
        } else if (ilvl <= 75) {
            return Material.DIAMOND_HELMET;
        } else {
            return Material.GOLDEN_HELMET;
        }
    }
    private Material mapChestplateMaterial(int ilvl){
        if (ilvl <= 10){
            return Material.LEATHER_CHESTPLATE;
        } else if (ilvl <= 25) {
            return Material.CHAINMAIL_CHESTPLATE;
        } else if (ilvl <= 45) {
            return Material.IRON_CHESTPLATE;
        } else if (ilvl <= 75) {
            return Material.DIAMOND_CHESTPLATE;
        } else {
            return Material.GOLDEN_CHESTPLATE;
        }
    }
    private Material mapLeggingsMaterial(int ilvl){
        if (ilvl <= 10){
            return Material.LEATHER_LEGGINGS;
        } else if (ilvl <= 25) {
            return Material.CHAINMAIL_LEGGINGS;
        } else if (ilvl <= 45) {
            return Material.IRON_LEGGINGS;
        } else if (ilvl <= 75) {
            return Material.DIAMOND_LEGGINGS;
        } else {
            return Material.GOLDEN_LEGGINGS;
        }
    }
    private Material mapBootsMaterial(int ilvl){
        if (ilvl <= 10){
            return Material.LEATHER_BOOTS;
        } else if (ilvl <= 25) {
            return Material.CHAINMAIL_BOOTS;
        } else if (ilvl <= 45) {
            return Material.IRON_BOOTS;
        } else if (ilvl <= 75) {
            return Material.DIAMOND_BOOTS;
        } else {
            return Material.GOLDEN_BOOTS;
        }
    }
    private void loadAllAffixes(){
        loadBasicAffixes();
    }
    private void loadBasicAffixes(){
        Map<String, Map<String, Map<String, Map<String, Map<Integer, int[]>>>>> modifiersJSON = ModifiersJSON.getBasicModifiers();
        //The "Entire set" of a armor specialization is loaded at once and accessed later via getPiece() methods.
//        this.basicHelmetPrefixes = getAffixTable(modifiersJSON, ItemTypes.HELMET, Affix.PREFIX);
//        this.basicHelmetSuffixes = getAffixTable(modifiersJSON, ItemTypes.HELMET, Affix.SUFFIX);
//
//        this.basicChestlatePrefixes = getAffixTable(modifiersJSON, ItemTypes.CHESTPLATE, Affix.PREFIX);
//        this.basicChesplateSuffixes = getAffixTable(modifiersJSON, ItemTypes.CHESTPLATE, Affix.SUFFIX);
//
//        this.basicLeggingsPrefixes = getAffixTable(modifiersJSON, ItemTypes.LEGGINGS, Affix.PREFIX);
//        this.basicLeggingsSuffixes = getAffixTable(modifiersJSON, ItemTypes.LEGGINGS, Affix.SUFFIX);
//
//        this.basicBootsPrefixes = getAffixTable(modifiersJSON, ItemTypes.BOOTS, Affix.PREFIX);
//        this.basicBootsSuffixes = getAffixTable(modifiersJSON, ItemTypes.BOOTS, Affix.SUFFIX);
        Utils.log("Modifiers loaded successfully! (" + this + ")");
    }
    public Map<String, Map<Integer, int[]>> getBasicHelmetPrefixes(){
        return this.basicHelmetPrefixes;
    }
    public Map<String, Map<Integer, int[]>> getBasicHelmetSuffixes(){
        return this.basicHelmetSuffixes;
    }

    public Map<String, Map<Integer, int[]>> getBasicChestlatePrefixes() {
        return basicChestlatePrefixes;
    }
    public Map<String, Map<Integer, int[]>> getBasicChesplateSuffixes() {
        return basicChesplateSuffixes;
    }

    public Map<String, Map<Integer, int[]>> getBasicLeggingsPrefixes() {
        return basicLeggingsPrefixes;
    }
    public Map<String, Map<Integer, int[]>> getBasicLeggingsSuffixes() {
        return basicLeggingsSuffixes;
    }

    public Map<String, Map<Integer, int[]>> getBasicBootsPrefixes() {
        return basicBootsPrefixes;
    }
    public Map<String, Map<Integer, int[]>> getBasicBootsSuffixes() {
        return basicBootsSuffixes;
    }
}
