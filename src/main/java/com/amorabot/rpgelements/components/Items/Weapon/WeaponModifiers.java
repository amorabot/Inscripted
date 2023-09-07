package com.amorabot.rpgelements.components.Items.Weapon;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.*;
import com.amorabot.rpgelements.components.Items.Interfaces.ItemModifier;

import java.util.ArrayList;
import java.util.List;

public enum WeaponModifiers implements ItemModifier {
    STAMINA(Affix.PREFIX, "+@value1@ Stamina", 6,
            TargetStats.STAMINA, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.UTILITY), false, 1),
//    HYBRID_PHYS_ACC(Affix.PREFIX, "+@hvalue1@ Physical DMG\n+@hvalue2@ Accuracy", 8,
//                    TargetStats.ACCURACY, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
//                    List.of(ModTags.DAMAGE, ModTags.PHYSICAL), true, 1),
    PERCENT_PHYSICAL(Affix.PREFIX, "+@value1@% Inc. Physical DMG", 8,
                    TargetStats.PHYSICAL_DAMAGE, ValueTypes.PERCENT_ADDED, RangeTypes.SINGLE_RANGE,
                    List.of(ModTags.DAMAGE, ModTags.PHYSICAL), false, 1),
    PERCENT_ELEMENTAL(Affix.PREFIX, "+@value1@% Inc. Elemental DMG", 8,
                    TargetStats.ELEMENTAL_DAMAGE, ValueTypes.PERCENT_ADDED, RangeTypes.SINGLE_RANGE,
                    List.of(ModTags.DAMAGE, ModTags.ELEMENTAL), false, 1),
    ADDED_PHYSICAL(Affix.PREFIX, "+@value1@ - @value2@ Physical DMG", 9,
                    TargetStats.PHYSICAL_DAMAGE, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
                    List.of(ModTags.DAMAGE, ModTags.PHYSICAL), false, 1),
    ADDED_FIRE(Affix.PREFIX, "+@value1@ - @value2@ Fire DMG", 10,
                TargetStats.FIRE_DAMAGE, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
                List.of(ModTags.DAMAGE, ModTags.ELEMENTAL), false, 1),
    ADDED_COLD(Affix.PREFIX, "+@value1@ - @value2@ Cold DMG", 10,
                TargetStats.COLD_DAMAGE, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
                List.of(ModTags.DAMAGE, ModTags.ELEMENTAL), false, 1),
    ADDED_LIGHTNING(Affix.PREFIX, "+@value1@ - @value2@ Lightning DMG", 10,
                    TargetStats.LIGHTNING_DAMAGE, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
                    List.of(ModTags.DAMAGE, ModTags.ELEMENTAL), false, 1),
    ADDED_ABYSSAL(Affix.PREFIX, "+@value1@ - @value2@ Abyssal DMG", 1,
                    TargetStats.ABYSSAL_DAMAGE, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
                    List.of(ModTags.DAMAGE, ModTags.ABYSSAL), false, 1), //ou void
    PHYSICAL_LIFESTEAL(Affix.PREFIX, "+@value1@% Physical lifesteal", 3,
                        TargetStats.LIFESTEAL, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_RANGE,
                        List.of(ModTags.UTILITY), false, 1),
    SHRED(Affix.PREFIX, "+@value1@% Shred", 5,
            TargetStats.SHRED, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.DAMAGE,ModTags.PHYSICAL), false, 1),
    MAELSTROM(Affix.PREFIX, "+@value1@% Maelstrom", 5,
            TargetStats.MAELSTROM, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.DAMAGE,ModTags.ELEMENTAL), false, 1),
    //extra proj



    STRENGTH(Affix.SUFFIX, "+@value1@ STR", 8,
            TargetStats.STRENGTH, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.ATTRIBUTE, ModTags.HEALTH), false, 1),
    DEXTERITY(Affix.SUFFIX, "+@value1@ DEX", 8,
            TargetStats.DEXTERITY, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.ATTRIBUTE), false, 1),
    INTELLIGENCE(Affix.SUFFIX, "+@value1@ INT", 8,
            TargetStats.INTELLIGENCE, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.ATTRIBUTE), false, 1),
//    ATTACK_SPEED(Affix.SUFFIX, "+@value1@% Inc. Attack Speed", 7,
//            TargetStats.PHYSICAL_DAMAGE, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_RANGE,
//            List.of(ModTags.DAMAGE, ModTags.UTILITY), false, 1),
    LIFE_ON_HIT(Affix.SUFFIX, "+@value1@ Life per hit", 5,
            TargetStats.LIFE_ON_HIT, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.UTILITY), false, 1),
    ACCURACY(Affix.SUFFIX, "+@value1@ Accuracy", 5,
            TargetStats.ACCURACY, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.UTILITY), false, 1),
    STAMINA_REGEN(Affix.SUFFIX, "+@value1@% Stamina regen", 6,
            TargetStats.STAMINA, ValueTypes.PERCENT_ADDED, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.DAMAGE,ModTags.PHYSICAL), false, 1),
    BLEEDING(Affix.SUFFIX, "+@value1@% Bleeding chance", 4,
            TargetStats.BLEED, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.PHYSICAL,ModTags.AILMENT), false, 1),
    CRITICAL_CHANCE(Affix.SUFFIX, "+@value1@% Inc. Crit Chance", 6,
            TargetStats.CRITICAL, ValueTypes.PERCENT_ADDED, RangeTypes.SINGLE_RANGE,
            List.of(ModTags.DAMAGE), false, 1);
    //TODO: adicionar freeze, ignite, shock (sonzinho de poção)


    private final Affix affix;
    private final String displayName;
    private final int tiers;
    private final TargetStats targetStat;
    private final ValueTypes valueType;
    private final RangeTypes rangeType;
    private final List<ModTags> modTags;
    private final boolean hybrid;
    private final int weight;

    WeaponModifiers(Affix affix, String displayName, int tiers, TargetStats targetStat, ValueTypes valueType, RangeTypes range, List<ModTags> modTags, boolean hybrid, int weight) {
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
        return this.affix;
    }
    @Override
    public String getDisplayName() {
        return this.displayName;
    }
    @Override
    public int getNumberOfTiers() {
        return this.tiers;
    }
    @Override
    public TargetStats getTargetStat() {
        return this.targetStat;
    }
    @Override
    public ValueTypes getValueType() {
        return this.valueType;
    }
    @Override
    public RangeTypes getRangeType() {
        return this.rangeType;
    }
    @Override
    public int getModifierWeight() {
        return this.weight;
    }
    public List<ModTags> getModTags(){
        return modTags;
    }

    public List<WeaponModifiers> getPrefixes() {
        List<WeaponModifiers> aux = new ArrayList<>();
        for (WeaponModifiers mod : WeaponModifiers.values()){
            if (mod.affix == Affix.PREFIX){
                aux.add(mod);
            }
        }
        return aux;
    }
    public List<WeaponModifiers> getSuffixes() {
        List<WeaponModifiers> aux = new ArrayList<>();
        for (WeaponModifiers mod : WeaponModifiers.values()){
            if (mod.affix == Affix.SUFFIX){
                aux.add(mod);
            }
        }
        return aux;
    }
    public List<WeaponModifiers> getUniques() {
        List<WeaponModifiers> aux = new ArrayList<>();
        for (WeaponModifiers mod : WeaponModifiers.values()){
            if (mod.affix == Affix.UNIQUE){
                aux.add(mod);
            }
        }
        return aux;
    }
}
