package com.amorabot.rpgelements.components.Player;

import com.amorabot.rpgelements.components.Items.Armor.Armor;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.rpgelements.components.Items.Weapon.Weapon;
import com.amorabot.rpgelements.utils.Utils;

public class Stats {
    private Weapon weaponSlot;
    private Armor[] armorSet;

    public Stats(){
        //Construtor vazio
        this.weaponSlot = null;
        this.armorSet = new Armor[4];
    }

    public Weapon getWeaponSlot(){
        return weaponSlot;
    }
    public void setWeaponSlot(Weapon weapon) {
        this.weaponSlot = weapon;
    }

    public Armor[] getArmorSet() {
        return armorSet;
    }
    public Armor getHelmet(){
        return armorSet[3];
    }
    public Armor getChestplate(){
        return armorSet[2];
    }
    public Armor getLeggings(){
        return armorSet[1];
    }
    public Armor getBoots(){
        return armorSet[0];
    }

//    public void setArmorSet(Armor helmet, Armor chestplate, Armor leggings, Armor boots){
//        setArmorPiece(helmet, ItemTypes.HELMET);
//        setArmorPiece(chestplate, ItemTypes.CHESTPLATE);
//        setArmorPiece(leggings, ItemTypes.LEGGINGS);
//        setArmorPiece(boots, ItemTypes.BOOTS);
//    }
    public void setArmorPiece(Armor armorPiece, ItemTypes expectedType){
        if (expectedType == ItemTypes.WEAPON){return;}
        if (matchingSetPiece(armorPiece, expectedType)){
            switch (expectedType){
                case HELMET -> armorSet[3] = armorPiece;
                case CHESTPLATE -> armorSet[2] = armorPiece;
                case LEGGINGS -> armorSet[1] = armorPiece;
                case BOOTS -> armorSet[0] = armorPiece;
            }
            return;
        }
        if (expectedType != null){
            switch (expectedType){
                case HELMET -> armorSet[3] = null;
                case CHESTPLATE -> armorSet[2] = null;
                case LEGGINGS -> armorSet[1] = null;
                case BOOTS -> armorSet[0] = null;
            }
            Utils.log(expectedType.toString().toLowerCase() +" unequip");
        }
    }
    private boolean matchingSetPiece(Armor armorPiece, ItemTypes expectedPiece){
        if (armorPiece == null || expectedPiece == null){
            return false;
        }
        return armorPiece.getCategory() == expectedPiece;
    }
}
