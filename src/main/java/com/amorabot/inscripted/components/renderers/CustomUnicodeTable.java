package com.amorabot.inscripted.components.renderers;

import lombok.Getter;

public enum CustomUnicodeTable {
    M1("\uF001"),
    M2("\uF002"),
    M3("\uF003"),
    M4("\uF004"),
    M5("\uF005"),
    M6("\uF006"),
    M7("\uF007"),
    M8("\uF008"),
    M9("\uF009"),
    M10("\uF010"),
    M16("\uF011"),
    M32("\uF012"),
    M64("\uF013"),
    M128("\uF014"),
    P1("\uF021"),
    P2("\uF022"),
    P3("\uF023"),
    P4("\uF024"),
    P5("\uF025"),
    P6("\uF026"),
    P7("\uF027"),
    P8("\uF028"),
    P9("\uF029"),
    P10("\uF030"),
    P16("\uF031"),
    P32("\uF032"),

    //new assets
    HP_BAR("\uE010"),
    HP_ICON("\uE011"),

    HP_HEAD("\uE020"),
    HP_FULL("\uE021"),
    HP_TAIL("\uE022"),
    HP_HALF("\uE023"),
    EMPTY_HP("\uE024"),
    EMPTY_HP_TAIL("\uE025"),

    WARD_HEAD("\uE026"),
    WARD_FULL("\uE027"),
    WARD_HALF("\uE028"),
    WARD_TAIL("\uE029");

    @Getter
    private final String unicodeChar;

    CustomUnicodeTable(String unicode){
        this.unicodeChar = unicode;
    }

    @Override
    public String toString() {
        return unicodeChar;
    }
}
