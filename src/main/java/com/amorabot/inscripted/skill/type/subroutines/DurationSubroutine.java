package com.amorabot.inscripted.skill.type.subroutines;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.skill.casting.CastType;
import com.amorabot.inscripted.tasks.base.Skillcast;
import com.amorabot.inscripted.utils.Utils;

import java.util.Map;

public class DurationSubroutine extends PersistentSubroutine{

    private int elapsedTicks = 0;

    public  DurationSubroutine(Skillcast parentSkillcast) {
        super(parentSkillcast);
    }

    @Override
    public boolean isValid() {
        boolean isPersistent = (parentSkillcast instanceof Skillcast.Persistent);
        boolean isDuration = parentSkillcast.getCastedSkill().isDuration();
        return (isPersistent && isDuration);
    }

    @Override
    public void uninstantiate() {
        if (parentSkillcast.getPlayer().isOnline()){
            PlayerDataContainer dataContainer = PlayerDataContainer.getDataContainerFor(parentSkillcast.getPlayerID());
            Map<CastType, PersistentSubroutine> activePersistentInstances = dataContainer.getPersistentSubroutines();
            PersistentSubroutine subroutine = activePersistentInstances.remove(parentSkillcast.getCastedSkill().getType());
        }
        ((Skillcast.Persistent)parentSkillcast).getSubroutine().getRoutine().cancel();
    }

    @Override
    public void startSubroutine(int delay) {
        if (DEBUG_MODE) Utils.log("New duration skill instantiated");
        if (!isValid() || routine == null){
            if (DEBUG_MODE) Utils.log("Invalid aura internal state");
            return;
        }
        Skillcast.Persistent durationCast = (Skillcast.Persistent)parentSkillcast;
//        int durationInTicks = (int) (durationCast.getSubroutineMaxDurationInSeconds() * 20);
        int period = durationCast.getSubroutineRefreshRate();
        routine.runTaskTimer(Inscripted.getPlugin(),delay, period);
        if (DEBUG_MODE) Utils.log("Setting parent duration subroutine");
        durationCast.setSubroutine(this);

    }
    public void addDelta(int ticks){
//        Utils.error("Elapsed: " + elapsedTicks + " | ++" + ticks + " | Total dur.: " + getTotalDuration());
        elapsedTicks += ticks;
    }
    public void addPeriodToElapsedTime(){
        Skillcast.Persistent durationCast = (Skillcast.Persistent)parentSkillcast;
        addDelta(durationCast.getSubroutineRefreshRate());
    }

    public void cancelIfInvalid(){
        Skillcast.Persistent durationCast = (Skillcast.Persistent)parentSkillcast;
        int totalDuration = (int) ((durationCast).getSubroutineMaxDurationInSeconds() * 20);
        int period = durationCast.getSubroutineRefreshRate();

        boolean expired = elapsedTicks + period >= totalDuration;
        boolean invalidParent = !checkParentCast(durationCast);
        boolean invalidState = !isValid();
        if (expired || invalidParent || invalidState){
            routine.cancel();
            return;
        }
    }
    public int getTotalDuration(){ //In Ticks
        return (int) (((Skillcast.Persistent) parentSkillcast).getSubroutineMaxDurationInSeconds() * 20);
    }
}
