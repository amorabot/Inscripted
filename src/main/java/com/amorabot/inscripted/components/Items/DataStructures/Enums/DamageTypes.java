package com.amorabot.inscripted.components.Items.DataStructures.Enums;

import com.amorabot.inscripted.components.Items.Weapon.RangeCategory;
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
    LIGHTNING("&e", "⚡") {
        @Override
        void setColorComponent() {
            this.colorComponent = NamedTextColor.YELLOW;
        }
    },
    COLD("&b", "✽") {
        @Override
        void setColorComponent() {
            this.colorComponent = NamedTextColor.AQUA;
        }
    },
    ABYSSAL("&#7734AA", "☽") {
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
    public String getPhysicalDamageIcon(RangeCategory rangeCategory){
        switch (rangeCategory){
            case MELEE -> {
                return getCharacter();
            }
            case RANGED -> {
                return "\uD83C\uDFF9";
            }
            default -> {
                return "X";
            }
        }
    }
    abstract void setColorComponent();
    public TextColor getTextColor(){
        return colorComponent;
    }
}
