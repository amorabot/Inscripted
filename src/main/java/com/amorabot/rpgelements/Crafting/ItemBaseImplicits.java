package com.amorabot.rpgelements.Crafting;

public enum ItemBaseImplicits {

    AXE("3% CRIT", "CORRUPT IMPLICIT1", "CORRUPT IMPLICIT2", "SPECIAL IMPLICIT"),
    SHORTSWORD("30% ACC", "CORRUPT IMPLICIT1", "CORRUPT IMPLICIT2", "SPECIAL IMPLICIT"),
    STAFF("15% BLOCK", "CORRUPT IMPLICIT1", "CORRUPT IMPLICIT2", "SPECIAL IMPLICIT");

    private final String[] implicitArray;

    ItemBaseImplicits(String... implicits){
        implicitArray = implicits;
    }
    public String getBasicImplicit(){
        return implicitArray[0];
    }
}
