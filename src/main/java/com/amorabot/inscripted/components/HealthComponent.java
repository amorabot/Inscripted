package com.amorabot.inscripted.components;

import com.amorabot.inscripted.APIs.EventAPI;
import com.amorabot.inscripted.APIs.damageAPI.EntityStateManager;
import com.amorabot.inscripted.components.Player.stats.PlayerStats;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;
import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;
import com.amorabot.inscripted.components.Items.relic.enums.Keystones;
import com.amorabot.inscripted.components.Items.relic.enums.TriggerTimes;
import com.amorabot.inscripted.components.Items.relic.enums.TriggerTypes;
import com.amorabot.inscripted.components.Player.StatsComponent;
import com.amorabot.inscripted.components.Player.stats.BaseStats;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.components.Player.stats.StatPool;
import com.amorabot.inscripted.components.buffs.Buffs;
import com.amorabot.inscripted.components.renderers.CustomUnicodeTable;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerBuffManager;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

import static com.amorabot.inscripted.APIs.damageAPI.DamageRouter.notifyProfile;

@Getter
@Setter
public class HealthComponent implements EntityComponent,Cloneable {

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
        HealthComponent.updateHealthHearts(player,playerHP);
        HealthComponent.updateWardHearts(player,playerHP);
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
        final int basePlayerHearts = 20;
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
            this.currentHealth += finalAmount;
            updateHealthHearts(target, this);
        }
        return finalAmount;
    }

    @Override
    public void update(UUID profileID) {
        Profile profileData = JSONProfileManager.getProfile(profileID);

        StatsComponent playerStatsComponent = profileData.getStatsComponent();
        StatPool updatedStats = playerStatsComponent.getMergedStatsSnapshot();
        StatPool originalPlayerStats = playerStatsComponent.getPlayerStats();


        setMaxHealth(updatedStats.getFinalValueFor(PlayerStats.HEALTH,true));
        originalPlayerStats.clearStat(PlayerStats.HEALTH);

        setMaxWard(Math.max(0, updatedStats.getFinalValueFor(PlayerStats.WARD,true)));
        originalPlayerStats.clearStat(PlayerStats.WARD);

        setHealthRegen((int) updatedStats.getFinalValueFor(PlayerStats.HEALTH_REGEN,true));
        originalPlayerStats.clearStat(PlayerStats.HEALTH_REGEN);

        setWardRecovery((int) updatedStats.getFinalValueFor(PlayerStats.WARD_RECOVERY_RATE,true, ValueTypes.PERCENT));
        originalPlayerStats.clearStat(PlayerStats.WARD_RECOVERY_RATE);

        //Capping current values whenever their max is updated
        if (getCurrentHealth() > getMaxHealth()){
            currentHealth = getMaxHealth();
        }
        if (getCurrentWard() > getMaxWard()){
            currentWard = getMaxWard();
        }
    }

    public static void updateHeartContainers(LivingEntity entity, HealthComponent healthComponent){
        if (entity instanceof Player p){
            updateHealthHearts(p,healthComponent);
            updateWardHearts(p,healthComponent);
            return;
        }
        if (EntityStateManager.isMob(entity)){
            //Do whatever
            return;
        }
    }
    public static void updateHealthHearts(Player player, HealthComponent playerHP){
        double mappedHealth = playerHP.getMappedHealth();
        if (mappedHealth==0){
            execute(player);
            return;
        }
        double HPDiff = Math.abs((mappedHealth - player.getHealth()));
        if (HPDiff >= 0.5D){
            player.setHealth(mappedHealth);
        }
    }
    public static void updateWardHearts(Player player, HealthComponent playerHP){
        double mappedWard = playerHP.getMappedWard();
        double wardDiff = Math.abs((mappedWard - player.getAbsorptionAmount()));
        if (wardDiff >= 0.5D){
            player.setAbsorptionAmount(mappedWard);
        }
    }
    public static boolean execute(Player player){
        EventAPI.entityDeath(player);
        Player killer = player.getKiller();
        if (killer!= null){ //Late death trigger (Can be a troll effect :D)
            notifyProfile(killer,player, TriggerTypes.ON_DEATH, TriggerTimes.EARLY, new int[5]);
        }
        return true;
    }


    public Component getHealthBarComponent(){
        String hpBarNegSpace = "" + CustomUnicodeTable.M16 + CustomUnicodeTable.M64;
        String wardBarNegSpace = ""+CustomUnicodeTable.P3 + CustomUnicodeTable.M16 + CustomUnicodeTable.M64;
        if (getMappedWard()>0){
            return Component.text(getBarFrame()+hpBarNegSpace+getHPBar()+wardBarNegSpace+getWardBar()).decoration(TextDecoration.ITALIC, false);
        }
        return Component.text(getBarFrame()+hpBarNegSpace+getHPBar()).decoration(TextDecoration.ITALIC, false);
    }

    public String getBarFrame(){
        return "" + CustomUnicodeTable.HP_ICON + CustomUnicodeTable.M8 + CustomUnicodeTable.HP_BAR;
    }

    public String getHPBar(){
        StringBuilder hpBar = new StringBuilder(CustomUnicodeTable.HP_HEAD.toString());
        double mappedHealth = getMappedHealth()/2;

        int currentSegments = 1;
        //Empty bar rendering
        if (mappedHealth<=1){
            hpBar.append((""+CustomUnicodeTable.M1+CustomUnicodeTable.EMPTY_HP).repeat(8));
            hpBar.append(CustomUnicodeTable.M1).append(CustomUnicodeTable.EMPTY_HP_TAIL);
            return hpBar.toString();
        }
        //Full bar rendering
        if (mappedHealth>9){
            hpBar.append((""+CustomUnicodeTable.M1+CustomUnicodeTable.HP_FULL).repeat(8));
            hpBar.append(CustomUnicodeTable.M1).append(CustomUnicodeTable.HP_TAIL);
            return hpBar.toString();
        }

        //Intermediate bar rendering
        int fullSegments = (int) Math.abs(mappedHealth-1);
        double decimalHP = (mappedHealth-fullSegments+1);
        boolean hasHalfSegment = (decimalHP>=0.5);
        hpBar.append((""+CustomUnicodeTable.M1+CustomUnicodeTable.HP_FULL).repeat(fullSegments));
        currentSegments+=fullSegments;
        if (hasHalfSegment){
            hpBar.append(CustomUnicodeTable.M1).append(CustomUnicodeTable.HP_HALF);
        } else {
            hpBar.append(CustomUnicodeTable.M1).append(CustomUnicodeTable.EMPTY_HP);
        }
        currentSegments++;
        int emptySegments = 10 - currentSegments;
        if (emptySegments == 1){
            hpBar.append(CustomUnicodeTable.M1).append(CustomUnicodeTable.EMPTY_HP_TAIL);
            return hpBar.toString();
        }

        hpBar.append((""+CustomUnicodeTable.M1+CustomUnicodeTable.EMPTY_HP).repeat(emptySegments-1));
        hpBar.append(CustomUnicodeTable.M1).append(CustomUnicodeTable.EMPTY_HP_TAIL);
        return hpBar.toString();
    }

    public String getWardBar(){
        StringBuilder wardBar = new StringBuilder();
        double mappedWard = getMappedWard()/2;
        if (mappedWard==0){return "";}
        if (mappedWard<=1){return CustomUnicodeTable.HP_HEAD+(CustomUnicodeTable.P8.toString()).repeat(9);}
        if (mappedWard>9){
            wardBar.append(CustomUnicodeTable.WARD_HEAD);
            wardBar.append((""+CustomUnicodeTable.M1+CustomUnicodeTable.WARD_FULL).repeat(8));
            wardBar.append(CustomUnicodeTable.M1).append(CustomUnicodeTable.WARD_TAIL);
            return wardBar.toString();
        }
        //Intermediate bar rendering
        wardBar.append(CustomUnicodeTable.WARD_HEAD);
        int fullSegments = (int) Math.abs(mappedWard-1);
        int emptySegments = 10-fullSegments+1;
        boolean hasHalfSegment = (mappedWard-fullSegments+1)>=0.5;
        wardBar.append((""+CustomUnicodeTable.M1+CustomUnicodeTable.WARD_FULL).repeat(fullSegments));
        if (hasHalfSegment){
            wardBar.append(CustomUnicodeTable.M1).append(CustomUnicodeTable.WARD_HALF);
        }
        wardBar.append((CustomUnicodeTable.P8.toString()).repeat(emptySegments-2));
//        wardBar.append((CustomUnicodeTable.P16));
        return wardBar.toString();
    }

    @Override
    public HealthComponent clone() {
        try {
            HealthComponent clone = (HealthComponent) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
