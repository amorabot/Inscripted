package com.amorabot.inscripted.item.inscription;

import com.amorabot.inscripted.item.inscription.definition.InscriptionDefinition;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@EqualsAndHashCode
public class UniqueInscription implements Inscription {
    private static final boolean DEBUG_MODE = false;

    private InscriptionDefinition definition;
    @Getter
    private double basePercentile;

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

    @Override
    public String getDisplayName(String valuesHex) {
        return ("<"+valuesHex+">" + definition.getDisplayName() + "</"+valuesHex+">");
    }
}
