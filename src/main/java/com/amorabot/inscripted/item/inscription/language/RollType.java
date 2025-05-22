package com.amorabot.inscripted.item.inscription.language;

import lombok.Getter;

@Getter
public enum RollType {
    CONSTANT(1,"<const>"),
    SINGLE_ROLL(2,"<value>"),
    DOUBLE_ROLL(4,"<v1> - <v2>");

    private final int preRollSize;
    private final String templateString;
    RollType(int size, String template){
        this.preRollSize = size;
        this.templateString = template;
    }
    public String getValuesTemplate(){
        return templateString;
    }
}
