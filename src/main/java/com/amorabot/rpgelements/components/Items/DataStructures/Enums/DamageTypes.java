package com.amorabot.rpgelements.components.Items.DataStructures.Enums;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public enum DamageTypes {
    PHYSICAL("&6", "🗡") {
        @Override
        void setColorComponent() {
            this.colorComponent = NamedTextColor.GOLD;
        }
    },
    FIRE("&c", "\uD83D\uDD25") {
        @Override
        void setColorComponent() {
            this.colorComponent = NamedTextColor.RED;
        }
    },
    COLD("&b", "✽") {
        @Override
        void setColorComponent() {
            this.colorComponent = NamedTextColor.AQUA;
        }
    },
    LIGHTNING("&e", "⚡") {
        @Override
        void setColorComponent() {
            this.colorComponent = NamedTextColor.YELLOW;
        }
    },
    ABYSSAL("&#7734AA", "\uD83C\uDF19") {
        @Override
        void setColorComponent() {
            this.colorComponent = TextColor.fromHexString("#7734AA");
        }
    };

    final String color;
    final String character;
    TextColor colorComponent;
    DamageTypes(String elementColor, String specialChar){
        this.color = elementColor;
        this.character = specialChar;
        setColorComponent();
    }

    public String getColor() {
        return color;
    }

    public String getCharacter() {
        return character;
    }
    abstract void setColorComponent();
    public TextColor getColorComponent(){
        return colorComponent;
    }
}
