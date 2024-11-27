package com.amorabot.inscripted.components.Items.DataStructures.Enums;

import lombok.Getter;

@Getter
public enum ValueTypes {
    FLAT("", "", ""),
    PERCENT("%", "", ""),
    INCREASED("%", "Incr.", "Redu."),
    MULTIPLIER("%", "More", "Less");

    private final String iconChar;
    private final String positiveIndicator;
    private final String negativeIndicator;

    ValueTypes(String iconChar, String positive, String negative){
        this.iconChar = iconChar;
        this.positiveIndicator = positive;
        this.negativeIndicator = negative;
    }

    public static ValueTypes mapValueTypeToken(String valueToken){
        return switch (valueToken) {
            case "++" -> FLAT;
            case "+%" -> PERCENT;
            case "%+" -> INCREASED;
            case "%*" -> MULTIPLIER;

            default -> FLAT;
        };
    }
    public static String getSign(ValueTypes type, boolean isPositive){
        if (type.equals(INCREASED) || type.equals(MULTIPLIER)){return "";}
        if (isPositive){ return "+";}
        return "-";
    }
    public String getSignIndicatorSuffix(boolean isPositive){
        if (isPositive){
            return getPositiveIndicator();
        }
        return getNegativeIndicator();
    }
}
