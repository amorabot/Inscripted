package com.amorabot.inscripted.APIs.damageAPI;

import com.amorabot.inscripted.components.Attack;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.utils.CraftingUtils;

import java.util.Map;

public class AttackProcessor {

    public static int[] processAttack(DefenceComponent defenderDefence, Attack attackerDamage){
        int[] incomingHit = rollDamages(attackerDamage.getDamages());
        //Incoming hit processing...
        return DefenceCalculator.applyDefences(incomingHit, attackerDamage, defenderDefence);
    }
    public static boolean attackResult(Attack attackerDamage, DefenceComponent defenderDefence){
        float dodgeChance = DefenceCalculator.getDefenderDodgeChance(defenderDefence);
        return DefenceCalculator.dodgeResult(attackerDamage, dodgeChance);
    }
    public static void dodgeAttack(int[] dmgArray, int mitigation){
        //Mitigate mitigation % amount of dmg
        float resultingDamagePercent = (100-mitigation)/100F;
        for (int i = 0; i < dmgArray.length; i++){
            dmgArray[i] = (int) (dmgArray[i] * resultingDamagePercent);
        }
    }

    private static int[] rollDamages(Map<DamageTypes, int[]> rawDamages){
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
}
