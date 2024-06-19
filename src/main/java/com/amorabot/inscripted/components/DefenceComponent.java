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
        return Math.min(fireResistance, baseElementalCap + fireCapMod);
    }
    public int getColdResistance() {
        return Math.min(coldResistance, baseElementalCap + coldCapMod);
    }
    public int getLightningResistance() {
        return Math.min(lightningResistance, baseElementalCap + lightningCapMod);
    }
    public int getAbyssalResistance() {
        return Math.min(abyssalResistance, abyssalCap);
    }

    @Override
    public void update(UUID profileID) {
        Profile profileData = JSONProfileManager.getProfile(profileID);
        Stats playerStats = profileData.getStats();

        //Set elemental mod caps (When implemented)
        setFireCapMod(playerStats.getFinalPercentValueFor(PlayerStats.MAX_FIRE_RESISTANCE));
        setLightningCapMod(playerStats.getFinalPercentValueFor(PlayerStats.MAX_LIGHTNING_RESISTANCE));
        setColdCapMod(playerStats.getFinalPercentValueFor(PlayerStats.MAX_COLD_RESISTANCE));

        //Setting resistances
        setFireResistance(playerStats.getFinalPercentValueFor(PlayerStats.FIRE_RESISTANCE));
        setLightningResistance(playerStats.getFinalPercentValueFor(PlayerStats.LIGHTNING_RESISTANCE));
        setColdResistance(playerStats.getFinalPercentValueFor(PlayerStats.COLD_RESISTANCE));
        setAbyssalResistance(playerStats.getFinalPercentValueFor(PlayerStats.ABYSSAL_RESISTANCE));

        //Setting defensive stats
        setArmor(playerStats.getFinalFlatValueFor(PlayerStats.ARMOR));

        setDodge((int)playerStats.getFinalFlatValueFor(PlayerStats.DODGE));
    }
}