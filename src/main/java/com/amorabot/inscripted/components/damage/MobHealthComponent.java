package com.amorabot.inscripted.components.damage;

import com.amorabot.inscripted.APIs.damageAPI.DamageSource;
import com.amorabot.inscripted.APIs.damageAPI.EntityStateManager;
import com.amorabot.inscripted.components.Mobs.Bestiary;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class MobHealthComponent {
    private static final Map<UUID, MobHealthComponent> mobHealthComponents = new HashMap<>();
    
    private final UUID mobId;
    private final Bestiary mobType;
    private final DamageTracker damageTracker;
    
    private double maxHealth;
    private double currentHealth;
    private boolean isDead;
    private long lastRegenTime;
    private String spawnerID;

    private MobHealthComponent(LivingEntity mob) {
        this.mobId = mob.getUniqueId();
        this.mobType = Bestiary.getBestiaryEntryFor(mob);
        this.damageTracker = new DamageTracker();
        this.spawnerID = EntityStateManager.getMobSpawnerID(mob);
        
        initializeHealth();
    }

    private void initializeHealth() {
        if (mobType != null && mobType.getHealthComponent() != null) {
            this.maxHealth = mobType.getHealthComponent().getMaxHealth();
            this.currentHealth = maxHealth;
        } else {
            this.maxHealth = 100.0;
            this.currentHealth = maxHealth;
        }
        this.isDead = false;
        this.lastRegenTime = System.currentTimeMillis();
    }

    public static MobHealthComponent getOrCreateHealthComponent(LivingEntity mob) {
        if (!EntityStateManager.isMob(mob)) {
            return null;
        }
        
        UUID mobId = mob.getUniqueId();
        return mobHealthComponents.computeIfAbsent(mobId, k -> new MobHealthComponent(mob));
    }

    public static MobHealthComponent getHealthComponent(UUID mobId) {
        return mobHealthComponents.get(mobId);
    }

    public static void removeHealthComponent(UUID mobId) {
        mobHealthComponents.remove(mobId);
    }

    public boolean takeDamage(LivingEntity attacker, int[] damageAmounts, DamageSource source, 
                             boolean isCritical, boolean dodged) {
        if (isDead || dodged) {
            return false;
        }

        int totalDamage = 0;
        for (int damage : damageAmounts) {
            totalDamage += damage;
        }

        LivingEntity mobEntity = getMobEntity();
        if (mobEntity == null) {
            return false;
        }

        DamageEvent event = new DamageEvent(mobEntity, attacker, damageAmounts, source, isCritical, dodged);
        
        damageTracker.recordDamageTaken(mobEntity, attacker, damageAmounts, source, isCritical, dodged);

        currentHealth = Math.max(0, currentHealth - totalDamage);
        
        // Notify observers about mob damage
        DamageEventNotifier.notifyMobDamaged(event);
        
        if (currentHealth <= 0) {
            isDead = true;
            // Notify observers about mob death
            DamageEventNotifier.notifyMobKilled(event);
            onDeath();
            return true;
        }

        return false;
    }

    public void heal(double healAmount) {
        if (isDead) return;
        
        currentHealth = Math.min(maxHealth, currentHealth + healAmount);
        damageTracker.recordHeal((int) healAmount);
        lastRegenTime = System.currentTimeMillis();
    }

    public void regenerate() {
        if (isDead || isInCombat()) return;
        
        long currentTime = System.currentTimeMillis();
        long timeSinceLastRegen = currentTime - lastRegenTime;
        
        if (timeSinceLastRegen >= 5000L && currentHealth < maxHealth) {
            double regenAmount = maxHealth * 0.01;
            heal(regenAmount);
        }
    }

    public boolean isInCombat() {
        return damageTracker.isInCombat(10000L);
    }

    public double getHealthPercentage() {
        return maxHealth > 0 ? (currentHealth / maxHealth) * 100.0 : 0.0;
    }

    public boolean isLowHealth() {
        return getHealthPercentage() <= 25.0;
    }

    public boolean isCriticalHealth() {
        return getHealthPercentage() <= 10.0;
    }

    private void onDeath() {
        damageTracker.reset();
    }

    private LivingEntity getMobEntity() {
        for (LivingEntity entity : org.bukkit.Bukkit.getServer().getWorlds().get(0).getLivingEntities()) {
            if (entity.getUniqueId().equals(mobId)) {
                return entity;
            }
        }
        return null;
    }

    public static void cleanup() {
        mobHealthComponents.entrySet().removeIf(entry -> {
            UUID mobId = entry.getKey();
            LivingEntity mob = null;
            
            for (LivingEntity entity : org.bukkit.Bukkit.getServer().getWorlds().get(0).getLivingEntities()) {
                if (entity.getUniqueId().equals(mobId)) {
                    mob = entity;
                    break;
                }
            }
            
            return mob == null || mob.isDead();
        });
    }

    public Map<String, Object> getHealthStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("mobId", mobId.toString());
        stats.put("mobType", mobType != null ? mobType.name() : "UNKNOWN");
        stats.put("spawnerID", spawnerID);
        stats.put("maxHealth", maxHealth);
        stats.put("currentHealth", currentHealth);
        stats.put("healthPercentage", getHealthPercentage());
        stats.put("isDead", isDead);
        stats.put("isInCombat", isInCombat());
        stats.put("totalDamageTaken", damageTracker.getTotalDamageTaken());
        stats.put("recentDamage", damageTracker.getRecentDamageTotal());
        return stats;
    }
}