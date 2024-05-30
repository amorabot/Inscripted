package com.amorabot.inscripted.components;

import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.utils.Utils;

import java.util.UUID;

public class DamageComponent implements EntityComponent {

    private Attack hitData;

    private int lifeOnHit;
    private int lifeSteal;
    private int extraProjectiles;
    private int areaDamage;

    //Added damage is added directly to hitData, doesnt need to be stored

    //List of special on-hit keystones

    public DamageComponent(){
        this.hitData = new Attack();
    }
//    @Override
//    public void reset(){
//        Utils.error("Refrain from using no-arg DamageComponent reset() method, use reset(Weapon weaponData)");
//    }

//    public void reset(Weapon weaponData){
//        this.hitData = new Attack(weaponData);
//
//        this.lifeOnHit = 0;
//        this.lifeSteal = 0;
//        this.areaDamage = 0;
//    }

    public Attack getHitData() {
        return hitData;
    }


    @Override
    public void update(UUID profileID) {

    }


}
