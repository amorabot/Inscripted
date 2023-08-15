package com.amorabot.rpgelements.components.Items.DataStructures.Enums;

import com.amorabot.rpgelements.components.Items.Interfaces.ItemModifier;

import java.util.ArrayList;
import java.util.List;

public enum WeaponModifiers implements ItemModifier {
    STAMINA(Affix.PREFIX,
            "+@value1@ Stamina", 6, RangeTypes.SINGLE_RANGE, List.of(ModTags.UTILITY), false, 1),
    HYBRID_PHYS_ACC(Affix.PREFIX,
            "+@hvalue1@ Physical DMG\n+@hvalue2@ Accuracy", 8, RangeTypes.DOUBLE_RANGE, List.of(ModTags.DAMAGE, ModTags.PHYSICAL), true, 1),
    PERCENT_PHYSICAL(Affix.PREFIX,
            "+@value1@% Inc. Physical DMG", 6, RangeTypes.SINGLE_RANGE, List.of(ModTags.DAMAGE, ModTags.PHYSICAL), false, 1),
    PERCENT_ELEMENTAL(Affix.PREFIX,
            "+@value1@% Inc. Elemental DMG", 6, RangeTypes.SINGLE_RANGE, List.of(ModTags.DAMAGE, ModTags.ELEMENTAL), false, 1),
    ADDED_PHYSICAL(Affix.PREFIX,
            "+@value1@ - @value2@ Physical DMG", 6, RangeTypes.DOUBLE_RANGE, List.of(ModTags.DAMAGE, ModTags.PHYSICAL), false, 1),
    ADDED_FIRE(Affix.PREFIX,
            "+@value1@ - @value2@ Fire DMG", 6, RangeTypes.DOUBLE_RANGE, List.of(ModTags.DAMAGE, ModTags.ELEMENTAL), false, 1),
    ADDED_COLD(Affix.PREFIX,
            "+@value1@ - @value2@ Cold DMG", 10, RangeTypes.DOUBLE_RANGE, List.of(ModTags.DAMAGE, ModTags.ELEMENTAL), false, 1),
    ADDED_LIGHTNING(Affix.PREFIX,
            "+@value1@ - @value2@ Lightning DMG", 10, RangeTypes.DOUBLE_RANGE, List.of(ModTags.DAMAGE, ModTags.ELEMENTAL), false, 1),
    ADDED_ABYSSAL(Affix.PREFIX,
            "+@value1@ - @value2@ Abyssal DMG", 1, RangeTypes.DOUBLE_RANGE, List.of(ModTags.DAMAGE, ModTags.ABYSSAL), false, 1), //ou void
    PHYSICAL_LIFESTEAL(Affix.PREFIX,
            "+@value1@% Physical lifesteal", 3, RangeTypes.SINGLE_RANGE, List.of(ModTags.UTILITY), false, 1),



    STRENGTH(Affix.SUFFIX, "+@value1@ STR", 6, RangeTypes.SINGLE_RANGE, List.of(ModTags.ATTRIBUTE, ModTags.HEALTH), false, 1),
    DEXTERITY(Affix.SUFFIX, "+@value1@ DEX", 6, RangeTypes.SINGLE_RANGE, List.of(ModTags.ATTRIBUTE), false, 1),
    INTELLIGENCE(Affix.SUFFIX, "+@value1@ INT", 6, RangeTypes.SINGLE_RANGE, List.of(ModTags.ATTRIBUTE), false, 1),
    ATTACK_SPEED(Affix.SUFFIX, "+@value1@% Inc. Attack Speed", 7, RangeTypes.SINGLE_RANGE, List.of(ModTags.DAMAGE, ModTags.UTILITY), false, 1),
    LIFE_ON_HIT(Affix.SUFFIX, "+@value1@ Life per hit", 4, RangeTypes.SINGLE_RANGE, List.of(ModTags.UTILITY), false, 1),
    ACCURACY(Affix.SUFFIX, "+@value1@ Accuracy", 5, RangeTypes.SINGLE_RANGE, List.of(ModTags.UTILITY), false, 1),
    BLEEDING(Affix.SUFFIX, "+@value1@% Bleeding chance", 3, RangeTypes.SINGLE_RANGE, List.of(ModTags.PHYSICAL,ModTags.AILMENT), false, 1),
    CRITICAL_CHANCE(Affix.SUFFIX, "+@value1@% Inc. Crit Chance", 6, RangeTypes.SINGLE_RANGE, List.of(ModTags.DAMAGE), false, 1);


    private final Affix affix;
    private final String displayName;
    private final int tiers;
    private final RangeTypes rangeType;
    private final List<ModTags> modTags;
    private final boolean hybrid;
    private final int weight;

    WeaponModifiers(Affix affix, String displayName, int tiers, RangeTypes range, List<ModTags> modTags, boolean hybrid, int weight) {
        this.affix = affix;
        this.displayName = displayName;
        this.tiers = tiers;
        this.rangeType = range;
        this.modTags = modTags;
        this.hybrid = hybrid;
        this.weight = weight;
    }

    public List<ModTags> getModTags(){
        return modTags;
    }

    @Override
    public Affix getAffixType() {
        return this.affix;
    }
    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public int getModifierWeight() {
        return this.weight;
    }

    @Override
    public List<? extends Enum<?>> getPrefixes() {
        List<WeaponModifiers> aux = new ArrayList<>();
        for (WeaponModifiers mod : WeaponModifiers.values()){
            if (mod.affix == Affix.PREFIX){
                aux.add(mod);
            }
        }
        return aux;
    }

    @Override
    public List<? extends Enum<?>> getSuffixes() {
        List<WeaponModifiers> aux = new ArrayList<>();
        for (WeaponModifiers mod : WeaponModifiers.values()){
            if (mod.affix == Affix.SUFFIX){
                aux.add(mod);
            }
        }
        return aux;
    }

    @Override
    public List<? extends Enum<?>> getUniques() {
        List<WeaponModifiers> aux = new ArrayList<>();
        for (WeaponModifiers mod : WeaponModifiers.values()){
            if (mod.affix == Affix.UNIQUE){
                aux.add(mod);
            }
        }
        return aux;
    }

    @Override
    public int getNumberOfTiers() {
        return this.tiers;
    }
    @Override
    public RangeTypes getRangeType() {
        return this.rangeType;
    }
}
