package com.amorabot.inscripted.APIs.damageAPI;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Attack;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerRegenManager;
import com.amorabot.inscripted.tasks.CombatLogger;
import com.amorabot.inscripted.tasks.CombatHologramsDepleter;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DamageRouter {
    private static final NamespacedKey mobKey = new NamespacedKey(Inscripted.getPlugin(), "INSCRIPTED_MOB");

    public static void playerAttack(Player player, LivingEntity defender){ //Rename to entityDamage??
        defender.damage(0.01);
        boolean playerDefender = defender instanceof Player;
        if (playerDefender){
            //PvP Logic
            playerVsPlayerDamage(player, (Player) defender);
            if (defender.isDead()){
                Utils.log("he ded :(");
            }
        } else {
            //PvE Logic
            //...
        }
    }

    public static void playerVsPlayerDamage(Player attacker, Player defender){
        boolean hitLanded;

        Profile attackerProfile = JSONProfileManager.getProfile(attacker.getUniqueId());
        Profile defenderProfile = JSONProfileManager.getProfile(defender.getUniqueId());

        Attack atk = attackerProfile.getDamageComponent().getHitData();
        DefenceComponent def = defenderProfile.getDefenceComponent();

        hitLanded = AttackProcessor.attackResult(atk, def);
        int[] incomingHit = AttackProcessor.processAttack(def, atk);
        //Artificial Scaling to compensate for atk speed diff (0s -> 0.5s-ish delay)
        for (int i = 0; i < incomingHit.length; i++){
            incomingHit[i] = (int) (incomingHit[i]*1.5);
        }

        if (!hitLanded){
            CombatEffects.playDodgeEffectsAt(defender, attacker);
            AttackProcessor.dodgeAttack(incomingHit, 70);
        }
        String damageHoloString = Attack.getDamageString(incomingHit);

        CombatHologramsDepleter.getInstance().instantiateDamageHologramAt(defender.getLocation(), incomingHit);

        //DMG DEBUG FOR DEFdr ---------------------------------------------
        Utils.msgPlayer(defender ,"&c&l<- " + damageHoloString  + " &ffrom " + attacker.getName());
        //Adds defender to combat
        defender.setKiller(attacker);
        boolean died = playerDamaged(defender, incomingHit);
        if (died){
            CombatLogger.addToPvPCombat(attacker, defender);
            return;
        }

        //DMG DEBUG FOR ATKr ---------------------------------------------
        Utils.msgPlayer(attacker, damageHoloString + "&a&l -> &f" + defender.getName() +
                " &c&l[" + ( int )(defenderProfile.getHealthComponent().getCurrentHealth()) + "]");
        //Adds attacker to combat
        CombatLogger.addToPvPCombat(attacker, defender);
    }

    //Handles the effects of a player being hit
    public static boolean playerDamaged(Player player, int[] incomingHit){
        HealthComponent HPComponent = JSONProfileManager.getProfile(player.getUniqueId()).getHealthComponent();

        HPComponent.damage(incomingHit);

        UUID playerID = player.getUniqueId();
        boolean died = JSONProfileManager.getProfile(playerID).mapPlayerHearts(player);
        PlayerRegenManager.startWardRegenCooldownFor(player.getUniqueId());
        CombatLogger.addToCombat(player);
        return died;
    }

}
