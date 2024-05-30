package com.amorabot.inscripted.APIs.damageAPI;

import com.amorabot.inscripted.components.Attack;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.utils.CraftingUtils;

public class DefenceCalculator {

    private static float getDefenderPhysicalMitigation(float defenderArmor){
        return 100*(1 - (100 / ( 100 + defenderArmor ))); // DamageReduction = 1 - physDmgMulti;
    }
    private static float shredArmorMitigation(float damageReduction, int attackerShred){
        return (damageReduction - attackerShred); //Can be negative => More damage multiplier
    }
    public static float getDefenderDodgeChance(DefenceComponent defenderDefence){
        final float scalingFactor = 300F;
        return 100 * ( 1 - ( scalingFactor / (scalingFactor + defenderDefence.getDodge()) ) );
    }

    public static int[] applyDefences(int[] incomingHit, Attack attackerDamage, DefenceComponent defenderDefences){
        if (incomingHit[0] > 0 && defenderDefences.getArmor()>0){ //Physical mitigation
            incomingHit[0] = resultingPhysical(incomingHit[0], attackerDamage, defenderDefences.getArmor());
        }
        applyElementalDamageCalculations(incomingHit, attackerDamage, 1, defenderDefences.getFireResistance());
        applyElementalDamageCalculations(incomingHit, attackerDamage, 2, defenderDefences.getLightningResistance());
        applyElementalDamageCalculations(incomingHit, attackerDamage, 3, defenderDefences.getColdResistance());
        applyElementalDamageCalculations(incomingHit, attackerDamage, 4, defenderDefences.getAbyssalResistance());

        return incomingHit;
    }
    private static int resultingPhysical(int rawPhysicalDamage, Attack attackerDamage, float defenderArmor){
        float damageReduction = getDefenderPhysicalMitigation(defenderArmor);
        float resultingReduction = shredArmorMitigation(damageReduction, attackerDamage.getShred());

        float resultingDamage = rawPhysicalDamage * ( 1 - (resultingReduction/100F) );
        return (int) resultingDamage;
    }
    private static void applyElementalDamageCalculations(int[] incomingHit, Attack attackerDamage, int elementIndex, int elementalRes){
        //If the incoming damage is 0, or the there is no resulting resistance, there's no need to calculate changes
        //TODO: Factor in specific elemental penetrations
        float elementalPenetration = attackerDamage.getMaelstrom(); // + any specific elemental penetrations that apply
        if (incomingHit[elementIndex] > 0 && (elementalRes-(elementalPenetration/100F))!=0){
            incomingHit[elementIndex] = resultingElementalDamage(incomingHit[elementIndex], elementalPenetration,  elementalRes);
        }
    }
    private static int resultingElementalDamage(int rawEleDamage, float elementalPen, float defenderEleRes){
        //In case of negative resistances, elemental damage gets amplified
        //defenderEleRes is capped below 100, but can be negative

        float newDamage = ( 1 - ((defenderEleRes - elementalPen)/100F) ) * rawEleDamage;
        return (int) newDamage;
    }

    public static boolean dodgeResult(Attack attackerDamage, float dodgeChance){
        float defenderDodgeChance = AccuracyCalculator.getFinalDefenderDodgeChance(attackerDamage, dodgeChance);
        int dodgeRoll = CraftingUtils.getRandomNumber(0, 100);
        //If the roll is higher than the dodge chance, true (hit lands)
        return dodgeRoll > defenderDodgeChance;
    }
    //TODO: Lucky roll results
}
