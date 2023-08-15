package com.amorabot.rpgelements.components.PlayerComponents;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.rpgelements.components.Items.Weapon.Weapon;

import java.util.Map;

public class DamageComponent {
    private float DPS;

    private int[] physicalDmg = new int[2];
    private int[] fireDmg = new int[2];
    private int[] coldDmg = new int[2];
    private int[] lightningDmg = new int[2];
    private int[] abyssalDmg = new int[2];

    public DamageComponent(){
        this.DPS = 1;
    }

    public void setDamage(Weapon weaponStats){
        //TODO: implementar :D
        //considerar weapon como podendo ser um itemStack vazio, nesse caso setar p/ 0
        //usar o container de weapon pra identificar todos os danos e os valores
        //e serializar os valores no local correto
        if (weaponStats == null){
            this.physicalDmg = new int[]{0,0};
            this.fireDmg = new int[]{0,0};
            this.coldDmg = new int[]{0,0};
            this.lightningDmg = new int[]{0,0};
            this.abyssalDmg = new int[]{0,0};
            setDps();
            return;
        }

        Map<DamageTypes, int[]> damageTypesMap = weaponStats.getBaseDamage();
        this.physicalDmg = damageTypesMap.get(DamageTypes.PHYSICAL);
        if (damageTypesMap.containsKey(DamageTypes.FIRE)){
            this.fireDmg = damageTypesMap.get(DamageTypes.FIRE);
        } else { this.fireDmg = new int[]{0,0}; }
        if (damageTypesMap.containsKey(DamageTypes.COLD)){
            this.coldDmg= damageTypesMap.get(DamageTypes.COLD);
        } else { this.coldDmg = new int[]{0,0}; }
        if (damageTypesMap.containsKey(DamageTypes.LIGHTNING)){
            this.lightningDmg = damageTypesMap.get(DamageTypes.LIGHTNING);
        } else { this.lightningDmg = new int[]{0,0}; }
        if (damageTypesMap.containsKey(DamageTypes.ABYSSAL)){
            this.abyssalDmg = damageTypesMap.get(DamageTypes.ABYSSAL);
        } else { this.abyssalDmg = new int[]{0,0}; }
        setDps();
    }
    private void setDps(){
        int physDps = (physicalDmg[0] + physicalDmg[1]) / 2;
        int fireDps = (fireDmg[0] + fireDmg[1]) / 2;
        int coldDps = (coldDmg[0] + coldDmg[1]) / 2;
        int lightDps = (lightningDmg[0] + lightningDmg[1] / 2);
        int abyssDps = (abyssalDmg[0] + abyssalDmg[1] / 2);

        DPS = physDps + fireDps + coldDps + lightDps + abyssDps;
    }
    public float getDPS(){
        return DPS;
    }

    public int[] getPhysicalDmg() {
        return physicalDmg;
    }

    public int[] getFireDmg() {
        return fireDmg;
    }

    public int[] getColdDmg() {
        return coldDmg;
    }

    public int[] getLightningDmg() {
        return lightningDmg;
    }

    public int[] getAbyssalDmg() {
        return abyssalDmg;
    }
}
