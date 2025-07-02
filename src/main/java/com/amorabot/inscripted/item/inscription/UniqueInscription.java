package com.amorabot.inscripted.item.inscription;

import com.amorabot.inscripted.item.inscription.definition.InscriptionDefinition;
import com.amorabot.inscripted.item.relic.Relics;
import com.amorabot.inscripted.utils.Utils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
public class UniqueInscription implements Serializable, Inscription {
    private static final boolean DEBUG_MODE = false;

    private final Relics sourceRelic;
    private final InscriptionDefinition definition;
    @Setter
    private double basePercentile;

    public UniqueInscription(InscriptionDefinition definitionData, Relics sourceRelic){
        this.sourceRelic = sourceRelic;
        this.definition = definitionData;
        this.basePercentile = Utils.getNormalizedValue();
    }

    @Override
    public <R> R accept(InscriptionVisitor<R> visitor) {
        return visitor.visitUniqueInscription(this);
    }

    @Override
    public InscriptionDefinition getInscriptionDefinition() {
        return definition;
    }

    @Override
    public boolean getDebugState() {
        return DEBUG_MODE;
    }
    @Override
    public boolean isModifiable() {
        return true;
    }
}
