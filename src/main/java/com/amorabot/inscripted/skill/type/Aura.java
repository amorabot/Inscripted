package com.amorabot.inscripted.skill.type;

import com.amorabot.inscripted.Inscripted;
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
        Aura removedAura = activePlayerAuras.remove(auraSkill);
        int removedAuraID = removedAura.getPersistentRoutineID();
        Inscripted.getScheduler().cancelTask(removedAuraID);
    }

    public boolean isInactive(){
        PlayerDataContainer dataContainer = PlayerDataContainer.getDataContainerFor(getPlayerID());
        Map<Skills, Aura> activePlayerAuras = dataContainer.getActiveAuras();
        Skills auraSkill = getCastedSkill();
        return !activePlayerAuras.containsKey(auraSkill);
    }

    public boolean isDamagingAura(){
        Skills auraSkill = getCastedSkill();
        return (auraSkill.isPersistent() && auraSkill.isAttackSkill());
    }
    public AttackData getAuraDamage(){
        Skills auraSkill = getCastedSkill();
        if (!isDamagingAura()){
            Utils.error(auraSkill + " is has no damage data.");
            return null;
        }
        return new AttackData(getPlayerID(),auraSkill,PlayerDataContainer.getDataContainerFor(getPlayerID()).getGlobalStats());
    }
}
