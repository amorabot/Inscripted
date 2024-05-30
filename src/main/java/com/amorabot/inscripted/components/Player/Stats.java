package com.amorabot.inscripted.components.Player;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.PlayerStats;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;
import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class Stats implements EntityComponent {
    /*
    https://www.poewiki.net/wiki/Stat
    https://www.poewiki.net/wiki/Damage_conversion#Conversion_order
    */

    @Setter
    private Map<PlayerStats, Map<ValueTypes, int[]>> playerStats = new HashMap<>();

    public Stats(){

    }

    @Override
    public void update(UUID profileID) {
        /*
        TODO: Set custom walk speeds, jump heights, etc...
        */
//        Profile profileData = JSONProfileManager.getProfile(profileID);

//        Player player = Bukkit.getPlayer(profileID);
//        assert player != null;
//        /*
//        The final walkSpeed stat reflects the % multiplier that is applied to the base player's movement speed
//        Ex:  100 (Base) MS = 0.2  player speed
//             169 (100 + 54) * 1.1 => 169% base MS,   1,69 multiplier overall to the base 0.2 MS => 0.3388
//         */
//        float mappedWS = ( walkSpeed ) * 0.002F;
//        // input ->  min -1 |  max 1
//        player.setWalkSpeed(mappedWS);
        //DEX can give 1% inc MS per 10
        //Default speed value for players: 0.2 (EMPIRIC FUCKING VALUE)  (https://minecraft.wiki/w/Attribute)
    }

    public void addSingleStat(PlayerStats targetStat, ValueTypes type, int[] values){
        if (!this.playerStats.containsKey(targetStat)){
            Map<ValueTypes, int[]> newStatMap = new HashMap<>();
            newStatMap.put(type, values);
            this.playerStats.put(targetStat, newStatMap);
            return;
        }
        Map<ValueTypes, int[]> valuesMap = this.playerStats.get(targetStat);
        if (!valuesMap.containsKey(type)){
            valuesMap.put(type, values);
            return;
        }
        int[] storedValues = valuesMap.get(type);
        int[] updatedValues = Utils.vectorSum(storedValues, values);
        valuesMap.put(type, updatedValues);
    }

    /*
        In every getter, things like unique effects, dmg conversion or nullifying any stats can be checked and executed
    */
    public int[][] getFinalDamages(){
        /*
        TODO: Handle dmg conversion inside this method
        ------------CONVERSION CHAIN------------
        PHYS -> LIGHTNING -> COLD -> FIRE -> ABYSSAL

            When fetching physical, check for any needed conversions on top of the calculated value
            When fetching cold, for instance, check all the elements in the chain first
        */
        int[] phys = getDmgValuesFor(PlayerStats.PHYSICAL_DAMAGE);
        int[] fire = getDmgValuesFor(PlayerStats.FIRE_DAMAGE);
        int[] light = getDmgValuesFor(PlayerStats.LIGHTNING_DAMAGE);
        int[] cold = getDmgValuesFor(PlayerStats.COLD_DAMAGE);
        int[] abyss = getDmgValuesFor(PlayerStats.ABYSSAL_DAMAGE);

        //Before returning do the conversion routines for all elements

        return new int[][]{phys, fire, light, cold, abyss};
    }

    private int[] getDmgValuesFor(PlayerStats stat){
        if (!playerStats.containsKey(stat)){return new int[2];}
        int[] baseDmg = playerStats.get(stat).getOrDefault(ValueTypes.FLAT, new int[2]);
        int incrDmg = playerStats.get(stat).getOrDefault(ValueTypes.INCREASED, new int[1])[0];
        int dmgMulti = playerStats.get(stat).getOrDefault(ValueTypes.MULTIPLIER, new int[1])[0];

        return calculateFinalDmgValue(baseDmg, incrDmg, dmgMulti);
    }
    public float getFinalFlatValueFor(PlayerStats stat){
        if (!playerStats.containsKey(stat)){return 0F;} //Invalid query
        int baseValue = playerStats.get(stat).getOrDefault(ValueTypes.FLAT, new int[1])[0];
        int increasedMod = playerStats.get(stat).getOrDefault(ValueTypes.INCREASED, new int[1])[0];
        int multiplierMod = playerStats.get(stat).getOrDefault(ValueTypes.MULTIPLIER, new int[1])[0];
        return calculateSingleFinalValue(baseValue, increasedMod, multiplierMod);
    }
    public int getPercentValueFor(PlayerStats stat){
        if (!playerStats.containsKey(stat)){return 0;}
        return playerStats.get(stat).getOrDefault(ValueTypes.PERCENT, new int[1])[0];
    }
    //  finalValue = (Value) * ( 1 + ((Inc - Dec)/100F) ) * ( 1 + (More/100F) ) * ( 1 - (Less/100F) )
    private int[] calculateFinalDmgValue(int[] baseValues, int increasedMod, int multiplierMod){
        int firstValue = (int) calculateSingleFinalValue(baseValues[0], increasedMod, multiplierMod);
        int secValue = (int) calculateSingleFinalValue(baseValues[0], increasedMod, multiplierMod);
        return new int[]{firstValue, secValue};
    }
    private float calculateSingleFinalValue(int baseValue, int inc, int mult){
        return baseValue * ( 1 + (inc/100F)) * ( 1 + (mult/100F));
    }

    public void debug(){
        Utils.log("-----COMPILED STATS-----");
        for (PlayerStats stat : playerStats.keySet()){
            Map<ValueTypes, int[]> valuesMap = playerStats.get(stat);
            Utils.log(stat.getAlias()+"-------");
            for (ValueTypes type : valuesMap.keySet()){
                Utils.log(type+": " + Arrays.toString(valuesMap.get(type))+"\n");
            }
        }
        Utils.log("--------------------------");
    }
}
