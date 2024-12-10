package com.amorabot.inscripted.APIs.damageAPI;

import com.amorabot.inscripted.components.Attack;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.EntityProfile;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.components.Items.relic.enums.Keystones;
import com.amorabot.inscripted.components.Mobs.MobStats;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.utils.CraftingUtils;

public class DefenceCalculator {

    private static float getDefenderPhysicalMitigation(float defenderArmor){
        float boundArmor = Math.max(0, defenderArmor);
        return 80*(1 - (110 / ( 100 + boundArmor ))); // DamageReduction = 1 - physDmgMulti;
        /* Armor lower bound should be 0
        0 -> -8.5 phys. mitigaiton
        10 -> 0 PM
        100 -> 38.2 PM
        ...
        800 -> 74 PM (Softcap, but can be done with higher scaling and a upper bound)
        */
    }
    private static float shredArmorMitigation(float damageReduction, int attackerShred){
        return (damageReduction - attackerShred); //Can be negative => More damage multiplier
    }
    public static float getDefenderDodgeChance(DefenceComponent defenderDefence){
        final float scalingFactor = 300F;
        int boundDodge = Math.max(0, defenderDefence.getDodge());
        return 100 * ( 1 - ( scalingFactor / (scalingFactor + boundDodge) ) );
        /*
        Lower bound: 0
        Starts getting diminishing returns around 600 (75%)
        0 -> 0%
        100 -> 33%
        300 -> 60%
        500 -> 71%
        600 -> 75%
        */
    }

    public static int[] applyDefences(int[] incomingHit, EntityProfile attackerProfile, EntityProfile defenderProfile){
        Attack attackerDamage = attackerProfile.getAttackData();
        DefenceComponent defenderDefences = defenderProfile.getDefenceComponent();
        if (incomingHit[0] > 0 && defenderDefences.getArmor()>0){ //Physical mitigation
            incomingHit[0] = resultingPhysical(incomingHit[0], attackerDamage, defenderDefences.getArmor());
        }
        if (incomingHit[4] > 0 && defenderProfile.hasKeystone(Keystones.FORBIDDEN_PACT)){
            incomingHit[4] = 0;
        } else {
            applyElementalDamageCalculations(incomingHit, attackerDamage, DamageTypes.ABYSSAL, defenderDefences.getAbyssalResistance());
        }
        applyElementalDamageCalculations(incomingHit, attackerDamage, DamageTypes.FIRE, defenderDefences.getFireResistance());
        applyElementalDamageCalculations(incomingHit, attackerDamage, DamageTypes.LIGHTNING, defenderDefences.getLightningResistance());
        applyElementalDamageCalculations(incomingHit, attackerDamage, DamageTypes.COLD, defenderDefences.getColdResistance());

        return incomingHit;
    }


    private static int resultingPhysical(int rawPhysicalDamage, Attack attackerDamage, float defenderArmor){
        float damageReduction = getDefenderPhysicalMitigation(defenderArmor);
        float resultingReduction = shredArmorMitigation(damageReduction, attackerDamage.getShred());

        float resultingDamage = rawPhysicalDamage * ( 1 - (resultingReduction/100F) );
        return (int) resultingDamage;
    }
    private static void applyElementalDamageCalculations(int[] incomingHit, Attack attackerDamage, DamageTypes dmgType, int elementalRes){
        //If the incoming damage is 0, or the there is no resulting resistance, there's no need to calculate changes
        float elementalPenetration = attackerDamage.getMaelstrom(); // + any specific elemental penetrations that apply
        int elementIndex = dmgType.ordinal();
        switch (dmgType){
            case FIRE -> elementalPenetration += attackerDamage.getFirePen();
            case LIGHTNING -> elementalPenetration += attackerDamage.getLightningPen();
            case COLD -> elementalPenetration += attackerDamage.getColdPen();
            case ABYSSAL -> {
                if (incomingHit[elementIndex] > 0 && (elementalRes)!=0){
                    incomingHit[elementIndex] = resultingElementalDamage(incomingHit[elementIndex], attackerDamage.getMaelstrom(), elementalRes);
                }
                return;
            }
        }
        if (incomingHit[elementIndex] > 0 && (elementalRes-(elementalPenetration/100F))!=0){
            incomingHit[elementIndex] = resultingElementalDamage(incomingHit[elementIndex], elementalPenetration,  elementalRes);
        }
    }
    private static int resultingElementalDamage(int rawEleDamage, float elementalPen, float defenderEleRes){
        //In case of negative resistances, elemental damage gets amplified
        //defenderEleRes is capped at 90, but can be negative

        float newDamage = ( 1 - ((defenderEleRes - elementalPen)/100F) ) * rawEleDamage;
        return (int) newDamage;
    }

    public static boolean dodgeResult(Attack attackerDamage, float dodgeChance){
        float defenderDodgeChance = AccuracyCalculator.getFinalDefenderDodgeChance(attackerDamage, dodgeChance);
        int dodgeRoll = CraftingUtils.getRandomNumber(0, 100);
        //If the roll is lower than the defender's dodge chance, true (dodged the hit)
        return dodgeRoll < defenderDodgeChance;
    }
    //TODO: Lucky roll results
}
