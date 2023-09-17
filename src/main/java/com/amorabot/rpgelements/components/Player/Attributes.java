package com.amorabot.rpgelements.components.Player;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.TargetStats;
import com.amorabot.rpgelements.components.Items.DataStructures.Modifier;
import com.amorabot.rpgelements.components.Items.Weapon.WeaponModifiers;

import java.util.List;

public class Attributes {
    private int intelligence;
    private int dexterity;
    private int strength;

    public Attributes(int intelligence, int dexterity, int strength) {
        this.intelligence = intelligence;
        this.dexterity = dexterity;
        this.strength = strength;
    }
    public void update(Profile playerProfile){
        Stats playerStats = playerProfile.getStats();
        int strSum = 0;
        int dexSum = 0;
        int intSum = 0;
        //Getting stats from weapon
        if (playerStats.getWeaponSlot() != null){
            List<Modifier<WeaponModifiers>> weaponModifiers = playerStats.getWeaponSlot().getModifiers();
            for (Modifier<WeaponModifiers> weaponMod : weaponModifiers){
                TargetStats targetStat = weaponMod.getModifier().getTargetStat();
                if (targetStat == TargetStats.STRENGTH){
                    strSum += weaponMod.getValue()[0];
                }
                if (targetStat == TargetStats.DEXTERITY){
                    dexSum += weaponMod.getValue()[0];
                }
                if (targetStat == TargetStats.INTELLIGENCE){
                    intSum += weaponMod.getValue()[0];
                }
            }
        }
        //Getting stats from other slots...
        //...
        setStrength(strSum);
        setDexterity(dexSum);
        setIntelligence(intSum);
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
}
