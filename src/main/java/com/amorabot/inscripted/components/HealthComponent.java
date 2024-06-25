package com.amorabot.inscripted.components;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.PlayerStats;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;
import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;
import com.amorabot.inscripted.components.Items.modifiers.unique.Keystones;
import com.amorabot.inscripted.components.Player.BaseStats;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.components.Player.Stats;
import com.amorabot.inscripted.components.buffs.Buffs;
import com.amorabot.inscripted.components.buffs.categories.damage.DamageBuff;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerBuffManager;
import com.amorabot.inscripted.tasks.CombatHologramsDepleter;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;
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
    public static void replenishHitPoints(Player player){
        Profile playerProfile = JSONProfileManager.getProfile(player.getUniqueId());
        HealthComponent playerHP = playerProfile.getHealthComponent();
        playerHP.setCurrentHealth(playerHP.getMaxHealth());
        playerHP.setCurrentWard(playerHP.getMaxWard());
        playerProfile.updatePlayerHearts(player);
    }
    public int regenHealth(boolean inCombat, UUID playerID){
        Player player = Bukkit.getPlayer(playerID);
        assert player != null;
        boolean isBleeding = PlayerBuffManager.hasActiveBuff(Buffs.BLEED, player);
        //In the specific case its already been capped out, ignore
        boolean isFullLife = currentHealth == maxHealth;
        if (isFullLife){
            return 0;
        }

        int regeneratedHealthTick = healthRegen;
        if (isBleeding){
            regeneratedHealthTick = (int) (regeneratedHealthTick * 0.2);
        }
        if (inCombat){
            regeneratedHealthTick = regeneratedHealthTick/2;
        }


        //If this tick of regen surpasses the maxHP, cap it to maxHP
        if (currentHealth+regeneratedHealthTick>maxHealth){
            int regenTick = (int) (maxHealth-currentHealth);
            currentHealth = maxHealth;
            //Regenerated TO full heath, apply organ failure, if applicable
            Set<Keystones> keystones = JSONProfileManager.getProfile(playerID).getKeystones();
            if (!keystones.isEmpty()){
                if (keystones.contains(Keystones.ORGAN_FAILURE)){
                    Keystones.ORGAN_FAILURE.apply(playerID);
                }
            }
            return (regenTick);
        }
        //If theres room to regenerate, do
        if (currentHealth+regeneratedHealthTick <= maxHealth){
            this.currentHealth += regeneratedHealthTick;
        }
        return regeneratedHealthTick;
    }
    public void damage(int[] incomingDamage, Set<Keystones> defKeystones, Set<Keystones> atkrKeystones){
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
    public void damageHealth(int damage){
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
    public int regenWard(boolean inCombat){ //Standard ward regen call
        float wardRegen = getMaxWard() * (getWardRecovery()/100F);
        if (inCombat){
            wardRegen = wardRegen/2;
        }
        //In the specific case its already been capped out, ignore
        if (currentWard == maxWard){
            return 0;
        }
        //If this tick of regen surpasses the max ward, cap it to max ward
        if (currentWard+wardRegen>maxWard){
            int wardRegenTick = (int) (maxWard - currentWard);
            currentWard = maxWard;
            return wardRegenTick;
        }
        //If theres room to regenerate, do
        if (currentWard+wardRegen <= maxWard){
            this.currentWard += wardRegen;
        }
        return (int) wardRegen;
    }

    public float getNormalizedHP(){
        return Math.min(currentHealth/maxHealth, 1F);
    }
    public double getMappedHealth(){
        int basePlayerHearts = 20;
        if (currentHealth == 0){return 0;}
        return Math.max(0.5, getNormalizedHP()*basePlayerHearts);
    }

    public float getNormalizedWard(){
        return Math.min(currentWard/maxWard, 1F);
    }
    public double getMappedWard(){
        int basePlayerHearts = 20;
        if (currentWard == 0){return 0;}
        return Math.max(0.5, getNormalizedWard()*basePlayerHearts);
    }

    public boolean isLowLife(){
        return (getCurrentHealth()/getMaxHealth()*(100)) < LOW_LIFE_THRESHOLD;
    }

    public int healHealth(int amount, boolean bleeding, Player target, Set<Keystones> targetKeystones){
        boolean isFullLife = currentHealth == maxHealth;
        if (isFullLife){
            return 0;
        }

        int finalAmount = amount;
        if (bleeding){
            finalAmount = (int) (finalAmount * 0.2);
        }
        if (targetKeystones.contains(Keystones.BLOOD_PACT)){
            finalAmount = 2*finalAmount;
        }

        if (currentHealth+finalAmount>maxHealth){
            currentHealth = maxHealth;
            updateHealthHearts(target, this);
            return (int) (maxHealth - currentHealth);
        }
        if (currentHealth+finalAmount <= maxHealth){
            updateHealthHearts(target, this);
            this.currentHealth += finalAmount;
        }
        return finalAmount;
    }

    @Override
    public void update(UUID profileID) {
        Profile profileData = JSONProfileManager.getProfile(profileID);
        Stats playerStats = profileData.getStats();
        setMaxHealth(playerStats.getFinalFlatValueFor(PlayerStats.HEALTH));

        setMaxWard(Math.max(0, playerStats.getFinalFlatValueFor(PlayerStats.WARD)));

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

    public static void updateHealthHearts(Player player, HealthComponent playerHP){
        double mappedHealth = playerHP.getMappedHealth();
        if ((mappedHealth - player.getHealth()) >= 0.5D){
            player.setHealth(mappedHealth);
        }
    }
    public static void updateWardHearts(Player player, HealthComponent playerHP){
        double mappedWard = playerHP.getMappedWard();
        if ((mappedWard - player.getAbsorptionAmount()) >= 0.5D){
            player.setAbsorptionAmount(mappedWard);
        }
    }
}
