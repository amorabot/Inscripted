package com.amorabot.inscripted.item.inscription;

import com.amorabot.inscripted.item.generation.ValuesTableSizeExtractor;
import com.amorabot.inscripted.item.inscription.definition.InscriptionDefinition;
import com.amorabot.inscripted.item.inscription.language.RollType;
import com.amorabot.inscripted.item.inscription.table.InscriptionTable;
import com.amorabot.inscripted.utils.Utils;

import java.util.Arrays;

public class InscriptionValuesMapper implements InscriptionVisitor<int[]>{

    @Override
    public int[] visitProceduralInscription(ProceduralInscription proceduralInscription) {
        int[] tableValues = InscriptionTable.queryValuesFor(proceduralInscription);
        if (proceduralInscription.getDebugState()){
            Utils.log(Arrays.toString(tableValues));}
        return mapProceduralInscriptionFinalValues(proceduralInscription, tableValues);
    }

    @Override
    public int[] visitUniqueInscription(UniqueInscription uniqueInscription) {
        //Logica pra pegar o valor dos mods unique
        return new int[0];
    }

    private int[] mapProceduralInscriptionFinalValues(ProceduralInscription proceduralInscription, int[] tableValues){
        InscriptionDefinition definition = proceduralInscription.getInscription().getDefinitionData();
        Integer[] storedValuesSizing = definition.accept(new ValuesTableSizeExtractor());
        if (storedValuesSizing.length==0){return new int[]{999};}

        int[] mappedValues = new int[getFinalValuesArraySize(storedValuesSizing)];
        int mappedOffset = 0;
        int rawOffset = 0;

        for (int i = 0; i < storedValuesSizing.length; i++){
            final int currentSizing = storedValuesSizing[i];
            final int mappedValuesArrayIndex = mappedOffset;
            if (currentSizing == RollType.CONSTANT.getPreRollSize()){
                mappedValues[mappedValuesArrayIndex] = tableValues[i+rawOffset]; // Store the i'th mapped value (1 to 1) on the corresponding mappedValues index
                mappedOffset++; //We just added a mapped value and the offset should reflect the size of the insertion ( 1 in this case)
                rawOffset++;
                continue;
            }
            // We are dealing with single and double rolls from now on
            if (currentSizing == RollType.SINGLE_ROLL.getPreRollSize()){
                // Get the offset values on the raw value table
                final int v1 = tableValues[rawOffset];
                final int v2 = tableValues[rawOffset+1];
                if (proceduralInscription.getDebugState()){Utils.log("v1: " + v1 + " " + "v2: " + v2 + "  BP:" + proceduralInscription.getBasePercentile());}
                final int m1 = Utils.getRoundedParametricValue(v1, v2, proceduralInscription.getBasePercentile());
                mappedValues[mappedValuesArrayIndex] = m1;

                mappedOffset+= 1;
                rawOffset+= RollType.SINGLE_ROLL.getPreRollSize();
                continue;
            }

            if (currentSizing == RollType.DOUBLE_ROLL.getPreRollSize()){
                final int v1 = tableValues[i+rawOffset];
                final int v2 = tableValues[i+rawOffset+1];
                final int m1 = Utils.getRoundedParametricValue(v1, v2, proceduralInscription.getBasePercentile());
                final int v3 = tableValues[i+rawOffset+2];
                final int v4 = tableValues[i+rawOffset+3];
                final int m2 = Utils.getRoundedParametricValue(v3, v4, proceduralInscription.getBasePercentile());

                mappedValues[mappedValuesArrayIndex] = m1;
                mappedValues[mappedValuesArrayIndex+1] = m2;

                mappedOffset+=2;
                rawOffset+=RollType.DOUBLE_ROLL.getPreRollSize();
            }
        }
        return mappedValues;
    }
    private int getFinalValuesArraySize(Integer[] rawSizings){
        int baseSize = 0;
        int variableSize = 0;
        for (Integer i : rawSizings) {
            if (i==1){baseSize+=1;}
            /*
            Every time we hit a 1, it means there needs to be dedicated slot on the array for that constant
            The array's size should only grow if i is higher than 1. Which means the additional size will vary
                (2->1 extra, 4->2 extra, 1->0 extra)
            */
            variableSize+= Math.floorDiv(i,2);
        }
        return baseSize+variableSize;
    }
}
