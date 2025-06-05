package com.amorabot.inscripted.player.profile.component;

import com.amorabot.inscripted.player.profile.parsing.StatPool;
import net.kyori.adventure.text.Component;

import java.util.List;

public class DamageComponent implements ProfileComponent {

    private AttackData baseAttackData;
    private int lifeOnHit;
    private int extraProjectiles;
    private int projectileDamage;
    private int meleeDamage;
    private int areaDamage;

    //Set with attack-related keystones and effects
    /*
     Wont support generic damage conversions for now, only skill conversions
         Skills need to have a getRawAttack() method, recieving DamageComponent as input
         and a final version of AttackData as output, taking in consideration
         any conversions or multipliers, aswell as any specific damage increases (filtered by tags)
    */

    public DamageComponent(){
        this.baseAttackData = new AttackData();
        this.lifeOnHit = 0;
        this.extraProjectiles = 0;
        this.meleeDamage = 0;
        this.projectileDamage = 0;
        this.areaDamage = 0;
    }

    @Override
    public void updateComponent(StatPool stats) {
        baseAttackData.updateComponent(stats);

        //...

    }
    @Override
    public List<Component> asTextComponent() {
        return List.of();
    }
}
