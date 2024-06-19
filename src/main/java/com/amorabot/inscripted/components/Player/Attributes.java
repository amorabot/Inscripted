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
        //10 STR -> 1% Melee DMG
        applyStrengthBonusesTo(stats);

        //3 DEX -> +1 Accuracy
        //10 DEX -> +1 Base stamina
        applyDexterityBonusesTo(stats);

        //3 INT -> +1 Base Ward
        //50 INT -> 1% Ward recovery rate
        applyIntelligenceBonusesTo(stats);

    }
    public void applyStrengthBonusesTo(Stats stats){
        stats.addSingleStat(PlayerStats.HEALTH, ValueTypes.FLAT, new int[]{getStrength()/3});
        stats.addSingleStat(PlayerStats.MELEE_DAMAGE, ValueTypes.PERCENT, new int[]{getStrength()/10});
    }
    public void applyDexterityBonusesTo(Stats stats){
        stats.addSingleStat(PlayerStats.ACCURACY, ValueTypes.FLAT, new int[]{getDexterity()/3});
        stats.addSingleStat(PlayerStats.STAMINA, ValueTypes.FLAT, new int[]{getDexterity()/10});
    }
    public void applyIntelligenceBonusesTo(Stats stats){
        stats.addSingleStat(PlayerStats.WARD, ValueTypes.FLAT, new int[]{getIntelligence()/3});
        stats.addSingleStat(PlayerStats.WARD_RECOVERY_RATE, ValueTypes.PERCENT, new int[]{getIntelligence()/50});
    }
}
