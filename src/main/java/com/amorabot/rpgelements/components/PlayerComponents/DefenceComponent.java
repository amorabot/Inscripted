package com.amorabot.rpgelements.components.PlayerComponents;

public class DefenceComponent {
    private int fireResistance;
    private int coldResistance;
    private int lightningResistance;


    private int evasion; //%stat now


    private float finalArmor;
    private int baseArmor;
    private int increasedArmor;

    public DefenceComponent(){
        this.fireResistance = 15;
        this.coldResistance = 15;
        this.lightningResistance = 15;

        this.evasion = 0;
        this.baseArmor = 0;
    }

    //------------ELE RES METHODS-------------
    public void setFireResistance(int newFireResistance) {
        this.fireResistance = newFireResistance;
    }

    public void setColdResistance(int newColdResistance) {
        this.coldResistance = newColdResistance;
    }

    public void setLightningResistance(int newLightningResistance) {
        this.lightningResistance = newLightningResistance;
    }
    //------------EVASION METHODS-------------
    public void setEvasion(int newEvasion){
        this.evasion = newEvasion;
    }
    //------------ARMOR METHODS-------------
    public void setBaseArmor(int newBaseArmor){
        this.baseArmor = newBaseArmor;
    }
    public void setIncreasedArmor(int incArmor){
        this.increasedArmor = incArmor;

    }
    private void setFinalArmor(int incArmor){
        this.finalArmor = baseArmor * (1 + incArmor);
    }
}