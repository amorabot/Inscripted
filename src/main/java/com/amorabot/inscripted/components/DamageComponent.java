package com.amorabot.inscripted.components;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.PlayerStats;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;
import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.components.Player.Stats;
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

    //Added damage is added directly to hitData, doesnt need to be stored

    //List of special on-hit keystones

    public DamageComponent(){
        this.hitData = new Attack();
    }

    @Override
    public void update(UUID profileID) {
        Profile profileData = JSONProfileManager.getProfile(profileID);
        Stats playerStats = profileData.getStats();

        setLifeOnHit((int) playerStats.getFinalFlatValueFor(PlayerStats.LIFE_ON_HIT));
        setLifeSteal(playerStats.getFinalPercentValueFor(PlayerStats.LIFESTEAL));
        setExtraProjectiles((int) playerStats.getFinalFlatValueFor(PlayerStats.EXTRA_PROJECTILES));
        setMeleeDamage(playerStats.getFinalPercentValueFor(PlayerStats.MELEE_DAMAGE));
        setAreaDamage(playerStats.getFinalPercentValueFor(PlayerStats.AREA_DAMAGE));

        Attack hitData = getHitData();
        hitData.update(profileID);
    }


}
