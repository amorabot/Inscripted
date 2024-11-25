package com.amorabot.inscripted.components.Items.DataStructures.Enums;

public enum DefenceTypes {
    HEALTH("❤"),
    WARD("✤"),
    FIRE("\uD83D\uDD25"),
    LIGHTNING("⚡"),
    COLD("✽"),
    ABYSSAL("\uD83C\uDF19"),
    ARMOR("\uD83D\uDEE1"),
    DODGE("✦");

    private final String specialChar;

    DefenceTypes(String specialCharacter){
        this.specialChar = specialCharacter;
    }
    public String getSpecialChar() {
        return specialChar;
    }
}
