package com.amorabot.rpgelements.Crafting.Weapons.Modifiers;

public enum WeaponModifiers {
    // TODO: TAG SYSTEM
    STAMINA(true, "+# to Stamina", 12, true, false, false, 1),
    HYBRID_PHYS_ACC(true, "+# to Physical DMG\n+@ to Accuracy", 8, true, true, false, 1),
    PERCENT_PHYSICAL(true, "+#% Increased Physical DMG", 8, true, false, false, 1),
    PERCENT_ELEMENTAL(true, "+#% Increased Elemental DMG", 6, true, false, false, 1),
    ADDED_PHYSICAL(true, "Added # to @ Physical DMG", 8, false, false, false, 1),
    ADDED_FIRE(true, "Added # to @ Fire DMG", 10, false, false, false, 1),
    ADDED_COLD(true, "Added # to @ Cold DMG", 10, false, false, false, 1),
    ADDED_LIGHTNING(true, "Added # to @ Lightning DMG", 10, false, false, false, 1),
    ADDED_ABYSSAL(true, "Added # to @ Abyssal DMG", 1, false, false, false, 1), //ou void
    PHYSICAL_LIFESTEAL(true, "+#% Physical lifesteal", 3, true, false, false, 1),



    STRENGTH(false, "+# STR", 9, true, false, false, 1),
    DEXTERITY(false, "+# DEX", 9, true, false, false, 1),
    INTELLIGENCE(false, "+# INT", 9, true, false, false, 1),
    ATTACK_SPEED(false, "+#% Increased Attack Speed", 8, true, false, false, 1),
    LIFE_ON_HIT(false, "+# Life per hit", 4, true, false, false, 1),
    ACCURACY(false, "+# to Accuracy", 5, true, false, false, 1),
    BLEEDING(false, "+#% Chance to cause Bleeding", 3, true, true, false, 1),
    CRITICAL_CHANCE(false, "+#% Increased Crit Chance", 6, true, false, false, 1)
    ;


    private final boolean prefix;
    private final String displayName;
    private final int tiers;
    private final boolean singeRange;
    private final boolean hybrid;
    private final boolean unique;
    private final int weight;

    WeaponModifiers(boolean prefix, String displayName, int tiers, boolean singleRange, boolean hybrid, boolean unique, int weight) {
        this.prefix = prefix;
        this.displayName = displayName;
        this.tiers = tiers;
        this.singeRange = singleRange;
        this.hybrid = hybrid;
        this.unique = unique;
        this.weight = weight;
    }

    public int getTiers() {
        return tiers;
    }

    public boolean hasSingleRange(){
        return singeRange;
    }

    public String getDisplayName(){
        return displayName;
    }
}
