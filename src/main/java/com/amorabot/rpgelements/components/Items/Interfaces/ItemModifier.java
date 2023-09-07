package com.amorabot.rpgelements.components.Items.Interfaces;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.Affix;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.RangeTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.TargetStats;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ValueTypes;

import java.util.List;

public interface ItemModifier {
    Affix getAffixType();
    String getDisplayName();
    int getNumberOfTiers();
    TargetStats getTargetStat();
    ValueTypes getValueType();
    RangeTypes getRangeType();
    int getModifierWeight();

    //Modtags
//    List<? extends Enum<?>> getPrefixes();
//    List<? extends Enum<?>> getSuffixes();
//    List<? extends Enum<?>> getUniques();
}
