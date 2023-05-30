package com.amorabot.rpgelements.components.PlayerComponents;

import org.bukkit.inventory.ItemStack;

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

    public void setDamage(ItemStack weapon){
        //TODO: implementar :D
        //considerar weapon como podendo ser um itemStack vazio, nesse caso setar p/ 0
        //usar o container de weapon pra identificar todos os danos e os valores
        //e serializar os valores no local correto

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
