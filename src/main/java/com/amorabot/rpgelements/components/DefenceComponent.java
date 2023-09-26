package com.amorabot.rpgelements.components;

import com.amorabot.rpgelements.components.Items.Interfaces.EntityComponent;
import com.amorabot.rpgelements.components.Player.Profile;

public class DefenceComponent implements EntityComponent {
    private int fireResistance;
    private int coldResistance;
    private int lightningResistance;
    private int abyssalResistance;

    private int dodge;

    private float finalArmor;
    private int baseArmor;
    private int increasedArmor;
    //Set<ItemModifier> specialModList; // (effects calculations in a specific way)

    public DefenceComponent(){
        this.fireResistance = 15;
        this.coldResistance = 15;
        this.lightningResistance = 15;

        this.dodge = 0;
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
    public void setAbyssalResistance(int abyssalResistance) {
        this.abyssalResistance = abyssalResistance;
    }

    public int getFireResistance() {
        return fireResistance;
    }
    public int getColdResistance() {
        return coldResistance;
    }
    public int getLightningResistance() {
        return lightningResistance;
    }
    public int getAbyssalResistance() {
        return abyssalResistance;
    }

    //------------DODGE METHODS-------------
    public void setDodge(int newDodge){
        this.dodge = newDodge;
    }
    public int getDodge() {
        return dodge;
    }
    //------------ARMOR METHODS-------------
    public void setBaseArmor(int newBaseArmor){
        this.baseArmor = newBaseArmor;
    }
    public void setIncreasedArmor(int incArmor){
        this.increasedArmor = incArmor;
    }
    private void setFinalArmor(int flatArmor, int incArmor){
        setBaseArmor(flatArmor);
        setIncreasedArmor(incArmor);
        this.finalArmor = (float) flatArmor * (1 + incArmor/100f);
    }

    public float getFinalArmor() {
        return finalArmor;
    }
    public int getBaseArmor() {
        return baseArmor;
    }

    public int getIncreasedArmor() {
        return increasedArmor;
    }

    @Override
    public void update(Profile profileData) {
    }
    public void update(int fireResistance, int coldResistance, int lightningResistance, int abyssalResistance, int baseArmor, int increasedArmor, int dodge){
        setFireResistance(fireResistance);
        setColdResistance(coldResistance);
        setLightningResistance(lightningResistance);
        setAbyssalResistance(abyssalResistance);

        setFinalArmor(baseArmor, increasedArmor);
        setDodge(dodge);
    }
}