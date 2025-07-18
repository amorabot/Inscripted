package com.amorabot.inscripted.item.inscription.definition;

import com.amorabot.inscripted.combat.buffs.Buffs;
import com.amorabot.inscripted.combat.buffs.PlayerBuffManager;
import com.amorabot.inscripted.combat.buffs.categories.stat.StatBuff;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.PlayerEvents;
import com.amorabot.inscripted.player.profile.Profile;
import com.amorabot.inscripted.player.profile.component.HealthComponent;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

@Getter
public enum EffectIDs {
    THRILL_OF_THE_HUNT(TriggerTypes.ON_HIT, TriggerTimes.LATE, 10) {
        @Override
        void execute(Player caster, Player target, int[] incomingHit) {
            int durationInSec = 20;
            PotionEffect speedBuff = new PotionEffect(PotionEffectType.SPEED, durationInSec*20, 1, true, false, false);
            PotionEffect nighVision = new PotionEffect(PotionEffectType.NIGHT_VISION, durationInSec*20, 0, true, false, false);
            speedBuff.apply(caster);
            nighVision.apply(caster);
        }
        @Override
        public boolean check(Player caster, Player target, int[] incomingHit) {
            Profile targetProfile = PlayerDataContainer.getProfile(target.getUniqueId());
            HealthComponent targetHP = targetProfile.getHealthComponent();
            boolean canTriggerEffect = PlayerDataContainer.getDataContainerFor(caster.getUniqueId()).effecTriggered(this);

            return (targetHP.isLowLife() & canTriggerEffect);
        }
    },
    OVERDRIVE(TriggerTypes.WHEN_HIT, TriggerTimes.LATE, 0) {
        @Override
        void execute(Player caster, Player target, int[] incomingHit) {
            PotionEffect speedBuff = new PotionEffect(PotionEffectType.SPEED, 100, 1, true, false, false);
            speedBuff.apply(caster);
        }
        @Override
        public boolean check(Player caster, Player target, int[] incomingHit) {
            if (incomingHit == null){return false;}
            boolean hasLightningDmg = false;
            if (incomingHit.length == 5){
                if (incomingHit[2]>0){ //If the final damage has a lightning component
                    hasLightningDmg = true;
                }
            }
            return hasLightningDmg;
        }
    },
    ADRENALINE_RUSH(TriggerTypes.ON_DEATH, TriggerTimes.EARLY, 120) {
        @Override
        void execute(Player caster, Player target, int[] incomingHit) {
            PlayerDataContainer casterData = PlayerDataContainer.getDataContainerFor(caster.getUniqueId());
            Profile casterProfile = casterData.getProfile();
            HealthComponent casterHP = casterProfile.getHealthComponent();
            int healedAmount = (int) (casterHP.getMaxHealth() * 0.1);
            boolean isBleeding = PlayerBuffManager.hasActiveBuff(Buffs.BLEED, caster.getUniqueId());
            int finalHealedAmount = casterHP.healHealth(healedAmount, isBleeding, caster, casterData.getEquipment().getSpecialInscriptions().getKeystones());
            Utils.error("HEALED AMOUNT ON ADREN. RUSH PROC: " + finalHealedAmount);
            HealthComponent.updateHealthHearts(caster,casterHP);
        }
        @Override
        public boolean check(Player caster, Player target, int[] incomingHit) {
            PlayerDataContainer casterData = PlayerDataContainer.getDataContainerFor(caster.getUniqueId());
            return casterData.effecTriggered(this);
        }
    },
    GRACEFUL_LANDING(TriggerTypes.ON_MOVEMENT, TriggerTimes.LATE, 60) {
        @Override
        void execute(Player caster, Player target, int[] incomingHit) {
            StatBuff tailwind = new StatBuff(Buffs.TAILWIND, caster);
            PlayerBuffManager.addBuffToPlayer(tailwind, caster.getUniqueId());
        }
        @Override
        public boolean check(Player caster, Player target, int[] incomingHit) {
            PlayerDataContainer casterData = PlayerDataContainer.getDataContainerFor(caster.getUniqueId());
            return casterData.effecTriggered(this);
        }
    },
    SADISM(TriggerTypes.ON_BLEED, TriggerTimes.LATE, 0) {
        @Override
        void execute(Player caster, Player target, int[] incomingHit) {
            StatBuff maim = new StatBuff(Buffs.MAIM, target);
            PlayerBuffManager.addBuffToPlayer(maim, target.getUniqueId());
        }
        @Override
        public boolean check(Player caster, Player target, int[] incomingHit) {return true;}
    },
    COUP_DE_GRACE(TriggerTypes.ON_HIT, TriggerTimes.EARLY, 0) {
        @Override
        void execute(Player caster, Player target, int[] incomingHit) {
            target.setKiller(caster);
            PlayerDataContainer.getDataContainerFor(target.getUniqueId()).onNotify(PlayerEvents.DEATH);
        }
        @Override
        public boolean check(Player caster, Player target, int[] incomingHit) {
            boolean shouldExecute;
            int totalDamage = Arrays.stream(incomingHit).sum();
            double executeHealth = 0;
            Profile targetProfile = PlayerDataContainer.getProfile(target.getUniqueId());
            HealthComponent targetHP = targetProfile.getHealthComponent();
            executeHealth = targetHP.getMaxHealth()*0.1;

            shouldExecute = (targetHP.getHealth() <= executeHealth) && (totalDamage<executeHealth);
            if (shouldExecute) caster.sendMessage(Utils.color("&e&lCOUP DE GRACE! ("+(int)executeHealth+")"));
            return shouldExecute;
        }
    },
    OPPORTUNIST(TriggerTypes.ON_CRIT, TriggerTimes.EARLY, 0) {
        @Override
        void execute(Player caster, Player defender, int[] incomingHit) {
            StatBuff tailwind = new StatBuff(Buffs.TAILWIND, caster);
            PlayerBuffManager.addBuffToPlayer(tailwind, caster.getUniqueId());
        }
        @Override
        public boolean check(Player caster, Player target, int[] incomingHit) {return true;}
    };


    private final TriggerTypes trigger;
    private final TriggerTimes triggerTime;
    private final int cooldowInSeconds;

    EffectIDs(TriggerTypes trigger,TriggerTimes triggerTime,int cdInSeconds){
        this.trigger = trigger;
        this.triggerTime = triggerTime;
        this.cooldowInSeconds = cdInSeconds;
    }

    public String getInfo(){
        String triggerDN = getTrigger().getDisplayName();
        if (getCooldowInSeconds() == 0){
            return triggerDN;
        }
        return (triggerDN + " - ⏳" + getCooldowInSeconds() + "s");
    }

    public void trigger(Player attacker, Player defender, int[] incomingHit){
        if (check(attacker, defender, incomingHit)){
            Utils.log(this + " got triggered!");
            execute(attacker, defender, incomingHit);
        }
    }
    abstract void execute(Player caster, Player target, int[] incomingHit);

    public boolean check(Player caster, Player target, int[] incomingHit) {
        Utils.error("Cooldown/Logic check not implemented for " + this);
        return false;
    }
}
