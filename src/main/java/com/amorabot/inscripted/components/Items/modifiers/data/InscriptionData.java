package com.amorabot.inscripted.components.Items.modifiers.data;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.RangeTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.PlayerStats;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class InscriptionData implements ModifierData, Serializable {

    private final Affix affixType;
    private final StatDefinition definitionData;

    public InscriptionData(Affix affix, ValueTypes valueType, RangeTypes rangeType, PlayerStats stat){
        this.affixType = affix;
        this.definitionData = new StatDefinition(valueType, rangeType, stat);
    }
}
