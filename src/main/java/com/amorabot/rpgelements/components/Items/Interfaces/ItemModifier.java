package com.amorabot.rpgelements.components.Items.Interfaces;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.Affix;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.RangeTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.TargetStats;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ValueTypes;

import java.util.List;

public interface ItemModifier {
    Affix getAffixType();
    int getNumberOfTiers();
    TargetStats getTargetStat();
    ValueTypes getValueType();
    RangeTypes getRangeType();
    String getDisplayName();
    int getModifierWeight();

    List<? extends Enum<?>> getPrefixes();
    List<? extends Enum<?>> getSuffixes();
    List<? extends Enum<?>> getUniques();
}
