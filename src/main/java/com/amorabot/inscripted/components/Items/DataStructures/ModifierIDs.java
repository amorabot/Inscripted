package com.amorabot.inscripted.components.Items.DataStructures;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum ModifierIDs {

    HEALTH(
            Set.of(ModifierIDs.ARMORTAG),Affix.PREFIX, "+@value1@ Health", 12,
            TargetStats.HEALTH, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.HEALTH), false, 1),
    HEALTH_PERCENT(
            Set.of(ModifierIDs.ARMORTAG),Affix.PREFIX, "+@value1@% Inc. Health", 6,
            TargetStats.HEALTH, ValueTypes.ADDED, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.HEALTH), false, 1),
    HEALTH_REGEN_PERCENT(
            Set.of(ModifierIDs.ARMORTAG),Affix.PREFIX, "+@value1@% Health Regen", 6,
            TargetStats.HEALTH_REGEN, ValueTypes.ADDED, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.HEALTH), false, 1),
    ARMOR(
            Set.of(ModifierIDs.ARMORTAG),Affix.PREFIX, "+@value1@ Armor", 10,
            TargetStats.ARMOR, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.PHYSICAL, ModTags.DEFENSE), false, 1),
    ARMOR_PERCENT(
            Set.of(ModifierIDs.ARMORTAG),Affix.PREFIX, "+@value1@% Inc. Armor", 6,
            TargetStats.ARMOR, ValueTypes.ADDED, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.PHYSICAL, ModTags.DEFENSE), false, 1),
    DODGE(
            Set.of(ModifierIDs.ARMORTAG),Affix.PREFIX, "+@value1@ Dodge", 4,
            TargetStats.DODGE, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY, ModTags.DEFENSE), false, 1),
    WARD(
            Set.of(ModifierIDs.ARMORTAG),Affix.PREFIX, "+@value1@ Ward", 12,
            TargetStats.WARD, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.HEALTH, ModTags.DEFENSE), false, 1),
    WARD_PERCENT(
            Set.of(ModifierIDs.ARMORTAG),Affix.PREFIX, "+@value1@% Inc. Ward", 6,
            TargetStats.WARD, ValueTypes.ADDED, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.HEALTH, ModTags.DEFENSE), false, 1),
    ARMOR_HEALTH(
            Set.of(ModifierIDs.ARMORTAG),Affix.PREFIX, "+@value1@ Armor / +@value2@ Health", 4,//-------------------------
            TargetStats.ARMOR_HEALTH, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
            Set.of(ModTags.HEALTH,ModTags.PHYSICAL, ModTags.DEFENSE), true, 1),
    WARD_HEALTH(
            Set.of(ModifierIDs.ARMORTAG),Affix.PREFIX, "+@value1@ Ward / +@value2@ Health", 4,
            TargetStats.WARD_HEALTH, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
            Set.of(ModTags.HEALTH, ModTags.DEFENSE), true, 1),
    DODGE_HEALTH(
            Set.of(ModifierIDs.ARMORTAG),Affix.PREFIX, "+@value1@ Dodge / +@value2@ Health", 4,
            TargetStats.DODGE_HEALTH, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
            Set.of(ModTags.UTILITY, ModTags.HEALTH, ModTags.DEFENSE), true, 1),
    ARMOR_WARD(
            Set.of(ModifierIDs.ARMORTAG),Affix.PREFIX, "+@value1@ Armor / +@value2@ Ward", 4,
            TargetStats.ARMOR_WARD, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
            Set.of(ModTags.HEALTH,ModTags.PHYSICAL, ModTags.DEFENSE), true, 1),
    ARMOR_DODGE(
            Set.of(ModifierIDs.ARMORTAG),Affix.PREFIX, "+@value1@ Armor / +@value2@% Dodge", 4,
            TargetStats.ARMOR_DODGE, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
            Set.of(ModTags.UTILITY, ModTags.PHYSICAL, ModTags.DEFENSE), true, 1),
    DODGE_WARD(
            Set.of(ModifierIDs.ARMORTAG),Affix.PREFIX, "+@value1@ Dodge / +@value2@ Ward", 4,
            TargetStats.DODGE_WARD, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
            Set.of(ModTags.UTILITY), true, 1),//-------------------------------------------------
    WALK_SPEED(
            Set.of(ModifierIDs.ARMORTAG),Affix.PREFIX, "+@value1@ Walk speed", 4,
            TargetStats.WALK_SPEED, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),
    STRENGTH_PERCENT(
            Set.of(ModifierIDs.ARMORTAG),Affix.PREFIX, "+@value1@% STR", 3,
            TargetStats.STRENGTH, ValueTypes.MULTI, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),
    DEXTERITY_PERCENT(
            Set.of(ModifierIDs.ARMORTAG),Affix.PREFIX, "+@value1@% DEX", 3,
            TargetStats.DEXTERITY, ValueTypes.MULTI, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),
    INTELLIGENCE_PERCENT(
            Set.of(ModifierIDs.ARMORTAG),Affix.PREFIX, "+@value1@% INT", 3,
            TargetStats.INTELLIGENCE, ValueTypes.MULTI, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),


    STAMINA(
            Set.of(ModifierIDs.ARMORTAG, ModifierIDs.WEAPONTAG),Affix.PREFIX, "+@value1@ Stamina", 6,
            TargetStats.STAMINA, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),



    STRENGTH(
            Set.of(ModifierIDs.ARMORTAG, ModifierIDs.WEAPONTAG),Affix.SUFFIX, "+@value1@ STR", 8,
            TargetStats.STRENGTH, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),
    DEXTERITY(
            Set.of(ModifierIDs.ARMORTAG, ModifierIDs.WEAPONTAG),Affix.SUFFIX, "+@value1@ DEX", 8,
            TargetStats.DEXTERITY, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),
    INTELLIGENCE(
            Set.of(ModifierIDs.ARMORTAG, ModifierIDs.WEAPONTAG),Affix.SUFFIX, "+@value1@ INT", 8,
            TargetStats.INTELLIGENCE, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),
    HEALTH_REGEN(
            Set.of(ModifierIDs.ARMORTAG),Affix.SUFFIX, "+@value1@ Health Regen", 8,
            TargetStats.HEALTH_REGEN, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),
    FIRE_RESISTANCE(
            Set.of(ModifierIDs.ARMORTAG),Affix.SUFFIX, "+@value1@% Fire Resistance", 8,
            TargetStats.FIRE_RESISTANCE, ValueTypes.PERCENT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),
    LIGHTNING_RESISTANCE(
            Set.of(ModifierIDs.ARMORTAG),Affix.SUFFIX, "+@value1@% Lightning Resistance", 8,
            TargetStats.LIGHTNING_RESISTANCE, ValueTypes.PERCENT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),
    COLD_RESISTANCE(
            Set.of(ModifierIDs.ARMORTAG),Affix.SUFFIX, "+@value1@% Cold Resistance", 8,
            TargetStats.COLD_RESISTANCE, ValueTypes.PERCENT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),
    ABYSSAL_RESISTANCE(
            Set.of(ModifierIDs.ARMORTAG),Affix.SUFFIX, "+@value1@% Abyssal Resistance", 8,
            TargetStats.ABYSSAL_RESISTANCE, ValueTypes.PERCENT, RangeTypes.SINGLE_RANGE,
            Set.of(ModTags.UTILITY), false, 1),





    HYBRID_PHYS_ACC( //Not finished
            Set.of(ModifierIDs.WEAPONTAG),Affix.PREFIX, "+@hvalue1@ Physical DMG / +@hvalue2@ Accuracy", 8,
            TargetStats.PHYSICALDAMAGE_ACCURACY, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
            Set.of(ModTags.DAMAGE, ModTags.PHYSICAL), true, 1),
    PERCENT_PHYSICAL(Set.of(ModifierIDs.WEAPONTAG),Affix.PREFIX, "+@value1@% Inc. Physical DMG", 8,
                     TargetStats.PHYSICAL_DAMAGE, ValueTypes.ADDED, RangeTypes.SINGLE_RANGE,
                     Set.of(ModTags.DAMAGE, ModTags.PHYSICAL), false, 1),
    PERCENT_ELEMENTAL(Set.of(ModifierIDs.WEAPONTAG),Affix.PREFIX, "+@value1@% Inc. Elemental DMG", 8,
                      TargetStats.ELEMENTAL_DAMAGE, ValueTypes.ADDED, RangeTypes.SINGLE_RANGE,
                      Set.of(ModTags.DAMAGE, ModTags.ELEMENTAL), false, 1),
    ADDED_PHYSICAL(Set.of(ModifierIDs.WEAPONTAG),Affix.PREFIX, "+@value1@ - @value2@ Physical DMG", 9,
                   TargetStats.PHYSICAL_DAMAGE, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
                   Set.of(ModTags.DAMAGE, ModTags.PHYSICAL), false, 1),
    ADDED_FIRE(Set.of(ModifierIDs.WEAPONTAG),Affix.PREFIX, "+@value1@ - @value2@ Fire DMG", 10,
               TargetStats.FIRE_DAMAGE, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
               Set.of(ModTags.DAMAGE, ModTags.ELEMENTAL), false, 1),
    ADDED_COLD(Set.of(ModifierIDs.WEAPONTAG),Affix.PREFIX, "+@value1@ - @value2@ Cold DMG", 10,
               TargetStats.COLD_DAMAGE, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
               Set.of(ModTags.DAMAGE, ModTags.ELEMENTAL), false, 1),
    ADDED_LIGHTNING(Set.of(ModifierIDs.WEAPONTAG),Affix.PREFIX, "+@value1@ - @value2@ Lightning DMG", 10,
                    TargetStats.LIGHTNING_DAMAGE, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
                    Set.of(ModTags.DAMAGE, ModTags.ELEMENTAL), false, 1),
    ADDED_ABYSSAL(Set.of(ModifierIDs.WEAPONTAG),Affix.PREFIX, "+@value1@ - @value2@ Abyssal DMG", 1,
                  TargetStats.ABYSSAL_DAMAGE, ValueTypes.FLAT, RangeTypes.DOUBLE_RANGE,
                  Set.of(ModTags.DAMAGE, ModTags.ABYSSAL), false, 1), //ou void
    PHYSICAL_LIFESTEAL(Set.of(ModifierIDs.WEAPONTAG),Affix.PREFIX, "+@value1@% Physical lifesteal", 3,
                       TargetStats.LIFESTEAL, ValueTypes.PERCENT, RangeTypes.SINGLE_RANGE,
                       Set.of(ModTags.UTILITY), false, 1),
    SHRED(Set.of(ModifierIDs.WEAPONTAG),Affix.PREFIX, "+@value1@% Shred", 5,
          TargetStats.SHRED, ValueTypes.PERCENT, RangeTypes.SINGLE_RANGE,
          Set.of(ModTags.DAMAGE,ModTags.PHYSICAL), false, 1),
    MAELSTROM(Set.of(ModifierIDs.WEAPONTAG),Affix.PREFIX, "+@value1@% Maelstrom", 5,
              TargetStats.MAELSTROM, ValueTypes.PERCENT, RangeTypes.SINGLE_RANGE,
              Set.of(ModTags.DAMAGE,ModTags.ELEMENTAL), false, 1),
    //extra proj


    //    ATTACK_SPEED(Affix.SUFFIX, "+@value1@% Inc. Attack Speed", 7,
//            TargetStats.PHYSICAL_DAMAGE, ValueTypes.FLAT_PERCENT, RangeTypes.SINGLE_RANGE,
//            List.of(ModTags.DAMAGE, ModTags.UTILITY), false, 1),
    LIFE_ON_HIT(Set.of(ModifierIDs.WEAPONTAG),Affix.SUFFIX, "+@value1@ Life per hit", 5,
                TargetStats.LIFE_ON_HIT, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
                Set.of(ModTags.UTILITY), false, 1),
    ACCURACY(Set.of(ModifierIDs.WEAPONTAG),Affix.SUFFIX, "+@value1@ Accuracy", 5,
             TargetStats.ACCURACY, ValueTypes.FLAT, RangeTypes.SINGLE_RANGE,
             Set.of(ModTags.UTILITY), false, 1),
    STAMINA_REGEN(Set.of(ModifierIDs.WEAPONTAG),Affix.SUFFIX, "+@value1@% Stamina regen", 6,
                  TargetStats.STAMINA_REGEN, ValueTypes.ADDED, RangeTypes.SINGLE_RANGE,
                  Set.of(ModTags.DAMAGE,ModTags.PHYSICAL), false, 1),
    BLEEDING(Set.of(ModifierIDs.WEAPONTAG),Affix.SUFFIX, "+@value1@% Bleeding chance", 4,
             TargetStats.BLEED, ValueTypes.PERCENT, RangeTypes.SINGLE_RANGE,
             Set.of(ModTags.PHYSICAL,ModTags.AILMENT), false, 1),
    CRITICAL_CHANCE(Set.of(ModifierIDs.WEAPONTAG),Affix.SUFFIX, "+@value1@% Inc. Crit Chance", 6,
                    TargetStats.CRITICAL_CHANCE, ValueTypes.ADDED, RangeTypes.SINGLE_RANGE,
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

    ModifierIDs(Set<String> filterTags, Affix affix, String displayName, int tiers, TargetStats targetStat, ValueTypes valueType, RangeTypes range, Set<ModTags> modTags, boolean hybrid, int weight) {
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
    public List<ModifierIDs> getWeaponMods(){
        List<ModifierIDs> weaponMods = new ArrayList<>();
        for (ModifierIDs mod : ModifierIDs.values()){
            addIfMatch(weaponMods, mod, WEAPONTAG);
        }
        return weaponMods;
    }
    public List<ModifierIDs> getArmorMods(){
        List<ModifierIDs> armorMods = new ArrayList<>();
        for (ModifierIDs mod : ModifierIDs.values()){
            addIfMatch(armorMods, mod, ARMORTAG);
        }
        return armorMods;
    }
    private void addIfMatch(List<ModifierIDs> modSet, ModifierIDs mod, String filterString){
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

    public String getModifierKey(){
        String modKey;
        if (!isHybrid()){
            modKey = getTargetStat().toString().replace("_", "") + "_" + getValueType();
        } else {
            modKey = getTargetStat() + "_" + getValueType();
        }
        return modKey;
    }
}
