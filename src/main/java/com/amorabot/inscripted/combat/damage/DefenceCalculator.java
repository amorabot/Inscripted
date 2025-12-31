package com.amorabot.inscripted.combat.damage;

import com.amorabot.inscripted.item.inscription.definition.KeystoneIDs;
import com.amorabot.inscripted.item.structure.Weapon.DamageTypes;
import com.amorabot.inscripted.math.MathUtils;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.Profile;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.player.profile.component.DefenceComponent;

public class DefenceCalculator {

    public static final int DODGE_MITIGATION = 60;

    private static float getDefenderPhysicalMitigation(float defenderArmor){
        float boundArmor = Math.max(0, defenderArmor);
        return 70*(1 - (110 / ( 100 + boundArmor ))); // DamageReduction = 1 - physDmgMulti;
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

    public static int[] applyDefences(int[] incomingHit, PlayerDataContainer attackerData, AttackData attackerDamage, PlayerDataContainer defenderData){
        Profile defenderProfile = defenderData.getProfile();

        DefenceComponent defenderDefences = defenderProfile.getDefenceComponent();
        if (incomingHit[0] > 0 && defenderDefences.getArmor()>0){ //Physical mitigation
            incomingHit[0] = getFinalPhysicalDamage(incomingHit[0], attackerDamage, defenderDefences.getArmor());
        }
        if (incomingHit[4] > 0 && defenderData.hasKeystone(KeystoneIDs.FORBIDDEN_PACT)){
            incomingHit[4] = 0;
        } else {
            applyElementalDamageCalculations(incomingHit, attackerDamage, DamageTypes.ABYSSAL, defenderDefences.getAbyssalResistance());
        }
        applyElementalDamageCalculations(incomingHit, attackerDamage, DamageTypes.FIRE, defenderDefences.getFireResistance());
        applyElementalDamageCalculations(incomingHit, attackerDamage, DamageTypes.LIGHTNING, defenderDefences.getLightningResistance());
        applyElementalDamageCalculations(incomingHit, attackerDamage, DamageTypes.COLD, defenderDefences.getColdResistance());

        return incomingHit;
    }


    private static int getFinalPhysicalDamage(int rawPhysicalDamage, AttackData attackerDamage, float defenderArmor){
        float damageReduction = getDefenderPhysicalMitigation(defenderArmor);
        float resultingReduction = shredArmorMitigation(damageReduction, attackerDamage.getShred());

        float resultingDamage = rawPhysicalDamage * ( 1 - (resultingReduction/100F) );
        return (int) resultingDamage;
    }
    private static void applyElementalDamageCalculations(int[] incomingHit, AttackData attackerDamage, DamageTypes dmgType, int elementalRes){
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

    public static boolean dodgeResult(AttackData attackerDamage, float dodgeChance){
        float defenderDodgeChance = getFinalDefenderDodgeChance(attackerDamage, dodgeChance);
        int dodgeRoll = MathUtils.getRandomNumber(0, 100);
        //If the roll is lower than the defender's dodge chance, true (dodged the hit)
        return dodgeRoll < defenderDodgeChance;
    }
    public static float getFinalDefenderDodgeChance(AttackData attackerDamage, float dodgeChance) {
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

    private static float getAttackerPrecision(AttackData attackerDamage){
        return attackerDamage.getAccuracy() / 10F; //Precision is "dodge pen", its as simples as 10 acc -> 1 precision
    }
    //TODO: Lucky roll results
}
