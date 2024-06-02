package com.amorabot.inscripted.components.Items.DataStructures.Enums;

import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum RangeTypes {
    SINGLE_VALUE(new int[1], "@v1@"){
        @Override
        public String substitutePlaceholders(int[] mappedValues, String templateString, String valuesColor) {
            return templateString.replace(getValuesPlaceholder(), valuesColor + Math.abs(mappedValues[0]) + "&7");
        }
    },
    SINGLE_RANGE(new int[2],"@v1@") {
        @Override
        public String substitutePlaceholders(int[] mappedValues, String templateString, String valuesColor) {
            return templateString.replace(getValuesPlaceholder(), valuesColor + Math.abs(mappedValues[0]) + "&7");
        }
    },
    DOUBLE_RANGE(new int[4], "@v1@ - @v2@") {
        @Override
        public String substitutePlaceholders(int[] mappedValues, String templateString, String valuesColor) {
            String replacement = getValuesPlaceholder()
                    .replace("@v1@", valuesColor + Math.abs(mappedValues[0]) + "&7")
                    .replace("@v2@", valuesColor + Math.abs(mappedValues[1]) + "&7");
            return templateString.replace(getValuesPlaceholder(), replacement);
        }
    };

    private final int[] valueContainerTemplate;
    private final String valuesPlaceholder;

    RangeTypes(int[] valueContainerTemplate, String valuesPlaceholderString){
        this.valueContainerTemplate = valueContainerTemplate;
        this.valuesPlaceholder = valuesPlaceholderString;
    }

    public static RangeTypes mapRangeTypeToken(String rangeToken){
        return switch (rangeToken) {
            case "x" -> SINGLE_VALUE;
            case "-" -> SINGLE_RANGE;
            case "-/-" -> DOUBLE_RANGE;

            default -> SINGLE_RANGE;
        };
    }
    public static int[] mapFinalValuesFor(RangeTypes range, int[] tableValues, double basePercentile){
        switch (range){
            case SINGLE_VALUE -> {
                return tableValues.clone();
            }
            case SINGLE_RANGE -> {
                int valA = tableValues[0];
                int valB = tableValues[1];
                int offset = -1;
                if (valA < 0){offset = 1;}
                return new int[]{Utils.getRoundedParametricValue(valA+offset, valB, basePercentile)};
            }
            case DOUBLE_RANGE -> {
                int valA = tableValues[0];
                int valB = tableValues[1];
                int valM = tableValues[2];
                int valN = tableValues[3];
                int offset = -1;
                if (valA < 0){offset = 1;}
                return new int[]
                        {
                                Utils.getRoundedParametricValue(valA+offset, valB, basePercentile),
                                Utils.getRoundedParametricValue(valM+offset, valN, basePercentile)
                        };
            }
        }
        return new int[2];
    }
    abstract public String substitutePlaceholders(int[] mappedValues, String templateString, String valuesColor);

}
