package com.amorabot.rpgelements.components.Items.DataStructures.Enums;

import com.amorabot.rpgelements.components.Items.Weapon.WeaponTypes;

public enum Implicit {
    //TODO> add *alternate corrupted and *special implicits
//    NONE("null", TargetStats.ACCURACY, new int[1],ValueTypes.FLAT,RangeTypes.SINGLE_VALUE),
    AXE_STANDARD("&"+WeaponTypes.AXE.getDefaulNameColor()+"@value1@** SHRED",
            TargetStats.SHRED, new int[]{5}, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_VALUE),
    AXE_CORRUPTED("&l"+"@value1@% LOREM IPSUM",
            null, new int[]{0}, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_VALUE),
    SWORD_STANDARD("&"+WeaponTypes.SWORD.getDefaulNameColor()+"@value1@** ACC",
            TargetStats.ACCURACY, new int[]{30}, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_VALUE),
    SWORD_CORRUPTED("&l"+"@value1@% LOREM IPSUM",
            null, new int[]{0}, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_VALUE),
    BOW_STANDARD("&"+WeaponTypes.BOW.getDefaulNameColor()+"@value1@** DODGE",
            TargetStats.DODGE, new int[]{1}, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_VALUE),
    BOW_CORRUPTED("&l"+"@value1@% LOREM IPSUM",
            null, new int[]{0}, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_VALUE),
    DAGGER_STANDARD("&"+WeaponTypes.DAGGER.getDefaulNameColor()+"@value1@** CRIT DMG",
            TargetStats.CRITICAL_DAMAGE, new int[]{10}, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_VALUE),
    DAGGER_CORRUPTED("&l"+"@value1@% LOREM IPSUM",
            null, new int[]{0}, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_VALUE),
    WAND_STANDARD("&"+WeaponTypes.WAND.getDefaulNameColor()+"@value1@** MAELSTROM",
            TargetStats.MAELSTROM, new int[]{5}, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_VALUE),
    WAND_CORRUPTED("&l"+"@value1@% LOREM IPSUM",
            null, new int[]{0}, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_VALUE),
    SCEPTRE_STANDARD("&"+WeaponTypes.SCEPTRE.getDefaulNameColor()+"@value1@** ELE DMG",
            TargetStats.ELEMENTAL_DAMAGE, new int[]{10}, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_VALUE),
    SCEPTRE_CORRUPTED("&l"+"@value1@% LOREM IPSUM",
            null, new int[]{0}, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_VALUE);

    private final String displayName;
    private final int[] value;
    private final TargetStats targetStat;
    private final ValueTypes valueType;
    private final RangeTypes rangeType;

    Implicit(String templateDisplayName, TargetStats targetStat, int[] value, ValueTypes valueType, RangeTypes rangeType){
        switch (rangeType){
            case SINGLE_VALUE -> {
                String implicitDisplayName = templateDisplayName.replace("@value1@", ""+value[0]);
                if (valueType != ValueTypes.FLAT){
                    this.displayName = implicitDisplayName.replace("**", "%");
                } else {
                    this.displayName = implicitDisplayName.replace("**", "+");
                }
            }
            case SINGLE_RANGE -> this.displayName = templateDisplayName; //...Implement single range string substitution
            default -> this.displayName = "undefined";
        }
        this.targetStat = targetStat;
        this.value = value;
        this.valueType =valueType;
        this.rangeType = rangeType;
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
    public RangeTypes getRangeType() {
        return rangeType;
    }
}
