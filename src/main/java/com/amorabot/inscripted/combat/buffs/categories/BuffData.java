package com.amorabot.inscripted.combat.buffs.categories;

import com.amorabot.inscripted.combat.buffs.BuffTask;
import com.amorabot.inscripted.combat.buffs.Buffs;

public interface BuffData {
    void storeFinalValue(int value);
    int getStoredValue();
    Buffs getBuff();
    boolean isDebuff();

    int getTaskID();
    BuffTask getBuffTask();
    void activate();
    void stop(); //TODO: Make default (getTask() + cancelling)
    void delete();
}
