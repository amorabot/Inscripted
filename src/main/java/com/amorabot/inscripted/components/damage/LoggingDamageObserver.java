package com.amorabot.inscripted.components.damage;

import com.amorabot.inscripted.utils.Utils;

public class LoggingDamageObserver implements DamageEventObserver {

    @Override
    public void onDamageTaken(DamageEvent event) {
        Utils.log(String.format("Player %s took %d damage from %s (Critical: %s, Dodged: %s)", 
            event.getTargetId(), 
            event.getTotalDamage(), 
            event.getAttackerName(),
            event.isCritical(),
            event.isDodged()));
    }

    @Override
    public void onDamageDealt(DamageEvent event) {
        Utils.log(String.format("Player %s dealt %d damage to %s (Critical: %s)", 
            event.getAttackerId(), 
            event.getTotalDamage(), 
            event.getTargetId(),
            event.isCritical()));
    }

    @Override
    public void onMobDamaged(DamageEvent event) {
        Utils.log(String.format("Mob %s took %d damage from %s (HP remaining: checking...)", 
            event.getTargetId(), 
            event.getTotalDamage(), 
            event.getAttackerName()));
    }

    @Override
    public void onMobKilled(DamageEvent event) {
        Utils.log(String.format("Mob %s was killed by %s with %d damage!", 
            event.getTargetId(), 
            event.getAttackerName(),
            event.getTotalDamage()));
    }
}