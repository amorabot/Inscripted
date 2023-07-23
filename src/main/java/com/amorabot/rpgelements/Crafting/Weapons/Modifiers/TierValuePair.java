package com.amorabot.rpgelements.Crafting.Weapons.Modifiers;

import java.io.Serializable;

public record TierValuePair(int tier, int[] value) implements Serializable {
    private static final long serialversionUID = 696969L;
}
