package com.amorabot.inscripted.skills;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GlobalCooldown {

    private long baseGCD;
    private long lastCastTime;

    public GlobalCooldown(long baseGCD, long lasCast){
        this.baseGCD = baseGCD;
        this.lastCastTime = lasCast;
    }

    public boolean canBeCast(){
        long timeElapsed = System.currentTimeMillis() - lastCastTime;
        return timeElapsed > baseGCD;
    }
}
