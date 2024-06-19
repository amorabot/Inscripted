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
    MAX_FIRE_RESISTANCE("Maximum fire res."),
    COLD_RESISTANCE("Cold resistance"),
    MAX_COLD_RESISTANCE("Maximum cold res."),
    LIGHTNING_RESISTANCE("Lightning resistance"),
    MAX_LIGHTNING_RESISTANCE("Maximum lightning res."),
    ABYSSAL_RESISTANCE("Abyssal resistance"),
    //Add resistance cap stats
    PHYSICAL_DAMAGE("Physical DMG"),
    MELEE_DAMAGE("Melee DMG"),
    FIRE_DAMAGE("Fire DMG"),
    COLD_DAMAGE("Cold DMG"),
    LIGHTNING_DAMAGE("Lightning DMG"),
    ELEMENTAL_DAMAGE("Elemental DMG"),
    ABYSSAL_DAMAGE("Abyssal DMG"),
    ACCURACY("Accuracy"),
    BLEED("Bleed chance"),
    BLEED_DAMAGE("Bleed damage"),
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
    HEALING_POWER("Healing power"),
    WARD_RECOVERY_RATE("Ward recovery rate");

    private final String alias;

    PlayerStats(String statAlias){
        this.alias = statAlias;
    }
}
