package com.amorabot.inscripted.components.Items.DataStructures.Enums;

import lombok.Getter;

@Getter
public enum PlayerStats {
    STRENGTH("STR"),
    DEXTERITY("DEX"),
    INTELLIGENCE("INT"),
    STAMINA("Stamina"),
    STAMINA_REGEN("Stamina regen"),
    HEALTH("Health"),
    HEALTH_REGEN("Health regen"),
    WARD("Ward"),
    DODGE("Dodge"),
    ARMOR("Armor"),
    FIRE_RESISTANCE("Fire resistance"),
    COLD_RESISTANCE("Cold resistance"),
    LIGHTNING_RESISTANCE("Lightning resistance"),
    ABYSSAL_RESISTANCE("Abyssal resistance"),
    //Add resistance cap targets and stats
    PHYSICAL_DAMAGE("Physical DMG"),
    MELEE_DAMAGE("Melee DMG"),
    FIRE_DAMAGE("Fire DMG"),
    COLD_DAMAGE("Cold DMG"),
    LIGHTNING_DAMAGE("Lightning DMG"),
    ELEMENTAL_DAMAGE("Elemental DMG"),
    ABYSSAL_DAMAGE("Abyssal DMG"),
    ACCURACY("Accuracy"),
    BLEED("Bleed chance"),
    CRITICAL_CHANCE("Crit. Chance"),
    CRITICAL_DAMAGE("Crit. DMG"),
    LIFE_ON_HIT("Life on hit"),
    LIFESTEAL("Lifesteal"),
    SHRED("Shred"),
    MAELSTROM("Maelström"),
    WALK_SPEED("Walk speed"),
    AREA_DAMAGE("Area DMG"),
    AOE("Area of Effect"),
    EXTRA_PROJECTILES("Extra proj."),
    HEALING_POWER("Healing power");

    private final String alias;

    PlayerStats(String statAlias){
        this.alias = statAlias;
    }
}
