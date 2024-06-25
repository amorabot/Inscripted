package com.amorabot.inscripted.components.buffs.categories.damage;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Damage {
    DamageTypes baseDamageType();
    int period();
    int timesApplied();
}
