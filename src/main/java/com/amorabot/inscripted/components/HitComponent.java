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

    private Map<DamageTypes, int[]> hitDamage;
    private float accuracy;
    private final float baseCrit = 5; //Weapon-dependent, but will be standard for now
    private float critChance;
    private int critDamage; //100% (base) + critDamage (modifier)
    private int shred;
    private int maelstrom;
    private float bleedChance;

    public HitComponent(Weapon weaponData){
        if (weaponData == null){
            reset();
            return;
        }
        hitDamage = weaponData.getBaseDamage();
        setDps();
    }

    @Override
    public void reset(){
        hitDamage = new HashMap<>();
        hitDamage.put(DamageTypes.PHYSICAL, new int[]{1,1});
        DPS = 1;
    }

    //For mobs
    public HitComponent(int[] phys, int[] fire, int[] light, int[] cold, int[] abyss, float critChance, int critDamage, int shred, int maelstrom, float bleedChance){
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
    public float getDPS(){return DPS;}
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

    public void applyPercentDamage(DamageTypes damageType, int genericPercentAdded){
        if (hitDamage.containsKey(damageType)){
            int[] damageValues = hitDamage.get(damageType).clone();
            damageValues[0] = (int) (damageValues[0] * (1 + genericPercentAdded/100f ));
            damageValues[1] = (int) (damageValues[1] * (1 + genericPercentAdded/100f ));

            hitDamage.put(damageType, damageValues);
        }
    }
    public void addFlatDamage(DamageTypes type, int[] damageSumVar){
        if (hitDamage.containsKey(type)){
            int[] weaponDmg = hitDamage.get(type);
            damageSumVar[0] += weaponDmg[0];
            damageSumVar[1] += weaponDmg[1];
            hitDamage.put(type,damageSumVar);
        } else {
            hitDamage.put(type, damageSumVar);
        }
    }

    @Override
    public void update(Profile profileData) {
        //No update() routine for HitComponent yet
    }

    public float getAccuracy() {
        return accuracy;
    }
    public void setFinalAccuracy(int percentAccuracy){
        this.accuracy = (accuracy * (1 + (percentAccuracy/100F) ) );
    }
    public void addBaseAccuracy(float accuracy) {
        this.accuracy += accuracy;
    }

    public float getCritChance() {
        return critChance;
    }
    public void setFinalCritChance(int percentCritChance) {
        this.critChance = (baseCrit * (1 + (percentCritChance/100F) ) );
    }

    public int getCritDamage() {
        return critDamage;
    }

    public void addBaseCritDamage(int critDamage) {
        this.critDamage += critDamage;
    }

    public int getShred() {
        return shred;
    }

    public void addBaseShred(int shred) {
        this.shred += shred;
    }

    public int getMaelstrom() {
        return maelstrom;
    }

    public void addBaseMaelstrom(int maelstrom) {
        this.maelstrom += maelstrom;
    }

    public float getBleedChance() {
        return bleedChance;
    }

    public void addBaseBleedChance(float bleedChance) {
        this.bleedChance += bleedChance;
    }
}
