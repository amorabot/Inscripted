package com.amorabot.inscripted.skill;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AttackSkill {
    int[] addedBaseDmg();
    int[] dmgEffectiveness();
    int[] dmgConversion();
    /*
    Skill-based damage conversions will only convert physical to other types.
        'Double-dipping' by having %increased Cold DMG and %increased Phys DMG globally can only work if:
            1) The base AttackData is not a stored snapshot of the player's equipments and instead
               is calculated every attack, which is very intensive
            2) Global player stats are stored in a cache (containing keystones, effects and whatnot)
               and, when calculating
    */
}
