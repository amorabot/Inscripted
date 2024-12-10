package com.amorabot.inscripted.components.Mobs;

import com.amorabot.inscripted.components.*;
import com.amorabot.inscripted.components.Items.relic.enums.Effects;
import com.amorabot.inscripted.components.Items.relic.enums.Keystones;
import com.amorabot.inscripted.components.Items.relic.enums.TriggerTimes;
import com.amorabot.inscripted.components.Items.relic.enums.TriggerTypes;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
public class MobStats implements Serializable, EntityProfile {

    private final int mobLevel;
    private final Attack mobHit;
    private final HealthComponent mobHealth;
    private final DefenceComponent mobDefence;
    private final Set<Keystones> mobKeystones = new HashSet<>();
    private final Set<Effects> mobEffects = new HashSet<>();

    public MobStats(int mobLevel, Attack mobHit, HealthComponent mobHP, DefenceComponent mobDef){
        this.mobLevel = mobLevel;
        this.mobHit = mobHit;
        this.mobHealth = mobHP;
        this.mobDefence = mobDef;
    }

    public MobStats(MobStats clonedStats){
        this.mobLevel = clonedStats.getMobLevel();
        this.mobHit = new Attack(clonedStats.getAttackData());
        this.mobHealth = new HealthComponent(clonedStats.getHealthComponent());
        this.mobDefence = new DefenceComponent(clonedStats.getDefenceComponent());
        this.mobKeystones.addAll(clonedStats.getMobKeystones());
        this.mobEffects.addAll(clonedStats.getMobEffects());
    }

    public MobStats cloneInternalData() {
        return new MobStats(this);
    }


    @Override
    public Attack getAttackData() {
        return mobHit;
    }

    @Override
    public HealthComponent getHealthComponent() {
        return mobHealth;
    }

    @Override
    public DefenceComponent getDefenceComponent() {
        return mobDefence;
    }

    @Override
    public Set<Keystones> getKeystones() {
        return new HashSet<>(mobKeystones);
    }

    @Override
    public Set<Effects> getEffects() {
        return new HashSet<>(mobEffects);
    }

//    public boolean hasKeystone(Keystones keystone){
//        return mobKeystones.contains(keystone);
//    }
//    public void notify(TriggerTypes effectTrigger, TriggerTimes timing, LivingEntity caster, LivingEntity target, int[] hit){
//        for (Effects effect : mobEffects){
//            if (!effect.getTrigger().equals(effectTrigger) || !effect.getTiming().equals(timing)){continue;} //If there isn't a match: ignore
//            Utils.log("Checking "+effectTrigger+" effects. Current: " + effect + " (For mobs)");
//            effect.check(caster,target, hit);
//        }
//    }
}
