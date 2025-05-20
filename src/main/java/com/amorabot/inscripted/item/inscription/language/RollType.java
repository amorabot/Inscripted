package com.amorabot.inscripted.item.inscription.language;

import lombok.Getter;

@Getter
public enum RollType {
    CONSTANT(1) {
        @Override
        public String getValuesTemplate() {
            return "<v1>";
        }
    },
    SINGLE_ROLL(2) {
        @Override
        public String getValuesTemplate() {
            return "<v1>";
        }
    },
    DOUBLE_ROLL(4) {
        @Override
        public String getValuesTemplate() {
            return "<v1> - <v2>";
        }
    };

    private final int preRollSize;
    RollType(int size){
        this.preRollSize = size;
    }
    public abstract String getValuesTemplate();
}
