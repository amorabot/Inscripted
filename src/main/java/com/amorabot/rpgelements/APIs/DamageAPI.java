package com.amorabot.rpgelements.APIs;

import com.amorabot.rpgelements.components.DamageComponent;
import com.amorabot.rpgelements.components.DefenceComponent;
import com.amorabot.rpgelements.components.HealthComponent;

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
