package com.amorabot.inscripted.components.Mobs;

import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Attack;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class MobStats implements Serializable,Cloneable {

    private final int mobLevel;
    private final Attack mobHit;
    private final HealthComponent mobHealth;
    private final DefenceComponent mobDefence;
    //TODO: Mob keystones

    public MobStats(int mobLevel, Attack mobHit, HealthComponent mobHP, DefenceComponent mobDef){
        this.mobLevel = mobLevel;
        this.mobHit = mobHit;
        this.mobHealth = mobHP;
        this.mobDefence = mobDef;
    }

    public MobStats clone() {
        try{
            return (MobStats) super.clone();
        } catch (CloneNotSupportedException e) {
            Utils.error("Unsupported clone() call on MobStats.");
            throw new RuntimeException(e);
        }
    }
}
