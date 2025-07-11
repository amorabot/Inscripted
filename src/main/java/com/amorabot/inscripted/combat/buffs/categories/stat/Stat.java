package com.amorabot.inscripted.combat.buffs.categories.stat;

import com.amorabot.inscripted.item.inscription.definition.Stats;
import com.amorabot.inscripted.item.inscription.language.ValueType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Stat {
    int[] amount();
    ValueType valueType();
    Stats targetStat();
    int durationInSeconds();
}
