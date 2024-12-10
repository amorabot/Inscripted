package com.amorabot.inscripted.components;

import com.amorabot.inscripted.components.Items.relic.enums.Effects;
import com.amorabot.inscripted.components.Items.relic.enums.Keystones;
import com.amorabot.inscripted.components.Items.relic.enums.TriggerTimes;
import com.amorabot.inscripted.components.Items.relic.enums.TriggerTypes;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.entity.LivingEntity;

import java.util.Set;

public interface EntityProfile {
    Attack getAttackData();
    HealthComponent getHealthComponent();
    DefenceComponent getDefenceComponent();


    Set<Keystones> getKeystones();
    Set<Effects> getEffects();

    default boolean hasKeystone(Keystones keystone){
        return getKeystones().contains(keystone);
    }
    default void notify(TriggerTypes effectTrigger, TriggerTimes timing, LivingEntity caster, LivingEntity target, int[] hit){
        for (Effects effect : getEffects()){
            if (!effect.getTrigger().equals(effectTrigger) || !effect.getTiming().equals(timing)){continue;} //If there isn't a match: ignore
            Utils.log("Checking "+effectTrigger+" effects. Current: " + effect);
            //TODO: Add effect+keystone support for mobs
            effect.check(caster,target, hit);
        }
    }
}
