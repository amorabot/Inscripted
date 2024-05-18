package com.amorabot.inscripted.components.Items.modifiers.data;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.PlayerStats;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD) //https://docs.oracle.com/javase%2F7%2Fdocs%2Fapi%2F%2F/java/lang/annotation/ElementType.html
@Retention(RetentionPolicy.RUNTIME)
public @interface Meta {
    PlayerStats convertedStat();
    int rate();
}
