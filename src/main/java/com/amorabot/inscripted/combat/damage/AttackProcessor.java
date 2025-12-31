package com.amorabot.inscripted.combat.damage;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.combat.buffs.Buffs;
import com.amorabot.inscripted.combat.buffs.PlayerBuffManager;
import com.amorabot.inscripted.combat.buffs.categories.damage.DamageBuff;
import com.amorabot.inscripted.item.inscription.definition.EffectIDs;
import com.amorabot.inscripted.item.inscription.definition.TriggerTimes;
import com.amorabot.inscripted.item.inscription.definition.TriggerTypes;
import com.amorabot.inscripted.item.structure.Weapon.DamageTypes;
import com.amorabot.inscripted.math.MathUtils;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.player.profile.component.DefenceComponent;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import static com.amorabot.inscripted.utils.Utils.applyPercentageToArray;

public class AttackProcessor {

    public static int[] processAttack(PlayerDataContainer attackerData, AttackData attack, PlayerDataContainer defenderData, int[] incomingHit, boolean isCrit){

        //Ability arg is only needed when its a player attacking, and is only accessed in this case

        if (isCrit){
            int critDmg = attack.getCritDamage();
            incomingHit = applyPercentageToArray(incomingHit, 50 + critDmg);
        }
        //Incoming hit processing...
        return DefenceCalculator.applyDefences(incomingHit, attackerData, attack, defenderData);
    }




    public static boolean attackResult(AttackData attackerDamage, DefenceComponent defenderDefence){
        float dodgeChance = DefenceCalculator.getDefenderDodgeChance(defenderDefence);
        return DefenceCalculator.dodgeResult(attackerDamage, dodgeChance);
    }
    public static void applyDodgeMitigation(int[] dmgArray, int mitigation){
        //Mitigate mitigation % amount of dmg
        float resultingDamagePercent = (100-Math.min(mitigation, 100))/100F;
        for (int i = 0; i < dmgArray.length; i++){
            dmgArray[i] = (int) (dmgArray[i] * resultingDamagePercent);
        }
    }
    public static boolean isCriticalHit(AttackData attackerHitData){
        int critChance = attackerHitData.getCritChance();
        double critRoll = Math.random();
        return (critRoll*100) <= critChance;
    }

    public static void bleedAttemptOnPlayer(Player attacker, Player defender, AttackData hitData, int[] incomingHit){
        if (incomingHit[0]<=0){return;}
        //Min dmg threshold check
        int baseDamage = (int) (incomingHit[0] * 0.15);
        final double minDamageThreshold = PlayerDataContainer.getProfile(defender.getUniqueId()).getHealthComponent().getMaxHealth()*0.01;
        if (incomingHit[0] < minDamageThreshold){
            //If the incoming physical hit itself is less than 1% the targets health, dont even apply bleed
            return;
        }
        PlayerDataContainer attackerData = PlayerDataContainer.getDataContainerFor(attacker.getUniqueId());

        int bleedChance = hitData.getBleedChance();
        double bleedRoll = Math.random();
        if ((bleedRoll*100) > bleedChance){return;}

        //Time to apply the debuff
        DamageBuff bleed = new DamageBuff(Buffs.BLEED);
        baseDamage = (int) Utils.applyPercentageTo(baseDamage, hitData.getBleedDamage());
        int[] dot = bleed.convertBaseHit(baseDamage);
        bleed.createDamageTask(dot, defender, false, attacker);
        if (attackerData.hasEffect(EffectIDs.SADISM)){
            EffectIDs.SADISM.check(attacker, defender, incomingHit);
        }

        PlayerBuffManager.addBuffToPlayer(bleed, defender.getUniqueId());
        //Notify bleed trigger
        attackerData.onNotify(TriggerTimes.LATE, TriggerTypes.ON_BLEED,defender,incomingHit);
        Audience audience = Audience.audience(attacker, defender);
        SoundAPI.playGenericSoundAtLocation(audience, defender.getLocation(),"block.pumpkin.carve", 2f, 0.5f);
    }

    public static int[] rollDamages(AttackData attackData){
        /*
        //Follows the same order as the enum
        0: Physical
        1: Fire
        2: Lightning
        3: Cold
        4: Abyssal
        */
        int[] hitDamage = new int[5];
        for (DamageTypes type : DamageTypes.values()){
            int[] damage = attackData.getDamage(type);
            hitDamage[type.ordinal()] = MathUtils.getRandomNumber(damage[0], damage[1]);
        }
        return hitDamage;
    }
}
