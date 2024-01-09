package com.amorabot.inscripted.components.Player;

import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;
import com.amorabot.inscripted.managers.JSONProfileManager;

import java.util.UUID;

public class Attributes implements EntityComponent {
    private int intelligence;
    private int intMulti;
    private int dexterity;
    private int dexMulti;
    private int strength;
    private int strMulti;

    public Attributes(int intelligence, int dexterity, int strength) {
        this.intelligence = intelligence;
        this.dexterity = dexterity;
        this.strength = strength;
    }

    public void reset(){
        intelligence = 0;
        intMulti = 0;

        dexterity = 0;
        dexMulti = 0;

        strength = 0;
        strMulti = 0;
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
    public void addBaseIntelligence(int intelligence){
        this.intelligence += intelligence;
    }
    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }
    public void addBaseDexterity(int dexterity){
        this.dexterity += dexterity;
    }
    public void setStrength(int strength) {
        this.strength = strength;
    }
    public void addBaseStrength(int strength){
        this.strength = strength;
    }

    public int getIntMulti() {
        return intMulti;
    }

    public void setIntMulti(int intMulti) {
        this.intMulti = intMulti;
    }
    public void addBaseIntMulti(int intMulti){
        this.intMulti += intMulti;
    }

    public int getDexMulti() {
        return dexMulti;
    }

    public void setDexMulti(int dexMulti) {
        this.dexMulti = dexMulti;
    }
    public void addBaseDexMulti(int dexMulti){
        this.dexMulti += dexMulti;
    }

    public int getStrMulti() {
        return strMulti;
    }

    public void setStrMulti(int strMulti) {
        this.strMulti = strMulti;
    }
    public void addBaseStrMulti(int strMulti){
        this.strMulti += strMulti;
    }

    private void applyMultipliers(){
        setStrength((int) (getStrength() * (1 + getStrMulti()/100F)));
        setDexterity((int) (getDexterity() * (1 + getDexMulti()/100F)));
        setIntelligence((int) (getIntelligence() * (1 + getIntMulti()/100F)));
    }

    @Override
    public void update(UUID profileID) {
        Profile profileData = JSONProfileManager.getProfile(profileID);
        applyMultipliers();
        HealthComponent playerHP = profileData.getHealthComponent();

        //Apply STR bonuses:
        //3 STR -> +1 Base HP
        //5 STR -> 1% Melee DMG
        int strengthHP = strength/3;
        playerHP.addBaseHealth(strengthHP);

        //Apply DEX bonuses:
        //5 INT -> +1 Base Ward
        //10 INT -> 1% ??????
        int intelligenceWard = intelligence/5;
        playerHP.addBaseWard(intelligenceWard);

    }
}
