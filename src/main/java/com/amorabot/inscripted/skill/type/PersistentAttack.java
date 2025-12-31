package com.amorabot.inscripted.skill.type;

import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.skill.annotations.DurationSkill;
import com.amorabot.inscripted.skill.casting.CastSource;
import com.amorabot.inscripted.tasks.base.Skillcast;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.util.Vector;

import java.util.UUID;
@Getter
public class PersistentAttack extends Skillcast.Persistent {
    private final AttackData attackData;
    private final DurationSkill durationData;


    public PersistentAttack(UUID playerID, Skills sourceSkill, CastSource castSource, WeaponAttackSpeeds weaponSpeed) {
        super(playerID, sourceSkill, castSource, weaponSpeed);
        if (!sourceSkill.isAttackSkill() || !sourceSkill.isDuration()){
            if (DEBUG_MODE) Utils.error("Persistent attack data not defined! ( " + sourceSkill + " )");
            this.attackData = null;
            this.durationData = null;
            return;
        }
        this.attackData = new AttackData(playerID,sourceSkill, PlayerDataContainer.getDataContainerFor(playerID).getGlobalStats());
        this.durationData = sourceSkill.getDurationSkillData();
        if (DEBUG_MODE) Utils.log("Persistent attack cast!");
    }
    public PersistentAttack(UUID playerID, Vector skillcastOrigin, Skills sourceSkill, CastSource castSource, WeaponAttackSpeeds weaponSpeed) {
        super(playerID, skillcastOrigin, sourceSkill, castSource, weaponSpeed);
        if (!sourceSkill.isAttackSkill() || !sourceSkill.isDuration()){
            if (DEBUG_MODE) Utils.error("Persistent attack data not defined! ( " + sourceSkill + " )");
            this.attackData = null;
            this.durationData = null;
            return;
        }
        this.attackData = new AttackData(playerID,sourceSkill, PlayerDataContainer.getDataContainerFor(playerID).getGlobalStats());
        this.durationData = sourceSkill.getDurationSkillData();
        if (DEBUG_MODE) Utils.log("Fixed location Persistent attack cast!");
    }
}
