package com.amorabot.inscripted.APIs;

import com.amorabot.inscripted.components.DamageComponent;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.utils.CraftingUtils;

import java.util.Map;

public class DamageAPI {
    public static int[] processAttack(HealthComponent defenderHealth, DefenceComponent defenderDefence, DamageComponent attackerDamage){
        int[] incomingHit = rollDamages(attackerDamage.getHitData().getDamages());
        //Incoming hit processing
        defenderHealth.damage(applyDefences(incomingHit, attackerDamage, defenderDefence));
        return incomingHit;
    }
    public static boolean attackResult(){
        return true;
    }
    private static int[] rollDamages(Map<DamageTypes, int[]> rawDamages){
        /*
        0: Physical
        1: Fire
        2: Lightning
        3: Cold
        4: Abyssal
         */
        int[] hitDamage = new int[5];
        hitDamage[0] = rollDamageType(rawDamages, DamageTypes.PHYSICAL);
        hitDamage[1] = rollDamageType(rawDamages, DamageTypes.FIRE);
        hitDamage[2] = rollDamageType(rawDamages, DamageTypes.LIGHTNING);
        hitDamage[3] = rollDamageType(rawDamages, DamageTypes.COLD);
        hitDamage[4] = rollDamageType(rawDamages, DamageTypes.ABYSSAL);

        return hitDamage;
    }

    //TODO: Possibly redundant method -> find other instances and merge
    private static int rollDamageType(Map<DamageTypes, int[]> rawDamages, DamageTypes type){
        if (rawDamages.containsKey(type)){
            int[] dmg = rawDamages.get(type);
            return CraftingUtils.getRandomNumber(dmg[0], dmg[1]);
        }
        return 0;
    }

    public static int[] applyDefences(int[] incomingHit, DamageComponent attackerDamage, DefenceComponent defenderDefences){
        if (incomingHit[0] > 0 && defenderDefences.getFinalArmor()>0){ //Physical mitigation
            incomingHit[0] = mitigatePhysical(incomingHit[0], attackerDamage, defenderDefences.getFinalArmor());
        }
        applyElementalResistance(incomingHit, attackerDamage, 1, defenderDefences.getFireResistance());
        applyElementalResistance(incomingHit, attackerDamage, 2, defenderDefences.getLightningResistance());
        applyElementalResistance(incomingHit, attackerDamage, 3, defenderDefences.getColdResistance());
        applyElementalResistance(incomingHit, attackerDamage, 4, defenderDefences.getAbyssalResistance());

        return incomingHit;
    }
    private static int mitigatePhysical(int rawPhysicalDamage, DamageComponent attackerDamage, float defenderArmor){
        //Armour will never prevent more damage than its value divided by X (e.g. X = 5 -> 1000 armour will never prevent more than 200 damage)
        //Bigger hits -> less effective armor
        //https://www.poewiki.net/wiki/Armour/math

        // Alternative -> armorMult = 100 / (100 + armor)
        int armorMitigationFactor = 10;
        float armorPenetration = (attackerDamage.getHitData().getShred()/100F); // 0 - 1 Shred value
        float damageReduction = (defenderArmor / ( defenderArmor + armorMitigationFactor*rawPhysicalDamage )) - armorPenetration;
        float resultingDamage = rawPhysicalDamage - (rawPhysicalDamage * damageReduction);

        return (int) resultingDamage;
    }
    private static void applyElementalResistance(int[] incomingHit, DamageComponent attackerDamage, int elementIndex, int elementalRes){
        //If the incoming damage is 0, or the there is no resulting resistance, there's no need to calculate changes
        float elementalPenetration = attackerDamage.getHitData().getMaelstrom()/100F;
        if (incomingHit[elementIndex] > 0 && (elementalRes-elementalPenetration)!=0){
            incomingHit[elementIndex] = mitigateElemental(incomingHit[elementIndex], attackerDamage,  elementalRes);
        }
    }
    private static int mitigateElemental(int rawEleDamage, DamageComponent attackerDamage, float defenderEleRes){
        //In case of negative resistances, elemental damage gets amplified
        //defenderEleRes is capped below 100, but can be negative
        float newDamage = ( 1 - ((defenderEleRes-attackerDamage.getHitData().getMaelstrom())/100F) ) * rawEleDamage;
        return (int) newDamage;
    }

}
