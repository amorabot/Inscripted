package com.amorabot.inscripted.components.Items.Weapon;

import lombok.Getter;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Getter
public enum WeaponAttackSpeeds {

    HEAVY(1, -2),
    SLOW(1.5, -1),
    NORMAL(2.05, 0),
    QUICK(2.65, 1),
    FAST(2.9, 2);

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

    public PotionEffect getSwingAnimationBuff(){
        return switch (this.swingAnimationSpeed) {
            case -2 -> new PotionEffect(PotionEffectType.MINING_FATIGUE, 30, 2, true, false, false);
            case -1 -> new PotionEffect(PotionEffectType.MINING_FATIGUE, 30, 1,true, false, false);
            case 1 -> new PotionEffect(PotionEffectType.HASTE, 30, 1,true, false, false);
            case 2 -> new PotionEffect(PotionEffectType.HASTE, 30, 2,true, false, false);
            default -> null;
        };
    }
    public String getBarIndicator(){
        String filledDot = "♦";
        String emptyDot = "♢";
        StringBuilder bar = new StringBuilder("&8[ ");
        int filledDots = 3 + getSwingAnimationSpeed();
        int emptyDots = 5 - filledDots;
        String filled = "&f" + filledDot.repeat(filledDots);
        String empty = "&8" + emptyDot.repeat(emptyDots);
        bar.append(filled).append(empty).append(" ]  ");

        return bar.toString();
    }
    public int getAbilityCooldownModifier(){
        return 10 * (-swingAnimationSpeed); //Percentage value
    }
}
