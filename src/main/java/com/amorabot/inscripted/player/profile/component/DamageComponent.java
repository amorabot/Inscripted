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
    private AttackData baseAttackData;
    private int lifeOnHit;
    private int extraProjectiles;

    private int projectileDamage;
    private int meleeDamage;
    private int areaDamage;

    public DamageComponent(){
        this.baseAttackData = new AttackData();
        this.lifeOnHit = 0;
        this.extraProjectiles = 0;
        this.meleeDamage = 0;
        this.projectileDamage = 0;
        this.areaDamage = 0;
    }

    @Override
    public void updateComponent(UUID playerID, Map<Stats, double[]> finalStats) {
        baseAttackData.updateComponent(playerID, finalStats);

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
