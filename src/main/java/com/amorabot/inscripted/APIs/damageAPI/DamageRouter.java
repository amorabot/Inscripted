package com.amorabot.inscripted.APIs.damageAPI;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Attack;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.modifiers.unique.Effects;
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
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.amorabot.inscripted.APIs.damageAPI.AttackProcessor.rollDamages;

public class DamageRouter {
    private static final NamespacedKey mobKey = new NamespacedKey(Inscripted.getPlugin(), "INSCRIPTED_MOB");

    public static void playerAttack(Player player, LivingEntity defender, DamageSource source, PlayerAbilities ability){ //Rename to entityDamage??
        defender.damage(0.01);
        boolean playerDefender = defender instanceof Player;
        if (playerDefender){
            //PvP Logic
            playerVsPlayerDamage(player, (Player) defender, source, ability);
            if (defender.isDead()){
                Utils.log("he ded :(");
            }
        } else {
            //PvE Logic
            //...
        }
    }

    public static void playerVsPlayerDamage(Player attacker, Player defender, DamageSource originalSource, PlayerAbilities ability){
        boolean hitLanded;
        DamageSource source = originalSource;
        if (attacker.getUniqueId().equals(defender.getUniqueId()) && !originalSource.equals(DamageSource.SELF)){
            source = DamageSource.SELF;
        }

        Profile attackerProfile = JSONProfileManager.getProfile(attacker.getUniqueId());
        Profile defenderProfile = JSONProfileManager.getProfile(defender.getUniqueId());

        Attack atk = attackerProfile.getDamageComponent().getHitData();
        DefenceComponent def = defenderProfile.getDefenceComponent();

        hitLanded = AttackProcessor.attackResult(atk, def);
        Attack attackerDamage = attackerProfile.getDamageComponent().getHitData();
        int[] baseDamage = rollDamages(attackerDamage.getDamages());
        boolean isCriticalHit = AttackProcessor.isCriticalHit(atk);
        int[] incomingHit = AttackProcessor.processAttack(attackerProfile, defenderProfile, baseDamage, isCriticalHit, ability); //Base damage for the incoming attack

        if (!hitLanded){
            CombatEffects.playDodgeEffectsAt(defender, attacker);
            AttackProcessor.dodgeAttack(incomingHit, 60);
        }

////        Apply bleed
//        AttackProcessor.applyBleed(attacker, defender, incomingHit);

//        debugCombat(attacker, defender, incomingHit, isCriticalHit, source.equals(DamageSource.SELF));
//
//        //Combat healing
//        combatHeal(attacker);

        damagePlayer(defender, incomingHit, isCriticalHit, source.equals(DamageSource.SELF), attacker, originalSource);
    }

    public static boolean damagePlayer(Player defender, int[] incomingHit, boolean isCriticalHit, boolean isSelfDamage,
                                    Player attacker,DamageSource originalSource){

//        boolean isPlayerAttacker = attacker instanceof Player; TODO: implement polymorphysm

        boolean isDoT = originalSource.equals(DamageSource.DOT);
        Profile defenderProfile = JSONProfileManager.getProfile(defender.getUniqueId());

        if (!isDoT){ //Early hit triggers
            notifyHitTrigger(TriggerTimes.EARLY, attacker, defender, incomingHit, isCriticalHit);
        }

        /*
        COUP DE GRACE MAY cause stuff like:
            A killing blow executes the target and kills it
            But a arbitraty damage debuff can tick on that player during the 5tick window where the they
            are:
            1) Dead by heart update -> 2) Health not restored yet after death (virtual HP = 0 + Remapping) -- 5 tick delay -> 3) Replenish player life
                which can cause the dmg debuff to tick in the meantime the player isnt fully reset. a possible solution may be
                checking whether the player's HP is 0 when the dmg buff is ticking (If a double death never happens again, delete this)
        */

        boolean wasExecutedEarly = defenderProfile.getHealthComponent().getCurrentHealth() == 0;
        if (wasExecutedEarly){
            defender.setKiller(attacker);
            defenderProfile.updatePlayerHearts(defender);
//            Utils.msgPlayer(attacker,"Executed " + defender.getName());
            return true;
        } else {
            //If the player was not executed immediatly, bleed can be applied
        //        Apply bleed
        AttackProcessor.applyBleed(attacker, defender, incomingHit);
        }


        playerDamaged(defender, incomingHit, isSelfDamage, attacker);
        HealthComponent defHP = defenderProfile.getHealthComponent();
        double newMappedHealth = defHP.getMappedHealth();
        double newMappedWard = defHP.getMappedWard();
        //The damage, its triggers and consequences must be processed and then the player can actually die

        //Late hit triggers
        if (!isDoT){
            notifyHitTrigger(TriggerTimes.LATE, attacker, defender, incomingHit, isCriticalHit);
        }


        debugCombat(attacker, defender, incomingHit, isCriticalHit, isSelfDamage);

        //Combat healing
        if (!isDoT){
            boolean isBleeding = PlayerBuffManager.hasActiveBuff(Buffs.BLEED, attacker);
            combatHeal(attacker, isBleeding);
        }


        if (!isSelfDamage){
            //Adds attacker to combat
            CombatLogger.addToPvPCombat(attacker, defender);
        }

        boolean died = newMappedHealth == 0;
        //Early death trigger
        if (died){
            notifyProfile(attacker,defender, TriggerTypes.ON_DEATH, TriggerTimes.EARLY, incomingHit);
        }
        //Lets get the updated health in case it's been changed
        double updatedHealth = defHP.getMappedHealth();
        defenderProfile.setPlayerHearts(defender, updatedHealth, newMappedWard);
        //Late death trigger


        return updatedHealth==0; //Updated died, basically
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
