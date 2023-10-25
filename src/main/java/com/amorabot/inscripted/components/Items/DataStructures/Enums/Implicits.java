package com.amorabot.inscripted.components.Items.DataStructures.Enums;

import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;

public enum Implicits {
    AXE_STANDARD(ImplicitType.STANDARD,"&"+WeaponTypes.AXE.getDefaulNameColor()+"@value1@** SHRED",
            TargetStats.SHRED, new int[]{5}, ValueTypes.FLAT_PERCENT, false),
    AXE_CORRUPTED(ImplicitType.CORRUPTED,"&l"+"@value1@% LOREM IPSUM",
            null, new int[]{0}, ValueTypes.FLAT_PERCENT, false),
    SWORD_STANDARD(ImplicitType.STANDARD,"&"+WeaponTypes.SWORD.getDefaulNameColor()+"@value1@** ACC",
            TargetStats.ACCURACY, new int[]{30}, ValueTypes.FLAT_PERCENT, false),
    SWORD_CORRUPTED(ImplicitType.CORRUPTED,"&l"+"@value1@% LOREM IPSUM",
            null, new int[]{0}, ValueTypes.FLAT_PERCENT, false),
    BOW_STANDARD(ImplicitType.STANDARD,"&"+WeaponTypes.BOW.getDefaulNameColor()+"@value1@** DODGE",
            TargetStats.DODGE, new int[]{10}, ValueTypes.FLAT, false),
    BOW_CORRUPTED(ImplicitType.CORRUPTED,"&l"+"@value1@% LOREM IPSUM",
            null, new int[]{0}, ValueTypes.FLAT_PERCENT, false),
    DAGGER_STANDARD(ImplicitType.STANDARD,"&"+WeaponTypes.DAGGER.getDefaulNameColor()+"@value1@** CRIT DMG",
            TargetStats.CRITICAL_DAMAGE, new int[]{10}, ValueTypes.FLAT_PERCENT, false),
    DAGGER_CORRUPTED(ImplicitType.CORRUPTED,"&l"+"@value1@% LOREM IPSUM",
            null, new int[]{0}, ValueTypes.FLAT_PERCENT, false),
    WAND_STANDARD(ImplicitType.STANDARD,"&"+WeaponTypes.WAND.getDefaulNameColor()+"@value1@** MAELSTROM",
            TargetStats.MAELSTROM, new int[]{5}, ValueTypes.FLAT_PERCENT, false),
    WAND_CORRUPTED(ImplicitType.CORRUPTED,"&l"+"@value1@% LOREM IPSUM",
            null, new int[]{0}, ValueTypes.FLAT_PERCENT, false),
    SCEPTRE_STANDARD(ImplicitType.STANDARD,"&"+WeaponTypes.SCEPTRE.getDefaulNameColor()+"@value1@** ELE DMG",
            TargetStats.ELEMENTAL_DAMAGE, new int[]{10}, ValueTypes.FLAT_PERCENT, false),
    SCEPTRE_CORRUPTED(ImplicitType.CORRUPTED,"&l"+"@value1@% LOREM IPSUM",
            null, new int[]{0}, ValueTypes.FLAT_PERCENT, false),

    HEAVY_PLATING_STANDARD(ImplicitType.STANDARD,"&"+WeaponTypes.AXE.getDefaulNameColor()+"**@value1@ STR",
            TargetStats.STRENGTH, new int[]{25}, ValueTypes.FLAT, false),
    CARVED_PLATING_STANDARD(ImplicitType.STANDARD,"&"+WeaponTypes.SWORD.getDefaulNameColor()+"**@value1@ STR"+
            "-brk-"+
            "&"+WeaponTypes.SWORD.getDefaulNameColor()+"**@value2@ DEX",
            TargetStats.STRENGTH_DEXTERITY, new int[]{15}, ValueTypes.FLAT, true),
    LIGHT_CLOTH_STANDARD(ImplicitType.STANDARD,"&"+WeaponTypes.BOW.getDefaulNameColor()+"**@value1@ DEX",
            TargetStats.DEXTERITY, new int[]{25}, ValueTypes.FLAT, false),
    RUNIC_LEATHER_STANDARD(ImplicitType.STANDARD,"&"+WeaponTypes.DAGGER.getDefaulNameColor()+"**@value1@ DEX"+
            "-brk-"+
            "&"+WeaponTypes.DAGGER.getDefaulNameColor()+"**@value2@ INT",
            TargetStats.DEXTERITY_INTELLIGENCE, new int[]{15}, ValueTypes.FLAT, true),
    ENCHANTED_SILK_STANDARD(ImplicitType.STANDARD,"&"+WeaponTypes.WAND.getDefaulNameColor()+"**@value1@ INT",
            TargetStats.INTELLIGENCE, new int[]{25}, ValueTypes.FLAT, false),
    RUNIC_STEEL_STANDARD(ImplicitType.STANDARD,"&"+WeaponTypes.SCEPTRE.getDefaulNameColor()+"**@value1@ INT"+
            "-brk-"+
            "&"+WeaponTypes.SCEPTRE.getDefaulNameColor()+"**@value2@ STR",
            TargetStats.DEXTERITY_INTELLIGENCE, new int[]{15}, ValueTypes.FLAT, true);

    private final ImplicitType implicitType;
    private final String displayName;
    private final int[] value;
    private final TargetStats targetStat;
    private final ValueTypes valueType;
    private final boolean hybrid;

    Implicits(ImplicitType implicitType, String templateDisplayName, TargetStats targetStat, int[] value, ValueTypes valueType, boolean hybrid){
        this.implicitType = implicitType;
        String implicitDisplayName = templateDisplayName
                .replace("@value1@", ""+value[0])
                .replace("@value2@", ""+value[0]);
        if (valueType == ValueTypes.FLAT){
            this.displayName = implicitDisplayName.replace("**", "+");
        } else {
            this.displayName = implicitDisplayName.replace("**", "%");
        }
        this.targetStat = targetStat;
        this.value = value;
        this.valueType =valueType;
        this.hybrid = hybrid;
    }
    public String getDisplayName() {
        return displayName;
    }
    public TargetStats getTargetStat() {
        return targetStat;
    }
    public int[] getValue() {
        return value;
    }
    public ValueTypes getValueType() {
        return valueType;
    }
    public boolean isHybrid() {
        return hybrid;
    }
}
