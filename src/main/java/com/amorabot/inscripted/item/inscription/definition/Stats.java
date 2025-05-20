package com.amorabot.inscripted.item.inscription.definition;

import lombok.Getter;

@Getter
public enum Stats {
    STRENGTH("Strength"),
    DEXTERITY("Dexterity"),
    INTELLIGENCE("Intelligence"),
    STAMINA("Stamina"),
    STAMINA_REGEN("Stamina Regen."),
    HEALTH("Health"),
    HEALTH_REGEN("Health Regen."),
    SOUL("Soul"),
    DODGE("Dodge"),
    ARMOR("Armor"),
    FIRE_RESISTANCE("Fire Resistance"),
    MAX_FIRE_RESISTANCE("Maximum Fire Res."),
    COLD_RESISTANCE("Cold Resistance"),
    MAX_COLD_RESISTANCE("Maximum Cold Res."),
    LIGHTNING_RESISTANCE("Lightning Resistance"),
    MAX_LIGHTNING_RESISTANCE("Maximum Lightning Res."),
    ABYSSAL_RESISTANCE("Abyssal Resistance"),
    PHYSICAL_DAMAGE("Physical Damage"),
    MELEE_DAMAGE("Melee Damage"),
    FIRE_DAMAGE("Fire Damage"),
    COLD_DAMAGE("Cold Damage"),
    LIGHTNING_DAMAGE("Lightning Damage"),
    ELEMENTAL_DAMAGE("Elemental Damage"),
    ABYSSAL_DAMAGE("Abyssal Damage"),
    ACCURACY("Accuracy"),
    BLEED("Bleed Chance"),
    BLEED_DAMAGE("Bleed Damage"),
    CRITICAL_CHANCE("Crit. Chance"),
    CRITICAL_DAMAGE("Crit. Damage"),
    LIFE_ON_HIT("Life on Hit"),
    LIFESTEAL("Lifesteal"),
    SHRED("Shred"),
    MAELSTROM("Maelstrom"),
    FIRE_PENETRATION("Fire Penetration"),
    LIGHTNING_PENETRATION("Lightning Penetration"),
    COLD_PENETRATION("Cold Penetration"),
    WALK_SPEED("Walk Speed"),
    AREA_DAMAGE("Area Damage"),
    COOLDOWN_REDUCTION("Cooldown Reduction"),
    AOE("Area of Effect"),
    EXTRA_PROJECTILES("Additional Proj."),
    HEALING_POWER("Healing Power"),
    SOUL_RECOVERY_RATE("Soul Recovery Rate");

    private final String alias;

    Stats(String alias){
        this.alias = alias;
    }
}
