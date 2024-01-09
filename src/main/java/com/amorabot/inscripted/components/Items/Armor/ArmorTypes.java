package com.amorabot.inscripted.components.Items.Armor;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Tiers;
import com.amorabot.inscripted.components.Items.Files.ResourcesJSONReader;
import com.amorabot.inscripted.components.Items.Interfaces.AffixTableSelector;
import com.amorabot.inscripted.utils.CraftingUtils;
import com.amorabot.inscripted.utils.Utils;
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
            int meanArmorPerPiece = 80;
            //Base item defences definition
            switch (armorBase){
                case HELMET -> {
                    int maximumHelmetArmor = (int)( meanArmorPerPiece*HELMET_MAIN_STAT_WEIGHT);
                    putArmor(ilvl, maxItemLevel, maximumHelmetArmor, defences);
                }
                case CHESTPLATE -> {
                    int maximumChestplateArmor = (int)( meanArmorPerPiece*CHESTPLATE_MAIN_STAT_WEIGHT);
                    putArmor(ilvl, maxItemLevel, maximumChestplateArmor, defences);
                }
                case LEGGINGS -> {
                    int maximumLeggingsArmor = (int)( meanArmorPerPiece*LEGGINGS_MAIN_STAT_WEIGHT);
                    putArmor(ilvl, maxItemLevel, maximumLeggingsArmor, defences);
                }
                case BOOTS -> {
                    int maximumBootsArmor = (int)( meanArmorPerPiece*BOOTS_MAIN_STAT_WEIGHT);
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
            int meanArmorPerPiece = 50; // Half of full STR armor + 1/8
            int meanDodgePerPiece = 50;
            switch (armorBase){
                case HELMET -> {
                    int maximumHelmetArmor = (int) ( meanArmorPerPiece * HELMET_MAIN_STAT_WEIGHT );
                    int maxDodge = (int) ( meanDodgePerPiece * HELMET_MAIN_STAT_WEIGHT );
                    putArmor(ilvl, maxItemLevel, maximumHelmetArmor, defences);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case CHESTPLATE -> {
                    int maximumChestplateArmor = (int) ( meanArmorPerPiece * CHESTPLATE_MAIN_STAT_WEIGHT );
                    int maxDodge = (int) ( meanDodgePerPiece * CHESTPLATE_MAIN_STAT_WEIGHT );
                    putArmor(ilvl, maxItemLevel, maximumChestplateArmor, defences);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case LEGGINGS -> {
                    int maximumLeggingsArmor = (int) ( meanArmorPerPiece * LEGGINGS_MAIN_STAT_WEIGHT );
                    int maxDodge = (int) ( meanDodgePerPiece * LEGGINGS_MAIN_STAT_WEIGHT );
                    putArmor(ilvl, maxItemLevel, maximumLeggingsArmor, defences);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case BOOTS -> {
                    int maximumBootsArmor = (int) ( meanArmorPerPiece * BOOTS_MAIN_STAT_WEIGHT );
                    int maxDodge = (int) ( meanDodgePerPiece * BOOTS_MAIN_STAT_WEIGHT );
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
            int meanDodgePerPiece = 100;
            switch (armorBase){
                case HELMET -> {
                    int maxDodge = (int) (meanDodgePerPiece * HELMET_MAIN_STAT_WEIGHT);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case CHESTPLATE -> {
                    int maxDodge = (int) (meanDodgePerPiece * CHESTPLATE_MAIN_STAT_WEIGHT);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case LEGGINGS -> {
                    int maxDodge = (int) (meanDodgePerPiece * LEGGINGS_MAIN_STAT_WEIGHT);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case BOOTS -> {
                    int maxDodge = (int) (meanDodgePerPiece * BOOTS_MAIN_STAT_WEIGHT);
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
                            , 350, 70
                            , 600, 100
                            , 1100,150);
                }
                case CHESTPLATE -> {
                    //Chestplate base health definition
                    return defineTierBaseHealth(tier
                            , 80,  15
                            , 190, 40
                            , 450, 100
                            , 1100,160
                            , 1600,220);
                }
                case LEGGINGS -> {
                    //Leggings base health definition
                    return defineTierBaseHealth(tier
                            , 70,  15
                            , 200, 40
                            , 500, 80
                            , 1000, 140
                            , 1400,200);
                }
                case BOOTS -> {
                    //Boots base health definition
                    return defineTierBaseHealth(tier
                            , 60,  10
                            , 150, 30
                            , 350, 70
                            , 600, 100
                            , 1100,150);
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
            int meanWardPerPiece = 150;
            int meanDodgePerPiece = 50;
            switch (armorBase){
                case HELMET -> {
                    int maximumWard = (int) ( meanWardPerPiece * HELMET_MAIN_STAT_WEIGHT);
                    int maxDodge = (int) ( meanDodgePerPiece * HELMET_MAIN_STAT_WEIGHT);
                    putWard(ilvl, maxItemLevel, maximumWard, defences);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case CHESTPLATE -> {
                    int maximumWard = (int) ( meanWardPerPiece * CHESTPLATE_MAIN_STAT_WEIGHT);
                    int maxDodge = (int) ( meanDodgePerPiece * CHESTPLATE_MAIN_STAT_WEIGHT);
                    putWard(ilvl, maxItemLevel, maximumWard, defences);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case LEGGINGS -> {
                    int maximumWard = (int) ( meanWardPerPiece * LEGGINGS_MAIN_STAT_WEIGHT);
                    int maxDodge = (int) ( meanDodgePerPiece * LEGGINGS_MAIN_STAT_WEIGHT);
                    putWard(ilvl, maxItemLevel, maximumWard, defences);
                    putDodge(ilvl, maxItemLevel, maxDodge, defences);
                }
                case BOOTS -> {
                    int maximumWard = (int) ( meanWardPerPiece * BOOTS_MAIN_STAT_WEIGHT);
                    int maxDodge = (int) ( meanDodgePerPiece * BOOTS_MAIN_STAT_WEIGHT);
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
            int meanWardPerPiece = 500;
            switch (armorBase){
                case HELMET -> {
                    int maxWard = (int) ( meanWardPerPiece * HELMET_MAIN_STAT_WEIGHT );
                    putWard(ilvl, maxItemLevel, maxWard, defences);
                }
                case CHESTPLATE -> {
                    int maxWard = (int) ( meanWardPerPiece * CHESTPLATE_MAIN_STAT_WEIGHT );
                    putWard(ilvl, maxItemLevel, maxWard, defences);
                }
                case LEGGINGS -> {
                    int maxWard = (int) ( meanWardPerPiece * LEGGINGS_MAIN_STAT_WEIGHT );
                    putWard(ilvl, maxItemLevel, maxWard, defences);
                }
                case BOOTS -> {
                    int maxWard = (int) ( meanWardPerPiece * BOOTS_MAIN_STAT_WEIGHT );
                    putWard(ilvl, maxItemLevel, maxWard, defences);
                }
                default -> Utils.error("Invalid argument for armor mapping." + armorBase + " is not a armor type.");
            }
        }
        @Override
        public int mapBaseHealth(Tiers tier, ItemTypes armorBase) {
            //Variance -> 10% of base value per tier
            switch (armorBase){
                case HELMET -> {
                    //Helmet base health definition
                    return defineTierBaseHealth(tier
                            , 25,  5
                            , 75, 10
                            , 150, 20
                            , 350, 30
                            , 550,50);
                }
                case CHESTPLATE -> {
                    //Chestplate base health definition
                    return defineTierBaseHealth(tier
                            , 60,  15
                            , 130, 40
                            , 350, 80
                            , 700,150
                            , 1000,200);
                }
                case LEGGINGS -> {
                    //Leggings base health definition
                    return defineTierBaseHealth(tier
                            , 35,  15
                            , 80, 20
                            , 190, 60
                            , 300, 70
                            , 650,120);
                }
                case BOOTS -> {
                    //Boots base health definition
                    return defineTierBaseHealth(tier
                            , 30,  5
                            , 70, 10
                            , 150, 20
                            , 300, 30
                            , 420,60);
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
            int meanArmorPerPiece = 60;
            int meanWardPerPiece = 150;
            switch (armorBase){
                case HELMET -> {
                    int maximumHelmetArmor = (int) (meanArmorPerPiece * HELMET_MAIN_STAT_WEIGHT);
                    int maximumWard = (int) (meanWardPerPiece * HELMET_MAIN_STAT_WEIGHT);
                    putArmor(ilvl, maxItemLevel, maximumHelmetArmor, defences);
                    putWard(ilvl, maxItemLevel, maximumWard, defences);
                }
                case CHESTPLATE -> {
                    int maximumChestplateArmor = (int) (meanArmorPerPiece * CHESTPLATE_MAIN_STAT_WEIGHT);
                    int maximumWard = (int) (meanWardPerPiece * CHESTPLATE_MAIN_STAT_WEIGHT);
                    putArmor(ilvl, maxItemLevel, maximumChestplateArmor, defences);
                    putWard(ilvl, maxItemLevel, maximumWard, defences);
                }
                case LEGGINGS -> {
                    int maximumLeggingsArmor = (int) (meanArmorPerPiece * LEGGINGS_MAIN_STAT_WEIGHT);
                    int maximumWard = (int) (meanWardPerPiece * LEGGINGS_MAIN_STAT_WEIGHT);
                    putArmor(ilvl, maxItemLevel, maximumLeggingsArmor, defences);
                    putWard(ilvl, maxItemLevel, maximumWard, defences);
                }
                case BOOTS -> {
                    int maximumBootsArmor = (int) (meanArmorPerPiece * BOOTS_MAIN_STAT_WEIGHT);
                    int maximumWard = (int) (meanWardPerPiece * BOOTS_MAIN_STAT_WEIGHT);
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

    private Map<String, Map<String, Map<Integer, Integer>>> helmetAffixes;
    private Map<String, Map<String, Map<Integer, Integer>>> chestplateAffixes;
    private Map<String, Map<String, Map<Integer, Integer>>> leggingsAffixes;
    private Map<String, Map<String, Map<Integer, Integer>>> bootsAffixes;


    private static final double HELMET_MAIN_STAT_WEIGHT = 0.8;
    private static final double CHESTPLATE_MAIN_STAT_WEIGHT = 1.3;
    private static final double LEGGINGS_MAIN_STAT_WEIGHT = 1.2;
    private static final double BOOTS_MAIN_STAT_WEIGHT = 0.7;

    //TODO: Move these stats to Tiers enum, specify names for each tier of itemType, for example
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
    protected void putDodge(int givenItemLevel, float maxItemLevel, int maximumBaseStat, Map<DefenceTypes, Integer> defences){
        //Will put -1 if invalid maxIlvl is given
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
