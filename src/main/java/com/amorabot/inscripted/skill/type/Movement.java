package com.amorabot.inscripted.skill.type;

import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.skill.casting.CastSource;
import com.amorabot.inscripted.tasks.base.Skillcast;

import java.util.UUID;

public class Movement extends Skillcast.Simple {
    public Movement(UUID playerID, Skills sourceSkill, CastSource castSource, WeaponAttackSpeeds weaponSpeed) {
        super(playerID, sourceSkill, castSource, weaponSpeed);
    }
}
