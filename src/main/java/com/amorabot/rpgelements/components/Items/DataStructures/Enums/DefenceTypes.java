package com.amorabot.rpgelements.components.Items.DataStructures.Enums;

public enum DefenceTypes {
    HEALTH("&#fc0341", "&#ed2f5f", "❤"),
    WARD("&#26edcc", "&#73bab5", "✤"),
    //&#26edcc - &#73bab5
    //"&#562bff", "&#6f5fed"
    FIRE("&4", "&c", "\uD83D\uDD25"),
    COLD("&b", "&#79b7c7", "✽"),
    LIGHTNING("&e", "&#d6d463", "⚡"),
//    ARMOR("&#e625cc", "&#d46cce", "\uD83D\uDEE1"),
    ARMOR("&#e625cc", "&#8170c4", "\uD83D\uDEE1"),
    DODGE("&a", "&#9dc797", "✦");

    private final String statColor;
    private final String textColor;
    private final String specialChar;

    DefenceTypes(String statColor, String textColor, String specialCharacter){
        this.statColor = statColor;
        this.textColor = textColor;
        this.specialChar = specialCharacter;
    }

    public String getStatColor() {
        return statColor;
    }
    public String getTextColor() {
        return textColor;
    }
    public String getSpecialChar() {
        return specialChar;
    }
}
