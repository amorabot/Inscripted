package com.amorabot.inscripted.APIs.damageAPI;

import com.amorabot.inscripted.components.Attack;

public class AccuracyCalculator {

    public static float getFinalDefenderDodgeChance(Attack attackerDamage, float dodgeChance) {
        /*

        Attack miss algorithm

        get base miss chance
        get attack cooldown -> map to the new value

        see if its a miss (never more than 10%, even if not charged at all)

        if its not, calculate the enemies dodge chance, capped at 70%

        calculate the attackers precision (capped at 30% for now)

        defenderDodgeChance = dodgeChance - precision,   capped at 0%

        roll for a hit
         */

        float precision = getAttackerPrecision(attackerDamage);
        //the final dodge value for the defender is deduced by this value, capping (down) at 0%
        // Will work similar to shred, Accuracy scales to a certain value and negates dodge
        float defenderDodgeChance = Math.min(dodgeChance, 70F); //Caps at 70

        return Math.max(defenderDodgeChance - precision, 0);
    }

    private static float getAttackerPrecision(Attack attackerDamage){
        return attackerDamage.getAccuracy() / 10; //Precision is "dodge pen", its as simples as 10 acc -> 1 precision
    }
}
