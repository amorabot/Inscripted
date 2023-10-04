package com.amorabot.inscripted.APIs;

import com.amorabot.inscripted.components.DamageComponent;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.HealthComponent;

public class DamageAPI {
    //attacker
    //defender
    //attack result
    //...

    public static void processAttack(HealthComponent defenderHealth, DefenceComponent defenderDefence, DamageComponent attackerDamage){
        if (attackResult()){
            defenderHealth.damage(attackerDamage.getDamage());
        }
    }
    private static boolean attackResult(){
        return true;
    }

//    private static void damage(HealthComponent defenderHealth){
//
//    }
}
