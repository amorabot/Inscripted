package com.amorabot.rpgelements.components.Items.DataStructures;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum ModifierList {

    HEALTH(
            Set.of(ModifierList.ARMORTAG),Affix.PREFIX, "+@value1@ Health", 12,
            TargetStats.HEALTH, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.HEALTH), false, 1),
    HEALTH_PERCENT(
            Set.of(ModifierList.ARMORTAG),Affix.PREFIX, "+@value1@% Inc. Health", 6,
            TargetStats.HEALTH, ValueTypes.PERCENT_ADDED, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.HEALTH), false, 1),
    HEALTH_REGEN_PERCENT(
            Set.of(ModifierList.ARMORTAG),Affix.PREFIX, "+@value1@% Health Regen", 6,
            TargetStats.HEALTH, ValueTypes.PERCENT_ADDED, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.HEALTH), false, 1),
    ARMOR(
            Set.of(ModifierList.ARMORTAG),Affix.PREFIX, "+@value1@ Armor", 10,
            TargetStats.ARMOR, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.PHYSICAL, ModTags.DEFENSE), false, 1),
    ARMOR_PERCENT(
            Set.of(ModifierList.ARMORTAG),Affix.PREFIX, "+@value1@% Inc. Armor", 6,
            TargetStats.ARMOR, ValueTypes.PERCENT_ADDED, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.PHYSICAL, ModTags.DEFENSE), false, 1),
    DODGE(
            Set.of(ModifierList.ARMORTAG),Affix.PREFIX, "+@value1@ Dodge", 4,
            TargetStats.DODGE, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY, ModTags.DEFENSE), false, 1),
    WARD(
            Set.of(ModifierList.ARMORTAG),Affix.PREFIX, "+@value1@ Ward", 12,
            TargetStats.WARD, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.HEALTH, ModTags.DEFENSE), false, 1),
    WARD_PERCENT(
            Set.of(ModifierList.ARMORTAG),Affix.PREFIX, "+@value1@% Inc. Ward", 6,
            TargetStats.WARD, ValueTypes.PERCENT_ADDED, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.HEALTH, ModTags.DEFENSE), false, 1),
    ARMOR_HEALTH(
            Set.of(ModifierList.ARMORTAG),Affix.PREFIX, "+@value1@ Armor / +@value2@ Health", 4,//-------------------------
            TargetStats.ARMOR_HEALTH, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
            Set.of(ModTags.HEALTH,ModTags.PHYSICAL, ModTags.DEFENSE), true, 1),
    WARD_HEALTH(
            Set.of(ModifierList.ARMORTAG),Affix.PREFIX, "+@value1@ Ward / +@value2@ Health", 4,
            TargetStats.WARD_HEALTH, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
            Set.of(ModTags.HEALTH, ModTags.DEFENSE), true, 1),
    DODGE_HEALTH(
            Set.of(ModifierList.ARMORTAG),Affix.PREFIX, "+@value1@ Dodge / +@value2@ Health", 4,
            TargetStats.DODGE_HEALTH, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
            Set.of(ModTags.UTILITY, ModTags.HEALTH, ModTags.DEFENSE), true, 1),
    ARMOR_WARD(
            Set.of(ModifierList.ARMORTAG),Affix.PREFIX, "+@value1@ Armor / +@value2@ Ward", 4,
            TargetStats.ARMOR_WARD, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
            Set.of(ModTags.HEALTH,ModTags.PHYSICAL, ModTags.DEFENSE), true, 1),
    ARMOR_DODGE(
            Set.of(ModifierList.ARMORTAG),Affix.PREFIX, "+@value1@ Armor / +@value2@% Dodge", 4,
            TargetStats.ARMOR_DODGE, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
            Set.of(ModTags.UTILITY, ModTags.PHYSICAL, ModTags.DEFENSE), true, 1),
    DODGE_WARD(
            Set.of(ModifierList.ARMORTAG),Affix.PREFIX, "+@value1@ Dodge / +@value2@ Ward", 4,
            TargetStats.DODGE_WARD, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
            Set.of(ModTags.UTILITY), true, 1),//-------------------------------------------------
    WALK_SPEED(
            Set.of(ModifierList.ARMORTAG),Affix.PREFIX, "+@value1@% Walk speed", 4,
            TargetStats.WALK_SPEED, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),
    STRENGTH_PERCENT(
            Set.of(ModifierList.ARMORTAG),Affix.PREFIX, "+@value1@% STR", 3,
            TargetStats.STRENGTH, ValueTypes.PERCENT_MULTI, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),
    DEXTERITY_PERCENT(
            Set.of(ModifierList.ARMORTAG),Affix.PREFIX, "+@value1@% DEX", 3,
            TargetStats.DEXTERITY, ValueTypes.PERCENT_MULTI, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),
    INTELLIGENCE_PERCENT(
            Set.of(ModifierList.ARMORTAG),Affix.PREFIX, "+@value1@% INT", 3,
            TargetStats.INTELLIGENCE, ValueTypes.PERCENT_MULTI, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),


    STAMINA(
            Set.of(ModifierList.ARMORTAG, ModifierList.WEAPONTAG),Affix.PREFIX, "+@value1@ Stamina", 6,
            TargetStats.STAMINA, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),



    STRENGTH(
            Set.of(ModifierList.ARMORTAG, ModifierList.WEAPONTAG),Affix.SUFFIX, "+@value1@ STR", 8,
            TargetStats.STRENGTH, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),
    DEXTERITY(
            Set.of(ModifierList.ARMORTAG, ModifierList.WEAPONTAG),Affix.SUFFIX, "+@value1@ DEX", 8,
            TargetStats.DEXTERITY, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),
    INTELLIGENCE(
            Set.of(ModifierList.ARMORTAG, ModifierList.WEAPONTAG),Affix.SUFFIX, "+@value1@ INT", 8,
            TargetStats.INTELLIGENCE, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),
    HEALTH_REGEN(
            Set.of(ModifierList.ARMORTAG),Affix.SUFFIX, "+@value1@ Health Regen", 8,
            TargetStats.HEALTH_REGEN, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),
    FIRE_RESISTANCE(
            Set.of(ModifierList.ARMORTAG),Affix.SUFFIX, "+@value1@% Fire Resistance", 8,
            TargetStats.FIRE_RESISTANCE, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),
    LIGHTNING_RESISTANCE(
            Set.of(ModifierList.ARMORTAG),Affix.SUFFIX, "+@value1@% Lightning Resistance", 8,
            TargetStats.LIGHTNING_RESISTANCE, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),
    COLD_RESISTANCE(
            Set.of(ModifierList.ARMORTAG),Affix.SUFFIX, "+@value1@% Cold Resistance", 8,
            TargetStats.COLD_RESISTANCE, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),
    ABYSSAL_RESISTANCE(
            Set.of(ModifierList.ARMORTAG),Affix.SUFFIX, "+@value1@% Abyssal Resistance", 8,
            TargetStats.ABYSSAL_RESISTANCE, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),








//    STAMINA(Affix.PREFIX, "+@value1@ Stamina", 6,
//            TargetStats.STAMINA, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
//            List.of(ModTags.UTILITY), false, 1),
    HYBRID_PHYS_ACC(
            Set.of(ModifierList.WEAPONTAG),Affix.PREFIX, "+@hvalue1@ Physical DMG / +@hvalue2@ Accuracy", 8,
            TargetStats.ACCURACY, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
            Set.of(ModTags.DAMAGE, ModTags.PHYSICAL), true, 1),
    PERCENT_PHYSICAL(Set.of(ModifierList.WEAPONTAG),Affix.PREFIX, "+@value1@% Inc. Physical DMG", 8,
                     TargetStats.PHYSICAL_DAMAGE, ValueTypes.PERCENT_ADDED, RangeTypes.SINGLE_RANGE,
                     Set.of(ModTags.DAMAGE, ModTags.PHYSICAL), false, 1),
    PERCENT_ELEMENTAL(Set.of(ModifierList.WEAPONTAG),Affix.PREFIX, "+@value1@% Inc. Elemental DMG", 8,
                      TargetStats.ELEMENTAL_DAMAGE, ValueTypes.PERCENT_ADDED, RangeTypes.SINGLE_RANGE,
                      Set.of(ModTags.DAMAGE, ModTags.ELEMENTAL), false, 1),
    ADDED_PHYSICAL(Set.of(ModifierList.WEAPONTAG),Affix.PREFIX, "+@value1@ - @value2@ Physical DMG", 9,
                   TargetStats.PHYSICAL_DAMAGE, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
                   Set.of(ModTags.DAMAGE, ModTags.PHYSICAL), false, 1),
    ADDED_FIRE(Set.of(ModifierList.WEAPONTAG),Affix.PREFIX, "+@value1@ - @value2@ Fire DMG", 10,
               TargetStats.FIRE_DAMAGE, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
               Set.of(ModTags.DAMAGE, ModTags.ELEMENTAL), false, 1),
    ADDED_COLD(Set.of(ModifierList.WEAPONTAG),Affix.PREFIX, "+@value1@ - @value2@ Cold DMG", 10,
               TargetStats.COLD_DAMAGE, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
               Set.of(ModTags.DAMAGE, ModTags.ELEMENTAL), false, 1),
    ADDED_LIGHTNING(Set.of(ModifierList.WEAPONTAG),Affix.PREFIX, "+@value1@ - @value2@ Lightning DMG", 10,
                    TargetStats.LIGHTNING_DAMAGE, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
                    Set.of(ModTags.DAMAGE, ModTags.ELEMENTAL), false, 1),
    ADDED_ABYSSAL(Set.of(ModifierList.WEAPONTAG),Affix.PREFIX, "+@value1@ - @value2@ Abyssal DMG", 1,
                  TargetStats.ABYSSAL_DAMAGE, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
                  Set.of(ModTags.DAMAGE, ModTags.ABYSSAL), false, 1), //ou void
    PHYSICAL_LIFESTEAL(Set.of(ModifierList.WEAPONTAG),Affix.PREFIX, "+@value1@% Physical lifesteal", 3,
                       TargetStats.LIFESTEAL, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_RANGE,
                       Set.of(ModTags.UTILITY), false, 1),
    SHRED(Set.of(ModifierList.WEAPONTAG),Affix.PREFIX, "+@value1@% Shred", 5,
          TargetStats.SHRED, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_RANGE,
          Set.of(ModTags.DAMAGE,ModTags.PHYSICAL), false, 1),
    MAELSTROM(Set.of(ModifierList.WEAPONTAG),Affix.PREFIX, "+@value1@% Maelstrom", 5,
              TargetStats.MAELSTROM, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_RANGE,
              Set.of(ModTags.DAMAGE,ModTags.ELEMENTAL), false, 1),
    //extra proj



//    STRENGTH(Affix.SUFFIX, "+@value1@ STR", 8,
//             TargetStats.STRENGTH, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
//             List.of(ModTags.ATTRIBUTE, ModTags.HEALTH), false, 1),
//    DEXTERITY(Affix.SUFFIX, "+@value1@ DEX", 8,
//              TargetStats.DEXTERITY, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
//              List.of(ModTags.ATTRIBUTE), false, 1),
//    INTELLIGENCE(Affix.SUFFIX, "+@value1@ INT", 8,
//                 TargetStats.INTELLIGENCE, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
//                 List.of(ModTags.ATTRIBUTE), false, 1),
    //    ATTACK_SPEED(Affix.SUFFIX, "+@value1@% Inc. Attack Speed", 7,
//            TargetStats.PHYSICAL_DAMAGE, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_RANGE,
//            List.of(ModTags.DAMAGE, ModTags.UTILITY), false, 1),
    LIFE_ON_HIT(Set.of(ModifierList.WEAPONTAG),Affix.SUFFIX, "+@value1@ Life per hit", 5,
                TargetStats.LIFE_ON_HIT, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
                Set.of(ModTags.UTILITY), false, 1),
    ACCURACY(Set.of(ModifierList.WEAPONTAG),Affix.SUFFIX, "+@value1@ Accuracy", 5,
             TargetStats.ACCURACY, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
             Set.of(ModTags.UTILITY), false, 1),
    STAMINA_REGEN(Set.of(ModifierList.WEAPONTAG),Affix.SUFFIX, "+@value1@% Stamina regen", 6,
                  TargetStats.STAMINA, ValueTypes.PERCENT_ADDED, RangeTypes.SINGLE_RANGE,
                  Set.of(ModTags.DAMAGE,ModTags.PHYSICAL), false, 1),
    BLEEDING(Set.of(ModifierList.WEAPONTAG),Affix.SUFFIX, "+@value1@% Bleeding chance", 4,
             TargetStats.BLEED, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_RANGE,
             Set.of(ModTags.PHYSICAL,ModTags.AILMENT), false, 1),
    CRITICAL_CHANCE(Set.of(ModifierList.WEAPONTAG),Affix.SUFFIX, "+@value1@% Inc. Crit Chance", 6,
                    TargetStats.CRITICAL, ValueTypes.PERCENT_ADDED, RangeTypes.SINGLE_RANGE,
                    Set.of(ModTags.DAMAGE), false, 1);
    //TODO: adicionar freeze, ignite, shock (sonzinho de poção)







    public static final String WEAPONTAG = "#WEAPONMOD";
    public static final String ARMORTAG = "#ARMORMOD"; //Encapsulates HELMET, CHESTPLATE, ... ItemTypes values

    private final Set<String> filterTags;
    private final Affix affix;
    private final String displayName;
    private final int tiers;
    private final TargetStats targetStat;
    private final ValueTypes valueType;
    private final RangeTypes rangeType;
    private final Set<ModTags> modTags;
    private final boolean hybrid;
    private final int weight;

    ModifierList(Set<String> filterTags, Affix affix, String displayName, int tiers, TargetStats targetStat, ValueTypes valueType, RangeTypes range, Set<ModTags> modTags, boolean hybrid, int weight) {
        Set<String> allFilters = new HashSet<>();
        Set<String> armors = Set.of(ItemTypes.HELMET.toString(), ItemTypes.CHESTPLATE.toString(), ItemTypes.LEGGINGS.toString(), ItemTypes.BOOTS.toString());
        if (filterTags.contains(WEAPONTAG)){
            allFilters.add(WEAPONTAG);
        }
        if (filterTags.contains(ARMORTAG)){
            allFilters.addAll(armors);
        }
        if (allFilters.isEmpty()){
            allFilters.add("NONE"); //Mods without filters wont be selected when generating fully generic items. (But can be specifically selected via this tag)
        }

        this.filterTags = allFilters;
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
    public List<ModifierList> getWeaponMods(){
        List<ModifierList> weaponMods = new ArrayList<>();
        for (ModifierList mod : ModifierList.values()){
            addIfMatch(weaponMods, mod, WEAPONTAG);
        }
        return weaponMods;
    }
    public List<ModifierList> getArmorMods(){
        List<ModifierList> armorMods = new ArrayList<>();
        for (ModifierList mod : ModifierList.values()){
            addIfMatch(armorMods, mod, ARMORTAG);
        }
        return armorMods;
    }
    private void addIfMatch(List<ModifierList> modSet, ModifierList mod, String filterString){
        if (mod.filterTags.contains(filterString)){
            modSet.add(mod);
        }
    }
    public Affix getAffixType() {
        return affix;
    }
    public String getDisplayName() {
        return displayName;
    }
    public int getNumberOfTiers() {
        return tiers;
    }
    public TargetStats getTargetStat() {
        return targetStat;
    }
    public ValueTypes getValueType() {
        return valueType;
    }
    public RangeTypes getRangeType() {
        return rangeType;
    }
    public Set<ModTags> getModTags() {
        return modTags;
    }
    public boolean isHybrid() {
        return hybrid;
    }
    public int getModifierWeight() {
        return weight;
    }
}
