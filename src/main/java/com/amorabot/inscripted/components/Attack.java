package com.amorabot.inscripted.components;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.components.Player.stats.PlayerStats;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;
import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.components.Player.StatsComponent;
import com.amorabot.inscripted.components.Player.stats.StatPool;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class Attack implements EntityComponent,Cloneable {

    private float DPS;

    private Map<DamageTypes, int[]> hitDamage = new HashMap<>();
    private float accuracy;
//    private final float baseCrit = 5; //Weapon-dependent, but will be standard for now
    private int critChance;
    private int critDamage;
    private int shred;
    private int maelstrom;
    private int firePen;
    private int lightningPen;
    private int coldPen;
    private int bleedChance;

    public Attack(){
        hitDamage.put(DamageTypes.PHYSICAL, new int[]{1,1});
        this.accuracy = 0;
        this.critChance = 0;
        this.critDamage = 0;
        this.shred = 0;
        this.maelstrom = 0;
        this.firePen = 0;
        this.lightningPen = 0;
        this.coldPen = 0;
        this.bleedChance = 0;
        setDPS();
    }

    //For mobs
    public Attack(int[] phys, int[] fire, int[] light, int[] cold, int[] abyss, float accuracy, int critChance, int critDamage, int shred, int maelstrom, int bleedChance,
                  int firePen, int coldPen, int lightningPen){
        this.hitDamage = new HashMap<>();
        hitDamage.put(DamageTypes.PHYSICAL, phys);
        hitDamage.put(DamageTypes.FIRE, fire);
        hitDamage.put(DamageTypes.LIGHTNING, light);
        hitDamage.put(DamageTypes.COLD, cold);
        hitDamage.put(DamageTypes.ABYSSAL, abyss);
        this.accuracy = accuracy;
        this.critChance = critChance;
        this.critDamage = critDamage;
        this.shred = shred;
        this.maelstrom = maelstrom;
        this.bleedChance = bleedChance;

        this.firePen = firePen;
        this.coldPen = coldPen;
        this.lightningPen = lightningPen;
        setDPS();
    }
    public Attack(Attack clonedAttack){
        this.hitDamage = new HashMap<>(clonedAttack.getDamages());
        this.accuracy = clonedAttack.getAccuracy();
        this.critChance = clonedAttack.getCritChance();
        this.critDamage = clonedAttack.getCritDamage();
        this.shred = clonedAttack.getShred();
        this.maelstrom = clonedAttack.getMaelstrom();
        this.bleedChance = clonedAttack.getBleedChance();

        this.firePen = clonedAttack.getFirePen();
        this.coldPen = clonedAttack.getColdPen();
        this.lightningPen = clonedAttack.getLightningPen();
        setDPS();
    }

    public Map<DamageTypes, int[]> getDamages() {
        return hitDamage;
    }

    private void setDPS(){
        int lowerDamageSum = 0;
        int upperDamageSum = 0;
        for (DamageTypes dmgType : hitDamage.keySet()){
            if (hitDamage.containsKey(dmgType)){
                int[] dmg = hitDamage.get(dmgType);
                lowerDamageSum += dmg[0];
                upperDamageSum += dmg[1];
            }
        }
        float avgDamage = (float) (lowerDamageSum + upperDamageSum)/2;
        if (avgDamage == 0){
            Utils.log("No DPS ???? (HitComponent)");
            hitDamage.put(DamageTypes.PHYSICAL, new int[]{1,1});
            this.DPS = 1;
            return;
        }
        DPS = avgDamage;
    }

    @Override
    public void update(UUID profileID) {
        Profile profile = JSONProfileManager.getProfile(profileID);
        StatsComponent playerStatsComponent = profile.getStatsComponent();
        StatPool statsSnapshot = playerStatsComponent.getMergedStatsSnapshot();
        StatPool originalPlayerStats = playerStatsComponent.getPlayerStats();

        hitDamage.clear();
        int[][] playerDamage = statsSnapshot.getFinalDamages();
        hitDamage.put(DamageTypes.PHYSICAL, playerDamage[0]);
        hitDamage.put(DamageTypes.FIRE, playerDamage[1]);
        hitDamage.put(DamageTypes.LIGHTNING, playerDamage[2]);
        hitDamage.put(DamageTypes.COLD, playerDamage[3]);
        hitDamage.put(DamageTypes.ABYSSAL, playerDamage[4]);

        setDPS();

        this.accuracy = statsSnapshot.getFinalValueFor(PlayerStats.ACCURACY,true);
        originalPlayerStats.clearStat(PlayerStats.ACCURACY);
        this.critChance = (5 + getPercentFrom(statsSnapshot, PlayerStats.CRITICAL_CHANCE, originalPlayerStats));
        this.critDamage = getPercentFrom(statsSnapshot, PlayerStats.CRITICAL_DAMAGE, originalPlayerStats);
        this.shred = getPercentFrom(statsSnapshot, PlayerStats.SHRED, originalPlayerStats);
        this.maelstrom = getPercentFrom(statsSnapshot, PlayerStats.MAELSTROM, originalPlayerStats);
        this.bleedChance = getPercentFrom(statsSnapshot, PlayerStats.BLEED, originalPlayerStats);

        this.firePen = getPercentFrom(statsSnapshot, PlayerStats.FIRE_PENETRATION, originalPlayerStats);
        this.lightningPen = getPercentFrom(statsSnapshot, PlayerStats.LIGHTNING_PENETRATION, originalPlayerStats);
        this.coldPen = getPercentFrom(statsSnapshot, PlayerStats.COLD_PENETRATION, originalPlayerStats);

    }

    public static @NotNull String getDamageString(int[] damagesArray){
        StringBuilder dmgString = new StringBuilder();
        addDamageToString(dmgString, damagesArray[0], DamageTypes.PHYSICAL);
        addDamageToString(dmgString, damagesArray[1], DamageTypes.FIRE);
        addDamageToString(dmgString, damagesArray[2], DamageTypes.LIGHTNING);
        addDamageToString(dmgString, damagesArray[3], DamageTypes.COLD);
        addDamageToString(dmgString, damagesArray[4], DamageTypes.ABYSSAL);
        String finalDmgString = dmgString.toString().trim();
        if (finalDmgString.isEmpty()){
            return DamageTypes.PHYSICAL.getCharacter();
        } else {
            return dmgString.toString().trim();
        }
    }
    private int getPercentFrom(StatPool statsSnapshot, PlayerStats dmgStat, StatPool originalStatPool){
        final int resistValue = (int) statsSnapshot.getFinalValueFor(dmgStat,true, ValueTypes.PERCENT);
        //Clear the original pool from this stat
        originalStatPool.clearStat(dmgStat);
        return resistValue;
    }

    private static void addDamageToString(StringBuilder builder, int damage, DamageTypes damageType){
        if (damage > 0){
            String damageIcon = damageType.getCharacter();
            String damageColor = "&#FFFFFF";
            builder.append(ColorUtils.translateColorCodes(damageColor + damage + damageIcon))
                    .append(" ");
        }
    }

    @Override
    public Attack clone() {
        try {
            Attack clone = (Attack) super.clone();
            clone.setHitDamage(new HashMap<>(getDamages()));
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
