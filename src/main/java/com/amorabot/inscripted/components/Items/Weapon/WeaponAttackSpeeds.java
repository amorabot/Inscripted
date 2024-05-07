package com.amorabot.inscripted.components.Items.Weapon;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum WeaponAttackSpeeds {

    HEAVY(0.83, -2),
    SLOW(1.5, -1),
    NORMAL(2.05, 0),
    QUICK(2.5, 1),
    FAST(3.1, 2);

    private final double attacksPerSecond;
    private final double itemUsageCooldown;
    private final int swingAnimationSpeed;

    //https://wynncraft.fandom.com/wiki/Weapons
    WeaponAttackSpeeds(double APS, int swingSpeed){
        this.attacksPerSecond = APS;
        this.itemUsageCooldown = 1 / APS;   // X Attacks -> 1s
                                            // 1 Attacks -> Ys
        this.swingAnimationSpeed = swingSpeed;
    }

    public double getAttacksPerSecond() {
        return attacksPerSecond;
    }
    public double getItemUsageCooldown() {
        return itemUsageCooldown;
    }
    public PotionEffect getSwingAnimationBuff(){
        return switch (this.swingAnimationSpeed) {
            case -2 -> new PotionEffect(PotionEffectType.SLOW_DIGGING, 30, 2);
            case -1 -> new PotionEffect(PotionEffectType.SLOW_DIGGING, 30, 1);
            case 1 -> new PotionEffect(PotionEffectType.FAST_DIGGING, 30, 1);
            case 2 -> new PotionEffect(PotionEffectType.FAST_DIGGING, 30, 2);
            default -> null;
        };
    }
}
