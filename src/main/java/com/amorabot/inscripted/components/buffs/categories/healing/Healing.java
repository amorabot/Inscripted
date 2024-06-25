package com.amorabot.inscripted.components.buffs.categories.healing;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Healing {
    double baseHealing();
    ValueTypes healingType();
    int period();
    int timesApplied();
}
