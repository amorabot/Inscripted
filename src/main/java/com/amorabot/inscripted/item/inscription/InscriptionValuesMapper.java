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
        if (proceduralInscription.getDebugState()){Utils.log(Arrays.toString(tableValues));}
        return mapInscriptionFinalValues(proceduralInscription, tableValues);
    }

    @Override
    public int[] visitUniqueInscription(UniqueInscription uniqueInscription) {
        InscriptionDefinition uniqueDef = uniqueInscription.getInscriptionDefinition();
        if (uniqueInscription.isEffect() || uniqueInscription.isKeystone()){return new int[0];}
        int[] uniqueValues = uniqueInscription.getSourceRelic().getParsedInscriptions().get(uniqueDef.hashCode()).values();
        if (uniqueInscription.getDebugState()){Utils.log(Arrays.toString(uniqueValues));}
        return mapInscriptionFinalValues(uniqueInscription,uniqueValues);
    }

    private int[] mapInscriptionFinalValues(Inscription inscription, int[] tableValues){
        InscriptionDefinition definition = inscription.getInscriptionDefinition();
        Integer[] storedValuesSizing = definition.accept(new ValuesTableSizeExtractor());
        if (storedValuesSizing.length==0){return new int[]{999};}

        int[] mappedValues = new int[getFinalValuesArraySize(storedValuesSizing)];
        int invertionFactor = 1;
        double basePercentile = inscription.getBasePercentile();
        boolean invertValues = InscriptionValuesMapper.shouldMappedValuesBeInverted(definition);
        if (invertValues){
            invertionFactor = -1;
            basePercentile = (1-basePercentile);
        }

        int mappedOffset = 0;
        int rawOffset = 0;

        for (int i = 0; i < storedValuesSizing.length; i++){
            final int currentSizing = storedValuesSizing[i];
            final int mappedValuesArrayIndex = mappedOffset;
            if (currentSizing == RollType.CONSTANT.getPreRollSize()){
                mappedValues[mappedValuesArrayIndex] = tableValues[i+rawOffset]*invertionFactor; // Store the i'th mapped value (1 to 1) on the corresponding mappedValues index
                mappedOffset++; //We just added a mapped value and the offset should reflect the size of the insertion ( 1 in this case)
                rawOffset++;
                continue;
            }
            // We are dealing with single and double rolls from now on
            if (currentSizing == RollType.SINGLE_ROLL.getPreRollSize()){
                // Get the offset values on the raw value table
                final int v1 = tableValues[rawOffset]*invertionFactor;
                final int v2 = tableValues[rawOffset+1]*invertionFactor;
                if (inscription.getDebugState()){Utils.log("v1: " + v1 + " " + "v2: " + v2 +
                        "  BP:" + inscription.getBasePercentile() + "Remapped Base Per.: " + basePercentile);}
                final int m1 = Utils.getRoundedParametricValue(v1, v2, basePercentile);
                mappedValues[mappedValuesArrayIndex] = m1;

                mappedOffset+= 1;
                rawOffset+= RollType.SINGLE_ROLL.getPreRollSize();
                continue;
            }

            if (currentSizing == RollType.DOUBLE_ROLL.getPreRollSize()){
                final int v1 = tableValues[i+rawOffset];
                final int v2 = tableValues[i+rawOffset+1];
                final int m1 = Utils.getRoundedParametricValue(v1, v2, basePercentile);
                final int v3 = tableValues[i+rawOffset+2];
                final int v4 = tableValues[i+rawOffset+3];
                final int m2 = Utils.getRoundedParametricValue(v3, v4, basePercentile);

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
            Every time we hit a 1, it means there needs to be dedicated armorSlot on the array for that constant
            The array's size should only grow if i is higher than 1. Which means the additional size will vary
                (2->1 extra, 4->2 extra, 1->0 extra)
            */
            variableSize+= Math.floorDiv(i,2);
        }
        return baseSize+variableSize;
    }
    public static boolean shouldMappedValuesBeInverted(InscriptionDefinition inscriptionDefinition){
        boolean regular = inscriptionDefinition instanceof InscriptionDefinition.Regular;
        boolean hybrid = inscriptionDefinition instanceof InscriptionDefinition.Hybrid;
        //Meta inscriptions are positive by design
        if (regular){
            return !((InscriptionDefinition.Regular) inscriptionDefinition).isPositive();
        } else if (hybrid){
            return !((InscriptionDefinition.Hybrid) inscriptionDefinition).isPositive();
        } else {
            return false;
        }
    }
}
