package com.amorabot.inscripted.components.Items.modifiers.data;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.PlayerStats;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.RangeTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;

public record StatDefinition(ValueTypes valueType, RangeTypes rangeType, PlayerStats stat) {

    public String getDisplayName(Meta metaData, boolean isPositive) {
        StringBuilder baseString = new StringBuilder();

        baseString.append(ValueTypes.getSign(valueType, isPositive));

        baseString.append(rangeType.getValuesPlaceholder());
        baseString.append(valueType.getIconChar()).append(" ");

        String signIndicator = valueType.getSignIndicatorSuffix(isPositive);
        baseString.append(signIndicator);
        if (!signIndicator.isEmpty()){baseString.append(" ");}

        baseString.append(stat.getAlias());

        if (metaData != null) {
            baseString.append(" per ").append(metaData.rate()).append(" ").append(metaData.convertedStat().getAlias());
            return baseString.toString();
        }

        return baseString.toString();
    }
}
