package com.amorabot.inscripted.player.profile;

import com.amorabot.inscripted.components.damage.DamageTracker;
import com.amorabot.inscripted.item.inscription.definition.Stats;
import com.amorabot.inscripted.player.profile.component.*;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class Profile {

    private final HealthComponent healthComponent;
    private final DefenceComponent defenceComponent;
    private final DamageComponent damageComponent;
    private final DamageTracker damageTracker;

    private final Attributes attributes;
    private final Miscellaneous miscStats;


    public Profile(){
        this.healthComponent = new HealthComponent();
        this.damageComponent = new DamageComponent();
        this.defenceComponent = new DefenceComponent();
        this.damageTracker = new DamageTracker();

        this.attributes = new Attributes(0,0,0);
        this.miscStats = new Miscellaneous();
    }

    public AttackData getPreSkillAttackData(){
        return damageComponent.getBaseAttackData();
    }

    public void update(UUID playerID,Map<Stats, double[]> newStats){
        healthComponent.updateComponent(playerID,newStats);
        defenceComponent.updateComponent(playerID,newStats);
        damageComponent.updateComponent(playerID,newStats);

        attributes.updateComponent(playerID,newStats);
        miscStats.updateComponent(playerID,newStats);
    }
}
