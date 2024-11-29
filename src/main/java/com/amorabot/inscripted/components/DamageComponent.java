package com.amorabot.inscripted.components;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.PlayerStats;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;
import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.components.Player.StatsComponent;
import com.amorabot.inscripted.components.Player.stats.StatPool;
import com.amorabot.inscripted.managers.JSONProfileManager;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DamageComponent implements EntityComponent {

    private Attack hitData;

    private int lifeOnHit;
    private int lifeSteal;
    private int extraProjectiles;
    private int meleeDamage;
    private int areaDamage;
    private int bleedDamage;

    //List of special on-hit keystones

    public DamageComponent(){
        this.hitData = new Attack();
    }

    @Override
    public void update(UUID profileID) {
        Profile profileData = JSONProfileManager.getProfile(profileID);
        StatsComponent playerStatsComponent = profileData.getStatsComponent();
        StatPool statsSnapshot = playerStatsComponent.getMergedStatsSnapshot();
        StatPool originalPlayerStats = playerStatsComponent.getPlayerStats();

        setLifeOnHit((int) statsSnapshot.getFinalValueFor(PlayerStats.LIFE_ON_HIT,true));
        originalPlayerStats.clearStat(PlayerStats.LIFE_ON_HIT);
        setLifeSteal((int) statsSnapshot.getFinalValueFor(PlayerStats.LIFESTEAL,true, ValueTypes.PERCENT));
        originalPlayerStats.clearStat(PlayerStats.LIFESTEAL);
        setExtraProjectiles((int) statsSnapshot.getFinalValueFor(PlayerStats.EXTRA_PROJECTILES,true));
        originalPlayerStats.clearStat(PlayerStats.EXTRA_PROJECTILES);
        setMeleeDamage((int) statsSnapshot.getFinalValueFor(PlayerStats.MELEE_DAMAGE,true, ValueTypes.PERCENT));
        originalPlayerStats.clearStat(PlayerStats.MELEE_DAMAGE);
        setAreaDamage((int) statsSnapshot.getFinalValueFor(PlayerStats.AREA_DAMAGE,true, ValueTypes.PERCENT));
        originalPlayerStats.clearStat(PlayerStats.AREA_DAMAGE);
        setBleedDamage((int) statsSnapshot.getFinalValueFor(PlayerStats.BLEED_DAMAGE,true, ValueTypes.PERCENT));
        originalPlayerStats.clearStat(PlayerStats.BLEED_DAMAGE);

        Attack hitData = getHitData();
        hitData.update(profileID);
    }


}
