package com.amorabot.inscripted.item.inscription;

import com.amorabot.inscripted.item.generation.ValuesTableSizeExtractor;
import com.amorabot.inscripted.item.inscription.definition.InscriptionDefinition;
import com.amorabot.inscripted.item.inscription.definition.InscriptionIDs;
import com.amorabot.inscripted.item.inscription.language.AffixType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class ProceduralInscription implements Serializable, Inscription {

    private static final boolean DEBUG_MODE = false;

    private final InscriptionIDs inscription;
    private final int tier;
    private double basePercentile;
    private boolean modifiable = true;

    public ProceduralInscription(InscriptionIDs inscription, int tier, double basePercentile) {
        this.inscription = inscription;
        this.tier = Math.min(tier, inscription.getTiers());
        this.basePercentile = basePercentile; //0-1
    }

    @Override
    public <R> R accept(InscriptionVisitor<R> visitor) {
        return visitor.visitProceduralInscription(this);
    }

    @Override
    public InscriptionDefinition getInscriptionDefinition() {
        return getInscription().getDefinitionData();
    }

    @Override
    public boolean getDebugState() {
        return DEBUG_MODE;
    }

    @Override
    public String getDisplayName(String valuesHex){
        String template = getTemplateDisplayName();
        Integer[] templateOrdering = getInscription().getDefinitionData().accept(new ValuesTableSizeExtractor());
        int[] mappedValues = getMappedFinalValues();
        return substituteTemplates(mappedValues,template,templateOrdering, valuesHex);
    }

    public boolean isSpecial(){
        return (isEffect() || isKeystone());
    }
    public boolean isImplicit(){
        return (getInscription().getDefinitionData().getAffix().equals(AffixType.IMPLICIT));
    }
}
