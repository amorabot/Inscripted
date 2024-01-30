package com.amorabot.inscripted.components.Items.DataStructures.Enums;

import com.amorabot.inscripted.components.Items.Interfaces.ItemSubtype;
import com.amorabot.inscripted.components.Player.archetypes.Archetypes;
import com.amorabot.inscripted.utils.Utils;

public enum Implicits {
    //TODO: Change implicits to be tier-based
    AXE_STANDARD(ImplicitType.STANDARD,"&"+ Archetypes.MARAUDER.getColor() +"@value1@** SHRED",
            TargetStats.SHRED, new int[]{5}, ValueTypes.PERCENT, false),
    AXE_CORRUPTED(ImplicitType.CORRUPTED,"&"+ Archetypes.MARAUDER.getColor() +"@value1@** AREA DAMAGE",
            TargetStats.AREA_DAMAGE, new int[]{10}, ValueTypes.PERCENT, false),
    SWORD_STANDARD(ImplicitType.STANDARD,"&"+ Archetypes.GLADIATOR.getColor() +"@value1@** ACC",
            TargetStats.ACCURACY, new int[]{30}, ValueTypes.INCREASED, false),
    SWORD_CORRUPTED(ImplicitType.CORRUPTED,"&"+Archetypes.GLADIATOR.getColor()+"@value1@** BLEED CHANCE",
            TargetStats.BLEED, new int[]{15}, ValueTypes.PERCENT, false),
    BOW_STANDARD(ImplicitType.STANDARD,"&"+ Archetypes.MERCENARY.getColor() +"@value1@** DODGE",
            TargetStats.DODGE, new int[]{10}, ValueTypes.FLAT, false),
    BOW_CORRUPTED(ImplicitType.CORRUPTED,"&"+ Archetypes.MERCENARY.getColor()+"**@value1@ PROJECTILE",
            TargetStats.EXTRA_PROJECTILES, new int[]{1}, ValueTypes.FLAT, false),
    DAGGER_STANDARD(ImplicitType.STANDARD,"&"+ Archetypes.ROGUE.getColor() +"@value1@** CRIT DMG",
            TargetStats.CRITICAL_DAMAGE, new int[]{10}, ValueTypes.INCREASED, false),
    DAGGER_CORRUPTED(ImplicitType.CORRUPTED,"&"+ Archetypes.ROGUE.getColor()+"**@value1@ WALK SPEED",
            TargetStats.WALK_SPEED, new int[]{25}, ValueTypes.FLAT, false),
    WAND_STANDARD(ImplicitType.STANDARD,"&"+ Archetypes.SORCERER.getColor() +"@value1@** MAELSTROM",
            TargetStats.MAELSTROM, new int[]{5}, ValueTypes.PERCENT, false),
    WAND_CORRUPTED(ImplicitType.CORRUPTED,"&"+ Archetypes.SORCERER.getColor()+"@value1@** HEALING POWER",
            TargetStats.HEALING_POWER, new int[]{10}, ValueTypes.PERCENT, false),
    SCEPTRE_STANDARD(ImplicitType.STANDARD,"&"+ Archetypes.TEMPLAR.getColor() +"@value1@** ELE DMG",
            TargetStats.ELEMENTAL_DAMAGE, new int[]{10}, ValueTypes.INCREASED, false),
    SCEPTRE_CORRUPTED(ImplicitType.CORRUPTED,"&"+ Archetypes.TEMPLAR.getColor()+"@value1@** AREA OF EFFECT",
            TargetStats.AOE, new int[]{10}, ValueTypes.PERCENT, false),

    HEAVY_PLATING_STANDARD(ImplicitType.STANDARD,"&"+ Archetypes.MARAUDER.getColor() +"**@value1@ STR",
            TargetStats.STRENGTH, new int[]{25}, ValueTypes.FLAT, false),
    HEAVY_PLATING_CORRUPTED(ImplicitType.CORRUPTED,"&"+ Archetypes.MARAUDER.getColor() +"@value1@** GLOBAL ARMOR",
            TargetStats.ARMOR, new int[]{10}, ValueTypes.INCREASED, false),
    CARVED_PLATING_STANDARD(ImplicitType.STANDARD,"&"+ Archetypes.GLADIATOR.getColor() +"**@value1@ STR"+
            "-brk-"+
            "&"+ Archetypes.GLADIATOR.getColor() +"**@value2@ DEX",
            TargetStats.STRENGTH_DEXTERITY, new int[]{15, 15}, ValueTypes.FLAT, true),
    CARVED_PLATING_CORRUPTED(ImplicitType.CORRUPTED,"&"+ Archetypes.GLADIATOR.getColor() +"@value1@** ACCURACY",
            TargetStats.ACCURACY, new int[]{20}, ValueTypes.INCREASED, false),
    LIGHT_CLOTH_STANDARD(ImplicitType.STANDARD,"&"+ Archetypes.MERCENARY.getColor() +"**@value1@ DEX",
            TargetStats.DEXTERITY, new int[]{25}, ValueTypes.FLAT, false),
    LIGHT_CLOTH_CORRUPTED(ImplicitType.CORRUPTED,"&"+ Archetypes.MERCENARY.getColor() +"@value1@** WALK SPEED",
            TargetStats.WALK_SPEED, new int[]{10}, ValueTypes.INCREASED, false),
    RUNIC_LEATHER_STANDARD(ImplicitType.STANDARD,"&"+ Archetypes.ROGUE.getColor() +"**@value1@ DEX"+
            "-brk-"+
            "&"+ Archetypes.ROGUE.getColor() +"**@value2@ INT",
            TargetStats.DEXTERITY_INTELLIGENCE, new int[]{15, 15}, ValueTypes.FLAT, true),
    RUNIC_LEATHER_CORRUPTED(ImplicitType.CORRUPTED,"&"+ Archetypes.ROGUE.getColor() +"**@value1@ DODGE",
            TargetStats.DODGE, new int[]{15}, ValueTypes.FLAT, false),
    ENCHANTED_SILK_STANDARD(ImplicitType.STANDARD,"&"+ Archetypes.SORCERER.getColor() +"**@value1@ INT",
            TargetStats.INTELLIGENCE, new int[]{25}, ValueTypes.FLAT, false),
    ENCHANTED_SILK_CORRUPTED(ImplicitType.CORRUPTED,"&"+ Archetypes.SORCERER.getColor() +"**@value1@ STAMINA",
            TargetStats.STAMINA, new int[]{15}, ValueTypes.FLAT, false),
    RUNIC_STEEL_STANDARD(ImplicitType.STANDARD,"&"+ Archetypes.TEMPLAR.getColor() +"**@value1@ INT"+
            "-brk-"+
            "&"+ Archetypes.TEMPLAR.getColor() +"**@value2@ STR",
            TargetStats.INTELLIGENCE_STRENGTH, new int[]{15, 15}, ValueTypes.FLAT, true),
    RUNIC_STEEL_CORRUPTED(ImplicitType.CORRUPTED,"&"+ Archetypes.TEMPLAR.getColor() +"@value1@** ABYSSAL RES.",
            TargetStats.ABYSSAL_RESISTANCE, new int[]{15}, ValueTypes.PERCENT, false),;

    private final ImplicitType implicitType;
    private final String displayName;
    private final int[] value;
    private final TargetStats targetStat;
    private final ValueTypes valueType;
    private final boolean hybrid;

    Implicits(ImplicitType implicitType, String templateDisplayName, TargetStats targetStat, int[] value, ValueTypes valueType, boolean hybrid){
        this.implicitType = implicitType;
        String implicitDisplayName;
        if (hybrid){
            implicitDisplayName = templateDisplayName
                    .replace("@value1@", ""+value[0])
                    .replace("@value2@", ""+value[1]);
        } else {
            implicitDisplayName = templateDisplayName
                    .replace("@value1@", ""+value[0]);
        }
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

    public String getModifierKey(){ //Behaves the same as ModifierIDs version, consider merging
        String modKey;
        if (!isHybrid()){
            modKey = getTargetStat().toString().replace("_", "") + "_" + getValueType();
        } else {
            modKey = getTargetStat() + "_" + getValueType();
        }
        return modKey;
    }

    //TODO: Make this tier-base too
    public static <subType extends Enum<subType> & ItemSubtype> Implicits getImplicitFor(subType itemSubtype, boolean isCorrupted){
        String subtypeString = itemSubtype.toString();
        if (isCorrupted){
            subtypeString += "_CORRUPTED";
        } else {
            subtypeString += "_STANDARD";
        }
        try {
            return Implicits.valueOf(subtypeString);
        }catch (IllegalArgumentException exception){
            exception.printStackTrace();
            Utils.error("Implicit mapping error (Implicits Enum)");
            return AXE_STANDARD;
        }
    }
}
