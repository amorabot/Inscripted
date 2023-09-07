package com.amorabot.rpgelements.components.Items.DataStructures.Enums;

public enum DefenceTypes {
    HEALTH("&#fc0341", "&#ed2f5f"), //Magenta bonitao
    WARD("&#26edcc", "&#73bab5"),
    //&#26edcc - &#73bab5
    //"&#562bff", "&#6f5fed"
    FIRE("&4", "&c"),
    COLD("&b", "&#79b7c7"),
    LIGHTNING("&e", "&#d6d463"),
    ARMOR("&#e625cc", "&#d46cce"),
    DODGE("&a", "&#9dc797");

    final String statColor;
    final String textColor;

    DefenceTypes(String statColor, String textColor){
        this.statColor = statColor;
        this.textColor = textColor;
    }

    public String getStatColor() {
        return statColor;
    }
    public String getTextColor() {
        return textColor;
    }
}
