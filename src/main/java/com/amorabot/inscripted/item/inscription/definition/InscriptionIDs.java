package com.amorabot.inscripted.item.inscription.definition;

import static com.amorabot.inscripted.item.inscription.definition.Stats.*;
import static com.amorabot.inscripted.item.inscription.language.ValueType.*;
import com.amorabot.inscripted.item.inscription.language.DefinitionScanner;
import com.amorabot.inscripted.item.inscription.language.InscriptionSyntaxException;

import java.lang.reflect.Field;
import java.util.Objects;

public enum InscriptionIDs {

    HEALTH("+ local PREFIX: flat single_roll health", 12),
    HEALTH_PERCENT("+ local PREFIX: percentage single_roll health",6),
    HEALTH_REGEN_PERCENT("+ global PREFIX: percentage single_roll health_regen",6),
    ARMOR("+ local PREFIX: flat single_roll armor",10),
    HYBRID_ACC_PHYS("+ local PREFIX: flat single_roll accuracy & increased single_roll physical_damage",8),

    @MetaInscription(convertedStat = STRENGTH, convertedValueType = FLAT, rate = 8)
    STRENGTH_TO_FIRE_DMG("+ meta PREFIX: flat double_roll fire_damage",3);


    private final InscriptionDefinition definitionData;
    private final int tiers;

    InscriptionIDs(String definitionString, int tiers){
        String finalDefinitionString = definitionString;
        if (hasMetadata()){
            finalDefinitionString += getMetadataDefinitionSection(
                    Objects.requireNonNull(getMetaAnnotationData())
            );
        }
        DefinitionScanner scanner = new DefinitionScanner(finalDefinitionString);
        this.definitionData = scanner.run();
        this.tiers = tiers;
    }

    public boolean hasMetadata(){
        return getMetaAnnotationData() != null;
    }
    public MetaInscription getMetaAnnotationData(){
        try {
            Field inscriptionEnumField = InscriptionIDs.class.getField(this.name());
            if (inscriptionEnumField.isAnnotationPresent(MetaInscription.class)){
                return inscriptionEnumField.getAnnotation(MetaInscription.class);
            }
            return null;
        } catch (NoSuchFieldException e) {
            throw new InscriptionSyntaxException("No Metadata defined for " + this.name());
        }
    }
    private String getMetadataDefinitionSection(MetaInscription metaAnnotation){
        StringBuilder builder = new StringBuilder("<<");
        builder.append(metaAnnotation.rate()).append(" ");
        builder.append(metaAnnotation.convertedValueType()).append(" ");
        builder.append(metaAnnotation.convertedStat());
        return builder.toString();
    }

    public InscriptionDefinition getDefinitionData() {
        return definitionData;
    }
    public int getTiers() {
        return tiers;
    }
}
