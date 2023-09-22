package com.amorabot.rpgelements.components.Items.Armor;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.*;
import com.amorabot.rpgelements.components.Items.Interfaces.ItemModifier;

import java.util.List;

public enum ArmorModifiers implements ItemModifier {

    HEALTH(Affix.PREFIX, "+@value1@ Health", 12,
            TargetStats.HEALTH, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.HEALTH), false, 1),
    HEALTH_PERCENT(Affix.PREFIX, "+@value1@% Inc. Health", 6,
            TargetStats.HEALTH, ValueTypes.PERCENT_ADDED, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.HEALTH), false, 1),
    HEALTH_REGEN_PERCENT(Affix.PREFIX, "+@value1@% Health Regen", 6,
            TargetStats.HEALTH, ValueTypes.PERCENT_ADDED, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.HEALTH), false, 1),
    ARMOR(Affix.PREFIX, "+@value1@ Armor", 10,
            TargetStats.ARMOR, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.PHYSICAL, ModTags.DEFENSE), false, 1),
    ARMOR_PERCENT(Affix.PREFIX, "+@value1@% Inc. Armor", 6,
            TargetStats.ARMOR, ValueTypes.PERCENT_ADDED, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.PHYSICAL, ModTags.DEFENSE), false, 1),
    DODGE(Affix.PREFIX, "+@value1@ Dodge", 4,
            TargetStats.DODGE, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.UTILITY, ModTags.DEFENSE), false, 1),
    WARD(Affix.PREFIX, "+@value1@ Ward", 12,
            TargetStats.WARD, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.HEALTH, ModTags.DEFENSE), false, 1),
    WARD_PERCENT(Affix.PREFIX, "+@value1@% Inc. Ward", 6,
            TargetStats.WARD, ValueTypes.PERCENT_ADDED, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.HEALTH, ModTags.DEFENSE), false, 1),
    ARMOR_HEALTH(Affix.PREFIX, "+@value1@ Armor / +@value2@ Health", 4,//-------------------------
            TargetStats.ARMOR_HEALTH, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
            List.of(ModTags.HEALTH,ModTags.PHYSICAL, ModTags.DEFENSE), true, 1),
    WARD_HEALTH(Affix.PREFIX, "+@value1@ Ward / +@value2@ Health", 4,
            TargetStats.WARD_HEALTH, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
            List.of(ModTags.HEALTH, ModTags.DEFENSE), true, 1),
    DODGE_HEALTH(Affix.PREFIX, "+@value1@ Dodge / +@value2@ Health", 4,
            TargetStats.DODGE_HEALTH, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
            List.of(ModTags.UTILITY, ModTags.HEALTH, ModTags.DEFENSE), true, 1),
    ARMOR_WARD(Affix.PREFIX, "+@value1@ Armor / +@value2@ Ward", 4,
            TargetStats.ARMOR_WARD, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
            List.of(ModTags.HEALTH,ModTags.PHYSICAL, ModTags.DEFENSE), true, 1),
    ARMOR_DODGE(Affix.PREFIX, "+@value1@ Armor / +@value2@% Dodge", 4,
            TargetStats.ARMOR_DODGE, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
            List.of(ModTags.UTILITY, ModTags.PHYSICAL, ModTags.DEFENSE), true, 1),
    DODGE_WARD(Affix.PREFIX, "+@value1@ Dodge / +@value2@ Ward", 4,
            TargetStats.DODGE_WARD, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
            List.of(ModTags.UTILITY), true, 1),//-------------------------------------------------
    WALK_SPEED(Affix.PREFIX, "+@value1@% Walk speed", 4,
            TargetStats.WALK_SPEED, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.UTILITY), false, 1),
    STAMINA(Affix.PREFIX, "+@value1@ Stamina", 6,
            TargetStats.STAMINA, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.UTILITY), false, 1),
    STRENGTH_PERCENT(Affix.PREFIX, "+@value1@% STR", 3,
            TargetStats.STRENGTH, ValueTypes.PERCENT_MULTI, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.UTILITY), false, 1),
    DEXTERITY_PERCENT(Affix.PREFIX, "+@value1@% DEX", 3,
            TargetStats.DEXTERITY, ValueTypes.PERCENT_MULTI, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.UTILITY), false, 1),
    INTELLIGENCE_PERCENT(Affix.PREFIX, "+@value1@% INT", 3,
            TargetStats.INTELLIGENCE, ValueTypes.PERCENT_MULTI, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.UTILITY), false, 1),


    STRENGTH(Affix.SUFFIX, "+@value1@ STR", 8,
            TargetStats.STRENGTH, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.UTILITY), false, 1),
    DEXTERITY(Affix.SUFFIX, "+@value1@ DEX", 8,
            TargetStats.DEXTERITY, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.UTILITY), false, 1),
    INTELLIGENCE(Affix.SUFFIX, "+@value1@ INT", 8,
            TargetStats.INTELLIGENCE, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.UTILITY), false, 1),
    HEALTH_REGEN(Affix.SUFFIX, "+@value1@ Health Regen", 8,
            TargetStats.HEALTH_REGEN, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.UTILITY), false, 1),
    FIRE_RESISTANCE(Affix.SUFFIX, "+@value1@% Fire Resistance", 8,
            TargetStats.FIRE_RESISTANCE, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.UTILITY), false, 1),
    LIGHTNING_RESISTANCE(Affix.SUFFIX, "+@value1@% Lightning Resistance", 8,
            TargetStats.LIGHTNING_RESISTANCE, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.UTILITY), false, 1),
    COLD_RESISTANCE(Affix.SUFFIX, "+@value1@% Cold Resistance", 8,
            TargetStats.COLD_RESISTANCE, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.UTILITY), false, 1),
    ABYSSAL_RESISTANCE(Affix.SUFFIX, "+@value1@% Abyssal Resistance", 8,
            TargetStats.ABYSSAL_RESISTANCE, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.UTILITY), false, 1);

    private final Affix affix;
    private final String displayName;
    private final int tiers;
    private final TargetStats targetStat;
    private final ValueTypes valueType;
    private final RangeTypes rangeType;
    private final List<ModTags> modTags;
    private final boolean hybrid;
    private final int weight;
    ArmorModifiers(Affix affix, String displayName, int tiers, TargetStats targetStat, ValueTypes valueType, RangeTypes range, List<ModTags> modTags, boolean hybrid, int weight) {
        this.affix = affix;
        this.displayName = displayName;
        this.tiers = tiers;
        this.targetStat = targetStat;
        this.valueType = valueType;
        this.rangeType = range;
        this.modTags = modTags;
        this.hybrid = hybrid;
        this.weight = weight;
    }

    @Override
    public Affix getAffixType() {
        return affix;
    }
    @Override
    public String getDisplayName() {
        return displayName;
    }
    @Override
    public TargetStats getTargetStat() {
        return targetStat;
    }
    @Override
    public ValueTypes getValueType() {
        return valueType;
    }
    @Override
    public RangeTypes getRangeType() {
        return rangeType;
    }
    @Override
    public int getNumberOfTiers() {
        return tiers;
    }
    @Override
    public int getModifierWeight() {
        return weight;
    }
}
