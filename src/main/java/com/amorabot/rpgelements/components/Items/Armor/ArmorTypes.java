package com.amorabot.rpgelements.components.Items.Armor;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.Affix;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.Tiers;
import com.amorabot.rpgelements.components.Items.Files.ModifiersJSON;
import com.amorabot.rpgelements.components.Items.Interfaces.AffixTableSelector;
import com.amorabot.rpgelements.utils.CraftingUtils;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public enum ArmorTypes implements AffixTableSelector {

    HEAVY_PLATING(
            List.of("Thickwood", "Compound leather"),
            List.of("Compound chainmail", "Heavy mail"),
            List.of("Steel War-Vest", "Heavy plate"),
            List.of("Heavy crystal", "Reinforced opal"),
            List.of("Juggernault", "Warmonger", "Heavy runic-gold")
    ){
        @Override
        public void mapBaseDefences(int ilvl, ItemTypes armorBase, Map<DefenceTypes, Integer> defences) {
            float maxItemLevel = 120;
            //Base item defences definition
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
        @Override
        public int mapBaseHealth(Tiers tier, ItemTypes armorBase) {
            switch (armorBase){
                case HELMET -> {
                    //Helmet base health definition
                    return defineTierBaseHealth(tier
                            , 50,  10
                            , 150, 30
                            , 400, 100
                            , 900, 120
                            , 1500,200);
                }
                case CHESTPLATE -> {
                    //Chestplate base health definition
                    return defineTierBaseHealth(tier
                            , 80,  15
                            , 190, 40
                            , 450, 150
                            , 1100,220
                            , 1800,260);
                }
                case LEGGINGS -> {
                    //Leggings base health definition
                    return defineTierBaseHealth(tier
                            , 70,  15
                            , 200, 40
                            , 500, 120
                            , 1000, 140
                            , 1700,240);
                }
                case BOOTS -> {
                    //Boots base health definition
                    return defineTierBaseHealth(tier
                            , 60,  10
                            , 150, 30
                            , 400, 100
                            , 900, 120
                            , 1600,200);
                }
                default -> {
                    Utils.log("No such armor base: " + armorBase);
                    return 0;
                }
            }
        }
    },
    CARVED_PLATING(
            List.of("Light plating", "Fancy leather"),
            List.of("Exotic chainmail", "Battle-chainmail"),
            List.of("War-Plate", "Arena plate"),
            List.of("Opal-Engraved", "Majestic plate"),
            List.of("Shiny runic-gold", "Gladiator", "Carved plating")
    ) {
        @Override
        public void mapBaseDefences(int ilvl, ItemTypes armorBase, Map<DefenceTypes, Integer> defences) {
            float maxItemLevel = 120;
            switch (armorBase){
                case HELMET -> {
                    int maximumHelmetArmor = 250;
                    int maxDodge = 100;
                    putArmor(ilvl, maxItemLevel, maximumHelmetArmor, defences);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case CHESTPLATE -> {
                    int maximumChestplateArmor = 600;
                    int maxDodge = 400;
                    putArmor(ilvl, maxItemLevel, maximumChestplateArmor, defences);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case LEGGINGS -> {
                    int maximumLeggingsArmor = 400;
                    int maxDodge = 300;
                    putArmor(ilvl, maxItemLevel, maximumLeggingsArmor, defences);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case BOOTS -> {
                    int maximumBootsArmor = 150;
                    int maxDodge = 200;
                    putArmor(ilvl, maxItemLevel, maximumBootsArmor, defences);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                default -> Utils.error("Invalid argument for armor mapping." + armorBase + " is not a armor type.");
            }
        }
        @Override
        public int mapBaseHealth(Tiers tier, ItemTypes armorBase) {
            switch (armorBase){
                case HELMET -> {
                    //Helmet base health definition
                    return defineTierBaseHealth(tier
                            , 50,  10
                            , 150, 30
                            , 400, 100
                            , 900, 120
                            , 1500,200);
                }
                case CHESTPLATE -> {
                    //Chestplate base health definition
                    return defineTierBaseHealth(tier
                            , 80,  15
                            , 190, 40
                            , 450, 150
                            , 1100,220
                            , 1800,260);
                }
                case LEGGINGS -> {
                    //Leggings base health definition
                    return defineTierBaseHealth(tier
                            , 70,  15
                            , 200, 40
                            , 500, 120
                            , 1000, 140
                            , 1700,240);
                }
                case BOOTS -> {
                    //Boots base health definition
                    return defineTierBaseHealth(tier
                            , 60,  10
                            , 150, 30
                            , 400, 100
                            , 900, 120
                            , 1600,200);
                }
                default -> {
                    Utils.log("No such armor base: " + armorBase);
                    return 0;
                }
            }
        }
    },
    LIGHT_CLOTH(
            List.of("Light cloth", "Wild leather"),
            List.of("Light chainmail", "Leatherbound mail"),
            List.of("Reinforced light", "Mercenary"),
            List.of("Opal-engraved cloth", "Ancient strappings"),
            List.of("Ranger leather", "Exquisite leather", "Runic light cloth")
    ) {
        @Override
        public void mapBaseDefences(int ilvl, ItemTypes armorBase, Map<DefenceTypes, Integer> defences) {
            float maxItemLevel = 120;
            switch (armorBase){
                case HELMET -> {
                    int maxDodge = 200;
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case CHESTPLATE -> {
                    int maxDodge = 800;
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case LEGGINGS -> {
                    int maxDodge = 600;
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case BOOTS -> {
                    int maxDodge = 400;
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                default -> Utils.error("Invalid argument for armor mapping." + armorBase + " is not a armor type.");
            }
        }
        @Override
        public int mapBaseHealth(Tiers tier, ItemTypes armorBase) {
            switch (armorBase){
                case HELMET -> {
                    //Helmet base health definition
                    return defineTierBaseHealth(tier
                            , 50,  10
                            , 150, 30
                            , 400, 100
                            , 900, 120
                            , 1500,200);
                }
                case CHESTPLATE -> {
                    //Chestplate base health definition
                    return defineTierBaseHealth(tier
                            , 80,  15
                            , 190, 40
                            , 450, 150
                            , 1100,220
                            , 1800,260);
                }
                case LEGGINGS -> {
                    //Leggings base health definition
                    return defineTierBaseHealth(tier
                            , 70,  15
                            , 200, 40
                            , 500, 120
                            , 1000, 140
                            , 1700,240);
                }
                case BOOTS -> {
                    //Boots base health definition
                    return defineTierBaseHealth(tier
                            , 60,  10
                            , 150, 30
                            , 400, 100
                            , 900, 120
                            , 1600,200);
                }
                default -> {
                    Utils.log("No such armor base: " + armorBase);
                    return 0;
                }
            }
        }
    },
    RUNIC_LEATHER(
            List.of("Magic leather"),
            List.of("Infused ringmail"),
            List.of("Reinforced magic leather"),
            List.of("Ancient strapped leather", "Crystal-infused cloth"),
            List.of("Assassin", "Runic-gold leather", "Rogue")
    ) {
        @Override
        public void mapBaseDefences(int ilvl, ItemTypes armorBase, Map<DefenceTypes, Integer> defences) {
            float maxItemLevel = 120;
            switch (armorBase){
                case HELMET -> {
                    int maximumWard = 50;
                    int maxDodge = 100;
                    putWard(ilvl, maxItemLevel, maximumWard, defences);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case CHESTPLATE -> {
                    int maximumWard = 120;
                    int maxDodge = 400;
                    putWard(ilvl, maxItemLevel, maximumWard, defences);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case LEGGINGS -> {
                    int maximumWard = 90;
                    int maxDodge = 300;
                    putWard(ilvl, maxItemLevel, maximumWard, defences);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case BOOTS -> {
                    int maximumWard = 40;
                    int maxDodge = 200;
                    putWard(ilvl, maxItemLevel, maximumWard, defences);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                default -> Utils.error("Invalid argument for armor mapping." + armorBase + " is not a armor type.");
            }
        }
        @Override
        public int mapBaseHealth(Tiers tier, ItemTypes armorBase) {
            switch (armorBase){
                case HELMET -> {
                    //Helmet base health definition
                    return defineTierBaseHealth(tier
                            , 50,  10
                            , 150, 30
                            , 400, 100
                            , 900, 120
                            , 1500,200);
                }
                case CHESTPLATE -> {
                    //Chestplate base health definition
                    return defineTierBaseHealth(tier
                            , 80,  15
                            , 190, 40
                            , 450, 150
                            , 1100,220
                            , 1800,260);
                }
                case LEGGINGS -> {
                    //Leggings base health definition
                    return defineTierBaseHealth(tier
                            , 70,  15
                            , 200, 40
                            , 500, 120
                            , 1000, 140
                            , 1700,240);
                }
                case BOOTS -> {
                    //Boots base health definition
                    return defineTierBaseHealth(tier
                            , 60,  10
                            , 150, 30
                            , 400, 100
                            , 900, 120
                            , 1600,200);
                }
                default -> {
                    Utils.log("No such armor base: " + armorBase);
                    return 0;
                }
            }
        }
    },
    ENCHANTED_SILK(
            List.of("Silken"),
            List.of("Arcane ringmail"),
            List.of("Iron-plated silk"),
            List.of("Shining ancient silk"),
            List.of("Mage", "Woven runic silk", "Arcane silk")
    ) {
        @Override
        public void mapBaseDefences(int ilvl, ItemTypes armorBase, Map<DefenceTypes, Integer> defences) {
            float maxItemLevel = 120;
            switch (armorBase){
                case HELMET -> {
                    int maxWard = 360;
                    putWard(ilvl, maxItemLevel, maxWard, defences);
                }
                case CHESTPLATE -> {
                    int maxWard = 700;
                    putWard(ilvl, maxItemLevel, maxWard, defences);
                }
                case LEGGINGS -> {
                    int maxWard = 600;
                    putWard(ilvl, maxItemLevel, maxWard, defences);
                }
                case BOOTS -> {
                    int maxWard = 300;
                    putWard(ilvl, maxItemLevel, maxWard, defences);
                }
                default -> Utils.error("Invalid argument for armor mapping." + armorBase + " is not a armor type.");
            }
        }
        @Override
        public int mapBaseHealth(Tiers tier, ItemTypes armorBase) {
            switch (armorBase){
                case HELMET -> {
                    //Helmet base health definition
                    return defineTierBaseHealth(tier
                            , 25,  10
                            , 75, 15
                            , 200, 50
                            , 450, 60
                            , 750,100);
                }
                case CHESTPLATE -> {
                    //Chestplate base health definition
                    return defineTierBaseHealth(tier
                            , 80,  15
                            , 190, 40
                            , 450, 150
                            , 1100,220
                            , 1800,260);
                }
                case LEGGINGS -> {
                    //Leggings base health definition
                    return defineTierBaseHealth(tier
                            , 35,  15
                            , 100, 20
                            , 250, 60
                            , 500, 70
                            , 850,120);
                }
                case BOOTS -> {
                    //Boots base health definition
                    return defineTierBaseHealth(tier
                            , 30,  10
                            , 75, 15
                            , 200, 50
                            , 450, 60
                            , 800,100);
                }
                default -> {
                    Utils.log("No such armor base: " + armorBase);
                    return 0;
                }
            }
        }
    },
    RUNIC_STEEL(
            List.of("Magic battleplate"),
            List.of("Battlemage ringmail"),
            List.of("Enhanced magic steel"),
            List.of("Shining Opal ", "Ancient arcane steel"),
            List.of("Templar", "Gilded runic steel")
    ) {
        @Override
        public void mapBaseDefences(int ilvl, ItemTypes armorBase, Map<DefenceTypes, Integer> defences) {
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
        @Override
        public int mapBaseHealth(Tiers tier, ItemTypes armorBase) {
            switch (armorBase){
                case HELMET -> {
                    //Helmet base health definition
                    return defineTierBaseHealth(tier
                            , 50,  10
                            , 150, 30
                            , 400, 100
                            , 900, 120
                            , 1500,200);
                }
                case CHESTPLATE -> {
                    //Chestplate base health definition
                    return defineTierBaseHealth(tier
                            , 80,  15
                            , 190, 40
                            , 450, 150
                            , 1100,220
                            , 1800,260);
                }
                case LEGGINGS -> {
                    //Leggings base health definition
                    return defineTierBaseHealth(tier
                            , 70,  15
                            , 200, 40
                            , 500, 120
                            , 1000, 140
                            , 1700,240);
                }
                case BOOTS -> {
                    //Boots base health definition
                    return defineTierBaseHealth(tier
                            , 60,  10
                            , 150, 30
                            , 400, 100
                            , 900, 120
                            , 1600,200);
                }
                default -> {
                    Utils.log("No such armor base: " + armorBase);
                    return 0;
                }
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

    private final List<String> tier1Names;
    private final List<String> tier2Names;
    private final List<String> tier3Names;
    private final List<String> tier4Names;
    private final List<String> tier5Names;

    ArmorTypes(List<String> t1, List<String> t2, List<String> t3, List<String> t4, List<String> t5){
        this.tier1Names = t1;
        this.tier2Names = t2;
        this.tier3Names = t3;
        this.tier4Names = t4;
        this.tier5Names = t5;
        loadAllAffixes();
    }
    protected abstract void mapBaseDefences(int ilvl, ItemTypes armorBase, Map<DefenceTypes, Integer> defences);
    /*
    * @return mapped base health value, based on tier and armor piece. Every armor material defines a different health map
    * */
    public abstract int mapBaseHealth(Tiers tier, ItemTypes armorBase);
    private Integer linearScaleStat(int givenItemLevel, float maxItemLevel, int maximumBaseStat){
        float itemLevelPercentile = givenItemLevel / maxItemLevel; // ilvl->ilvlRange normalization
        return (int) (maximumBaseStat * itemLevelPercentile);
    }
    protected void putArmor(int givenItemLevel, float maxItemLevel, int maximumBaseStat, Map<DefenceTypes, Integer> defences){
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
    protected void putWard(int givenItemLevel, float maxItemLevel, int maximumBaseStat, Map<DefenceTypes, Integer> defences){
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
    protected void putDodge(int givenItemLevel, float maxItemLevel, int maximumBaseStat, Map<DefenceTypes, Integer> defences){ //Will put -1 if invalid maxIlvl is given
        //Dodge will cap below the max item level given
        //Dodge will be a int value to represent a percentage (100x)  1% = 100, 56% = 5600
        int softCapOffset = 20;
        int softLevelCap = (int) (maxItemLevel)-softCapOffset;
        if (maxItemLevel <= softCapOffset){
            defences.put(DefenceTypes.DODGE, -1);
            return;
        }
        //If the givenItemLevel is valid (between 1 and levelCap) map it accordingly
        if (givenItemLevel > 0 && givenItemLevel <= softLevelCap){
            int dodgeValue = linearScaleStat(givenItemLevel, softLevelCap, maximumBaseStat);
            //Dodge wont be a percentage by itself, convert when displaying or doing percentage math
            defences.put(DefenceTypes.DODGE,dodgeValue);
        } else if(givenItemLevel>softLevelCap && givenItemLevel<=maxItemLevel){ //If ilvl is greater than softCap and lower than maxIlvl allowed, put the maximum value for Dodge
            defences.put(DefenceTypes.DODGE, maximumBaseStat);
        } else { //If the given Ilvl was higher than the max allowed, put 0
            defences.put(DefenceTypes.DODGE, 0);
        }
    }
    protected Material mapArmorBase(Tiers tier, ItemTypes armorBase){
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
    protected int defineTierBaseHealth(Tiers tier,
                                       int T1MaxHealth, int T1Variance,
                                       int T2MaxHealth, int T2Variance,
                                       int T3MaxHealth, int T3Variance,
                                       int T4MaxHealth, int T4Variance,
                                       int T5MaxHealth, int T5Variance){
        switch (tier){
            case T1 -> {
                return T1MaxHealth + CraftingUtils.getRandomNumber(0, T1Variance);
            }
            case T2 -> {
                return T2MaxHealth + CraftingUtils.getRandomNumber(0, T2Variance);
            }
            case T3 -> {
                return T3MaxHealth + CraftingUtils.getRandomNumber(0, T3Variance);
            }
            case T4 -> {
                return T4MaxHealth + CraftingUtils.getRandomNumber(0, T4Variance);
            }
            case T5 -> {
                return T5MaxHealth + CraftingUtils.getRandomNumber(0, T5Variance);
            }
        }
        //If none returns, set to 1
        return 1;
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
        Map<String, Map<String, Map<String, Map<String, Map<Integer, int[]>>>>> modifiersJSON = ModifiersJSON.getBasicModifiers();
        //The "Entire set" of a armor specialization is loaded at once and accessed later via getPiece() methods.
        this.basicHelmetPrefixes = getAffixTable(modifiersJSON, ItemTypes.HELMET, Affix.PREFIX);
        this.basicHelmetSuffixes = getAffixTable(modifiersJSON, ItemTypes.HELMET, Affix.SUFFIX);
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

//    public Map<String, Map<Integer, int[]>> getBasicChestlatePrefixes() {
//        return basicChestlatePrefixes;
//    }
//    public Map<String, Map<Integer, int[]>> getBasicChesplateSuffixes() {
//        return basicChesplateSuffixes;
//    }
//
//    public Map<String, Map<Integer, int[]>> getBasicLeggingsPrefixes() {
//        return basicLeggingsPrefixes;
//    }
//    public Map<String, Map<Integer, int[]>> getBasicLeggingsSuffixes() {
//        return basicLeggingsSuffixes;
//    }
//
//    public Map<String, Map<Integer, int[]>> getBasicBootsPrefixes() {
//        return basicBootsPrefixes;
//    }
//    public Map<String, Map<Integer, int[]>> getBasicBootsSuffixes() {
//        return basicBootsSuffixes;
//    }

    public String getRandomName(Tiers tier){
        switch (tier){
            case T1 -> {
                int rand = CraftingUtils.getRandomNumber(0, this.tier1Names.size()-1);
                return tier1Names.get(rand);
            }
            case T2 -> {
                int rand = CraftingUtils.getRandomNumber(0, this.tier2Names.size()-1);
                return tier2Names.get(rand);
            }
            case T3 -> {
                int rand = CraftingUtils.getRandomNumber(0, this.tier3Names.size()-1);
                return tier3Names.get(rand);
            }
            case T4 -> {
                int rand = CraftingUtils.getRandomNumber(0, this.tier4Names.size()-1);
                return tier4Names.get(rand);
            }
            case T5 -> {
                int rand = CraftingUtils.getRandomNumber(0, this.tier5Names.size()-1);
                return tier5Names.get(rand);
            }
            default -> {
                return "";
            }
        }
    }
}
