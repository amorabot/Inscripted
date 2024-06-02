package com.amorabot.inscripted.components.Items.modifiers.data;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.PlayerStats;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.RangeTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class HybridInscriptionData implements ModifierData, Serializable {
    public static final String HYBRID_SEPARATOR = " & ";

    private final Affix affixType;
    private final StatDefinition[] statDefinitions;

    public HybridInscriptionData(Affix affix, ValueTypes[] valueTypes, RangeTypes[] rangeTypes, PlayerStats[] stats){
        this.affixType = affix;
        int mods = valueTypes.length;
        StatDefinition[] def = new StatDefinition[mods];
        for (int i = 0; i< mods; i++){
            def[i] = new StatDefinition(valueTypes[i], rangeTypes[i], stats[i]);
        }
        this.statDefinitions = def;
    }

    public List<int[]> splitValuesArray(int[] values){
        int offset = 0;
        List<int[]> orderedValuesList = new ArrayList<>();
        for (int i = 0; i < getStatDefinitions().length; i++){
            int currentTemplateLength = getStatDefinitions()[i].rangeType().getValueContainerTemplate().length;
            int[] currentSubArray = Arrays.copyOfRange(values, offset, offset + currentTemplateLength);
            orderedValuesList.add(currentSubArray);
            offset += currentTemplateLength;
        }
        return orderedValuesList;
    }
}
