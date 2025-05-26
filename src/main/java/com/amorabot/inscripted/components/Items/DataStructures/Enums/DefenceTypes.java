package com.amorabot.inscripted.components.Items.DataStructures.Enums;

import com.amorabot.inscripted.item.inscription.definition.Stats;

public enum DefenceTypes {
    HEALTH("❤") {
        @Override
        public Stats getStat() {
            return Stats.HEALTH;
        }
    },
    SOUL("✤") {
        @Override
        public Stats getStat() {
            return Stats.SOUL;
        }
    },
    FIRE("\uD83D\uDD25") {
        @Override
        public Stats getStat() {
            return Stats.FIRE_RESISTANCE;
        }
    },
    LIGHTNING("⚡") {
        @Override
        public Stats getStat() {
            return Stats.LIGHTNING_RESISTANCE;
        }
    },
    COLD("✽") {
        @Override
        public Stats getStat() {
            return Stats.COLD_RESISTANCE;
        }
    },
    ABYSSAL("\uD83C\uDF19") {
        @Override
        public Stats getStat() {
            return Stats.ABYSSAL_RESISTANCE;
        }
    },
    ARMOR("\uD83D\uDEE1") {
        @Override
        public Stats getStat() {
            return Stats.ARMOR;
        }
    },
    DODGE("✦") {
        @Override
        public Stats getStat() {
            return Stats.DODGE;
        }
    };

    private final String specialChar;

    DefenceTypes(String specialCharacter){
        this.specialChar = specialCharacter;
    }
    public String getSpecialChar() {
        return specialChar;
    }
    public abstract Stats getStat();
}
