package com.amorabot.inscripted.components.Player;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.PlayerStats;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;
import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;
import com.amorabot.inscripted.components.Player.stats.StatPool;
import com.amorabot.inscripted.managers.JSONProfileManager;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Attributes implements EntityComponent {
    private int intelligence;
    private int dexterity;
    private int strength;

    public Attributes(int intelligence, int dexterity, int strength) {
        this.intelligence = intelligence;
        this.dexterity = dexterity;
        this.strength = strength;
    }

    @Override
    public void update(UUID profileID) {
        Profile profileData = JSONProfileManager.getProfile(profileID);

        StatsComponent statsComponent = profileData.getStatsComponent();
        StatPool updatedStats = statsComponent.getMergedStatsSnapshot();

        StatPool originalPlayerStats = statsComponent.getPlayerStats();

        /*
        Once the snapshot is taken, this component can sample from it and then add
        attribute-specific bonuses to the root 'playerStats', that might be used for
        future snapshots within the remaining components
        */

        this.strength = (int) updatedStats.getFinalValueFor(PlayerStats.STRENGTH,true);
        originalPlayerStats.clearStat(PlayerStats.STRENGTH);

        this.dexterity = (int) updatedStats.getFinalValueFor(PlayerStats.DEXTERITY,true);
        originalPlayerStats.clearStat(PlayerStats.DEXTERITY);

        this.intelligence = (int) updatedStats.getFinalValueFor(PlayerStats.INTELLIGENCE,true);
        originalPlayerStats.clearStat(PlayerStats.INTELLIGENCE);

        //3 STR -> +1 Base HP
        //10 STR -> 1% Melee DMG
        applyStrengthBonusesTo(statsComponent.getPlayerStats());

        //3 DEX -> +1 Accuracy
        //10 DEX -> +1 Base stamina
        applyDexterityBonusesTo(statsComponent.getPlayerStats());

        //3 INT -> +1 Base Ward
        //50 INT -> 1% Ward recovery rate
        applyIntelligenceBonusesTo(statsComponent.getPlayerStats());

    }
    public void applyStrengthBonusesTo(StatPool playerStats){
        playerStats.addStat(PlayerStats.HEALTH, ValueTypes.FLAT, new int[]{getStrength()/3});
        playerStats.addStat(PlayerStats.MELEE_DAMAGE, ValueTypes.PERCENT, new int[]{getStrength()/10});
    }
    public void applyDexterityBonusesTo(StatPool playerStats){
        playerStats.addStat(PlayerStats.ACCURACY, ValueTypes.FLAT, new int[]{getDexterity()/3});
        playerStats.addStat(PlayerStats.STAMINA, ValueTypes.FLAT, new int[]{getDexterity()/10});
    }
    public void applyIntelligenceBonusesTo(StatPool playerStats){
        playerStats.addStat(PlayerStats.WARD, ValueTypes.FLAT, new int[]{getIntelligence()/3});
        playerStats.addStat(PlayerStats.WARD_RECOVERY_RATE, ValueTypes.PERCENT, new int[]{getIntelligence()/50});
    }
}
