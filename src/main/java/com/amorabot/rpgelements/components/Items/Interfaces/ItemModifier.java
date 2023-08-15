package com.amorabot.rpgelements.components.Items.Interfaces;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.Affix;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.RangeTypes;

import java.util.List;

public interface ItemModifier {
    Affix getAffixType();
    int getNumberOfTiers();

    RangeTypes getRangeType();

    String getDisplayName();

    int getModifierWeight();

    List<? extends Enum<?>> getPrefixes();
    List<? extends Enum<?>> getSuffixes();
    List<? extends Enum<?>> getUniques();
}
