package com.amorabot.inscripted.player.profile.component;

public class DamageComponent {

    private AttackData baseAttackData;
    private int lifeOnHit;
    private int lifeSteal;
    private int extraProjectiles;
    private int meleeDamage;
    private int areaDamage;

    //Set with attack-related keystones and effects
    /*
     Wont support generic damage conversions for now, only skill conversions
         Skills need to have a getRawAttack() method, recieving DamageComponent as input
         and a final version of AttackData as output, taking in consideration
         any conversions or multipliers, aswell as any specific damage increases (filtered by tags)
    */
}
