package com.amorabot.inscripted.item.render;

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

    //frame(-1 blank + 1 default rendering)-80->soulSegm-1,soulSegm-1,...,SS-16-64+2(3 blank -1 dft rend)->
    //new assets \uE010\uE021\uE022\uE025\uE028\uE029\uE011\uE012\uE012\uE012\uE013

    SPECIAL_ATTACK_ICON("\uE030"),
    UTILITY_ICON("\uE031"),
    MOBILITY_ICON("\uE032"),

    HP_FRAME("\uE010"),

    HP_FULL_HEAD("\uE011"),
    HP_FULL_MID("\uE012"),
    HP_FULL_TAIL("\uE013"),

    HP_HALF_HEAD("\uE014"),
    HP_HALF_MID("\uE015"),
    HP_HALF_TAIL("\uE016"),

    NO_HP_HEAD("\uE017"),
    NO_HP_MID("\uE018"),
    NO_HP_TAIL("\uE019"),

    SOUL_FULL_HEAD("\uE021"),
    SOUL_FULL_MID("\uE022"),
    SOUL_FULL_TAIL("\uE023"),

    SOUL_HALF_HEAD("\uE024"),
    SOUL_HALF_MID("\uE025"),
    SOUL_HALF_TAIL("\uE026"),

    NO_SOUL_HEAD("\uE027"),
    NO_SOUL_MID("\uE028"),
    NO_SOUL_TAIL("\uE029");

    @Getter
    private final String unicode;

    CustomUnicodeTable(String unicode){
        this.unicode = unicode;
    }

    @Override
    public String toString() {
        return unicode;
    }
}
