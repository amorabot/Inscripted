package com.amorabot.inscripted.combat.buffs.categories.stat;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.combat.buffs.BuffTask;
import com.amorabot.inscripted.combat.buffs.Buffs;
import com.amorabot.inscripted.combat.buffs.categories.BuffData;
import org.bukkit.entity.Player;

public class StatBuff implements BuffData {

    private final Buffs buff;
    private final BuffTask countdownTask;
    private int taskID;

    public StatBuff(Buffs buff, Player target){
        this.buff = buff;
        this.countdownTask = new StatBuffCountdown(this.buff, target);
    }


    @Override
    public void storeFinalValue(int value) {
    }

    @Override
    public int getStoredValue() {
        return 0;
    }

    @Override
    public Buffs getBuff() {
        return buff;
    }

    @Override
    public boolean isDebuff() {
        return buff.isDebuff();
    }

    @Override
    public int getTaskID() {
        return taskID;
    }

    @Override
    public BuffTask getBuffTask() {
        return countdownTask;
    }

    @Override
    public void activate() {
        this.taskID = this.countdownTask.runTaskTimer(Inscripted.getPlugin(), 0, 5).getTaskId();
    }

    @Override
    public void stop() {
    }

    @Override
    public void delete() {

    }
}
