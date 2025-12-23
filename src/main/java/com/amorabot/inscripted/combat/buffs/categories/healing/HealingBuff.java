package com.amorabot.inscripted.combat.buffs.categories.healing;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.combat.buffs.BuffTask;
//import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.combat.buffs.Buffs;
import com.amorabot.inscripted.combat.buffs.categories.BuffData;
import com.amorabot.inscripted.item.inscription.language.ValueType;
import com.amorabot.inscripted.item.render.InscriptedPalette;
import com.amorabot.inscripted.item.structure.Weapon.DamageTypes;
import com.amorabot.inscripted.player.profile.Profile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class HealingBuff implements BuffData {

    private int healingTick;
    private final Buffs buff;
    private BuffTask healingTask;
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
    public BuffTask getBuffTask() {
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

    @Override
    public Component getMessage() {
        Component baseMessage = Component.text(getBuff().getApplyMessage()).color(NamedTextColor.GREEN);
        int timeApplied = getHealingAnnotationData().timesApplied();
        String messageString = "[ " + timeApplied + "x +" + healingTick + " ]";
        Component buffDataMessage = Component.text(messageString).color(InscriptedPalette.DARK_GRAY.getColor());
        return baseMessage.hoverEvent(HoverEvent.showText(buffDataMessage));
//        return baseMessage.append(Component.text(" ").append(buffDataMessage));
    }

    public int getFinalHealingTick(Profile targetProfile){
        Healing healingData = getHealingAnnotationData();
        if (healingData.healingType().equals(ValueType.FLAT)){
            return (int) healingData.baseHealing();
        }
        else if (healingData.healingType().equals(ValueType.PERCENTAGE)) {
            return (int) (targetProfile.getHealthComponent().getMaxHealth() * (healingData.baseHealing()/100D));
        }
        return 42069;
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
