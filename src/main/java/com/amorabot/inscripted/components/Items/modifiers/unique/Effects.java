package com.amorabot.inscripted.components.Items.modifiers.unique;

import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.components.buffs.Buffs;
import com.amorabot.inscripted.components.buffs.categories.stat.StatBuff;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerBuffManager;
import com.amorabot.inscripted.skills.GlobalCooldownManager;
import com.amorabot.inscripted.tasks.CombatHologramsDepleter;
import com.amorabot.inscripted.utils.Utils;
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
                boolean isBleeding = PlayerBuffManager.hasActiveBuff(Buffs.BLEED, playerCaster);
                int finalHealedAmount = casterHP.healHealth(healedAmount, isBleeding, (Player) target,casterProfile.getKeystones());
                CombatHologramsDepleter.getInstance().instantiateRegenHologram(caster.getLocation(),
                        "&c&lA. rush: "+"&2&l"+finalHealedAmount);
            }
        }

        @Override
        public void check(LivingEntity caster, LivingEntity target, int[] hit) {
            //add a little rng?
            execute(caster, target);
        }
    },
    GRACEFUL_LANDING(TriggerTypes.ON_MOVEMENT, TriggerTimes.LATE, 60,
            "Gracefully land","on your feet","when using your","movement ability.","","Gain Tailwind.","","⏳ 1m cooldown") {
        @Override
        public void execute(LivingEntity caster, LivingEntity target) {
            if (caster instanceof Player playerCaster){
                StatBuff tailwind = new StatBuff(Buffs.TAILWIND, playerCaster);
                PlayerBuffManager.addBuffToPlayer(tailwind, playerCaster);
            }
        }

        @Override
        public void check(LivingEntity caster, LivingEntity target, int[] hit) {
            if (GlobalCooldownManager.effecTriggered(caster.getUniqueId(),this)){
                execute(caster, target);
            }
        }
    },
    SADISM(TriggerTypes.ON_BLEED, TriggerTimes.LATE, 0,
            "Your Bleed debuffs","also apply Maim") {
        @Override
        public void execute(LivingEntity caster, LivingEntity target) {
            if (target instanceof Player targetCaster){
                StatBuff maim = new StatBuff(Buffs.MAIM, targetCaster);
                PlayerBuffManager.addBuffToPlayer(maim, targetCaster);
            }
        }

        @Override
        public void check(LivingEntity caster, LivingEntity target, int[] hit) {
            //If it was triggered, just execute
            execute(caster, target);
        }
    },
    COUP_DE_GRACE(TriggerTypes.ON_HIT, TriggerTimes.EARLY, 0,
            "Hits against","enemies below", "10% ❤ Health will","execute them") {
        @Override
        public void execute(LivingEntity caster, LivingEntity target) {
            if (target instanceof Player targetCaster){
                Profile.execute(targetCaster);
            }
        }

        @Override
        public void check(LivingEntity caster, LivingEntity target, int[] hit) {
            boolean shouldExecute = false;
            int totalDamage = 0;
            double executeHealth = 0;
            if (target instanceof Player targetCaster){
                Profile targetProfile = JSONProfileManager.getProfile(targetCaster.getUniqueId());
                HealthComponent targetHP = targetProfile.getHealthComponent();
                executeHealth = targetHP.getMaxHealth()*0.1;

                for (int dmg : hit){
                    totalDamage += dmg;
                }

                shouldExecute = (targetHP.getCurrentHealth() <= executeHealth) && (totalDamage<executeHealth);
            }

            if (shouldExecute){
                caster.sendMessage(Utils.color("&e&lCOUP DE GRACE! ("+(int)executeHealth+")"));
                execute(caster, target);
            }
        }
    },
    OPPORTUNIST(TriggerTypes.ON_CRIT, TriggerTimes.EARLY, 0,
            "Swiftly retreat","after a decisive","strike.","","Gain Tailwind on", "critical strikes.") {
        @Override
        public void execute(LivingEntity caster, LivingEntity target) {
            if (caster instanceof Player playerCaster){
                StatBuff tailwind = new StatBuff(Buffs.TAILWIND, playerCaster);
                PlayerBuffManager.addBuffToPlayer(tailwind, playerCaster);
            }
        }

        @Override
        public void check(LivingEntity caster, LivingEntity target, int[] hit) {
            execute(caster,target);
        }
    };


    private final TriggerTypes trigger;
    private final TriggerTimes timing;
    private final long cooldownInSeconds;
    private final String[] description; //Programatically substitute the "empty" last array item with a cooldown text, if it exists

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
