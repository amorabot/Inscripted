package com.amorabot.inscripted.item.generation;

import com.amorabot.inscripted.item.inscription.definition.InscriptionDefinition;
import com.amorabot.inscripted.item.inscription.language.RollType;

public class ValuesTableSizeExtractor implements InscriptionDefinition.InscriptionDefinitionVisitor<Integer[]> {
    @Override
    public Integer[] visitRegularInsc(InscriptionDefinition.Regular inscription) {
        // [12] -> [1]   |   [1,3, 5,7] -> [4]   |   ...
        return new Integer[]{inscription.getBaseData().roll().getPreRollSize()};
    }

    @Override
    public Integer[] visitHybridInsc(InscriptionDefinition.Hybrid inscription) {
        final int primarySize = inscription.getPrimaryData().roll().getPreRollSize();
        final int secondarySize = inscription.getSecondaryData().roll().getPreRollSize();
        return new Integer[]{primarySize,secondarySize};
    }

    @Override
    public Integer[] visitMetaInsc(InscriptionDefinition.Meta inscription) {
        return new Integer[]{inscription.getBaseData().roll().getPreRollSize()};
    }

    @Override
    public Integer[] visitEffect(InscriptionDefinition.Effect inscription) {
        return new Integer[0];
    }

    @Override
    public Integer[] visitKeystone(InscriptionDefinition.Keystone inscription) {
        return new Integer[0];
    }

    public static RollType[] mapSizingsToRollTypes(Integer[] sizings){
        RollType[] templateRolls = new RollType[sizings.length];
        for (int i = 0; i < sizings.length; i++) {
            if (sizings[i] == RollType.CONSTANT.getPreRollSize()){
                templateRolls[i] = RollType.CONSTANT;
                continue;
            }
            if (sizings[i] == RollType.SINGLE_ROLL.getPreRollSize()){
                templateRolls[i] = RollType.SINGLE_ROLL;
                continue;
            }
            if (sizings[i] == RollType.DOUBLE_ROLL.getPreRollSize()){
                templateRolls[i] = RollType.DOUBLE_ROLL;
            }
        }
        return templateRolls;
    }
}
