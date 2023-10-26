package com.amorabot.inscripted.components;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class HitComponent implements EntityComponent {

    private float DPS;

    private Map<DamageTypes, int[]> damage;
    private float accuracy;
    private final float baseCrit = 5; //Weapon-dependent, but will be standard for now
    private float critChance;
    private int critDamage; //100% (base) + critDamage (modifier)
    private int shred;
    private int maelstrom;
    private float bleedChance;

    public HitComponent(Weapon weaponData){
        if (weaponData == null){
            damage = new HashMap<>();
            damage.put(DamageTypes.PHYSICAL, new int[]{1,1});
            DPS = 1;
            return;
        }
        damage = weaponData.getBaseDamage();
        setDps();
    }

    //For mobs
    public HitComponent(int[] phys, int[] fire, int[] light, int[] cold, int[] abyss, float critChance, int critDamage, int shred, int maelstrom, float bleedChance){
        this.damage = new HashMap<>();
        damage.put(DamageTypes.PHYSICAL, phys);
        damage.put(DamageTypes.FIRE, fire);
        damage.put(DamageTypes.LIGHTNING, light);
        damage.put(DamageTypes.COLD, cold);
        damage.put(DamageTypes.ABYSSAL, abyss);
        this.critChance = critChance;
        this.critDamage = critDamage;
        this.shred = shred;
        this.maelstrom = maelstrom;
        this.bleedChance = bleedChance;

        setDps();
    }

    public Map<DamageTypes, int[]> getDamages() {
        return damage;
    }
    public float getDPS(){return DPS;}
    private void setDps(){
        int lowerDamageSum = 0;
        int upperDamageSum = 0;
        for (DamageTypes dmgType : damage.keySet()){
            if (damage.containsKey(dmgType)){
                int[] dmg = damage.get(dmgType);
                lowerDamageSum += dmg[0];
                upperDamageSum += dmg[1];
            }
        }
        float avgDamage = (float) (lowerDamageSum + upperDamageSum)/2;
        if (avgDamage == 0){
            Utils.log("No DPS ???? (HitComponent)");
            damage.put(DamageTypes.PHYSICAL, new int[]{1,1});
            this.DPS = 1;
            return;
        }
        DPS = avgDamage;
    }

    public void applyPercentDamage(DamageTypes damageType, int genericPercentAdded){
        if (damage.containsKey(damageType)){
            int[] damageValues = damage.get(damageType);
            damageValues[0] = (int) (damageValues[0] * (1 + genericPercentAdded/100f ));
            damageValues[1] = (int) (damageValues[1] * (1 + genericPercentAdded/100f ));

            damage.put(damageType, damageValues);
        }
    }
    public void addFlatDamage(DamageTypes type, int[] damageSumVar){
        if (damage.containsKey(type)){
            int[] weaponDmg = damage.get(type);
            damageSumVar[0] += weaponDmg[0];
            damageSumVar[1] += weaponDmg[1];
            damage.put(type,damageSumVar);
        } else {
            damage.put(type, damageSumVar);
        }
    }

    @Override
    public void update(Profile profileData) {

    }

    public float getAccuracy() {
        return accuracy;
    }
    //Accuracy does no consider percent accuracy, TODO: implement percent scaling :D

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getCritChance() {
        return (baseCrit * (1 + (critChance/100F) ) );
    }

    public void setCritChance(float critChance) {
        this.critChance = critChance;
    }

    public int getCritDamage() {
        return critDamage;
    }

    public void setCritDamage(int critDamage) {
        this.critDamage = critDamage;
    }

    public int getShred() {
        return shred;
    }

    public void setShred(int shred) {
        this.shred = shred;
    }

    public int getMaelstrom() {
        return maelstrom;
    }

    public void setMaelstrom(int maelstrom) {
        this.maelstrom = maelstrom;
    }

    public float getBleedChance() {
        return bleedChance;
    }

    public void setBleedChance(float bleedChance) {
        this.bleedChance = bleedChance;
    }
}
