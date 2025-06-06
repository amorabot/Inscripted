package com.amorabot.inscripted.player.profile.component;

import com.amorabot.inscripted.item.inscription.definition.KeystoneIDs;
import com.amorabot.inscripted.item.inscription.definition.Stats;
import com.amorabot.inscripted.player.profile.BaseStats;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.amorabot.inscripted.player.PlayerDataContainer.getPlayerEquipment;

@Getter
@Setter
public class HealthComponent implements ProfileComponent {

    public static final int LOW_LIFE_THRESHOLD = 20;

    private int health;
    private int maxHealth;
    private int healthRegen;

    private int soul;
    private int maxSoul;
    private int soulRecovery;

    public HealthComponent(){
        this.health = BaseStats.HEALTH.getValue();
        this.maxHealth = BaseStats.HEALTH.getValue();
        this.healthRegen = BaseStats.HEALTH_REGEN.getValue();

        this.soul=0;
        this.maxSoul=0;
        this.soulRecovery=BaseStats.SOUL_RECOVERY.getValue();
    }
    public HealthComponent(int maxHealth, int maxSoul){ //For Mobs
        setMaxHealth(maxHealth);
        setHealth(maxHealth);
        setMaxSoul(maxSoul);
        setSoul(maxSoul);
        setHealthRegen(0);
    }

    @Override
    public void updateComponent(UUID playerID,Map<Stats, double[]> finalStats) {
        setMaxHealth(getSingleValueFrom(Stats.HEALTH,finalStats));
        setMaxSoul(getSingleValueFrom(Stats.SOUL,finalStats));
        setHealthRegen(getSingleValueFrom(Stats.HEALTH_REGEN,finalStats));
        setSoulRecovery(getSingleValueFrom(Stats.SOUL_RECOVERY_RATE,finalStats));

        //Capping overflowing HP/Soul
        if (health > getMaxHealth()){health = getMaxHealth();}
        if (soul > getMaxSoul()){soul = getMaxSoul();}
    }
    @Override
    public List<Component> asTextComponent() {
        return List.of();
    }

    public int regenHealth(boolean inCombat, UUID playerID){
        Player player = Bukkit.getPlayer(playerID);
        assert player != null;
        boolean isBleeding = false; //TODO: remake buffs
        boolean isFullLife = health == maxHealth;
        if (isFullLife){return 0;} //Stop regening

        int baseRegenTick = healthRegen;
        if (isBleeding){baseRegenTick = (int) (baseRegenTick * 0.2);}
        if (inCombat){baseRegenTick = baseRegenTick/2;}

        //If this tick would surpass maxHP, cap it to maxHP
        if (health+baseRegenTick>maxHealth){
            int regenTick = (int) (maxHealth-health);
            health = maxHealth;
            //Regenerated TO full heath, apply organ failure, if applicable
            //TODO: remake organ failure
            Set<KeystoneIDs> keystones = getPlayerEquipment(playerID).getEquipmenKeystones();
            return (regenTick);
        }
        //If theres room to regenerate, do
        if (health+baseRegenTick <= maxHealth){
            this.health += baseRegenTick;
        }
        return baseRegenTick;
    }

    public int healHealth(int amount, boolean bleeding, Player target, Set<KeystoneIDs> targetKeystones){
        boolean isFullLife = health == maxHealth;
        if (isFullLife){
            return 0;
        }

        int finalAmount = amount;
        if (bleeding){
            finalAmount = (int) (finalAmount * 0.2);
        }
//        if (targetKeystones.contains(Keystones.BLOOD_PACT)){
//            finalAmount = 2*finalAmount;
//        }

        if (health+finalAmount>maxHealth){
            health = maxHealth;
            updateHealthHearts(target, this);
            return (int) (maxHealth - health);
        }
        if (health+finalAmount <= maxHealth){
            this.health += finalAmount;
            updateHealthHearts(target, this);
        }
        return finalAmount;
    }
    public static void updateHealthHearts(Player player, HealthComponent playerHP){
        double mappedHealth = playerHP.getPlayerHearts();
        if (mappedHealth==0){
            //TODO: Trigger death event?
//            execute(player);
            return;
        }
        double HPDiff = Math.abs((mappedHealth - player.getHealth()));
        if (HPDiff >= 0.5D){
            player.setHealth(mappedHealth);
        }
    }
    public static void updateWardHearts(Player player, HealthComponent playerHP){
        double mappedWard = playerHP.getPlayerSoulHearts();
        double wardDiff = Math.abs((mappedWard - player.getAbsorptionAmount()));
        if (wardDiff >= 0.5D){
            player.setAbsorptionAmount(mappedWard);
        }
    }

    public void damage(int[] incomingDamage, Set<KeystoneIDs> defKeystones, Set<KeystoneIDs> atkrKeystones){
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
        if (health-damage > 0){ //If the damage wont kill the entity, do:
            health -= damage;
            return;
        }
        //Death event should be handled outside the component
        health = 0;
    }
    private int damageWard(int damage){ //Returns de damage value that overflows to life
        if (soul == 0){ //If there's no ward, just return the base damage, it should cascade to life
            return damage;
        }
        if (soul-damage >= 0){ //If the damage wont deplete ward, do:
            soul -= damage;
            return 0;
        }
        int overflowDamage = damage - (int) soul;
        soul = 0;
        return overflowDamage;
    }




    public int regenWard(boolean inCombat){ //Standard ward regen call
        int soulRegen = (int) (getMaxSoul() * (getSoulRecovery()/100F));
        if (inCombat){
            soulRegen = soulRegen/2;
        }
        //In the specific case its already been capped out, ignore
        if (soul == maxSoul){
            return 0;
        }
        //If this tick of regen surpasses the max ward, cap it to max ward
        if (soul+soulRegen>maxSoul){
            int soulRegenTick = (maxSoul - soul);
            soul = maxSoul;
            return soulRegenTick;
        }
        //If theres room to regenerate, do
        if (soul+soulRegen <= maxSoul){
            this.soul += soulRegen;
        }
        return soulRegen;
    }

    public float getNormalizedHP(){
        return Math.min((float) health /maxHealth, 1F);
    }
    public double getPlayerHearts(){
        final int basePlayerHearts = 20;
        if (health == 0){return 0;}
        return Math.max(0.5, getNormalizedHP()*basePlayerHearts);
    }

    public float getNormalizedSoul(){
        return Math.min((float) soul /maxSoul, 1F);
    }
    public double getPlayerSoulHearts(){
        int basePlayerHearts = 20;
        if (soul == 0){return 0;}
        return Math.max(0.5, getNormalizedSoul()*basePlayerHearts);
    }
    public boolean isLowLife(){
        return (getHealth()/getMaxHealth()*(100)) < LOW_LIFE_THRESHOLD;
    }

}
