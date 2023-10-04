package com.amorabot.inscripted.components.Items.Interfaces;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.RangeTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.TargetStats;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;

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
