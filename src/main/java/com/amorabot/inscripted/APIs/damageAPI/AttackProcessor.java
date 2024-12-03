package com.amorabot.inscripted.APIs.damageAPI;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.components.Attack;
import com.amorabot.inscripted.components.DamageComponent;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.components.Items.relic.enums.Effects;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.components.buffs.Buffs;
import com.amorabot.inscripted.components.buffs.categories.damage.DamageBuff;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerBuffManager;
import com.amorabot.inscripted.skills.HitTypes;
import com.amorabot.inscripted.skills.PlayerAbilities;
import com.amorabot.inscripted.utils.CraftingUtils;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import java.util.Map;

import static com.amorabot.inscripted.utils.Utils.applyPercentageToArray;

public class AttackProcessor {

    public static int[] processAttack(Profile attackerProfile, Profile defenderProfile, int[] incomingHit, boolean isCrit, PlayerAbilities ability){

        incomingHit = ability.scaleDamage(incomingHit);
        /*
        Getting the base damage and scaling it according to the ability used
        Then this resulting damage can be applied to defences and be further altered
            If a ability nullifies, lets say, abyssal DMG, 0 will then be scaled by crits or ability tags such as MELEE, PROJ. ...
        */

        incomingHit = applyAbilityTags(attackerProfile, incomingHit, ability);

        if (isCrit){
            int critDmg = attackerProfile.getDamageComponent().getHitData().getCritDamage();
            incomingHit = applyPercentageToArray(incomingHit, 50 + critDmg);
        }
        //Incoming hit processing...
        return DefenceCalculator.applyDefences(incomingHit, attackerProfile, defenderProfile);
    }
    public static boolean attackResult(Attack attackerDamage, DefenceComponent defenderDefence){
        float dodgeChance = DefenceCalculator.getDefenderDodgeChance(defenderDefence);
        return DefenceCalculator.dodgeResult(attackerDamage, dodgeChance);
    }
    public static void dodgeAttack(int[] dmgArray, int mitigation){
        //Mitigate mitigation % amount of dmg
        float resultingDamagePercent = (100-Math.min(mitigation, 100))/100F;
        for (int i = 0; i < dmgArray.length; i++){
            dmgArray[i] = (int) (dmgArray[i] * resultingDamagePercent);
        }
    }
    public static boolean isCriticalHit(Attack attackerHitData){
        int critChance = attackerHitData.getCritChance();
        double critRoll = Math.random();
        return (critRoll*100) <= critChance;
    }

    public static void bleedAttempt(Player attacker, Player defender, int[] incomingHit){
        if (incomingHit[0]<=0){return;}
        //Min dmg threshold check
        int baseDamage = incomingHit[0]/10;
        if (incomingHit[0] < JSONProfileManager.getProfile(defender.getUniqueId()).getHealthComponent().getMaxHealth()*0.01){
            //If the incoming physical hit itself is less than 1% the targets health, dont even apply bleed
            return;
        }

        Profile attackerProfile = JSONProfileManager.getProfile(attacker.getUniqueId());
        Attack hitData = attackerProfile.getDamageComponent().getHitData();
        int bleedChance = hitData.getBleedChance();
        double bleedRoll = Math.random();
        if ((bleedRoll*100) > bleedChance){return;}

        //Time to apply the debuff
        DamageBuff bleed = new DamageBuff(Buffs.BLEED);
        baseDamage = (int) Utils.applyPercentageTo(baseDamage, attackerProfile.getDamageComponent().getBleedDamage());
        int[] dot = bleed.convertBaseHit(baseDamage);
        bleed.createDamageTask(dot, defender, false, attacker);
        if (attackerProfile.getEffects().contains(Effects.SADISM)){
            Effects.SADISM.check(attacker, defender, incomingHit);
        }

        PlayerBuffManager.addBuffToPlayer(bleed, defender);
        Audience audience = Audience.audience(attacker, defender);
        SoundAPI.playGenericSoundAtLocation(audience, defender.getLocation(),"block.pumpkin.carve", 2f, 0.5f);
    }

    public static int[] rollDamages(Map<DamageTypes, int[]> rawDamages){
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
            hitDamage[type.ordinal()] = rollDamageType(rawDamages, type);
        }
        return hitDamage;
    }
    private static int rollDamageType(Map<DamageTypes, int[]> rawDamages, DamageTypes type){
        int[] dmgRange = rawDamages.getOrDefault(type, new int[2]);
        return CraftingUtils.getRandomNumber(dmgRange[0], dmgRange[1]);
    }
    private static int[] applyAbilityTags(Profile attackerProfile, int[] incomingHit, PlayerAbilities ability){
        HitTypes[] tags = ability.getTags();
        DamageComponent atkrDmg = attackerProfile.getDamageComponent();
        int[] finalIncomingHit = incomingHit.clone();
        for(HitTypes abilityTag : tags){
            if (abilityTag.equals(HitTypes.NONE)){return new int[5];}
            switch (abilityTag){
                case MELEE -> finalIncomingHit = applyPercentageToArray(finalIncomingHit, atkrDmg.getMeleeDamage());
            }
        }
        return finalIncomingHit;
    }
}
