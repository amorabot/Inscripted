package com.amorabot.inscripted.combat.buffs.categories.stat;

import com.amorabot.inscripted.combat.buffs.BuffTask;
//import com.amorabot.inscripted.components.Player.stats.StatCompiler;
import com.amorabot.inscripted.combat.buffs.Buffs;
import com.amorabot.inscripted.combat.buffs.PlayerBuffManager;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.ProfileEvents;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public class StatBuffCountdown extends BuffTask {

    @Getter
    private final Buffs buff;
    private final Player target;

    private final int durationInTicks;
    @Setter
    private int ticksElapsed = 0;


    public StatBuffCountdown(Buffs buff, Player player){
        this.buff = buff;
        this.target = player;

        Stat statBuffData = (Stat) buff.getBuffAnnotationData();
        this.durationInTicks = statBuffData.durationInSeconds() * 20;
    }



    @Override
    public void run() {
        if (!target.isOnline()){
            expire();
            return;
        }
        if (ticksElapsed >= durationInTicks){
            Utils.log("Timer expired for " + buff);
            expire();
            return;
        }

        buff.effectOn(target);
        ticksElapsed+=3; //Period
    }



    @Override
    public void expire() {
        //Whatever
        Utils.log("STAT BUFF: "+buff+" expired for " + target.getName()+"!");
        PlayerBuffManager.removeBuffFrom(target.getUniqueId(), buff);
        PlayerDataContainer.getDataContainerFor(target.getUniqueId()).onNotify(ProfileEvents.EXTERNAL_STAT_CHANGE);
        this.cancel();
    }


    public int getRemainingDuration(){
        return durationInTicks - ticksElapsed;
    }
}
