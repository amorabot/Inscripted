package com.amorabot.rpgelements.components.PlayerComponents;

public class HealthComponent {

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
    }

    public void setBaseHealth(int newBaseHealth){
        if (this.startingHealth + newBaseHealth <= 0){ //If the base life modifiers gets your life to negative, your life is 1
            this.baseHealth = 1;
            return;
        }
        this.baseHealth = this.startingHealth + newBaseHealth;
    }
    public void setIncreasedHealth(int increasedHealth){
        this.increasedHealth = increasedHealth;
        setMaxHealth(increasedHealth);
    }
    private void setMaxHealth(int incHealth){
        this.maxHealth = baseHealth * (1 + incHealth);
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
    public void setIncreasedWard(int increasedWard){
        this.increasedWard = increasedWard;
        setMaxWard(increasedWard);
    }
    private void setMaxWard(int incWard){
        this.maxWard = baseWard * (1 + incWard);
    }
}
