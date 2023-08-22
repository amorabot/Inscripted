package com.amorabot.rpgelements.components.Items.Weapon;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.Affix;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.rpgelements.components.Items.Interfaces.AffixTableSelector;
import com.amorabot.rpgelements.components.Items.Files.ModifiersJSON;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.Material;

import java.util.Map;

public enum WeaponTypes implements AffixTableSelector {

    AXE("#b01330"){
        @Override
        public int[] mapDamage(int ilvl) {
            if (ilvl > 0 && ilvl <= 11){
                return new int[]{6,11};
            } else if (ilvl <= 30){
                return new int[]{11,21};
            } else if (ilvl <= 45){
                return new int[]{27,50};
            } else if (ilvl <= 60){
                return new int[]{35,65};
            } else if (ilvl <= 80){
                return new int[]{38,114};
            } else {
                return new int[]{60,150};
            }
        }
        @Override
        public Material mapWeaponBase(int ilvl) {
            if (ilvl <= 10){
                return Material.WOODEN_AXE;
            } else if (ilvl <= 25) {
                return Material.STONE_AXE;
            } else if (ilvl <= 45) {
                return Material.IRON_AXE;
            } else if (ilvl <= 75) {
                return Material.DIAMOND_AXE;
            } else {
                return Material.GOLDEN_AXE;
            }
        }
    },
    SWORD("#F9B147") {
        //"#e2e831" -greenish yellow
        @Override
        public int[] mapDamage(int ilvl) {
            if (ilvl > 0 && ilvl <= 11){
                return new int[]{3,9};
            } else if (ilvl <= 30){
                return new int[]{10,17};
            } else if (ilvl <= 45){
                return new int[]{25,32};
            } else if (ilvl <= 60){
                return new int[]{30,55};
            } else if (ilvl <= 80){
                return new int[]{33,90};
            } else {
                return new int[]{45,120};
            }
        }
        @Override
        public Material mapWeaponBase(int ilvl) {
            if (ilvl <= 10){
                return Material.WOODEN_SWORD;
            } else if (ilvl <= 25) {
                return Material.STONE_SWORD;
            } else if (ilvl <= 45) {
                return Material.IRON_SWORD;
            } else if (ilvl <= 75) {
                return Material.DIAMOND_SWORD;
            } else {
                return Material.GOLDEN_SWORD;
            }
        }
    };//,
//    BOW("#71C45A") {
//        @Override
//        public int[] mapDamage(int ilvl) {
//            if (ilvl > 0 && ilvl <= 11){
//                return new int[]{6,11};
//            } else if (ilvl <= 30){
//                return new int[]{11,21};
//            } else if (ilvl <= 45){
//                return new int[]{27,50};
//            } else if (ilvl <= 60){
//                return new int[]{35,65};
//            } else if (ilvl <= 80){
//                return new int[]{38,114};
//            } else {
//                return new int[]{60,150};
//            }
//        }
//
//        @Override
//        public Material mapWeaponBase(int ilvl) {
//            return Material.BOW;
//        }
//    },
//    DAGGER("#18A383") {
//        @Override
//        public int[] mapDamage(int ilvl) {
//            if (ilvl > 0 && ilvl <= 11){
//                return new int[]{6,11};
//            } else if (ilvl <= 30){
//                return new int[]{11,21};
//            } else if (ilvl <= 45){
//                return new int[]{27,50};
//            } else if (ilvl <= 60){
//                return new int[]{35,65};
//            } else if (ilvl <= 80){
//                return new int[]{38,114};
//            } else {
//                return new int[]{60,150};
//            }
//        }
//
//        @Override
//        public Material mapWeaponBase(int ilvl) {
//            return Material.SHEARS;
//        }
//    },
//    WAND("#496FE3") {
//        @Override
//        public int[] mapDamage(int ilvl) {
//            if (ilvl > 0 && ilvl <= 11){
//                return new int[]{6,11};
//            } else if (ilvl <= 30){
//                return new int[]{11,21};
//            } else if (ilvl <= 45){
//                return new int[]{27,50};
//            } else if (ilvl <= 60){
//                return new int[]{35,65};
//            } else if (ilvl <= 80){
//                return new int[]{38,114};
//            } else {
//                return new int[]{60,150};
//            }
//        }
//
//        @Override
//        public Material mapWeaponBase(int ilvl) {
//            return Material.STICK;
//        }
//    },
//    SCEPTRE("#A735D4") {
//        @Override
//        public int[] mapDamage(int ilvl) {
//            return new int[0];
//        }
//
//        @Override
//        public Material mapWeaponBase(int ilvl) {
//            if (ilvl <= 10){
//                return Material.WOODEN_SHOVEL;
//            } else if (ilvl <= 25) {
//                return Material.STONE_SHOVEL;
//            } else if (ilvl <= 45) {
//                return Material.IRON_SHOVEL;
//            } else if (ilvl <= 75) {
//                return Material.DIAMOND_SHOVEL;
//            } else {
//                return Material.GOLDEN_SHOVEL;
//            }
//        }
//    };

    private final String color;
    private Map<String, Map<Integer, int[]>> basicPrefixes;
    private Map<String, Map<Integer, int[]>> basicSuffixes;
    WeaponTypes(String defaultColor){
        this.color = defaultColor;
        loadAllAffixes();
    }
    public String getDefaulNameColor(){
        return color;
    }
    public abstract int[] mapDamage(int ilvl);
    public abstract Material mapWeaponBase(int ilvl);
    private void loadAllAffixes(){
        loadBasicAffixes();
    }
    private void loadBasicAffixes(){
        Map<String, Map<String, Map<String, Map<String, Map<Integer, int[]>>>>> modifiersJSON = ModifiersJSON.getBasicModifiers();
        this.basicPrefixes = getAffixTable(modifiersJSON, ItemTypes.WEAPON, Affix.PREFIX);
        this.basicSuffixes = getAffixTable(modifiersJSON, ItemTypes.WEAPON, Affix.SUFFIX);
        Utils.log("Modifiers loaded successfully!");
    }
    public Map<String, Map<Integer, int[]>> getBasicPrefixes(){
        return this.basicPrefixes;
    }
    public Map<String, Map<Integer, int[]>> getBasicSuffixes(){
        return this.basicSuffixes;
    }
}
