package com.amorabot.inscripted.components;

import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;
import com.amorabot.inscripted.components.Player.BaseStats;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.utils.Utils;

import java.util.UUID;

public class HealthComponent implements EntityComponent {

    private float currentHealth;
    private float maxHealth;
    private int addedHealth;

    private int increasedHealth;

    private int healthRegen;

    private float currentWard;
    private float maxWard;
    private int extraWard;

    private int increasedWard;

    public HealthComponent(){

        this.addedHealth = 0;
        this.extraWard = 0;
        setMaxHealth(this.addedHealth, 0);
        setMaxWard(this.extraWard, 0);
        setHealthRegen(0);
    }


    public HealthComponent(int addedHealth, int increasedHealth, int addedWard, int increasedWard){
        //Constructor mainly used for mob's health components
        setMaxHealth(addedHealth, increasedHealth);
        this.currentHealth = maxHealth;
        setMaxWard(addedWard, increasedWard);
        this.currentWard = maxWard;
        setHealthRegen(0);
    }

    @Override
    public void reset(){
        this.addedHealth = 0;
        this.maxHealth = BaseStats.HEALTH.getValue();
        this.healthRegen = BaseStats.HEALTH_REGEN.getValue();

        this.increasedHealth = 0;

        this.extraWard = 0;
        this.maxWard = 0;

        this.increasedWard = 0;
    }

    //------------LIFE METHODS-------------
    public void replenishLife(){
        currentHealth = maxHealth;
        currentWard = maxWard;
        Utils.log("Reseting player's life");
    }
    public void regenHealth(int regeneratedHealthTick){
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
    public void damage(int[] incomingDamage){
        int damage = 0;
        for (int dmg : incomingDamage){
            damage += dmg;
        }
        //If theres abyssal damage, target life first and the rest applies to Ward and then life
        if (incomingDamage[4] != 0){
            damageHealth(incomingDamage[4]);
            damage -= incomingDamage[4];
        }
        damage = damageWard(damage); //Will consume any ward before spilling the damage to life

        damageHealth(damage);
    }
    private void damageHealth(int damage){
        if (currentHealth-damage > 0){ //If the damage wont kill the entity, do:
            currentHealth -= damage;
        } else {
            currentHealth = 0;
        }
    }
    private int damageWard(int damage){ //Returns de damage value that overflows to life
        if (currentWard == 0){ //If there's no ward, just return the base damage, it should cascade to life
            return damage;
        }
        if (currentWard-damage >= 0){ //If the damage wont deplete ward, do:
            currentWard -= damage;
            return 0;
        } else {
            int overflowDamage = damage - (int) currentWard;
            currentWard = 0;
            return overflowDamage;
        }
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
        if (BaseStats.HEALTH.getValue() + addedHealth <= 0){ //If the base life modifiers gets your life to negative, your life is 1
            this.addedHealth = 0;
            return;
        }
        this.addedHealth = addedHealth;
    }
    //Alternative/Test method for adding life (needs a manual call to set the component as "final", so it can execute all the math)
    public void addBaseHealth(int addedHealth){
        this.addedHealth += addedHealth;
    }

    public int getIncreasedHealth() {
        return increasedHealth;
    }
    public void setIncreasedHealth(int increasedHealth){
        this.increasedHealth = increasedHealth;
    }
    //Compiler usage only
    public void addIncreasedHealth(int incHealth){
        this.increasedHealth += incHealth;
    }
    public void setMaxHealth(int addedHealth, int percentHealth){
        setAddedHealth(addedHealth);
        setIncreasedHealth(percentHealth);
        this.maxHealth = (getAddedHealth() + BaseStats.HEALTH.getValue()) * (1 + getIncreasedHealth()/100f);
    }
    public void setHealthRegen(int healthRegen) {
        this.healthRegen = healthRegen + BaseStats.HEALTH_REGEN.getValue();
    }
    public void addBaseHealthRegen(int healthRegen){
        this.healthRegen += healthRegen;
    }
    public int getHealthRegen() {
        return healthRegen;
    }
    //------------WARD METHODS-------------
    public void regenWard(float wardRegenTick){ //Standard ward regen call
        //In the specific case its already been capped out, ignore
        if (currentWard == maxWard){
            return;
        }
        //If this tick of regen surpasses the max ward, cap it to max ward
        if (currentWard+wardRegenTick>maxWard){
            currentWard = maxWard;
            return;
        }
        //If theres room to regenerate, do
        if (currentWard+wardRegenTick <= maxWard){
            this.currentWard += wardRegenTick;
        }
    }
    public float getWardRegenTick(){
        return (maxWard*(BaseStats.WARD_RECOVERY.getValue()/100F));
    }

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
        this.extraWard = extraWard;
    }
    //Same logic as addBaseHealth
    public void addBaseWard(int extraWard){
        this.extraWard += extraWard;
    }
    public int getIncreasedWard() {
        return increasedWard;
    }
    public void setIncreasedWard(int increasedWard){
        this.increasedWard = increasedWard;
    }
    //Compiler usage only
    public void addIncreasedWard(int incWard){
        this.increasedWard += incWard;
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
        if (currentHealth == 0){return 0;}
        return Math.max(0.5, getNormalizedHP()*basePlayerHearts);
    }

    public float getNormalizedWard(){
        return Math.min(currentWard/maxWard, 1F);
    }

    @Override
    public void update(UUID profileID) {
//        Profile profileData = JSONProfileManager.getProfile(profileID);
        setMaxHealth(getAddedHealth(), getIncreasedHealth());
        setMaxWard(getExtraWard(), getIncreasedWard());

        if (getCurrentHealth() > getMaxHealth()){
            currentHealth = getMaxHealth();
        }
        if (getCurrentWard() > getMaxWard()){
            currentWard = getMaxWard();
        }
    }
}
