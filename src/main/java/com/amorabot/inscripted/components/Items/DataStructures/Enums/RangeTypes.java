package com.amorabot.inscripted.components.Items.DataStructures.Enums;

import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;

@Getter
public enum RangeTypes {
    SINGLE_VALUE(new int[1], "@v1@"){
        @Override
        public String substitutePlaceholders(int[] mappedValues, String templateString, String valuesColor) {
            return templateString.replace(getValuesPlaceholder(), valuesColor + mappedValues[0] + "&7");
        }
    },
    SINGLE_RANGE(new int[2],"@v1@") {
        @Override
        public String substitutePlaceholders(int[] mappedValues, String templateString, String valuesColor) {
            return templateString.replace(getValuesPlaceholder(), valuesColor + mappedValues[0] + "&7");
        }
    },
    DOUBLE_RANGE(new int[4], "@v1@ - @v2@") {
        @Override
        public String substitutePlaceholders(int[] mappedValues, String templateString, String valuesColor) {
            String replacement = getValuesPlaceholder()
                    .replace("@v1@", valuesColor + mappedValues[0] + "&7")
                    .replace("@v2@", valuesColor + mappedValues[1] + "&7");
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
                return new int[]{Utils.getRoundedParametricValue(tableValues[0]-1, tableValues[1], basePercentile)};
            }
            case DOUBLE_RANGE -> {
                return new int[]
                        {
                                Utils.getRoundedParametricValue(tableValues[0]-1, tableValues[1], basePercentile),
                                Utils.getRoundedParametricValue(tableValues[2]-1, tableValues[3], basePercentile)
                        };
            }
        }
        return new int[2];
    }
    abstract public String substitutePlaceholders(int[] mappedValues, String templateString, String valuesColor);

}
