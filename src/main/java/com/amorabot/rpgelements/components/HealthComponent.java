package com.amorabot.rpgelements.components;

import com.amorabot.rpgelements.components.Items.Interfaces.EntityComponent;
import com.amorabot.rpgelements.components.Player.Profile;

public class HealthComponent implements EntityComponent {

    private float currentHealth;
    private float maxHealth; //addedHealth+startinghealth * 1 + incHealth
    private int addedHealth;
    private final int startingHealth;
    private int healthRegen;

    private int increasedHealth;

    private float currentWard;
    private float maxWard;
    private int extraWard;
    private final int startingWard;

    private int increasedWard;

    public HealthComponent(){
        this.startingHealth = 40;
        this.startingWard = 0;

        this.addedHealth = 0;
        this.extraWard = 0;
        setMaxHealth(this.startingHealth+this.addedHealth, 0);
        setMaxWard();
    }

    //------------LIFE METHODS-------------

    public float getCurrentHealth(){
        return this.currentHealth;
    }
    public float getMaxHealth() {
        return maxHealth;
    }
    public int getAddedHealth() {
        return this.addedHealth;
    } //Value without % modifiers

    public void setAddedHealth(int addedHealth){
        if (this.startingHealth + addedHealth <= 0){ //If the base life modifiers gets your life to negative, your life is 1
            this.addedHealth = 0;
            return;
        }
        this.addedHealth = addedHealth;
    }
    public int getIncreasedHealth() {
        return increasedHealth;
    }
    public void setIncreasedHealth(int increasedHealth){
        this.increasedHealth = increasedHealth;
    }
    public void setMaxHealth(int addedHealth, int percentHealth){
        setAddedHealth(addedHealth);
        setIncreasedHealth(percentHealth);
        this.maxHealth = (getAddedHealth()+this.startingHealth) * (1 + getIncreasedHealth()/100f);
    }

    public void setHealthRegen(int healthRegen) {
        this.healthRegen = healthRegen;
    }
    public int getHealthRegen() {
        return healthRegen;
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
    }

    public void updateHealthComponent(int flatHealth, int percentHealth, int healthRegen, int flatWard, int percentWard){
        setMaxHealth(flatHealth, percentHealth);
        setHealthRegen(healthRegen);
        setExtraWard(flatWard);
        setIncreasedWard(percentWard);
    }
}
