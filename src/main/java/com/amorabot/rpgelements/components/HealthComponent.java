package com.amorabot.rpgelements.components;

import com.amorabot.rpgelements.components.Items.Interfaces.EntityComponent;
import com.amorabot.rpgelements.components.Player.Attributes;
import com.amorabot.rpgelements.components.Player.Profile;

public class HealthComponent implements EntityComponent {

    private float currentHealth;
    private float maxHealth;
    private int extraHealth;
    private final int startingHealth;

    private int increasedHealth;

    private float currentWard;
    private float maxWard;
    private int extraWard;
    private final int startingWard;

    private int increasedWard;

    public HealthComponent(){
        this.startingHealth = 40;
        this.startingWard = 0;

        this.extraHealth = 0;
        this.extraWard = 0;
        setMaxHealth();
        setMaxWard();
    }

    //------------LIFE METHODS-------------

    public float getCurrentHealth(){
        return this.currentHealth;
    }
    public float getMaxHealth() {
        return maxHealth;
    }
    public int getExtraHealth() {
        return this.extraHealth;
    } //Value without % modifiers

    public void setExtraHealth(int extraHealth){
        if (this.startingHealth + extraHealth <= 0){ //If the base life modifiers gets your life to negative, your life is 1
            this.extraHealth = 1;
            return;
        }
        this.extraHealth = this.startingHealth + extraHealth;
    }
    public int getIncreasedHealth() {
        return increasedHealth;
    }
    public void setIncreasedHealth(int increasedHealth){
        this.increasedHealth = increasedHealth;
        setMaxHealth();
    }
    public void setMaxHealth(){
        this.maxHealth = extraHealth * (1 + getIncreasedHealth());
    }


    //------------WARD METHODS-------------

    public float getCurrentWard() {
        return currentWard;
    }
    public float getMaxWard(){
        return this.maxWard;
    }
    public int getExtraWard() {
        return extraWard;
    }

    public void setExtraWard(int extraWard){
        this.extraWard = this.startingWard + extraWard;
    }
    public int getIncreasedWard() {
        return increasedWard;
    }
    public void setIncreasedWard(int increasedWard){
        this.increasedWard = increasedWard;
        setMaxWard();
    }
    private void setMaxWard(){
        this.maxWard = extraWard * (1 + getIncreasedWard());
    }

    @Override
    public void update(Profile profileData) {
        int flatHealthSum = 0;
        int percentHealth = 0;
        int flatWardSum = 0;
        int percentWard = 0;
        //Getting health stats from all items...
        //CASO PRECISE CHECAR A ARMA, USAR if == NULL
        //Getting health stats from attributes
        Attributes attributes = profileData.getAttributes();
        flatHealthSum += attributes.getStrength()/3;
        flatWardSum += attributes.getIntelligence()/5;

        setExtraHealth(flatHealthSum);
        setIncreasedHealth(percentHealth);
        setExtraWard(flatWardSum);
        setIncreasedWard(percentWard);
    }
}
