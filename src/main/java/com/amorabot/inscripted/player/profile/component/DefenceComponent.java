package com.amorabot.inscripted.player.profile.component;

import com.amorabot.inscripted.item.inscription.definition.Stats;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Setter
public class DefenceComponent implements ProfileComponent {
    @Getter
    private final static int resistanceCap = 75;
    @Getter
    private final static int abyssalResCap = 60;

    @Getter
    private int fireCapMod;
    private int fireResistance;
    @Getter
    private int coldCapMod;
    private int coldResistance;
    @Getter
    private int lightningCapMod;
    private int lightningResistance;

    private int abyssalResistance;

    @Getter
    private int dodge;
    @Getter
    private float armor;

    public DefenceComponent(){
        this.fireResistance = 15;
        this.lightningResistance = 15;
        this.coldResistance = 15;

        this.fireCapMod = 0;
        this.lightningCapMod = 0;
        this.coldCapMod = 0;

        this.dodge = 0;
        this.armor = 0;
    }
    public int getFireResistance() {
        return getCappedRes(fireResistance, resistanceCap, fireCapMod);
    }
    public int getColdResistance() {
        return getCappedRes(coldResistance, resistanceCap, coldCapMod);
    }
    public int getLightningResistance() {
        return getCappedRes(lightningResistance, resistanceCap, lightningCapMod);
    }
    public int getAbyssalResistance() {
        return getCappedRes(abyssalResistance, abyssalResCap, 0);
    }
    private int getCappedRes(int uncappedRes, int resCap, int capMod){
        int cappedRes = Math.min(uncappedRes, resCap + capMod);
        return Math.min(cappedRes, 85); //Limits to 85 regardless
    }

    @Override
    public void updateComponent(UUID playerID, Map<Stats, double[]> finalStats) {
        //Setting elemental stats
        setFireCapMod(getSingleValueFrom(Stats.MAX_FIRE_RESISTANCE,finalStats));
        setLightningCapMod(getSingleValueFrom(Stats.MAX_LIGHTNING_RESISTANCE,finalStats));
        setColdCapMod(getSingleValueFrom(Stats.COLD_RESISTANCE,finalStats));

        setFireResistance(getSingleValueFrom(Stats.FIRE_RESISTANCE,finalStats));
        setLightningResistance(getSingleValueFrom(Stats.LIGHTNING_RESISTANCE,finalStats));
        setColdResistance(getSingleValueFrom(Stats.COLD_RESISTANCE,finalStats));

        //Setting defences
        setArmor(getSingleValueFrom(Stats.ARMOR,finalStats));
        setDodge(getSingleValueFrom(Stats.DODGE,finalStats));
    }

    @Override
    public List<Component> asTextComponent() {
        return List.of();
    }
}
