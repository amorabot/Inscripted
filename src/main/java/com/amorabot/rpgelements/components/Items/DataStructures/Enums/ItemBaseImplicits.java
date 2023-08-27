package com.amorabot.rpgelements.components.Items.DataStructures.Enums;

public enum ItemBaseImplicits {//TODO UNIFICAR COM WEAPONTYPES

    AXE("5% SHRED", "CORRUPT IMPLICIT1", "CORRUPT IMPLICIT2", "SPECIAL IMPLICIT"),
    SWORD("30% ACC", "CORRUPT IMPLICIT1", "CORRUPT IMPLICIT2", "SPECIAL IMPLICIT"),
    BOW("1% DODGE", "CORRUPT IMPLICIT1", "CORRUPT IMPLICIT2", "SPECIAL IMPLICIT"),
    DAGGER("10% CRIT DMG", "CORRUPT IMPLICIT1", "CORRUPT IMPLICIT2", "SPECIAL IMPLICIT"),
    WAND("5% MAELSTROM", "CORRUPT IMPLICIT1", "CORRUPT IMPLICIT2", "SPECIAL IMPLICIT"),
    SCEPTRE("10% ELE DMG", "CORRUPT IMPLICIT1", "CORRUPT IMPLICIT2", "SPECIAL IMPLICIT");

    private final String[] implicitArray;

    ItemBaseImplicits(String... implicits){
        implicitArray = implicits;
    }
    public String getBasicImplicit(){
        return implicitArray[0];
    }
}
