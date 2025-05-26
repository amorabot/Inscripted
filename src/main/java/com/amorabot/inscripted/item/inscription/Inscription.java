package com.amorabot.inscripted.item.inscription;

import com.amorabot.inscripted.item.generation.ValuesTableSizeExtractor;
import com.amorabot.inscripted.item.inscription.definition.InscriptionDefinition;
import com.amorabot.inscripted.item.inscription.definition.InscriptionIDs;
import com.amorabot.inscripted.item.inscription.language.AffixType;
import com.amorabot.inscripted.item.inscription.language.RollType;
import com.amorabot.inscripted.item.inscription.table.InscriptionTable;
import com.amorabot.inscripted.utils.Utils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@Setter
@EqualsAndHashCode
public class Inscription implements Serializable {

    private final InscriptionIDs inscription;
    private int tier;
    private double basePercentile; //0-1
    private boolean modifiable = true;


    public Inscription(InscriptionIDs inscription, int tier, double basePercentile) {
        this.inscription = inscription;
        this.tier = Math.min(tier, inscription.getTiers());
        this.basePercentile = basePercentile;
    }

    public String getDisplayName(String valuesHex){
        String template = getTemplateDisplayName();
        Integer[] templateOrdering = getInscription().getDefinitionData().accept(new ValuesTableSizeExtractor());
        int[] mappedValues = getMappedFinalValues();

        return substituteTemplates(mappedValues,template,templateOrdering, valuesHex);
    }
    private String getTemplateDisplayName(){
        return getInscription().getDefinitionData().getDisplayName();
    }
    private String substituteTemplates(int[] mappedValues, String templateString, Integer[] templateOrdering, String valueColorHex){
        String replacedTemplate = templateString;
        Utils.error(Arrays.toString(mappedValues));
        RollType[] templateRolls = ValuesTableSizeExtractor.mapSizingsToRollTypes(templateOrdering);
        int mappedOffset = 0;
        for (RollType roll : templateRolls) {
            String replacement = roll.getValuesTemplate();
            switch (roll){
                case CONSTANT -> {
                    replacement = substituteTemplate(mappedValues[mappedOffset],replacement,"const", valueColorHex);
                    mappedOffset+=1;
                }
                case SINGLE_ROLL -> {
                    replacement = substituteTemplate(mappedValues[mappedOffset],replacement,"value", valueColorHex);
                    mappedOffset+=1;
                }
                case DOUBLE_ROLL -> {
                    replacement = substituteTemplate(mappedValues[mappedOffset],replacement,"v1", valueColorHex);
                    replacement = substituteTemplate(mappedValues[mappedOffset+1],replacement,"v2", valueColorHex);
                    mappedOffset+=2;
                }
            }
            replacedTemplate = replacedTemplate.replaceFirst(roll.getValuesTemplate(), replacement);
        }
        return replacedTemplate;
    }
    private String substituteTemplate(int value, String templateString, String regex, String valueColor){
        String substitute = "<"+valueColor+">" + Math.abs(value) + "</"+valueColor+">";
        return templateString.replaceFirst(regex,substitute);
    }


    public int[] getMappedFinalValues(){
        int[] tableValues = InscriptionTable.queryValuesFor(this).clone();
        return mapFinalValues(tableValues);
    }
    private int[] mapFinalValues(int[] tableValues){
        InscriptionDefinition definition = getInscription().getDefinitionData();
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
            Every time we hit a 1, it means there needs to be dedicated slot on the array for that constant
            The array's size should only grow if i is higher than 1. Which means the additional size will vary
                (2->1 extra, 4->2 extra, 1->0 extra)
            */
            variableSize+= Math.floorDiv(i,2);
        }
        return baseSize+variableSize;
    }

    public boolean isSpecial(){
        return (inscription.isEffect() || inscription.isKeystone());
    }
    public boolean isImplicit(){
        return (getInscription().getDefinitionData().getAffix().equals(AffixType.IMPLICIT));
    }
}
