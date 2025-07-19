package com.amorabot.inscripted.combat.damage;

import com.amorabot.inscripted.APIs.damageAPI.CombatEffects;
import com.amorabot.inscripted.APIs.damageAPI.EntityStateManager;
import com.amorabot.inscripted.combat.buffs.Buffs;
import com.amorabot.inscripted.combat.buffs.PlayerBuffManager;
import com.amorabot.inscripted.item.inscription.definition.KeystoneIDs;
import com.amorabot.inscripted.item.inscription.definition.TriggerTimes;
import com.amorabot.inscripted.item.inscription.definition.TriggerTypes;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.Profile;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.player.profile.component.DefenceComponent;
import com.amorabot.inscripted.player.profile.component.HealthComponent;
import com.amorabot.inscripted.tasks.RegenerationTask;
import com.amorabot.inscripted.tasks.base.Skillcast;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.entity.Player;

import java.util.Set;

import static com.amorabot.inscripted.combat.damage.AttackProcessor.isCriticalHit;
import static com.amorabot.inscripted.combat.damage.AttackProcessor.rollDamages;

public class DamageRouter {
    //Will only support PvP for now

    public static void hit(Player attacker, Player defender, Skillcast attackerSkill, AttackData skillAttackData, DamageSource damageSource){
        defender.damage(0.001);
        DamageSource definitiveSource = damageSource;
        if (attackerSkill.getPlayerID().equals(defender.getUniqueId()) && !damageSource.equals(DamageSource.SELF)){
            // If the attacker skill triggered a self damage, without a explicit "SELF" dmg source, rectify that
            definitiveSource = DamageSource.SELF;
        }

        PlayerDataContainer attackerData = PlayerDataContainer.getDataContainerFor(attacker.getUniqueId());
        PlayerDataContainer defenderData = PlayerDataContainer.getDataContainerFor(defender.getUniqueId());

        DefenceComponent defenderDefences = defenderData.getProfile().getDefenceComponent();

        final boolean dodged = AttackProcessor.attackResult(skillAttackData,defenderDefences);
        final boolean dot = definitiveSource.equals(DamageSource.DOT);
        boolean critical = false;
        if (!dot) critical = isCriticalHit(skillAttackData);
        final boolean selfDamage = definitiveSource.equals(DamageSource.SELF);

        int[] baseAttackDamage = rollDamages(skillAttackData);

        int[] processedHitDamage = AttackProcessor.processAttack(attackerData,skillAttackData,defenderData,baseAttackDamage,critical);
        if (dodged){
            CombatEffects.playDodgeEffectsAt(defender,attacker);
            defenderData.onNotify(TriggerTimes.EARLY, TriggerTypes.ON_DODGE,attacker,processedHitDamage);

            AttackProcessor.applyDodgeMitigation(processedHitDamage,DefenceCalculator.DODGE_MITIGATION);
        }

        //Early hit trigger
        if (!dot) {notifyHitTriggers(TriggerTimes.EARLY, attacker, defender, processedHitDamage, critical);}

        hitPlayer(attacker,defender,skillAttackData,processedHitDamage,definitiveSource,critical,selfDamage,dot);
    }
    public static boolean hitPlayer(Player attacker, Player defender, AttackData baseHitData, int[] incomingHit, DamageSource damageSource,
                                 boolean criticalHit, boolean selfDamage, boolean isDot){
        PlayerDataContainer attackerData = PlayerDataContainer.getDataContainerFor(attacker.getUniqueId());
        PlayerDataContainer defenderData = PlayerDataContainer.getDataContainerFor(defender.getUniqueId());

        HealthComponent defenderHealth = defenderData.getProfile().getHealthComponent();
        double mappedHealth = defenderHealth.getPlayerHearts();
        if (mappedHealth == 0){
            Utils.error("Early death: Attempting to damage player with 0 HP");
            if (EntityStateManager.isPlayerDead(defender)){
                Utils.error("ded");
            }
            return true;
        }
        // Actual hit processing start
        //TODO: extract bleed chance & dmg for bleedAttempt() call
        AttackProcessor.bleedAttemptOnPlayer(attacker, defender, baseHitData, incomingHit);
        damagePlayer(defender, incomingHit, selfDamage, attacker);
        // Combat log attacker
        //...

        //Late hit trigger
        if (!isDot) {notifyHitTriggers(TriggerTimes.LATE, attacker, defender, incomingHit, criticalHit);}

        double updatedHP = defenderHealth.getPlayerHearts();
        // Player is virtually dead, trigger all before-death effects
        if (updatedHP == 0) defenderData.onNotify(TriggerTimes.EARLY,TriggerTypes.ON_DEATH,defender,incomingHit);

        // Updating HP after death triggers (Adrenaline, for instance)
        updatedHP = defenderHealth.getPlayerHearts();
        //TODO: COMBAT DEBUG

        final boolean definitivelyDead = updatedHP == 0;
        if (!definitivelyDead){
            HealthComponent.updateHealthHearts(defender,defenderHealth);
            HealthComponent.updateSoulHearts(defender,defenderHealth);
        } else {
            Utils.log("Defender " + defender.getName() + " actually died to damage!");
            return true;
        }

        //Combat healing
        if (!isDot){ // Heal attacker based on non-lethal attack
            boolean isBleeding = PlayerBuffManager.hasActiveBuff(Buffs.BLEED, attacker.getUniqueId());
            combatHeal(attacker, isBleeding);
        }
        return false;
    }
    //Handles the effects of a player being hit
    public static void damagePlayer(Player defender, int[] incomingHit, boolean isSelfDamage, Player attacker){
        PlayerDataContainer attackerData = PlayerDataContainer.getDataContainerFor(attacker.getUniqueId());
        PlayerDataContainer defenderData = PlayerDataContainer.getDataContainerFor(defender.getUniqueId());
        Set<KeystoneIDs> attackerKeystones = attackerData.getEquipment().getSpecialInscriptions().getKeystones();
        Set<KeystoneIDs> defenderKeystones = defenderData.getEquipment().getSpecialInscriptions().getKeystones();

        defenderData.getProfile().getHealthComponent().damage(incomingHit,defenderKeystones,attackerKeystones);
        RegenerationTask.startSoulRegenCooldownFor(defender.getUniqueId());
        if (!isSelfDamage){
            Utils.log("Combat logged!");
//            CombatLogger.addToCombat(player);
        }
    }
    private static void combatHeal(Player attacker, boolean isBleeding){
        PlayerDataContainer attackerData = PlayerDataContainer.getDataContainerFor(attacker.getUniqueId());
        Set<KeystoneIDs> attackerKeystones = attackerData.getEquipment().getSpecialInscriptions().getKeystones();
        Profile attackerProfile = attackerData.getProfile();
        int lifeHealed = attackerProfile.getDamageComponent().getLifeOnHit();

        int finalLifeHealed = attackerProfile.getHealthComponent().healHealth(lifeHealed, isBleeding, attacker, attackerKeystones);
        Utils.log("Life healed for " + attacker.getDisplayName() + ": " + finalLifeHealed);
//        if (finalLifeHealed>0){
//            CombatHologramsDepleter.getInstance().instantiateRegenHologram(attacker.getLocation(), "&2"+finalLifeHealed);
//        }
    }
    private static void notifyHitTriggers(TriggerTimes timing, Player attacker, Player defender, int[] incomingHit, boolean isCriticalHit){
        PlayerDataContainer attackerData = PlayerDataContainer.getDataContainerFor(attacker.getUniqueId());
        PlayerDataContainer defenderData = PlayerDataContainer.getDataContainerFor(defender.getUniqueId());
        attackerData.onNotify(timing, TriggerTypes.ON_HIT, defender, incomingHit);
        defenderData.onNotify(timing,TriggerTypes.WHEN_HIT, attacker, incomingHit);
        if (isCriticalHit){attackerData.onNotify(timing, TriggerTypes.ON_CRIT, defender, incomingHit);}
    }
}
