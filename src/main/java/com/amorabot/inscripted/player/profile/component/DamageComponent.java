package com.amorabot.inscripted.player.profile.component;

import com.amorabot.inscripted.item.inscription.definition.Stats;
import com.amorabot.inscripted.skill.Skills;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class DamageComponent implements ProfileComponent {

    /*
    TODO: reestructure DamageComponent to better handle global stats that need to be stored for generating AttackData instances
    possibly storing only a snapshot of specific damage-related stats instead of the entire thing
    */
//    private AttackData baseAttackData;
    private int lifeOnHit;
    private int extraProjectiles;

    // Make a map for damage-tag-related damages?
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
//        this.baseAttackData = new AttackData();
        this.lifeOnHit = 0;
        this.extraProjectiles = 0;
        this.meleeDamage = 0;
        this.projectileDamage = 0;
        this.areaDamage = 0;
    }

    @Override
    public void updateComponent(UUID playerID, Map<Stats, double[]> finalStats) {
//        baseAttackData.updateComponent(playerID, finalStats);

        setLifeOnHit(getSingleValueFrom(Stats.LIFE_ON_HIT,finalStats));
        setExtraProjectiles(getSingleValueFrom(Stats.EXTRA_PROJECTILES,finalStats));

        setProjectileDamage(getSingleValueFrom(Stats.PROJECTILE_DAMAGE,finalStats));
        setMeleeDamage(getSingleValueFrom(Stats.MELEE_DAMAGE,finalStats));
        setAreaDamage(getSingleValueFrom(Stats.AREA_DAMAGE,finalStats));
    }
    @Override
    public List<Component> asTextComponent() {
        return List.of();
    }
}
