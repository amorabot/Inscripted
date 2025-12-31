package com.amorabot.inscripted.skill.type.subroutines;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.skill.type.Aura;
import com.amorabot.inscripted.tasks.base.Skillcast;
import com.amorabot.inscripted.utils.Utils;

import java.util.Map;

public class AuraSubroutine extends PersistentSubroutine {


    public AuraSubroutine(Skillcast parentSkillcast) {
        super(parentSkillcast);
    }

    @Override
    public boolean isValid() {
        return (parentSkillcast instanceof Aura);
    }

    @Override
    public void uninstantiate() {
        if (parentSkillcast.getPlayer().isOnline()){
            PlayerDataContainer dataContainer = PlayerDataContainer.getDataContainerFor(parentSkillcast.getPlayerID());
            Map<Skills, Aura> activePlayerAuras = dataContainer.getActiveAuras();
            Skills auraSkill = parentSkillcast.getCastedSkill();
            Aura removedAura = activePlayerAuras.remove(auraSkill);
        }
        int removedAuraSubroutineID = ((Aura)parentSkillcast).getSubroutine().getRoutine().getTaskId();
        Inscripted.getScheduler().cancelTask(removedAuraSubroutineID);
    }

    @Override
    public void startSubroutine(int delay) {
        if (DEBUG_MODE) Utils.log("New aura instantiated");
        if (!isValid() || routine == null){
            if (DEBUG_MODE) Utils.log("Invalid aura internal state");
            return;
        }
        Aura aura = (Aura) parentSkillcast;
        int periodInTicks = (int) (aura.getSubroutinePeriodInSeconds() * 20);
        routine.runTaskTimer(Inscripted.getPlugin(),delay, periodInTicks);
        if (DEBUG_MODE) Utils.log("Setting parent subroutine");
        aura.setSubroutine(this);
        //Auras register themselves on activation, Player cast are also filtered
    }
}
