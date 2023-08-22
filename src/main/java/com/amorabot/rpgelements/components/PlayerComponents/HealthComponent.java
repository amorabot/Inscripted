package com.amorabot.rpgelements.components.PlayerComponents;

import com.amorabot.rpgelements.components.Items.Interfaces.PlayerComponent;

public class HealthComponent implements PlayerComponent {

    private float currentHealth;
    private float maxHealth;
    private int baseHealth;
    private final int startingHealth;

    private int increasedHealth;

    private float currentWard;
    private float maxWard;
    private int baseWard;
    private final int startingWard;

    private int increasedWard;

    public HealthComponent(){
        this.startingHealth = 40;
        this.startingWard = 0;

        this.baseHealth = 40;
        this.baseWard = 0;
    }
    public HealthComponent(int startingHealth, int startingWard){
        this.startingHealth = startingHealth;
        this.startingWard = startingWard;

        this.baseHealth = startingHealth;
        this.baseWard = startingWard;
    }

    //------------LIFE METHODS-------------

    public float getCurrentHealth(){
        return this.currentHealth;
    }
    public float getMaxHealth() {
        return maxHealth;
    }
    public int getBaseHealth() {
        return this.baseHealth;
    } //Value without % modifiers

    public void setBaseHealth(int addedHealth){
        if (this.startingHealth + addedHealth <= 0){ //If the base life modifiers gets your life to negative, your life is 1
            this.baseHealth = 1;
            return;
        }
        this.baseHealth = this.startingHealth + addedHealth;
    }
    public int getIncreasedHealth() {
        return increasedHealth;
    }
    public void setIncreasedHealth(int increasedHealth){
        this.increasedHealth = increasedHealth;
        setMaxHealth();
    }
    public void setMaxHealth(){
        this.maxHealth = baseHealth * (1 + getIncreasedHealth());
    }


    //------------WARD METHODS-------------

    public float getCurrentWard() {
        return currentWard;
    }
    public float getMaxWard(){
        return this.maxWard;
    }
    public int getBaseWard() {
        return baseWard;
    }

    public void setBaseWard(int newBaseWard){
        this.baseWard = this.startingWard + newBaseWard;
    }
    public int getIncreasedWard() {
        return increasedWard;
    }
    public void setIncreasedWard(int increasedWard){
        this.increasedWard = increasedWard;
        setMaxWard();
    }
    private void setMaxWard(){
        this.maxWard = baseWard * (1 + getIncreasedWard());
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

        setBaseHealth(flatHealthSum);
        setIncreasedHealth(percentHealth);
        setBaseWard(flatWardSum);
        setIncreasedWard(percentWard);
    }
}
