package com.amorabot.inscripted.item.inscription.definition;

import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.skill.casting.CastSource;
import com.amorabot.inscripted.skill.type.Aura;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;

import java.util.UUID;

@Getter
public enum KeystoneIDs {
    FORBIDDEN_PACT(TriggerTimes.LATE, true, "") {
        @Override
        public void applyKeystoneRule(PlayerDataContainer playerData) {
            Utils.log("Template Rule for " + this);
        }
    },
    LETHAL_STRIKES(TriggerTimes.LATE, true, "") {
        @Override
        public void applyKeystoneRule(PlayerDataContainer playerData) {
            Utils.log("Template Rule for " + this);
        }
    },
    BLOOD_PACT(TriggerTimes.CONDITIONAL, true, "") {
        @Override
        public void applyKeystoneRule(PlayerDataContainer playerData) {
            Utils.log("Template Rule for " + this);
        }
    },
    ORGAN_FAILURE(TriggerTimes.CONDITIONAL, true, "") {
        @Override
        public void applyKeystoneRule(PlayerDataContainer playerData) {
            Utils.log("Template Rule for " + this);
        }
    },
    FIRE_ATTUNEMENT(TriggerTimes.LATE, true, "") {
        @Override
        public void applyKeystoneRule(PlayerDataContainer playerData) {
            Utils.log("Template Rule for " + this);
        }
    },
    LIGHTNING_ATTUNEMENT(TriggerTimes.LATE, true, "") {
        @Override
        public void applyKeystoneRule(PlayerDataContainer playerData) {
            Utils.log("Template Rule for " + this);
        }
    },
    COLD_ATTUNEMENT(TriggerTimes.LATE, true, "") {
        @Override
        public void applyKeystoneRule(PlayerDataContainer playerData) {
            Utils.log("Template Rule for " + this);
        }
    },
    ELEMENTAL_BLESSING(TriggerTimes.LATE, true, "") {
        @Override
        public void applyKeystoneRule(PlayerDataContainer playerData) {
            Utils.log("Template Rule for " + this);
        }
    },
    AGNOSTIC(TriggerTimes.LATE, true, "") {
        @Override
        public void applyKeystoneRule(PlayerDataContainer playerData) {

        }
    },

    BERSERK(TriggerTimes.EARLY, false, ""),
    WINDS_OF_CHANGE(TriggerTimes.EARLY, false, ""),
    PERMAFROST(TriggerTimes.EARLY, false, ""),
    THUNDERSTRUCK(TriggerTimes.EARLY, false, ""),
    RIGHTEOUS_FIRE(TriggerTimes.EARLY, false, "");

    private final TriggerTimes triggerTime;
    private final String description;
    private final boolean rule; // If it's not a Rule, then it must be a Skill


    KeystoneIDs(TriggerTimes triggerTime,boolean isRule,String description){
        this.triggerTime = triggerTime;
        this.rule = isRule;
        this.description = description;
    }

    public void apply(PlayerDataContainer playerData){
        if (isRule()){
            applyKeystoneRule(playerData);
            return;
        }
        castKeystoneSkill(playerData.getPlayerID());
    }
    public void applyKeystoneRule(PlayerDataContainer playerData){
        //Empty body to be implemented by rule Keystones
        if (!rule){Utils.error(this + " is not a Keystone Rule.");}
        Utils.log("Unimplemented rule for " + this);
    }

    public void castKeystoneSkill(UUID playerID){
        Skills skill = getKeystoneSkill();
        if (skill==null){Utils.error("Unable to cast " + this);}
//        new Aura(playerID,skill, CastSource.ITEM, WeaponAttackSpeeds.HEAVY).start(2,0);
        Utils.log("Keystone aura toggle for " + this);
    }

    private Skills getKeystoneSkill(){
        try {
            return Skills.valueOf(this.name());
        } catch (IllegalArgumentException e) {
            Utils.error("No registered skill for " + this);
            return null;
        }
    }
    /*
    Attributes -> Trigger, Description, statRule bool & applyStatRule() Override, skill bool and apply()
    apply() will need explicit overrides and skill keystones will use the private cast() method inside its logic,
    meant only for them to use. Any non-skill calls should be invalid
     */
}
