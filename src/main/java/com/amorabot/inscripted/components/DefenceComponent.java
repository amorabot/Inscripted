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

@Setter
public class DefenceComponent implements EntityComponent {
    @Getter
    private final int baseElementalCap;
    @Getter
    private int fireCapMod;
    private int fireResistance;
    @Getter
    private int coldCapMod;
    private int coldResistance;
    @Getter
    private int lightningCapMod;
    private int lightningResistance;
    @Getter
    private final int abyssalCap = 60;
    private int abyssalResistance;

    @Getter
    private int dodge;
    @Getter
    private float armor;

    //TODO: ON HIT TRIGGERS (make notify() as a EntityComponent method)
    //Set<ItemModifier> specialModList; // (effects calculations in a specific way)

    //Player constructor
    public DefenceComponent(int elementalResCap){
        this.fireResistance = 15;
        this.lightningResistance = 15;
        this.coldResistance = 15;

        this.baseElementalCap = elementalResCap;
        this.fireCapMod = 0;
        this.lightningCapMod = 0;
        this.coldCapMod = 0;

        this.dodge = 0;
        this.armor = 0;
    }
    //Mob constructor
    public DefenceComponent(int fireRes, int coldRes, int lightRes, int abyssRes, int dodge, int finalArmor){
        this.baseElementalCap = 80; //Monster base elemental cap
        this.fireCapMod = 0;
        this.lightningCapMod = 0;
        this.coldCapMod = 0;

        this.fireResistance = fireRes;
        this.lightningResistance = coldRes;
        this.coldResistance = lightRes;
        this.abyssalResistance = abyssRes;

        this.dodge = dodge;

        this.armor = finalArmor;

    }
    public int getFireResistance() {
        return getCappedRes(fireResistance, fireCapMod);
    }
    public int getColdResistance() {
        return getCappedRes(coldResistance, coldCapMod);
    }
    public int getLightningResistance() {
        return getCappedRes(lightningResistance, lightningCapMod);
    }
    public int getAbyssalResistance() {
        return getCappedRes(abyssalResistance, 0);
    }

    @Override
    public void update(UUID profileID) {
        Profile profileData = JSONProfileManager.getProfile(profileID);

        StatsComponent playerStatsComponent = profileData.getStatsComponent();
        StatPool updatedStats = playerStatsComponent.getMergedStatsSnapshot();
        StatPool originalPlayerStats = playerStatsComponent.getPlayerStats();

        //Set elemental mod caps (When implemented)
        setFireCapMod(getPercentFrom(updatedStats, PlayerStats.MAX_FIRE_RESISTANCE, originalPlayerStats));
        setFireCapMod(getPercentFrom(updatedStats, PlayerStats.MAX_LIGHTNING_RESISTANCE, originalPlayerStats));
        setFireCapMod(getPercentFrom(updatedStats, PlayerStats.MAX_COLD_RESISTANCE, originalPlayerStats));

        //Setting resistances
        setFireResistance(getPercentFrom(updatedStats, PlayerStats.FIRE_RESISTANCE, originalPlayerStats));
        setLightningResistance(getPercentFrom(updatedStats, PlayerStats.LIGHTNING_RESISTANCE, originalPlayerStats));
        setColdResistance(getPercentFrom(updatedStats, PlayerStats.COLD_RESISTANCE, originalPlayerStats));
        setAbyssalResistance(getPercentFrom(updatedStats, PlayerStats.ABYSSAL_RESISTANCE, originalPlayerStats));

        //Setting defensive stats
        setArmor(updatedStats.getFinalValueFor(PlayerStats.ARMOR,true));
        originalPlayerStats.clearStat(PlayerStats.ARMOR);

        setDodge((int) updatedStats.getFinalValueFor(PlayerStats.DODGE,true));
        originalPlayerStats.clearStat(PlayerStats.DODGE);
    }
    private int getCappedRes(int uncappedRes, int capMod){
        int cappedRes = Math.min(uncappedRes, baseElementalCap + capMod);
        return Math.min(cappedRes, 85);
    }
    private int getPercentFrom(StatPool statsSnapshot, PlayerStats resistStat, StatPool originalStatPool){
        final int resistValue = (int) statsSnapshot.getFinalValueFor(resistStat,true, ValueTypes.PERCENT);
        //Clear the original pool from this stat
        originalStatPool.clearStat(resistStat);
        return resistValue;
    }
}