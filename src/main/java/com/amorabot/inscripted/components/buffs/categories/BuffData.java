package com.amorabot.inscripted.components.buffs.categories;

import com.amorabot.inscripted.components.Buff;
import com.amorabot.inscripted.components.buffs.Buffs;

public interface BuffData {
    void storeFinalValue(int value);
    int getStoredValue();
    Buffs getBuff();
    boolean isDebuff();

    int getTaskID();
    Buff getBuffTask();
    void activate();
    void stop(); //TODO: Make default (getTask() + cancelling)
    void delete();
}
