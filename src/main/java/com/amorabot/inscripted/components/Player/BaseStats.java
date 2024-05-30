package com.amorabot.inscripted.components.Player;

public enum BaseStats {
    HEALTH(40),
//    HEALTH_PER_LEVEL(6),
    HEALTH_REGEN(5),
    WARD_RECOVERY_RATE(5),
    STAMINA(100),
    STAMINA_REGEN(5),
    WALK_SPEED(100);

    private final int initialValue;

    BaseStats(int initialValue){
        this.initialValue = initialValue;
    }

    public int getValue(){
        return initialValue;
    }
}
