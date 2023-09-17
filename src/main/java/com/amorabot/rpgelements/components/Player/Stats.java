package com.amorabot.rpgelements.components.Player;

import com.amorabot.rpgelements.components.Items.Weapon.Weapon;

public class Stats {
    private Weapon weaponSlot;

    public Stats(){
        //Construtor vazio
        this.weaponSlot = null;
    }

    public Weapon getWeaponSlot(){
        return weaponSlot;
    }
    public void setWeaponSlot(Weapon weapon) {
        this.weaponSlot = weapon;
    }
}
