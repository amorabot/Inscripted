package com.amorabot.inscripted.components.damage;

public interface DamageEventObserver {
    void onDamageTaken(DamageEvent event);
    void onDamageDealt(DamageEvent event);
    void onMobDamaged(DamageEvent event);
    void onMobKilled(DamageEvent event);
}