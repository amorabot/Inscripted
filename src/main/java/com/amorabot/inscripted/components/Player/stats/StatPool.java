package com.amorabot.inscripted.components.Player.stats;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.PlayerStats;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;
import com.amorabot.inscripted.components.Items.modifiers.Inscription;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;
import com.amorabot.inscripted.components.Items.modifiers.data.HybridInscriptionData;
import com.amorabot.inscripted.components.Items.modifiers.data.InscriptionData;
import com.amorabot.inscripted.components.Items.modifiers.data.ModifierData;
import com.amorabot.inscripted.components.Items.modifiers.data.StatDefinition;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StatPool {

    private Map<PlayerStats, Map<ValueTypes, int[]>> playerStats;
    @Getter
    private Map<PlayerStats, Double> multipliers = new HashMap<>();

    public StatPool(){
        this.playerStats = new HashMap<>();
    }
    public StatPool(Map<PlayerStats, Map<ValueTypes, int[]>> stats){
        //Asserting all stats have their multipliers defined
        for (PlayerStats stat : stats.keySet()){
            this.multipliers.put(stat,1D);
        }
        this.playerStats = stats;
    }


    public Map<PlayerStats, Map<ValueTypes, int[]>> getStats(){
        return this.playerStats;
    }


    public void add(Inscription inscription){
        InscriptionID inscID = inscription.getInscription();
        ModifierData inscData = inscID.getData();
        if (inscData.isStandard()){
            InscriptionData regularInsc = (InscriptionData) inscData;
            addValues(regularInsc.getDefinitionData(),inscription.getMappedFinalValue(), inscID.isPositive());
            return;
        }
        if (inscData.isHybrid()){
            HybridInscriptionData hybridInsc = (HybridInscriptionData) inscData;
            StatDefinition[] definitions = hybridInsc.getStatDefinitions();
            for (int d = 0; d < definitions.length; d++){addValues(definitions[d],inscription.getMappedFinalValue(d), inscID.isPositive());}
            return;
        }
    }
    public void addValues(StatDefinition statData, int[] values, boolean isPositive){
        PlayerStats targetStat = statData.stat();
        ValueTypes type = statData.valueType();
        if (!isPositive){
            values = Arrays.stream(values).map(value -> -value).toArray();
        }
        if (type.equals(ValueTypes.MULTIPLIER)){ //Apply exclusively to the multi. map
            addPercentageMultiplier(targetStat,values[0]);
            return;
        }
        addStat(targetStat, type, values);
    }
    public void addPercentageMultiplier(PlayerStats stat, int multiplier){
        //The multi. value is decided before the call (Percentage value [-15%...72%...])
        double newMultiplier = (100D + multiplier) / 100D;
        if (!multipliers.containsKey(stat)){
            multipliers.put(stat, newMultiplier);
            return;
        }
        //The multiplier for that stat is already defined
        double storedMulti = multipliers.get(stat);
        multipliers.put(stat, (storedMulti*newMultiplier));
    }

    public void addStat(PlayerStats stat, ValueTypes type, int[] values){
        if (type.equals(ValueTypes.MULTIPLIER)){
            Utils.error("Invalid Multiplier handling. (addStat@StatPool)");
            addPercentageMultiplier(stat,values[0]);
            return;}
        Map<PlayerStats, Map<ValueTypes, int[]>> statsMap = getStats();
        if (!statsMap.containsKey(stat)){ //If the stat is not present:
            Map<ValueTypes, int[]> newValueTypeMap = new HashMap<>();
            newValueTypeMap.put(type, values);
            statsMap.put(stat, newValueTypeMap);
            multipliers.put(stat,1D);
            return;
        }
        if (!statsMap.get(stat).containsKey(type)){
            statsMap.get(stat).put(type, values);
            return;
        }
        int[] storedValues = statsMap.get(stat).get(type).clone();
        int[] updatedValues = Utils.vectorSum(storedValues, values);

        statsMap.get(stat).put(type, updatedValues);

    }



    public void merge(StatPool externalStatPool){
        Utils.log("Merging stat pools!");
        Map<PlayerStats, Map<ValueTypes, int[]>> statsToCombine = externalStatPool.getStats();
        //Combine stats
        for (PlayerStats stat : statsToCombine.keySet()){
            Map<ValueTypes, int[]> valueTypeMap = statsToCombine.get(stat);
            for (ValueTypes type : valueTypeMap.keySet()){
                if (type.equals(ValueTypes.MULTIPLIER)){continue;}
                int[] valuesToAdd = valueTypeMap.get(type).clone();
                addStat(stat,type, valuesToAdd);
            }
        }
        //Combine multipliers
        Map<PlayerStats,Double> multipliersToCombine = externalStatPool.getMultipliers();
        for (PlayerStats multipliedStat : multipliersToCombine.keySet()){
            double externalMultiplier = multipliersToCombine.get(multipliedStat);
            if (!multipliers.containsKey(multipliedStat)){
                multipliers.put(multipliedStat,externalMultiplier);
                continue;
            }
            //The multi. map already has a multiplier defined for that stat, lets combine them
            double storedMulti = multipliers.get(multipliedStat);
            double newMulti = storedMulti * externalMultiplier;
            multipliers.put(multipliedStat,newMulti);
        }
    }


    public float getFinalValueFor(PlayerStats stat, boolean clear, ValueTypes... specialType){ //Implies non-damage value
        if (!playerStats.containsKey(stat)){return 0F;} //Invalid query
        /*
        For now, the only types of values that come in a pair inside the values array are flat damages [12,48]
        And since getting final DMG values is a more complex anyway, it should be handled in it's own method.
        */

        int baseValue = playerStats.get(stat).getOrDefault(ValueTypes.FLAT, new int[1])[0];
        int increasedMod = playerStats.get(stat).getOrDefault(ValueTypes.INCREASED, new int[1])[0];

        double multiplier = multipliers.get(stat);

        if (specialType != null && specialType.length==1){
            if (specialType[0].equals(ValueTypes.INCREASED)){
                final float finalIncrease = (float) (increasedMod * multiplier);
                if (clear){clearStat(stat);}
                return finalIncrease;
            }
            if (specialType[0].equals(ValueTypes.PERCENT)){
                int basePercentValue = playerStats.get(stat).getOrDefault(ValueTypes.PERCENT, new int[1])[0];
                final float finalPercentValue = (float) ((basePercentValue * ( 1 + (increasedMod/100F))) * multiplier);
                if (clear){clearStat(stat);}
                return finalPercentValue;
            }
        }
        final float finalValue = (float) ((baseValue * ( 1 + (increasedMod/100F))) * multiplier);
        //Once a standard final value is fetched, it can be cleared from the map
        if (clear){clearStat(stat);}

        return finalValue;
    }
    public int[] getDamageValuesFor(PlayerStats damageStat){
        if (!playerStats.containsKey(damageStat)){return new int[2];}
        int[] baseDmg = playerStats.get(damageStat).getOrDefault(ValueTypes.FLAT, new int[2]);
        int incrDmg = playerStats.get(damageStat).getOrDefault(ValueTypes.INCREASED, new int[1])[0];

        double multiplier = multipliers.get(damageStat);
        switch (damageStat){ //Lets preserve the Ele. DMG after reading the value
            case FIRE_DAMAGE,LIGHTNING_DAMAGE,COLD_DAMAGE -> incrDmg = incrDmg + (int) getFinalValueFor(PlayerStats.ELEMENTAL_DAMAGE, false, ValueTypes.INCREASED);
        }

        final int finalIncrDmg = incrDmg;
        clearStat(damageStat);//After that damage stat is calculated, its no longer needed
        return Arrays.stream(baseDmg).map( baseDmgValue -> (int) (  (baseDmgValue * ( 1 + (finalIncrDmg /100F))  ) * multiplier) ).toArray();
    }
    public int[][] getFinalDamages(){
        /*
        TODO: Handle dmg conversion inside this method
        ------------CONVERSION CHAIN------------
        PHYS -> LIGHTNING -> COLD -> FIRE -> ABYSSAL

            When fetching physical, check for any needed conversions on top of the calculated value
            When fetching cold, for instance, check all the elements in the chain first
        */
        int[] phys = getDamageValuesFor(PlayerStats.PHYSICAL_DAMAGE);
        int[] fire = getDamageValuesFor(PlayerStats.FIRE_DAMAGE);
        int[] light = getDamageValuesFor(PlayerStats.LIGHTNING_DAMAGE);
        int[] cold = getDamageValuesFor(PlayerStats.COLD_DAMAGE);
        int[] abyss = getDamageValuesFor(PlayerStats.ABYSSAL_DAMAGE);

        //Clear the remaining stats that affect damage
        clearStat(PlayerStats.ELEMENTAL_DAMAGE);
        //Before returning do the conversion routines for all elements
        //...

        return new int[][]{phys, fire, light, cold, abyss};
    }


    public void clearStat(PlayerStats stat){
        if (!playerStats.containsKey(stat)){
            return;
        }
        playerStats.remove(stat);
        multipliers.remove(stat);
    }


    public void debug(String debuggedMapName){
        Utils.log("-----"+debuggedMapName+"-----\nStat values:");
        for (PlayerStats stat : playerStats.keySet()){
            Map<ValueTypes, int[]> valuesMap = playerStats.get(stat);
            Utils.log(stat.getAlias()+"-------");
            for (ValueTypes type : valuesMap.keySet()){
                Utils.log(type+": " + Arrays.toString(valuesMap.get(type))+"\n");
            }
        }
        Utils.log("Multipliers:");
        for (PlayerStats stat : multipliers.keySet()){
            Utils.log(stat.getAlias()+": " + multipliers.get(stat));
        }
        Utils.log("--------------------------");
    }
}
