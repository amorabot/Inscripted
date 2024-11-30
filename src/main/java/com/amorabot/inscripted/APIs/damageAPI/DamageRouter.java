package com.amorabot.inscripted.APIs.damageAPI;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.components.Attack;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.modifiers.unique.Keystones;
import com.amorabot.inscripted.components.Items.modifiers.unique.TriggerTimes;
import com.amorabot.inscripted.components.Items.modifiers.unique.TriggerTypes;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.components.buffs.Buffs;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerBuffManager;
import com.amorabot.inscripted.managers.PlayerRegenManager;
import com.amorabot.inscripted.skills.PlayerAbilities;
import com.amorabot.inscripted.tasks.CombatLogger;
import com.amorabot.inscripted.tasks.CombatHologramsDepleter;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.amorabot.inscripted.APIs.damageAPI.AttackProcessor.rollDamages;

public class DamageRouter {

    public static void entityDamage(LivingEntity attacker,LivingEntity defender, DamageSource source, PlayerAbilities ability){
        if (attacker instanceof Player playerAttacker){
            playerAttack(playerAttacker,defender,source,ability);
            return;
        }
        if (EntityStateManager.isMob(attacker)){
            mobAttack(attacker,defender,source);
        }
    }

    private static void playerAttack(Player player, LivingEntity defender, DamageSource source, PlayerAbilities ability){
        defender.damage(0.01);
        if (defender instanceof Player def){
            if (EntityStateManager.isDead(def)){ //If the player is recieving hits during the death invuln. period
                Utils.log("Ignoring PvP Hits against " + def.getName());
                return;
            }
            PvP(player, def,source,ability);
            //....
            return;
        }

        if (EntityStateManager.isMob(defender)){
            PvE();
            //...
            return;
        }
    }
    public static void mobAttack(LivingEntity attacker, LivingEntity defender, DamageSource source){

    }

    private static void PvP(Player attacker, Player defender, DamageSource originalSource, PlayerAbilities ability){
        DamageSource source = originalSource;
        if (attacker.getUniqueId().equals(defender.getUniqueId()) && !originalSource.equals(DamageSource.SELF)){
            source = DamageSource.SELF;
        }

        Profile attackerProfile = JSONProfileManager.getProfile(attacker.getUniqueId());
        Profile defenderProfile = JSONProfileManager.getProfile(defender.getUniqueId());
        Attack attackerHit = attackerProfile.getDamageComponent().getHitData();
        DefenceComponent defenderDefence = defenderProfile.getDefenceComponent();

        final boolean dodged;
        dodged = AttackProcessor.attackResult(attackerHit, defenderDefence);

        int[] baseDamage = rollDamages(attackerHit.getDamages());
        final boolean isCriticalHit = AttackProcessor.isCriticalHit(attackerHit);

        final boolean isSelfDamage = source.equals(DamageSource.SELF);

        int[] rawHitDamage = AttackProcessor.processAttack(attackerProfile, defenderProfile, baseDamage, isCriticalHit, ability);

        if (dodged){
            CombatEffects.playDodgeEffectsAt(defender, attacker);
            AttackProcessor.dodgeAttack(rawHitDamage, 60); //Mutates rawHitDamage
        }

        damageDefendingPlayer(defender, rawHitDamage, isCriticalHit, isSelfDamage, attacker, originalSource);
    }
    private static void PvE(){

    }

    public static boolean damageDefendingPlayer(Player defender, int[] incomingHit, boolean isCriticalHit, boolean isSelfDamage,
                                             Player attacker,DamageSource originalSource){

        Profile defenderProfile = JSONProfileManager.getProfile(defender.getUniqueId());

        final boolean isDot = originalSource.equals(DamageSource.DOT);
        //If the damage's origins is not dot, trigger early hit effects
        if (!isDot){notifyHitTrigger(TriggerTimes.EARLY, attacker, defender, incomingHit, isCriticalHit);}

        /*
        COUP DE GRACE MAY cause stuff like:
            A killing blow executes the target and kills it
            But a arbitraty damage debuff can tick on that player during the 5tick window where the they are:
            1) Dead by heart update -> 2) Health not restored yet after death (virtual HP = 0 + Remapping) -- 5 tick delay -> 3) Replenish player life
                which can cause the dmg debuff to tick in the meantime the player isnt fully reset. a possible solution may be
                checking whether the player's HP is 0 when the dmg buff is ticking (If a double death never happens again, delete this)
        */
        double mappedHealth = defenderProfile.getHealthComponent().getMappedHealth();
        if (mappedHealth == 0){
            Utils.error("Early death: Attempting to damage player with 0 HP");
            if (EntityStateManager.isPlayerDead(defender)){
                Utils.error("he DEAD, stop!!!!!!!!!");
            }
            return true;
        }

        AttackProcessor.bleedAttempt(attacker, defender, incomingHit);

        playerDamaged(defender, incomingHit, isSelfDamage, attacker);
        //Late hit triggers
        if (!isDot){notifyHitTrigger(TriggerTimes.LATE, attacker, defender, incomingHit, isCriticalHit);}


        HealthComponent defHP = defenderProfile.getHealthComponent();
        double newMappedHealth = defHP.getMappedHealth();

        final boolean diedFromDamage = newMappedHealth == 0;
        debugCombat(attacker, defender, incomingHit, isCriticalHit, isSelfDamage);
        //The damage, its triggers and their consequences must be processed and then the player can actually die

        //Combat healing
        if (!isDot){ //Cannot apply on-hit heals with DoT effects
            if (!diedFromDamage){ //And if the attacker is already effectively dead
                boolean isBleeding = PlayerBuffManager.hasActiveBuff(Buffs.BLEED, attacker);
                combatHeal(attacker, isBleeding);
            }
        }

        //Adds attacker to combat
        if (!isSelfDamage){CombatLogger.addToPvPCombat(attacker, defender);}

        //Early death trigger
        if (diedFromDamage){notifyProfile(attacker,defender, TriggerTypes.ON_DEATH, TriggerTimes.EARLY, incomingHit);}
        /*
        If the player is effectively dead from this point on, let's update it their health and expect it to reflect eventual deaths
            - The player could be saved from special keystones
        */

        //Getting the updated health in case it's changed
        newMappedHealth = defHP.getMappedHealth();
        HealthComponent.updateHeartContainers(defender,defenderProfile.getHealthComponent());
        return newMappedHealth == 0;
    }

    //Handles the effects of a player being hit
    public static void playerDamaged(Player player, int[] incomingHit, boolean isSelfDamage, LivingEntity attacker){
        UUID playerID = player.getUniqueId();
        Profile playerProfile = JSONProfileManager.getProfile(playerID);
        HealthComponent HPComponent = playerProfile.getHealthComponent();


        Set<Keystones> atkrKeystones = new HashSet<>();
        if (attacker instanceof Player playerAtkr){
            player.setKiller(playerAtkr);
            atkrKeystones = JSONProfileManager.getProfile(playerAtkr.getUniqueId()).getKeystones();
        }

        HPComponent.damage(incomingHit,playerProfile.getKeystones(),atkrKeystones);
        //

        PlayerRegenManager.startWardRegenCooldownFor(playerID);
        if (!isSelfDamage){
            CombatLogger.addToCombat(player);
        }
    }

    public static void notifyProfile(Player attacker, Player defender, TriggerTypes trigger, TriggerTimes triggerTiming, int[] hit){
        Profile attackerProfile = JSONProfileManager.getProfile(attacker.getUniqueId());
        Profile defenderProfile = JSONProfileManager.getProfile(defender.getUniqueId());
        if (trigger.equals(TriggerTypes.WHEN_HIT) || trigger.equals(TriggerTypes.ON_DEATH)){
            //In those cases, the recieving end of the hit is the caster
            defenderProfile.notify(trigger, triggerTiming, defender, attacker, hit);
            return;
        }
        attackerProfile.notify(trigger, triggerTiming, attacker, defender, hit);
    }


    private static void debugCombat(Player attacker, Player defender,int[] incomingHit, boolean isCriticalHit, boolean isSelfDamage){
        String damageHoloString = Attack.getDamageString(incomingHit);

        CombatHologramsDepleter.getInstance().instantiateDamageHologramAt(defender.getLocation(), incomingHit);

        //DMG DEBUG FOR DEFdr ---------------------------------------------
        StringBuilder defenderDebug = new StringBuilder("&c&l<- ");
        defenderDebug.append(damageHoloString).append(" ");
        defenderDebug.append("&ffrom").append(" ");
        defenderDebug.append(attacker.getName()).append(" ");
        //---------------------------------------------

        String critMsg = "&e&l!CRITICAL!";
        if (isCriticalHit){
            Audience aud = Audience.audience(defender, attacker);
            defenderDebug.append(critMsg);
            SoundAPI.playGenericSoundAtLocation(aud, defender.getLocation(), "entity.zombie.break_wooden_door", 0.1f, 1.3f);
        }

        if (!isSelfDamage){
            Utils.msgPlayer(defender ,defenderDebug.toString());
        }

        //DMG DEBUG FOR ATKr ---------------------------------------------
        HealthComponent defHP = JSONProfileManager.getProfile(defender.getUniqueId()).getHealthComponent();
        StringBuilder attackerDebug = new StringBuilder(damageHoloString);
        attackerDebug.append("&a&l -> &f").append(defender.getName()).append(" ");
        attackerDebug.append("&c&l[").append(( int )(defHP.getCurrentHealth())).append("]");
        if (defHP.getCurrentWard() > 0){attackerDebug.append("&3&l[").append(( int )(defHP.getCurrentWard())).append("] ");}

        if (isCriticalHit){attackerDebug.append(critMsg);}

        Utils.msgPlayer(attacker ,attackerDebug.toString());
    }

    private static void notifyHitTrigger(TriggerTimes timing, Player attacker, Player defender, int[] incomingHit, boolean isCriticalHit){
        notifyProfile(attacker, defender, TriggerTypes.ON_HIT, timing, incomingHit);
        notifyProfile(attacker, defender, TriggerTypes.WHEN_HIT, timing, incomingHit);
        if (isCriticalHit){notifyProfile(attacker, defender, TriggerTypes.ON_CRIT, timing ,incomingHit);}
    }
    private static void combatHeal(Player attacker, boolean isBleeding){
        Profile attackerProfile = JSONProfileManager.getProfile(attacker.getUniqueId());
        int lifeHealed = attackerProfile.getDamageComponent().getLifeOnHit();

        int finalLifeHealed = attackerProfile.getHealthComponent().healHealth(lifeHealed, isBleeding, attacker, attackerProfile.getKeystones());
        if (finalLifeHealed>0){
            CombatHologramsDepleter.getInstance().instantiateRegenHologram(attacker.getLocation(), "&2"+finalLifeHealed);
        }
    }
}
