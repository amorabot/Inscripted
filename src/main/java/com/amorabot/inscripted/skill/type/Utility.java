package com.amorabot.inscripted.skill.type;

import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.skill.casting.CastSource;
import com.amorabot.inscripted.tasks.base.Skillcast;
import com.amorabot.inscripted.utils.Utils;

import java.util.UUID;

public class Utility extends Skillcast.Persistent { //Utilities may not be persistent, treat as a regular cast if it's the case
    public Utility(UUID playerID, Skills sourceSkill, CastSource castSource, WeaponAttackSpeeds weaponSpeed) {
        super(playerID, sourceSkill, castSource, weaponSpeed);
        Utils.log("Utility cast!");
    }
}
