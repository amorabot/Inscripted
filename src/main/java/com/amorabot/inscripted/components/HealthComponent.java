package com.amorabot.inscripted.components;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.PlayerStats;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;
import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;
import com.amorabot.inscripted.components.Player.BaseStats;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.components.Player.Stats;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class HealthComponent implements EntityComponent {

    public static final int LOW_LIFE_THRESHOLD = 20;

    private float currentHealth;
    private float maxHealth;
    private int healthRegen;

    private float currentWard;
    private float maxWard;
    private int wardRecovery;


    public HealthComponent(){
        this.maxHealth = BaseStats.HEALTH.getValue();
        this.currentHealth = BaseStats.HEALTH.getValue();
        this.maxWard = 0;
        this.currentWard = 0;
        this.healthRegen = 0;
        this.wardRecovery = BaseStats.WARD_RECOVERY_RATE.getValue();
    }

    public HealthComponent(int maxHealth, int maxWard){
        setMaxHealth((float) maxHealth);
        setCurrentHealth(maxHealth);
        setMaxWard((float) maxWard);
        setCurrentWard(maxWard);
        setHealthRegen(0);
    }

    //------------LIFE METHODS-------------
    public void replenishHitPoints(){
        currentHealth = maxHealth;
        currentWard = maxWard;
        Utils.log("Reseting player's life");
    }
    public int regenHealth(boolean inCombat, boolean bleeding){
        int regeneratedHealthTick = healthRegen;
        if (inCombat && !bleeding){
            regeneratedHealthTick = regeneratedHealthTick/2;
        } else if (inCombat) { //If in combat AND bleeding
            regeneratedHealthTick = regeneratedHealthTick/4;
        }

        //In the specific case its already been capped out, ignore
        if (currentHealth == maxHealth){
            return 0;
        }
        //If this tick of regen surpasses the maxHP, cap it to maxHP
        if (currentHealth+regeneratedHealthTick>maxHealth){
            int regenTick = (int) (maxHealth-currentHealth);
            currentHealth = maxHealth;
            return (regenTick);
        }
        //If theres room to regenerate, do
        if (currentHealth+regeneratedHealthTick <= maxHealth){
            this.currentHealth += regeneratedHealthTick;
        }
        return regeneratedHealthTick;
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
    public double getMappedWard(int basePlayerHearts){
        if (currentWard == 0){return 0;}
        return Math.max(0.5, getNormalizedWard()*basePlayerHearts);
    }

    public boolean isLowLife(){
        return (getCurrentHealth()/getMaxHealth()*(100)) < LOW_LIFE_THRESHOLD;
    }

    public void healHealth(int amount){
        if (currentHealth == maxHealth){return;}
        if (currentHealth+amount>maxHealth){
            currentHealth = maxHealth;
            return;
        }
        if (currentHealth+amount <= maxHealth){this.currentHealth += amount;}
    }

    @Override
    public void update(UUID profileID) {
        Profile profileData = JSONProfileManager.getProfile(profileID);
        Stats playerStats = profileData.getStats();
        setMaxHealth(playerStats.getFinalFlatValueFor(PlayerStats.HEALTH));

        setMaxWard(playerStats.getFinalFlatValueFor(PlayerStats.WARD));

        setHealthRegen((int) playerStats.getFinalFlatValueFor(PlayerStats.HEALTH_REGEN));

        setWardRecovery(playerStats.getFinalPercentValueFor(PlayerStats.WARD_RECOVERY_RATE));

        //Capping current values whenever their max is updated
        if (getCurrentHealth() > getMaxHealth()){
            currentHealth = getMaxHealth();
        }
        if (getCurrentWard() > getMaxWard()){
            currentWard = getMaxWard();
        }
    }
}
