package com.amorabot.inscripted.components.buffs.categories.stat;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Buff;
import com.amorabot.inscripted.components.buffs.Buffs;
import com.amorabot.inscripted.components.buffs.categories.BuffData;
import org.bukkit.entity.Player;

public class StatBuff implements BuffData {

//    private int storedValue;
    private final Buffs buff;
    private final Buff countdownTask;
    private int taskID;

    public StatBuff(Buffs buff, Player target){
        this.buff = buff;
        this.countdownTask = new StatBuffCountdownTask(this.buff, target);
    }


    @Override
    public void storeFinalValue(int value) {
//        this.storedValue = value;
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
    public Buff getBuffTask() {
        return countdownTask;
    }

    @Override
    public void activate() {
        this.taskID = this.countdownTask.runTaskTimer(Inscripted.getPlugin(), 0, 1).getTaskId();
    }

    @Override
    public void stop() {
    }

    @Override
    public void delete() {

    }
}
