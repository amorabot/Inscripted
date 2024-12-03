package com.amorabot.inscripted.components.renderers;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

@Getter
public enum InscriptedPalette {
    //Plain text colors
    WHITE("#f7f4d2"),
    TINTED_BEIGE("#a8a588"),
    NEUTRAL_GRAY("#8f8d7f"),
    DARK_GRAY("#403e37"),
    DARKEST_TEXT("#241d1b"),

    //Misc. Item UI colors
    ITEM_VALUE("#95dbdb"),
    CORRUPTED("&4"),

    //Archetype colors
    MARAUDER("#b01330"),
    GLADIATOR("#c7a530"),
    MERCENARY("#bcd613"),
    ROGUE("#18A383"),
    SORCERER("#496FE3"),
    TEMPLAR("#A735D4"),

    //Rarity Colors
    AUGMENTED("#90cbf5"),
    RUNIC("#ffc517"),
    //#bf1935,#f23d5b,(wine)#ad1557,#db2a6e, (unique orange)#d46b08
    RELIC("#a63d7f"),

    //DEF Colors
    HEALTH("#ed2f5f"),
    WARD("#73bab5"),
    ARMOR("#9996b5"),
    DODGE("#9dc797"),

    //DMG Colors
    PHYSICAL("#e3b44f"),
    FIRE("&c"),
    COLD("&b"),
    LIGHTNING("&e"),
    ABYSSAL("#7734AA");

    private final String colorString;
    private final TextColor color;

    InscriptedPalette(String colorString){
        this.colorString = colorString;
        if (colorString.contains("#")){
            this.color = TextColor.fromHexString(colorString);
            return;
        }
        //Is legacy color code
        this.color = convertLegacyColor(colorString.replace("&",""));
    }


    public static Component colorizeComponent(Component component, TextColor color){
        return component.colorIfAbsent(color);
    }








    public NamedTextColor convertLegacyColor(String legacyColor){
        return switch (legacyColor) {
            case "6" -> NamedTextColor.GOLD;
            case "c" -> NamedTextColor.RED;
            case "e" -> NamedTextColor.YELLOW;
            case "b" -> NamedTextColor.AQUA;
            case "4" -> NamedTextColor.DARK_RED;
            case "7" -> NamedTextColor.GRAY;
            case "8" -> NamedTextColor.DARK_GRAY;
            default -> NamedTextColor.LIGHT_PURPLE;
        };
    }
}
