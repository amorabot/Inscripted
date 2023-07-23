package com.amorabot.rpgelements.Crafting.Weapons.Modifiers;

import com.amorabot.rpgelements.Crafting.Affix;
import com.amorabot.rpgelements.Crafting.ModTags;
import com.amorabot.rpgelements.Crafting.RangeTypes;

import java.util.List;

public enum WeaponModifiers {
    STAMINA(Affix.PREFIX,
            "+# Stamina", 6, RangeTypes.SINGLE_RANGE, List.of(ModTags.UTILITY), false, false, 1),
    HYBRID_PHYS_ACC(Affix.PREFIX,
            "+# Physical DMG\n+@ Accuracy", 8, RangeTypes.DOUBLE_RANGE, List.of(ModTags.DAMAGE, ModTags.PHYSICAL), true, false, 1),
    PERCENT_PHYSICAL(Affix.PREFIX,
            "+#% Inc. Physical DMG", 6, RangeTypes.SINGLE_RANGE, List.of(ModTags.DAMAGE, ModTags.PHYSICAL), false, false, 1),
    PERCENT_ELEMENTAL(Affix.PREFIX,
            "+#% Inc. Elemental DMG", 6, RangeTypes.SINGLE_RANGE, List.of(ModTags.DAMAGE, ModTags.ELEMENTAL), false, false, 1),
    ADDED_PHYSICAL(Affix.PREFIX,
            "+# - @ Physical DMG", 6, RangeTypes.DOUBLE_RANGE, List.of(ModTags.DAMAGE, ModTags.PHYSICAL), false, false, 1),
    ADDED_FIRE(Affix.PREFIX,
            "+# - @ Fire DMG", 6, RangeTypes.DOUBLE_RANGE, List.of(ModTags.DAMAGE, ModTags.ELEMENTAL), false, false, 1),
    ADDED_COLD(Affix.PREFIX,
            "+# - @ Cold DMG", 10, RangeTypes.DOUBLE_RANGE, List.of(ModTags.DAMAGE, ModTags.ELEMENTAL), false, false, 1),
    ADDED_LIGHTNING(Affix.PREFIX,
            "+# - @ Lightning DMG", 10, RangeTypes.DOUBLE_RANGE, List.of(ModTags.DAMAGE, ModTags.ELEMENTAL), false, false, 1),
    ADDED_ABYSSAL(Affix.PREFIX,
            "+# - @ Abyssal DMG", 1, RangeTypes.DOUBLE_RANGE, List.of(ModTags.DAMAGE, ModTags.ABYSSAL), false, false, 1), //ou void
    PHYSICAL_LIFESTEAL(Affix.PREFIX,
            "+#% Physical lifesteal", 3, RangeTypes.SINGLE_RANGE, List.of(ModTags.UTILITY), false, false, 1),



    STRENGTH(Affix.SUFFIX, "+# STR", 6, RangeTypes.SINGLE_RANGE, List.of(ModTags.ATTRIBUTE, ModTags.HEALTH), false, false, 1),
    DEXTERITY(Affix.SUFFIX, "+# DEX", 6, RangeTypes.SINGLE_RANGE, List.of(ModTags.ATTRIBUTE), false, false, 1),
    INTELLIGENCE(Affix.SUFFIX, "+# INT", 6, RangeTypes.SINGLE_RANGE, List.of(ModTags.ATTRIBUTE), false, false, 1),
    ATTACK_SPEED(Affix.SUFFIX, "+#% Inc. Attack Speed", 7, RangeTypes.SINGLE_RANGE, List.of(ModTags.DAMAGE, ModTags.UTILITY), false, false, 1),
    LIFE_ON_HIT(Affix.SUFFIX, "+# Life per hit", 4, RangeTypes.SINGLE_RANGE, List.of(ModTags.UTILITY), false, false, 1),
    ACCURACY(Affix.SUFFIX, "+# Accuracy", 5, RangeTypes.SINGLE_RANGE, List.of(ModTags.UTILITY), false, false, 1),
    BLEEDING(Affix.SUFFIX, "+#% Bleeding chance", 3, RangeTypes.SINGLE_RANGE, List.of(ModTags.PHYSICAL,ModTags.AILMENT), true, false, 1),
    CRITICAL_CHANCE(Affix.SUFFIX, "+#% Inc. Crit Chance", 6, RangeTypes.SINGLE_RANGE, List.of(ModTags.DAMAGE), false, false, 1);


    private final Affix affix;
    private final String displayName;
    private final int tiers;
    private final RangeTypes rangeType;
    private final List<ModTags> modTags;
    private final boolean hybrid;
    private final boolean unique;
    private final int weight;

    WeaponModifiers(Affix affix, String displayName, int tiers, RangeTypes range, List<ModTags> modTags, boolean hybrid, boolean unique, int weight) {
        this.affix = affix;
        this.displayName = displayName;
        this.tiers = tiers;
        this.rangeType = range;
        this.modTags = modTags;
        this.hybrid = hybrid;
        this.unique = unique;
        this.weight = weight;
    }

    public Affix getAffixType(){
        return this.affix;
    }
    public int getNumberOfTiers() {
        return tiers;
    }

    public RangeTypes getRangeType(){
        return rangeType;
    }

    public String getDisplayName(){
        return displayName;
    }

    public List<ModTags> getModTags(){
        return modTags;
    }
}
