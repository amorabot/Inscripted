package com.amorabot.inscripted.components.Items.modifiers.data;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.PlayerStats;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.RangeTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;

public record StatDefinition(ValueTypes valueType, RangeTypes rangeType, PlayerStats stat) {

    public String getDisplayName(Meta metaData, boolean isPositive, boolean isGlobal) {
        StringBuilder baseString = new StringBuilder();

        baseString.append(ValueTypes.getSign(valueType, isPositive));

        baseString.append(rangeType.getValuesPlaceholder());
        baseString.append(valueType.getIconChar()).append(" ");

        String signIndicator = valueType.getSignIndicatorSuffix(isPositive);
        baseString.append(signIndicator);
        if (!signIndicator.isEmpty()){baseString.append(" ");}

        if (!isGlobal){baseString.append("Local ");}


        if (metaData != null) {
            //Inc. walk speed per 15 DEX
            baseString.append(stat.getAlias());
            baseString.append(" per ")
                    .append(metaData.rate())
                    .append(" ")
                    .append(metaData.convertedValueType().getSignIndicatorSuffix(true))
                    .append(" ")
                    .append(metaData.convertedStat().getAlias());
            return baseString.toString();
        }
        baseString.append(stat.getAlias());

        return baseString.toString();
    }
}
