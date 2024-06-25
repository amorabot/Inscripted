package com.amorabot.inscripted.components.buffs.categories.healing;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Buff;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.components.buffs.Buffs;
import com.amorabot.inscripted.components.buffs.categories.BuffData;
import org.bukkit.entity.Player;

public class HealingBuff implements BuffData {

    private int healingTick;
    private final Buffs buff;
    private Buff healingTask;
    private int taskID;

    public HealingBuff(Buffs buff){
        this.buff = buff;
    }

    @Override
    public void storeFinalValue(int value) {
        this.healingTick = value;
    }

    @Override
    public int getStoredValue() {
        return healingTick;
    }

    @Override
    public Buffs getBuff() {
        return buff;
    }

    @Override
    public boolean isDebuff() {
        return getBuff().isDebuff();
    }

    @Override
    public int getTaskID() {
        return taskID;
    }

    @Override
    public Buff getBuffTask() {
        return this.healingTask;
    }

    @Override
    public void activate() {
        Healing healingBuffData = getHealingAnnotationData();
        this.taskID = this.healingTask.runTaskTimer(Inscripted.getPlugin(), 5, healingBuffData.period()).getTaskId();
    }

    @Override
    public void stop() {

    }

    @Override
    public void delete() {

    }

    public int getFinalHealingTick(Profile targetProfile){
        Healing healingData = getHealingAnnotationData();
        if (healingData.healingType().equals(ValueTypes.FLAT)){
            return (int) healingData.baseHealing();
        } else {
            return (int) (targetProfile.getHealthComponent().getMaxHealth() * (healingData.baseHealing()/100));
        }
    }

    public Healing getHealingAnnotationData(){
        Healing healingAnnotation = (Healing) getBuff().getBuffAnnotationData();
        assert healingAnnotation != null;
        return healingAnnotation;
    }

    public void createHealingTask(int healingTick, Player caster, Player target){
        this.healingTask = new HealingBuffTask(this.buff, healingTick,caster, target);
        storeFinalValue(healingTick);
    }

}
