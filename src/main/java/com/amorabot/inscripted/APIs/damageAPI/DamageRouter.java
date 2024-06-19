package com.amorabot.inscripted.APIs.damageAPI;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Attack;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerRegenManager;
import com.amorabot.inscripted.skills.PlayerAbilities;
import com.amorabot.inscripted.tasks.CombatLogger;
import com.amorabot.inscripted.tasks.CombatHologramsDepleter;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.audience.Audience;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

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
        if (attacker.getUniqueId().equals(defender.getUniqueId())){
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

        if (!source.equals(DamageSource.SELF)){
            Utils.msgPlayer(defender ,defenderDebug.toString());
        }

        //DMG DEBUG FOR ATKr ---------------------------------------------
        HealthComponent defHP = defenderProfile.getHealthComponent();
        StringBuilder attackerDebug = new StringBuilder(damageHoloString);
        attackerDebug.append("&a&l -> &f").append(defender.getName()).append(" ");
        attackerDebug.append("&c&l[").append(( int )(defHP.getCurrentHealth())).append("]");
        if (defHP.getCurrentWard() > 0){attackerDebug.append("&3&l[").append(( int )(defHP.getCurrentWard())).append("] ");}

        if (isCriticalHit){attackerDebug.append(critMsg);}

        Utils.msgPlayer(attacker ,attackerDebug.toString());


        //Combat healing
        //TODO: encapsulate into a combathealing method
        int lifeHealed = attackerProfile.getDamageComponent().getLifeOnHit();
        attackerProfile.getHealthComponent().healHealth(lifeHealed);
        if (lifeHealed>0){
            CombatHologramsDepleter.getInstance().instantiateRegenHologram(attacker.getLocation(),
                    "&2"+lifeHealed);
        }

        //Combat logging
        //Adds defender to combat
        defender.setKiller(attacker);
        boolean died = playerDamaged(defender, incomingHit, source);
        if (!source.equals(DamageSource.SELF)){
            if (died){
                CombatLogger.addToPvPCombat(attacker, defender);
                return;
            }
            //Adds attacker to combat
            CombatLogger.addToPvPCombat(attacker, defender);
        }



//        if (!source.equals(DamageSource.SELF)){
//            //Adds defender to combat
//            defender.setKiller(attacker);
//            boolean died = playerDamaged(defender, incomingHit, source);
//            if (died){
//                CombatLogger.addToPvPCombat(attacker, defender);
//                return;
//            }
//            //Adds attacker to combat
//            CombatLogger.addToPvPCombat(attacker, defender);
//        } else {
//            defender.setKiller(attacker);
//            boolean died = playerDamaged(defender, incomingHit, source);
//            if (died){Utils.msgPlayer(defender, "Congratulations! You killed yourself!");}
//        }
    }

    //Handles the effects of a player being hit
    public static boolean playerDamaged(Player player, int[] incomingHit, DamageSource source){
        HealthComponent HPComponent = JSONProfileManager.getProfile(player.getUniqueId()).getHealthComponent();

        HPComponent.damage(incomingHit);

        UUID playerID = player.getUniqueId();
        boolean died = JSONProfileManager.getProfile(playerID).mapPlayerHearts(player);
        PlayerRegenManager.startWardRegenCooldownFor(player.getUniqueId());
        if (!source.equals(DamageSource.SELF)){
            CombatLogger.addToCombat(player);
        }

        return died;
    }

}
