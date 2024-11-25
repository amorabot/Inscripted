package com.amorabot.inscripted.components.Items.DataStructures.Enums;

import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

@Getter
public enum RangeTypes {
    SINGLE_VALUE(new int[1], "<v1>"){
        @Override
        public Component substitutePlaceholders(int[] mappedValues, String templateString, String valuesColor) {
            return MiniMessage.miniMessage().deserialize(templateString,Placeholder.parsed("v1", "<"+valuesColor+">" + Math.abs(mappedValues[0]) + "</"+valuesColor+">"));
        }
    },
    SINGLE_RANGE(new int[2],"<v1>") {
        @Override
        public Component substitutePlaceholders(int[] mappedValues, String templateString, String valuesColor) {
            return MiniMessage.miniMessage().deserialize(templateString,Placeholder.parsed("v1", "<"+valuesColor+">" + Math.abs(mappedValues[0]) + "</"+valuesColor+">"));
        }
    },
    DOUBLE_RANGE(new int[4], "<v1> - <v2>") {
        @Override
        public Component substitutePlaceholders(int[] mappedValues, String templateString, String valuesColor) {
            return MiniMessage.miniMessage().deserialize(templateString,
                    Placeholder.parsed("v1", "<"+valuesColor+">" + Math.abs(mappedValues[0]) + "</"+valuesColor+">"),
                    Placeholder.parsed("v2", "<"+valuesColor+">" + Math.abs(mappedValues[1]) + "</"+valuesColor+">"));
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
    abstract public Component substitutePlaceholders(int[] mappedValues, String templateString, String valuesColor);
}
