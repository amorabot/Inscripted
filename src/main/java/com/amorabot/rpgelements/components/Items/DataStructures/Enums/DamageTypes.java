package com.amorabot.rpgelements.components.Items.DataStructures.Enums;

public enum DamageTypes {
    PHYSICAL("&c"),
    FIRE("&4"),
    COLD("&b"),
    LIGHTNING("&e"),
    ABYSSAL("&#7734AA");

    final String color;
    DamageTypes(String elementColor){
        this.color = elementColor;
    }

    public String getColor() {
        return color;
    }
}
