package com.amorabot.inscripted.components.damage;

import com.amorabot.inscripted.APIs.damageAPI.DamageSource;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

@Getter
public class DamageEvent {
    private final UUID targetId;
    private final UUID attackerId;
    private final String attackerName;
    private final int[] damageAmounts;
    private final int totalDamage;
    private final DamageSource source;
    private final boolean isCritical;
    private final boolean dodged;
    private final long timestamp;

    public DamageEvent(LivingEntity target, LivingEntity attacker, int[] damageAmounts, 
                      DamageSource source, boolean isCritical, boolean dodged) {
        this.targetId = target.getUniqueId();
        this.attackerId = attacker != null ? attacker.getUniqueId() : null;
        this.attackerName = attacker != null ? attacker.getName() : "Unknown";
        this.damageAmounts = damageAmounts.clone();
        this.totalDamage = calculateTotalDamage(damageAmounts);
        this.source = source;
        this.isCritical = isCritical;
        this.dodged = dodged;
        this.timestamp = System.currentTimeMillis();
    }

    private int calculateTotalDamage(int[] damageAmounts) {
        int total = 0;
        for (int damage : damageAmounts) {
            total += damage;
        }
        return total;
    }

    public boolean isRecent(long timeWindowMs) {
        return (System.currentTimeMillis() - timestamp) <= timeWindowMs;
    }

    public long getTimeSince() {
        return System.currentTimeMillis() - timestamp;
    }
}