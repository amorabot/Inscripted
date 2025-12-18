package com.amorabot.inscripted.combat.buffs.categories.stat;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.combat.buffs.BuffTask;
import com.amorabot.inscripted.combat.buffs.Buffs;
import com.amorabot.inscripted.combat.buffs.categories.BuffData;
import com.amorabot.inscripted.combat.buffs.categories.healing.Healing;
import com.amorabot.inscripted.item.inscription.language.ValueType;
import com.amorabot.inscripted.item.render.InscriptedPalette;
import com.amorabot.inscripted.item.structure.Weapon.DamageTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Arrays;

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

    public Stat getStatAnnotationData(){
        Stat statAnnotation = (Stat) getBuff().getBuffAnnotationData();
        assert statAnnotation != null;
        return statAnnotation;
    }

    @Override
    public Component getMessage() {
        Component baseMessage = Component.text(getBuff().getApplyMessage());
        if (isDebuff()){
            baseMessage = baseMessage.color(NamedTextColor.RED);
        } else {
            baseMessage = baseMessage.color(NamedTextColor.GREEN);
        }
        StringBuilder builder = new StringBuilder("[ ");

        Stat buffStatData = getStatAnnotationData();
        ValueType vType = buffStatData.valueType();
        String statKeyword = vType.getKeyword(!isDebuff());
        String statAlias = buffStatData.targetStat().getAlias();
        int[] amount = buffStatData.amount();

        if (vType.equals(ValueType.FLAT) || vType.equals(ValueType.PERCENTAGE)){
             builder.append(statKeyword).append(Arrays.toString(amount));
             if (vType.equals(ValueType.PERCENTAGE)){
                 builder.append("%");
             }
             builder.append(" ").append(statAlias);
        } else {
            builder.append(amount[0]).append(" ").append(statKeyword).append(" ").append(statAlias);
        }
        builder.append(" | ");
        builder.append(buffStatData.durationInSeconds());
        builder.append("s ]");
        Component statData = Component.text(builder.toString()).color(InscriptedPalette.DARK_GRAY.getColor());
        return baseMessage.append(Component.text(" ")).append(statData);
    }
}
