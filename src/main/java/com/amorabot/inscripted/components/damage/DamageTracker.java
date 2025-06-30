package com.amorabot.inscripted.components.damage;

import com.amorabot.inscripted.APIs.damageAPI.DamageSource;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

@Getter
public class DamageTracker {
    private static final long DEFAULT_DAMAGE_HISTORY_WINDOW_MS = 30_000L; // 30 seconds
    private static final int MAX_DAMAGE_EVENTS = 100;

    private final Deque<DamageEvent> damageHistory = new ConcurrentLinkedDeque<>();
    private final Map<UUID, Integer> damageByAttacker = new HashMap<>();
    
    private int totalDamageTaken = 0;
    private int totalDamageDealt = 0;
    private long lastDamageTimestamp = 0;
    private long lastHealTimestamp = 0;
    private boolean inCombat = false;
    private UUID lastAttacker = null;

    public void recordDamageTaken(LivingEntity target, LivingEntity attacker, int[] damageAmounts, 
                                 DamageSource source, boolean isCritical, boolean dodged) {
        
        DamageEvent event = new DamageEvent(target, attacker, damageAmounts, source, isCritical, dodged);
        
        synchronized (this) {
            damageHistory.addFirst(event);
            
            if (damageHistory.size() > MAX_DAMAGE_EVENTS) {
                damageHistory.removeLast();
            }
            
            if (!dodged) {
                totalDamageTaken += event.getTotalDamage();
                
                if (attacker != null) {
                    UUID attackerId = attacker.getUniqueId();
                    damageByAttacker.merge(attackerId, event.getTotalDamage(), Integer::sum);
                    lastAttacker = attackerId;
                }
                
                lastDamageTimestamp = System.currentTimeMillis();
                inCombat = true;
            }
        }
        
        // Notify observers of the damage event
        DamageEventNotifier.notifyDamageTaken(event);
        
        cleanupOldEvents();
    }

    public void recordDamageDealt(int totalDamage) {
        synchronized (this) {
            totalDamageDealt += totalDamage;
        }
    }

    public void recordHeal(int healAmount) {
        synchronized (this) {
            lastHealTimestamp = System.currentTimeMillis();
        }
    }

    public List<DamageEvent> getRecentDamage(long timeWindowMs) {
        cleanupOldEvents();
        List<DamageEvent> recentEvents = new ArrayList<>();
        
        synchronized (this) {
            for (DamageEvent event : damageHistory) {
                if (event.isRecent(timeWindowMs)) {
                    recentEvents.add(event);
                } else {
                    break;
                }
            }
        }
        
        return recentEvents;
    }

    public List<DamageEvent> getRecentDamage() {
        return getRecentDamage(DEFAULT_DAMAGE_HISTORY_WINDOW_MS);
    }

    public int getRecentDamageTotal(long timeWindowMs) {
        return getRecentDamage(timeWindowMs).stream()
                .filter(event -> !event.isDodged())
                .mapToInt(DamageEvent::getTotalDamage)
                .sum();
    }

    public int getRecentDamageTotal() {
        return getRecentDamageTotal(DEFAULT_DAMAGE_HISTORY_WINDOW_MS);
    }

    public Map<UUID, Integer> getDamageByAttacker(long timeWindowMs) {
        List<DamageEvent> recentEvents = getRecentDamage(timeWindowMs);
        Map<UUID, Integer> recentDamageByAttacker = new HashMap<>();
        
        for (DamageEvent event : recentEvents) {
            if (!event.isDodged() && event.getAttackerId() != null) {
                recentDamageByAttacker.merge(event.getAttackerId(), 
                                           event.getTotalDamage(), Integer::sum);
            }
        }
        
        return recentDamageByAttacker;
    }

    public boolean isInCombat(long combatTimeoutMs) {
        if (!inCombat) return false;
        
        long timeSinceLastDamage = System.currentTimeMillis() - lastDamageTimestamp;
        if (timeSinceLastDamage > combatTimeoutMs) {
            inCombat = false;
            return false;
        }
        
        return true;
    }

    public boolean isInCombat() {
        return isInCombat(10_000L); // 10 second default combat timeout
    }

    public double getDamagePerSecond(long timeWindowMs) {
        int recentDamage = getRecentDamageTotal(timeWindowMs);
        double timeWindowSeconds = timeWindowMs / 1000.0;
        return recentDamage / timeWindowSeconds;
    }

    public void reset() {
        synchronized (this) {
            damageHistory.clear();
            damageByAttacker.clear();
            totalDamageTaken = 0;
            totalDamageDealt = 0;
            lastDamageTimestamp = 0;
            lastHealTimestamp = 0;
            inCombat = false;
            lastAttacker = null;
        }
    }

    private void cleanupOldEvents() {
        long cutoffTime = System.currentTimeMillis() - DEFAULT_DAMAGE_HISTORY_WINDOW_MS;
        
        synchronized (this) {
            damageHistory.removeIf(event -> event.getTimestamp() < cutoffTime);
        }
    }
}