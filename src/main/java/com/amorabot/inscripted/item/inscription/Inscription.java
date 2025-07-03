package com.amorabot.inscripted.item.inscription;

import com.amorabot.inscripted.item.generation.ValuesTableSizeExtractor;
import com.amorabot.inscripted.item.inscription.definition.InscriptionDefinition;
import com.amorabot.inscripted.item.inscription.language.RollType;
import com.amorabot.inscripted.utils.Utils;

import java.io.Serializable;
import java.util.Arrays;

public interface Inscription extends Serializable {
    <R> R accept(InscriptionVisitor<R> visitor);
    InscriptionDefinition getInscriptionDefinition();
    boolean getDebugState();
    boolean isModifiable();
    double getBasePercentile();


    default String getDisplayName(String valuesHex) {
        String rawDisplayName = getRawDisplayName();
        if ((this instanceof UniqueInscription) && (isKeystone() || isEffect())){
            return rawDisplayName;
        }
        Integer[] templateOrdering = getInscriptionDefinition().accept(new ValuesTableSizeExtractor());
        int[] mappedValues = getMappedFinalValues();
        return substituteTemplates(mappedValues,rawDisplayName,templateOrdering, valuesHex);
    }

    default String getRawDisplayName(){
        return getInscriptionDefinition().getDisplayName();
    }
    default String substituteTemplates(int[] mappedValues, String templateString, Integer[] templateOrdering, String valueColorHex){
        String replacedTemplate = templateString;
        if (getDebugState()){Utils.error(Arrays.toString(mappedValues));}
        RollType[] templateRolls = ValuesTableSizeExtractor.mapSizingsToRollTypes(templateOrdering);
        int mappedOffset = 0;
        for (RollType roll : templateRolls) {
            String replacement = roll.getValuesTemplate();
            switch (roll){
                case CONSTANT -> {
                    replacement = substituteTemplateValue(mappedValues[mappedOffset],replacement,"const", valueColorHex);
                    mappedOffset+=1;
                }
                case SINGLE_ROLL -> {
                    replacement = substituteTemplateValue(mappedValues[mappedOffset],replacement,"value", valueColorHex);
                    mappedOffset+=1;
                }
                case DOUBLE_ROLL -> {
                    replacement = substituteTemplateValue(mappedValues[mappedOffset],replacement,"v1", valueColorHex);
                    replacement = substituteTemplateValue(mappedValues[mappedOffset+1],replacement,"v2", valueColorHex);
                    mappedOffset+=2;
                }
            }
            replacedTemplate = replacedTemplate.replaceFirst(roll.getValuesTemplate(), replacement);
        }
        return replacedTemplate;
    }
    default String substituteTemplateValue(int value, String templateString, String regex, String valueColor){
        String substitute = "<"+valueColor+">" + Math.abs(value) + "</"+valueColor+">";
        return templateString.replaceFirst(regex,substitute);
    }
    default int[] getMappedFinalValues(){
        return accept(new InscriptionValuesMapper());
    }

    default boolean isMeta(){
        return getInscriptionDefinition() instanceof InscriptionDefinition.Meta;
    }
    default boolean isEffect(){
        return getInscriptionDefinition() instanceof InscriptionDefinition.Effect;
    }
    default boolean isKeystone(){
        return getInscriptionDefinition() instanceof InscriptionDefinition.Keystone;
    }
}
