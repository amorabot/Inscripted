package com.amorabot.inscripted.player.profile;

import com.amorabot.inscripted.item.inscription.definition.Stats;
import com.amorabot.inscripted.item.inscription.language.ValueType;
import lombok.Getter;

@Getter
public enum BaseStats {
    HEALTH(Stats.HEALTH, ValueType.FLAT, 40),
    HEALTH_REGEN(Stats.HEALTH_REGEN, ValueType.FLAT, 5),
    SOUL_RECOVERY(Stats.SOUL_RECOVERY_RATE, ValueType.PERCENTAGE, 5),
    WALK_SPEED(Stats.WALK_SPEED, ValueType.FLAT, 100),
    STAMINA(Stats.STAMINA, ValueType.FLAT, 100),
    STAMINA_REGEN(Stats.STAMINA_REGEN, ValueType.FLAT, 5);

    //PER-LEVEL STAT MAP
    //Map<stat, <type, value>>
    private final Stats targetStat;
    private final ValueType type;
    private final int value;
    BaseStats(Stats targetStat, ValueType type, int value){
        this.targetStat = targetStat;
        this.type = type;
        this.value = value;
    }

}
