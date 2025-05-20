package com.amorabot.inscripted.item.inscription.definition;


import com.amorabot.inscripted.item.inscription.language.ValueType;

public class InscriptionNameBuilder implements InscriptionDefinition.InscriptionVisitor<String> {
    @Override
    public String visitRegularInsc(InscriptionDefinition.Regular inscription) {
        return "Regular: " + inscription.getBaseData().stat().name();
    }

    @Override
    public String visitHybridInsc(InscriptionDefinition.Hybrid inscription) {
        return "Hybrid: " + inscription.getPrimaryData().stat().name() + " | " + inscription.getSecondaryData().stat().name();
    }

    @Override
    public String visitMetaInsc(InscriptionDefinition.Meta inscription) {
        //Metadata
        Stats convertedStat = inscription.getMetaStat();
        ValueType type = inscription.getMetaValueType();
        int rate = inscription.getConversionRate();
        return "Meta: " + inscription.getBaseData().stat() + "<<" + rate + " " + type + " " + convertedStat;
    }

    @Override
    public String visitEffect(InscriptionDefinition.Effect inscription) {
        return "Effect";
    }

    @Override
    public String visitKeystone(InscriptionDefinition.Keystone inscription) {
        return "Effect";
    }
}
