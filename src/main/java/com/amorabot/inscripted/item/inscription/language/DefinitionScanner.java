package com.amorabot.inscripted.item.inscription.language;

import com.amorabot.inscripted.item.inscription.definition.EffectIDs;
import com.amorabot.inscripted.item.inscription.definition.InscriptionDefinition;
import com.amorabot.inscripted.item.inscription.definition.KeystoneIDs;
import com.amorabot.inscripted.item.inscription.definition.Stats;

import java.util.ArrayList;
import java.util.List;

public class DefinitionScanner {
    private static final List<String> keywords;
    static {
        keywords = new ArrayList<>();
        keywords.add("+");
        keywords.add("-");
        keywords.add("global");
        keywords.add("local");
        keywords.add("meta");
        for (AffixType affix : AffixType.values()){
            keywords.add(affix.name());
        }
        keywords.add("effect");
        keywords.add("keystone");
    }

    private final String source;
    public DefinitionScanner(String source){
        this.source = source;
    }

    public InscriptionDefinition run() throws InscriptionSyntaxException{
        if (!source.contains(":")){
            throw new InscriptionSyntaxException("Missing ':' on definition String");
        }
        String[] sections = source.split(":");

        return parse(sections);
    }

    public String[] splitSection(String sourceSection, String regex){
        return (sourceSection.strip().split(regex));
    }
    public InscriptionDefinition parse(String[] sections){
        String[] details = splitSection(sections[0], " ");
        if (!checkDetailKeywords(details)){return null;}

        ExprType inscriptionExpression;
        if (details[0].equals("effect")){inscriptionExpression = ExprType.EFFECT_INSCRIPTION;}
        else if (details[0].equals("keystone")){inscriptionExpression = ExprType.KEYSTONE_INSCRIPTION;}
        else if (details[1].equals("meta")){inscriptionExpression = ExprType.META_INSCRIPTION;}
        //We must be parsing a standard inscription ( Regular | Hybrid )
        else { inscriptionExpression = ExprType.REGULAR_INSCRIPTION; }
        switch (inscriptionExpression){
            case REGULAR_INSCRIPTION -> {
                boolean isPositive = mapPositive(details[0]);
                boolean isGlobal = mapScope(details[1]);
                AffixType affixType = AffixType.valueOf(details[2]);
                //Is it Hybrid?
                if (sections[1].contains("&")){
                    String[] hybridInscrData = splitSection(sections[1], "&");
                    String[] primaryInscData = splitSection(hybridInscrData[0]," ");
                    String[] secondaryInscData = splitSection(hybridInscrData[1]," ");

                    InscriptionDefinition.BaseInscription primInscription = parseBaseInscription(primaryInscData);
                    InscriptionDefinition.BaseInscription secInscription = parseBaseInscription(secondaryInscData);

                    return new InscriptionDefinition.Hybrid(affixType,primInscription,isPositive,isGlobal,secInscription);
                }
                //Its a regular one
                String[] mainInscriptionData = splitSection(sections[1], " ");
                InscriptionDefinition.BaseInscription baseInscription = parseBaseInscription(mainInscriptionData);

                return new InscriptionDefinition.Regular(affixType,baseInscription,isPositive,isGlobal);
            }
            case META_INSCRIPTION -> {
                boolean isPositive = mapPositive(details[0]);
                AffixType affixType = AffixType.valueOf(details[2]);

                String[] data = splitSection(sections[1], "<<");
                String[] mainStatData = splitSection(data[0], " ");
                String[] metaData = splitSection(data[1], " ");

                InscriptionDefinition.BaseInscription baseInscription = parseBaseInscription(mainStatData);
                try{
                    int conversionRate = Integer.parseInt(metaData[0]);
                    ValueType type = ValueType.valueOf(metaData[1].toUpperCase());
                    Stats targetStat = Stats.valueOf(metaData[2].toUpperCase());
                    return new InscriptionDefinition.Meta(affixType,baseInscription,isPositive,targetStat,type,conversionRate);
                } catch (IllegalArgumentException ex){
                    throw new InscriptionSyntaxException("Meta inscription definition error: Could not parse metadata");
                }
            }
            case EFFECT_INSCRIPTION -> {
                EffectIDs effect = EffectIDs.valueOf(sections[1].strip());
                return new InscriptionDefinition.Effect(effect);
            }
            case KEYSTONE_INSCRIPTION -> {
                KeystoneIDs keystone = KeystoneIDs.valueOf(sections[1].strip());
                return new InscriptionDefinition.Keystone(keystone);
            }
            default -> throw new InscriptionSyntaxException("Invalid inscriptionExpression on definition string.");
        }
    }
    private boolean checkDetailKeywords(String[] tokens){
        for(String token : tokens){
            if (!keywords.contains(token)){
                throw new InscriptionSyntaxException("Invalid keyword on Inscription definition string: " + token);
            }
        }
        return true;
    }
    private boolean mapPositive(String positive){
        return positive.equals("+");
    }
    private boolean mapScope(String scope){
        return scope.equals("global");
    }

    private InscriptionDefinition.BaseInscription parseBaseInscription(String[] mainDataTokens){
        try {
            ValueType type = ValueType.valueOf(mainDataTokens[0].toUpperCase());
            RollType roll = RollType.valueOf(mainDataTokens[1].toUpperCase());
            if (!type.equals(ValueType.FLAT) && roll.equals(RollType.DOUBLE_ROLL)){
                throw new InscriptionSyntaxException("Only FLAT values support DOUBLE_ROLL");
            }
            Stats stat = Stats.valueOf(mainDataTokens[2].toUpperCase());
            return new InscriptionDefinition.BaseInscription(type,roll,stat);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }
}
