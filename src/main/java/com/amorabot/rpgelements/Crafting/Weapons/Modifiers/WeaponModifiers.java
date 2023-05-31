package com.amorabot.rpgelements.Crafting.Weapons.Modifiers;

import com.amorabot.rpgelements.Crafting.Affix;
import com.amorabot.rpgelements.Crafting.RangeTypes;

public enum WeaponModifiers {
    // TODO: TAG SYSTEM AND IMPLEMENTATION OF RANGE TYPES
    STAMINA(Affix.PREFIX, "+# to Stamina", 6, RangeTypes.SINGLE_RANGE, false, false, 1),
    HYBRID_PHYS_ACC(Affix.PREFIX, "+# to Physical DMG\n+@ to Accuracy", 8, RangeTypes.DOUBLE_RANGE, true, false, 1),
    PERCENT_PHYSICAL(Affix.PREFIX, "+#% Increased Physical DMG", 6, RangeTypes.SINGLE_RANGE, false, false, 1),
    PERCENT_ELEMENTAL(Affix.PREFIX, "+#% Increased Elemental DMG", 6, RangeTypes.SINGLE_RANGE, false, false, 1),
    ADDED_PHYSICAL(Affix.PREFIX, "Added # to @ Physical DMG", 6, RangeTypes.DOUBLE_RANGE, false, false, 1),
    ADDED_FIRE(Affix.PREFIX, "Added # to @ Fire DMG", 6, RangeTypes.DOUBLE_RANGE, false, false, 1),
    ADDED_COLD(Affix.PREFIX, "Added # to @ Cold DMG", 10, RangeTypes.DOUBLE_RANGE, false, false, 1),
    ADDED_LIGHTNING(Affix.PREFIX, "Added # to @ Lightning DMG", 10, RangeTypes.DOUBLE_RANGE, false, false, 1),
    ADDED_ABYSSAL(Affix.PREFIX, "Added # to @ Abyssal DMG", 1, RangeTypes.DOUBLE_RANGE, false, false, 1), //ou void
    PHYSICAL_LIFESTEAL(Affix.PREFIX, "+#% Physical lifesteal", 3, RangeTypes.SINGLE_RANGE, false, false, 1),



    STRENGTH(Affix.SUFFIX, "+# STR", 6, RangeTypes.SINGLE_RANGE, false, false, 1),
    DEXTERITY(Affix.SUFFIX, "+# DEX", 6, RangeTypes.SINGLE_RANGE, false, false, 1),
    INTELLIGENCE(Affix.SUFFIX, "+# INT", 6, RangeTypes.SINGLE_RANGE, false, false, 1),
    ATTACK_SPEED(Affix.SUFFIX, "+#% Increased Attack Speed", 7, RangeTypes.SINGLE_RANGE, false, false, 1),
    LIFE_ON_HIT(Affix.SUFFIX, "+# Life per hit", 4, RangeTypes.SINGLE_RANGE, false, false, 1),
    ACCURACY(Affix.SUFFIX, "+# to Accuracy", 5, RangeTypes.SINGLE_RANGE, false, false, 1),
    BLEEDING(Affix.SUFFIX, "+#% Chance to cause Bleeding", 3, RangeTypes.SINGLE_RANGE, true, false, 1),
    CRITICAL_CHANCE(Affix.SUFFIX, "+#% Increased Crit Chance", 6, RangeTypes.SINGLE_RANGE, false, false, 1)
    ;


    private final Affix affix;
    private final String displayName;
    private final int tiers;
    private final RangeTypes rangeType;
    private final boolean hybrid;
    private final boolean unique;
    private final int weight;

    WeaponModifiers(Affix affix, String displayName, int tiers, RangeTypes range, boolean hybrid, boolean unique, int weight) {
        this.affix = affix;
        this.displayName = displayName;
        this.tiers = tiers;
        this.rangeType = range;
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
}
