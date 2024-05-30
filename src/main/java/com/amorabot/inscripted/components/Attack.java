package com.amorabot.inscripted.components;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.PlayerStats;
import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.components.Player.Stats;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class Attack implements EntityComponent {

    private float DPS;

    private Map<DamageTypes, int[]> hitDamage = new HashMap<>();
    private float accuracy;
    private final float baseCrit = 5; //Weapon-dependent, but will be standard for now
    private float critChance;
    private int critDamage; //100% (base) + critDamage (modifier)
    private int shred;
    private int maelstrom;
    private float bleedChance;

    //TODO: ON-ATTACK TRIGGERS/CHECKS
    //Special attack modifiers go here

    public Attack(){
        hitDamage.put(DamageTypes.PHYSICAL, new int[]{1,1});
        setDps();
    }

    //For mobs
    public Attack(int[] phys, int[] fire, int[] light, int[] cold, int[] abyss, float critChance, int critDamage, int shred, int maelstrom, float bleedChance){
        this.hitDamage = new HashMap<>();
        hitDamage.put(DamageTypes.PHYSICAL, phys);
        hitDamage.put(DamageTypes.FIRE, fire);
        hitDamage.put(DamageTypes.LIGHTNING, light);
        hitDamage.put(DamageTypes.COLD, cold);
        hitDamage.put(DamageTypes.ABYSSAL, abyss);
        this.critChance = critChance;
        this.critDamage = critDamage;
        this.shred = shred;
        this.maelstrom = maelstrom;
        this.bleedChance = bleedChance;

        setDps();
    }

    public Map<DamageTypes, int[]> getDamages() {
        return hitDamage;
    }

    private void setDps(){
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
        Stats playerStats = profile.getStats();
        hitDamage.clear();
        int[][] playerDamage = playerStats.getFinalDamages();
        hitDamage.put(DamageTypes.PHYSICAL, playerDamage[0]);
        hitDamage.put(DamageTypes.FIRE, playerDamage[1]);
        hitDamage.put(DamageTypes.LIGHTNING, playerDamage[2]);
        hitDamage.put(DamageTypes.COLD, playerDamage[3]);
        hitDamage.put(DamageTypes.ABYSSAL, playerDamage[4]);

        this.accuracy = playerStats.getFinalFlatValueFor(PlayerStats.ACCURACY);
        this.critChance = Utils.applyPercentageTo(baseCrit, playerStats.getPercentValueFor(PlayerStats.CRITICAL_CHANCE));
        this.critDamage = playerStats.getPercentValueFor(PlayerStats.CRITICAL_DAMAGE);
        this.shred = playerStats.getPercentValueFor(PlayerStats.SHRED);
        this.maelstrom = playerStats.getPercentValueFor(PlayerStats.MAELSTROM);
        this.bleedChance = playerStats.getPercentValueFor(PlayerStats.BLEED);
    }

    public static @NotNull String getDamageString(int[] damagesArray){
        StringBuilder dmgString = new StringBuilder();
        addDamageToString(dmgString, damagesArray[0], DamageTypes.PHYSICAL);
        addDamageToString(dmgString, damagesArray[1], DamageTypes.FIRE);
        addDamageToString(dmgString, damagesArray[2], DamageTypes.LIGHTNING);
        addDamageToString(dmgString, damagesArray[3], DamageTypes.COLD);
        addDamageToString(dmgString, damagesArray[4], DamageTypes.ABYSSAL);
        return dmgString.toString().trim();
    }

    private static void addDamageToString(StringBuilder builder, int damage, DamageTypes damageType){
        if (damage > 0){
            String damageIcon = damageType.getCharacter();
            String damageColor = damageType.getColor();
            builder.append(ColorUtils.translateColorCodes(damageColor + damage + damageIcon))
                    .append(" ");
        }
    }
}
