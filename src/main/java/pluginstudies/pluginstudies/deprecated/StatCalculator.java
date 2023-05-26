package pluginstudies.pluginstudies.deprecated;

import org.bukkit.inventory.ItemStack;
@Deprecated
public class StatCalculator {
    public static int updateMaxHealth(int baseHealth, int healthPercent){
        //get base health and apply the added percentage
        return (baseHealth * (1 + healthPercent)); //return current maxHealth
    }
    public static int updateMaxWard(int baseWard, int wardPercent){
        return baseWard * (1 + wardPercent); //return current maxWard
    }
    public static int updateHealthPercent(int healthPercent, int value, boolean adding){
        // Para stats que variam apenas de uma unica fonte
        // é interessante saber se desejamos adicionar ou
        // subtrair um valor com a variável adding.
        int mod;
        if (adding){
            mod = 1;
        } else {
            mod = -1;
        }
        healthPercent = healthPercent + value*mod;
        return healthPercent;
    }
    public static int updateWardPercent(int increasedWard, int intPercentward){ // São fornecidas as fontes de %ward para o somatório
        return increasedWard + intPercentward; //return current increased ward TODO: lembrar de adicionar increased ward(?)
    }

    public static int applyStrengthToHealth(int health, int strength, boolean adding){ // 2 str -> 1+ health
        int mod;
        if (adding){
            mod = 1;
        } else {
            mod = -1;
        }
        health = health + (strength/2)*mod; //Add the health corresponding to the strength gained

        return health;
    }
    public static int applyIntelligenceToWard(int playerIntelligence){ // 5 int -> 1% increased ward
        return (playerIntelligence / 5); //Update the new % contribution from current INT to wardPercent
    }
    public static int calculateDPS(ItemStack heldItem){ //TODO calculo de dps (phys base + ele * (elemods))
//        int avgDPS = physAvg + ( (fireAvg * (1 + (firePercent+ elePercent)) + coldAvg * (1 + (coldPercent + elePercent)) + lighningAvg * (1 + lighningPercent + elePercent)) );
        return 1;
    }
}
