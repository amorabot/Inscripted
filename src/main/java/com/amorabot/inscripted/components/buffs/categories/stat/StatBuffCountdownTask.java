package com.amorabot.inscripted.components.buffs.categories.stat;

import com.amorabot.inscripted.components.Buff;
import com.amorabot.inscripted.components.Player.stats.StatCompiler;
import com.amorabot.inscripted.components.buffs.Buffs;
import com.amorabot.inscripted.managers.PlayerBuffManager;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.entity.Player;

public class StatBuffCountdownTask extends Buff {

    private final Buffs buff;
    private final Player target;

    private final int durationInTicks;
    private int ticksElapsed = 0;


    public StatBuffCountdownTask(Buffs buff, Player player){
        this.buff = buff;
        this.target = player;

        //TODO: make it effected by player stats? :D
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
            //The buff itself should only call for a recompilation when it's timer runs out: (and is properly removed from memory)
            StatCompiler.updateProfile(target.getUniqueId());
            return;
        }

        buff.effectOn(target);
        ticksElapsed++;
    }



    @Override
    public void expire() {
        //Whatever
        Utils.log("STAT BUFF: "+buff+" expired for " + target.getName()+"!");
        PlayerBuffManager.removeBuffFrom(target, buff);

        this.cancel();
    }


    public int getRemainingDuration(){
        return durationInTicks - ticksElapsed;
    }
}
