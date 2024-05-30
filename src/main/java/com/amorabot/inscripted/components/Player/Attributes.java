package com.amorabot.inscripted.components.Player;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.PlayerStats;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;
import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;
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

        Stats stats = profileData.getStats();

        this.strength = (int) stats.getFinalFlatValueFor(PlayerStats.STRENGTH);
        this.dexterity = (int) stats.getFinalFlatValueFor(PlayerStats.DEXTERITY);
        this.intelligence = (int) stats.getFinalFlatValueFor(PlayerStats.INTELLIGENCE);

        //3 STR -> +1 Base HP
        //5 STR -> 1% Melee DMG
        applyStrengthBonusesTo(stats);

        //3 DEX -> ????
        //5 DEX -> ????
        applyDexterityBonusesTo(stats);

        //5 INT -> +1 Base Ward
        //50 INT -> 1% Ward recovery rate
        applyIntelligenceBonusesTo(stats);

    }
    public void applyStrengthBonusesTo(Stats stats){
        stats.addSingleStat(PlayerStats.HEALTH, ValueTypes.FLAT, new int[]{getStrength()/3});
        stats.addSingleStat(PlayerStats.MELEE_DAMAGE, ValueTypes.PERCENT, new int[]{getStrength()/10});
    }
    public void applyDexterityBonusesTo(Stats stats){

    }
    public void applyIntelligenceBonusesTo(Stats stats){

    }
}
