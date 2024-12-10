package com.amorabot.inscripted.components.buffs.categories.damage;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Buff;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.components.buffs.Buffs;
import com.amorabot.inscripted.components.buffs.categories.BuffData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class DamageBuff implements BuffData {

    private int damageTick;
    private final Buffs buff;
    private Buff dotTask;
    private int taskID;


    public DamageBuff(Buffs buff){
        if (!buff.isDamageBuff()){buff = null;}
        this.buff = buff;
    }


    @Override
    public void storeFinalValue(int value) {
        this.damageTick = value;
    }

    @Override
    public int getStoredValue() {
        return damageTick;
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
        return this.dotTask;
    }

    @Override
    public void activate() {
        Damage dmgBuffData = getDamageAnnotationData();
        this.taskID = this.dotTask.runTaskTimer(Inscripted.getPlugin(), 5, dmgBuffData.period()).getTaskId();
    }

    @Override
    public void stop() {
        if (dotTask == null){return;}
        if (dotTask.isCancelled()){return;}
        this.dotTask.cancel();
    }

    @Override
    public void delete() {
        if (dotTask == null){return;}
        this.dotTask.cancel();
        this.dotTask = null;
    }

    public Damage getDamageAnnotationData(){
        Damage dmgAnnotation = (Damage) getBuff().getBuffAnnotationData();
        assert dmgAnnotation != null;
        return dmgAnnotation;
    }

    public int[] convertBaseHit(int dot){
        if (!getBuff().isDamageBuff()){return new int[5];}
        Damage dmgAnnotation = getDamageAnnotationData();
        DamageTypes dmgType = dmgAnnotation.baseDamageType();
        int[] finalHitDmg = new int[5];
        finalHitDmg[dmgType.ordinal()] = dot;

        storeFinalValue(dot);

        return finalHitDmg;
    }

    public void createDamageTask(int[] dot, Player defender, boolean isSelfDamage, LivingEntity attacker){
        this.dotTask = new DamageDebuffTask(this.buff, dot, defender, isSelfDamage, attacker);
    }
}
