package com.amorabot.inscripted.components.Mobs;

import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Attack;

import java.io.Serializable;

public class MobStats implements Serializable {

    private final int mobLevel;
    private final Attack mobHit;
    private final HealthComponent mobHealth;
    private final DefenceComponent mobDefence;

    public MobStats(Attack mobHit, HealthComponent mobHP, DefenceComponent mobDef){
        this.mobLevel = 1;
        this.mobHit = mobHit;
        this.mobHealth = mobHP;
        this.mobDefence = mobDef;
    }
    public MobStats(int mobLevel, Attack mobHit, HealthComponent mobHP, DefensePresets mobDef){
        this.mobLevel = mobLevel;
        this.mobHit = mobHit;
        this.mobHealth = mobHP;
        this.mobDefence = mobDef.getDefense();
    }

    public int getMobLevel() {
        return mobLevel;
    }

    public Attack getMobHit() {
        return mobHit;
    }

    public HealthComponent getMobHealth() {
        return mobHealth;
    }

    public DefenceComponent getMobDefence() {
        return mobDefence;
    }
}
