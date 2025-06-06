package com.amorabot.inscripted.player.profile.component;

import com.amorabot.inscripted.item.inscription.definition.Stats;
import com.amorabot.inscripted.item.structure.Weapon.DamageTypes;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class AttackData implements ProfileComponent {

    private float DPS;

    private final int[][] hitDamage = new int[DamageTypes.values().length][2];
    private int accuracy;
    private int critChance;
    private int critDamage;
    private int shred;
    private int maelstrom;
    private int firePen;
    private int lightningPen;
    private int coldPen;
    private int bleedChance;
    private int bleedDamage;


    public int[] getPhysicalDmg(){
        return hitDamage[0];
    }
    public void setPhysicalDmg(int[] physDmg){
        this.hitDamage[0] = physDmg;
    }

    public int[] getFireDmg(){
        return hitDamage[1];
    }
    public void setFireDmg(int[] fireDmg){
        this.hitDamage[1] = fireDmg;
    }

    public int[] getLightningDmg(){
        return hitDamage[2];
    }
    public void setLightningDmg(int[] lightDmg){
        this.hitDamage[2] = lightDmg;
    }

    public int[] getColdDmg(){
        return hitDamage[3];
    }
    public void setColdDmg(int[] coldDmg){
        this.hitDamage[3] = coldDmg;
    }

    public int[] getAbyssalDmg(){
        return hitDamage[4];
    }
    public void setAbyssalDmg(int[] abyssDmg){
        this.hitDamage[4] = abyssDmg;
    }
    public void resetDamages(){
        for (int i = 0; i < hitDamage.length; i++) {hitDamage[i] = new int[2];}
    }

    @Override
    public void updateComponent(UUID playerID, Map<Stats, double[]> finalStats) {
        setPhysicalDmg(getAndCast(Stats.PHYSICAL_DAMAGE,finalStats));
        setFireDmg(getAndCast(Stats.FIRE_DAMAGE,finalStats));
        setLightningDmg(getAndCast(Stats.LIGHTNING_DAMAGE,finalStats));
        setColdDmg(getAndCast(Stats.COLD_DAMAGE,finalStats));
        setAbyssalDmg(getAndCast(Stats.ABYSSAL_DAMAGE,finalStats));

        setAccuracy(getSingleValueFrom(Stats.ACCURACY,finalStats));

        setCritChance(getSingleValueFrom(Stats.CRITICAL_CHANCE,finalStats));
        setCritDamage(getSingleValueFrom(Stats.CRITICAL_DAMAGE,finalStats));

        setShred(getSingleValueFrom(Stats.SHRED,finalStats));
        setMaelstrom(getSingleValueFrom(Stats.MAELSTROM,finalStats));

        setFirePen(getSingleValueFrom(Stats.FIRE_PENETRATION,finalStats));
        setLightningPen(getSingleValueFrom(Stats.LIGHTNING_PENETRATION,finalStats));
        setColdPen(getSingleValueFrom(Stats.COLD_PENETRATION,finalStats));

        setBleedChance(getSingleValueFrom(Stats.BLEED,finalStats));
        setBleedDamage(getSingleValueFrom(Stats.BLEED_DAMAGE,finalStats));


        //DPS is total dmg for now
        setDPS();
    }

    @Override
    public List<Component> asTextComponent() {
        return List.of();
    }

    private int[] getAndCast(Stats dmgStat, Map<Stats, double[]> finalStats){
        double[] dmgValues = finalStats.get(dmgStat);
        if (dmgValues == null){return new int[2];}
        return new int[]{(int) dmgValues[0], (int) dmgValues[1]};
    }

    private void setDPS(){
        int totalDmg = 0;
        for (int[] dmg : hitDamage){
            totalDmg += Arrays.stream(dmg).sum();
        }
        DPS = totalDmg;
    }
}
