package pluginstudies.pluginstudies.utils;

import org.bukkit.inventory.ItemStack;

public class StatCalculator {

    public static int applyStrengthToHealth(int health, int strength, boolean adding){
        health = health + (strength/2);

        return health;
    }
    public static int applyIntelligenceToWard(int ward, int intelligence, boolean adding){ // 5 int -> 1% increased ward
        int wardMultiplier = intelligence/5;

        ward = ward * (1 + wardMultiplier/100);
        return ward;
    }
    public static int calculateDPS(ItemStack heldItem){ //TODO calculo de dps (phys base + ele * (elemods))
//        int avgDPS = physAvg + ( (fireAvg * (1 + (firePercent+ elePercent)) + coldAvg * (1 + (coldPercent + elePercent)) + lighningAvg * (1 + lighningPercent + elePercent)) );
        return 1;
    }
}
