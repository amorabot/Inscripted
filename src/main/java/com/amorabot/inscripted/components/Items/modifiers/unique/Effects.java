package com.amorabot.inscripted.components.Items.modifiers.unique;

import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.skills.GlobalCooldownManager;
import com.amorabot.inscripted.tasks.CombatHologramsDepleter;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Getter
public enum Effects {

    THRILL_OF_THE_HUNT(TriggerTypes.ON_HIT, TriggerTimes.LATE, 10,
            "When striking low life", "targets, gain Speed",
            "and Night Vision","for 20s.", "", "⏳ 10s cooldown") {
        @Override
        public void execute(LivingEntity caster, LivingEntity target) {
            int durationInSec = 20;
            if (!GlobalCooldownManager.effecTriggered(caster.getUniqueId(),this)){return;}
            PotionEffect speedBuff = new PotionEffect(PotionEffectType.SPEED, durationInSec*20, 1, true, false, false);
            PotionEffect nighVision = new PotionEffect(PotionEffectType.NIGHT_VISION, durationInSec*20, 0, true, false, false);
            speedBuff.apply(caster);
            nighVision.apply(caster);
        }

        @Override
        public void check(LivingEntity caster, LivingEntity target, int[] hit) {
            if (target instanceof Player targetPlayer){
                Profile targetProfile = JSONProfileManager.getProfile(targetPlayer.getUniqueId());
                HealthComponent targetHP = targetProfile.getHealthComponent();
                float healthPercentage = (targetHP.getCurrentHealth() / targetHP.getMaxHealth())*100;
                if (healthPercentage < HealthComponent.LOW_LIFE_THRESHOLD){
                    execute(caster, target);
                }
            }
        }
    },
    OVERDRIVE(TriggerTypes.WHEN_HIT, TriggerTimes.LATE, 0,
            "When hit by " + DamageTypes.LIGHTNING.getCharacter() + " DMG", "gain Speed II for 4s") {
        @Override
        public void execute(LivingEntity caster, LivingEntity target) {
            if (!GlobalCooldownManager.effecTriggered(caster.getUniqueId(),this)){return;}
            PotionEffect speedBuff = new PotionEffect(PotionEffectType.SPEED, 100, 1, true, false, false);
            speedBuff.apply(caster);
            //TODO: particle effects?
        }

        @Override
        public void check(LivingEntity caster, LivingEntity target, int[] hit) {
            if (hit == null){return;}
            if (hit.length == 5){
                if (hit[2]>0){ //If the final damage has a lightning component
                    execute(caster, target);
                }
            }
        }
    },
    ADRENALINE_RUSH(TriggerTypes.ON_DEATH, TriggerTimes.EARLY, 120,
            "Upon taking a killing", "blow, instead heal", "10% ❤ Health", "", "⏳ 2m cooldown") {
        @Override
        public void execute(LivingEntity caster, LivingEntity target) {
            if (caster instanceof Player playerCaster){
                if (!GlobalCooldownManager.effecTriggered(caster.getUniqueId(),this)){return;}
                Profile casterProfile = JSONProfileManager.getProfile(playerCaster.getUniqueId());
                HealthComponent casterHP = casterProfile.getHealthComponent();
                int healedAmount = (int) (casterHP.getMaxHealth() * 0.1);
                int finalHealedAmount = casterHP.healHealth(healedAmount, casterProfile.getKeystones());
                CombatHologramsDepleter.getInstance().instantiateRegenHologram(caster.getLocation(),
                        "&c&lA. rush: "+"&2&l"+finalHealedAmount);
            }
        }

        @Override
        public void check(LivingEntity caster, LivingEntity target, int[] hit) {
            //add a little rng?
            execute(caster, target);
        }
    };


    private final TriggerTypes trigger;
    private final TriggerTimes timing;
    private final long cooldownInSeconds;
    private final String[] description;

    Effects(TriggerTypes trigger, TriggerTimes timing, long CD, String... description){
        this.trigger = trigger;
        this.timing = timing;
        this.cooldownInSeconds = CD;
        this.description = description;
    }

    public abstract void execute(LivingEntity caster, LivingEntity target);

    public abstract void check(LivingEntity caster, LivingEntity target, int[] hit);

    public boolean isPvP(LivingEntity caster, LivingEntity target){
        return caster instanceof Player == target instanceof Player;
    }
    public String getDisplayName(){
        return this.toString().replace("_"," ") + " - " + this.trigger.getIcon();
    }
}
