package com.amorabot.rpgelements.components.Player;

public class Attributes {
    private int intelligence;
    private int dexterity;
    private int strength;

    public Attributes(int intelligence, int dexterity, int strength) {
        this.intelligence = intelligence;
        this.dexterity = dexterity;
        this.strength = strength;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public int getDexterity() {
        return dexterity;
    }

    public int getStrength() {
        return strength;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public void updateStats(int strength, int strengthMulti, int dexterity, int dexterityMulti, int intelligence, int intelligenceMulti){
        int strResult = (int) (strength * (1+ strengthMulti/100f));
        int dexResult = (int) (dexterity * (1+ dexterityMulti/100f));
        int intResult = (int) (intelligence * (1+ intelligenceMulti/100f));
        setStrength(strResult);
        setDexterity(dexResult);
        setIntelligence(intResult);
    }
}
