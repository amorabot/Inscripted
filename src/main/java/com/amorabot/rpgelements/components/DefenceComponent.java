package com.amorabot.rpgelements.components;

import com.amorabot.rpgelements.components.Items.Interfaces.EntityComponent;
import com.amorabot.rpgelements.components.Player.Profile;

public class DefenceComponent implements EntityComponent {
    private int fireResistance;
    private int coldResistance;
    private int lightningResistance;

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
    //------------DODGE METHODS-------------
    public void setDodge(int newEvasion){
        this.dodge = newEvasion;
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

    @Override
    public void update(Profile profileData) {

    }
}