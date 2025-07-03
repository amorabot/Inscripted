package com.amorabot.inscripted.item.inscription.definition;

import com.amorabot.inscripted.item.inscription.language.RollType;
import com.amorabot.inscripted.item.inscription.language.ValueType;
import com.amorabot.inscripted.item.render.InscriptedPalette;

public class InscriptionTemplateBuilder implements InscriptionDefinition.InscriptionDefinitionVisitor<String> {

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
        EffectIDs effect = inscription.getEffectID();
        String effectName = effect.name().replaceFirst("_"," ").strip();
        String effectInfo = effect.getInfo();
        String effectColor = InscriptedPalette.RELIC.getColorString();
        String infoColor = InscriptedPalette.DARK_GRAY.getColorString();
        return ("<"+effectColor+"><b>" + effectName + "</b></"+effectColor+">" + "<"+infoColor+"> > " + effectInfo + "</"+infoColor+">");
    }

    @Override
    public String visitKeystone(InscriptionDefinition.Keystone inscription) {
        String keystoneName = inscription.getKeystoneID().name().replaceFirst("_"," ").strip();
        String keystoneColor = InscriptedPalette.RELIC.getColorString();
        return "<"+keystoneColor+"><b>" + keystoneName + " \uD83D\uDCD6</b></"+keystoneColor+">";
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
