package com.amorabot.inscripted.item.inscription.definition;

import com.amorabot.inscripted.item.inscription.language.RollType;
import com.amorabot.inscripted.item.inscription.language.ValueType;

public class InscriptionNameBuilder implements InscriptionDefinition.InscriptionVisitor<String> {

    @Override
    public String visitRegularInsc(InscriptionDefinition.Regular inscription) {
        return buildBaseInscriptionTemplate(inscription.getBaseData(), inscription.isPositive(), inscription.isGlobal());
    }

    @Override
    public String visitHybridInsc(InscriptionDefinition.Hybrid inscription) {
        boolean positive = inscription.isPositive();
        boolean global = inscription.isGlobal();
        return (buildBaseInscriptionTemplate(inscription.getPrimaryData(), positive, global) + " and " +
                buildBaseInscriptionTemplate(inscription.getSecondaryData(), positive, global));
    }

    @Override
    public String visitMetaInsc(InscriptionDefinition.Meta inscription) {
        //Metadata
        Stats convertedStat = inscription.getMetaStat();
        ValueType type = inscription.getMetaValueType();
        int rate = inscription.getConversionRate();
        return buildBaseInscriptionTemplate(inscription.getBaseData(), inscription.isPositive(), inscription.isGlobal())
                + buildMetaStatTemplate(convertedStat,type,rate, inscription.isPositive());
    }

    @Override
    public String visitEffect(InscriptionDefinition.Effect inscription) {
        return "Effect";
    }

    @Override
    public String visitKeystone(InscriptionDefinition.Keystone inscription) {
        return "Keystone";
    }

    private String buildBaseInscriptionTemplate(InscriptionDefinition.BaseInscription inscriptionData, boolean isPositive, boolean isGlobal){
        StringBuilder builder = new StringBuilder();

        Stats stat = inscriptionData.stat();
        RollType roll = inscriptionData.roll();
        ValueType type = inscriptionData.type();

        String valueTemplate = roll.getValuesTemplate();
        String positiveKeyword = type.getKeyword(isPositive);
        if (type.equals(ValueType.INCREASED) || type.equals(ValueType.MULTIPLIER)){
            builder.append(valueTemplate).append("% ");
            builder.append(positiveKeyword);
        } else {
            // Flat || Percentage
            builder.append(positiveKeyword);
            builder.append(valueTemplate);
            if(type.equals(ValueType.PERCENTAGE)){builder.append("%");}
        }
        builder.append(" ");
        if (!isGlobal){builder.append("Local").append(" ");}
        builder.append(stat.getAlias());
        return builder.toString();
    }
    private String buildMetaStatTemplate(Stats convertedStat, ValueType type, int rate, boolean isPositive){
        StringBuilder builder = new StringBuilder(" per ");
        builder.append(rate).append(" ");
        if (type.equals(ValueType.INCREASED) || type.equals(ValueType.MULTIPLIER)){
            builder.append(type.getKeyword(isPositive)).append(" ");
        }
        builder.append(convertedStat.getAlias());
        return builder.toString();
    }
}
