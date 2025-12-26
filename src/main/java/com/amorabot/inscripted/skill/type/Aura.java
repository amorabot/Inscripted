package com.amorabot.inscripted.skill.type;

import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.skill.casting.CastSource;
import com.amorabot.inscripted.tasks.base.Skillcast;
import com.amorabot.inscripted.utils.Utils;

import java.util.Map;
import java.util.UUID;

public class Aura extends Skillcast.Persistent {
    public Aura(UUID playerID, Skills sourceSkill, CastSource castSource, WeaponAttackSpeeds weaponSpeed) {
        super(playerID, sourceSkill, castSource, weaponSpeed);
    }

    /*
     For aura casts, the skill routine defines and starts the subroutine task
     Then, the aura subroutine ID must be set internally
    */
    @Override
    public void start(long delay, long timer) {
        if (isInactive()){
            Skills auraSkill = getCastedSkill();
            Utils.log("Creating new " + auraSkill + " instance.");
            run(); // If not active, create a new instance
        }
        register(); // Always Register the aura cast
    }
    @Override
    public void register(){
        Skills auraSkill = getCastedSkill();
        //TODO: if castSource is not NEUTRAL, check for toggle cooldown
        if (isInactive()){
            PlayerDataContainer dataContainer = PlayerDataContainer.getDataContainerFor(getPlayerID());
            Map<Skills, Aura> activePlayerAuras = dataContainer.getActiveAuras();
            activePlayerAuras.put(auraSkill,this);
            return;
        }
        //Active Aura & new register attempt -> Toggle off
        Utils.log("Toggling off " + auraSkill + " for player " + getPlayer().getDisplayName());
        unregister();
    }
    @Override
    public void unregister(){
        PlayerDataContainer dataContainer = PlayerDataContainer.getDataContainerFor(getPlayerID());
        Map<Skills, Aura> activePlayerAuras = dataContainer.getActiveAuras();
        Skills auraSkill = getCastedSkill();
        if (!activePlayerAuras.containsKey(auraSkill)){return;}
        //Aura present -> Un-instantiate it
        activePlayerAuras.get(auraSkill).getSubroutine().shutdown();
    }

    public boolean isInactive(){
        PlayerDataContainer dataContainer = PlayerDataContainer.getDataContainerFor(getPlayerID());
        Map<Skills, Aura> activePlayerAuras = dataContainer.getActiveAuras();
        Skills auraSkill = getCastedSkill();
        return !activePlayerAuras.containsKey(auraSkill);
    }

    public boolean isDamagingAura(){
        Skills auraSkill = getCastedSkill();
        return (auraSkill.isAura() && auraSkill.isAttackSkill());
    }
    public AttackData getAuraDamage(){
        Skills auraSkill = getCastedSkill();
        if (!isDamagingAura()){
            Utils.error(auraSkill + " is has no damage data.");
            return null;
        }
        return new AttackData(getPlayerID(),auraSkill,PlayerDataContainer.getDataContainerFor(getPlayerID()).getGlobalStats());
    }
    public double getSubroutinePeriodInSeconds(){
        if (!getCastData().getCastingContext().getSkillUsed().isAura()){return 0;}
        return getCastedSkill().getAuraSkillData().period();
    }
    public double getToggleCooldown(){
        if (!getCastData().getCastingContext().getSkillUsed().isAura()){return 0;}
        return getCastedSkill().getAuraSkillData().toggleCooldown();
    }
}
