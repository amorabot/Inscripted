package com.amorabot.inscripted.components;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.utils.CraftingUtils;

import java.util.Map;

public class HealthComponent implements EntityComponent {

    private float currentHealth;
    private float maxHealth; //addedHealth+startinghealth * 1 + incHealth
    private int addedHealth;
    private final int startingHealth;
    private final int baseHealthRegen;
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
        this.baseHealthRegen = 5;

        this.addedHealth = 0;
        this.extraWard = 0;
        setMaxHealth(this.addedHealth, 0);
        setMaxWard(this.extraWard, 0);
        setHealthRegen(0);
    }

    //------------LIFE METHODS-------------
    public void regenHealth(int regeneratedHealthTick){
//        int regenTick = regeneratedHealthTick;
//        int regenTick = regeneratedHealthTick + baseHealthRegen;
        //In the specific case its already been capped out, ignore
        if (currentHealth == maxHealth){
            return;
        }
        //If this tick of regen surpasses the maxHP, cap it to maxHP
        if (currentHealth+regeneratedHealthTick>maxHealth){
            currentHealth = maxHealth;
            return;
        }
        //If theres room to regenerate, do
        if (currentHealth+regeneratedHealthTick <= maxHealth){
            this.currentHealth += regeneratedHealthTick;
        }
    }
    public void damage(Map<DamageTypes, int[]> attackerDamages){
        int physDamage = 0;

        for (DamageTypes dmg : attackerDamages.keySet()){
            int[] currTypeDamage = attackerDamages.get(dmg);
            physDamage += CraftingUtils.getRandomNumber(currTypeDamage[0], currTypeDamage[1]);
        }
        if (currentHealth-physDamage > 0){
            currentHealth -= physDamage;
        } else {
            currentHealth = 1;
        }
    }
    public void resetCurrentHealth(){
        this.currentHealth = maxHealth;
    }

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
        this.healthRegen = healthRegen + baseHealthRegen;
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
//        setMaxWard();
    }
    private void setMaxWard(int addedWard, int increasedWard){
        setExtraWard(addedWard);
        setIncreasedWard(increasedWard);
        this.maxWard = extraWard * (1 + getIncreasedWard()/100f);
    }

    public float getNormalizedHP(){
        return Math.min(currentHealth/maxHealth, 1F);
    }
    public double getMappedHealth(int basePlayerHearts){
        return Math.max(0.5, getNormalizedHP()*basePlayerHearts);
    }

    @Override
    public void update(Profile profileData) {
    }

    public void updateHealthComponent(int flatHealth, int percentHealth, int healthRegen, int flatWard, int percentWard){
        setMaxHealth(flatHealth, percentHealth);
        setHealthRegen(healthRegen);
        setMaxWard(flatWard, percentWard);
    }
}
