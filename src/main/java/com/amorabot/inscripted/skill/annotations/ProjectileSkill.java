package com.amorabot.inscripted.skill.annotations;

import com.amorabot.inscripted.skill.SteeringBehaviors;
import com.amorabot.inscripted.skill.routine.projectile.ProjectileGenerators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProjectileSkill {
    int baseProjectiles();
    ProjectileGenerators spread();
    SteeringBehaviors defaultSteering();
    boolean uniqueTarget();
}
