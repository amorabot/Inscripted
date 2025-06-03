package com.amorabot.inscripted.player.profile.component;

import com.amorabot.inscripted.item.structure.Weapon.DamageTypes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttackData {

    private float DPS;

    private final int[][] hitDamage = new int[DamageTypes.values().length][2];
    private float accuracy;
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
}
