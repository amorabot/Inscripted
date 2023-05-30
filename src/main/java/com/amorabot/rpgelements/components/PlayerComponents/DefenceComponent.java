package com.amorabot.rpgelements.components.PlayerComponents;

public class DefenceComponent {
    private int fireResistance;
    private int coldResistance;
    private int lightningResistance;


    private float finalEvasion;
    private int baseEvasion;
    private int increasedEvasion;


    private float finalArmor;
    private int baseArmor;
    private int increasedArmor;

    public DefenceComponent(){
        this.fireResistance = 15;
        this.coldResistance = 15;
        this.lightningResistance = 15;

        this.baseEvasion = 0;
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
    public void setBaseEvasion(int newBaseEvasion){
        //TODO: tratamento de valores negativos (punitivo x neutro)
        this.baseEvasion = newBaseEvasion;
    }
    public void setIncreasedEvasion(int incEvasion){
        this.increasedEvasion = incEvasion;
        setFinalEvasion(incEvasion);
    }
    private void setFinalEvasion(int incEvasion) {
        this.finalEvasion = baseEvasion * (1 + incEvasion);
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