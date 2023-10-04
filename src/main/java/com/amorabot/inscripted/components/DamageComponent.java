package com.amorabot.inscripted.components;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.components.Items.Interfaces.EntityComponent;
import com.amorabot.inscripted.components.Player.Profile;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class DamageComponent implements EntityComponent {
    private float DPS;
    private Map<DamageTypes, int[]> damage;
    private int stamina;
    private float staminaRegen;
    private float accuracy;
    private float critChance;
    private int critDamage;
    private int shred;
    private int maelstrom;
    private float bleedChance;
    private float elementalDamage;
    private int lifeOnHit;
    //fire light cold damages

    public DamageComponent(){
        this.damage = new HashMap<>();
        setDps();
    }

    public DamageComponent(int lowerPhys, int upperPhys){
        this.damage = new HashMap<>();
        damage.put(DamageTypes.PHYSICAL, new int[]{lowerPhys, upperPhys});
        setDps();
    }

    public Map<DamageTypes, int[]> getDamage() {
        return damage;
    }
    private void setDps(){
        if (damage.isEmpty()){
            this.DPS = 1;
            return;
        }
        int lowerDamage = 0;
        int upperDamage = 0;
        for (DamageTypes dmgType : damage.keySet()){
            if (damage.containsKey(dmgType)){
                int[] dmg = damage.get(dmgType);
                lowerDamage += dmg[0];
                upperDamage += dmg[1];
            }
        }
        DPS = (float) (lowerDamage + upperDamage)/2;
    }
    public float getDPS(){
        return DPS;
    }
    @Override
    public void update(Profile profileData) {
    }
    public static void applyAddedPercentageToDamageMap(Map<DamageTypes, int[]> damages, DamageTypes damageType, int genericPercentAdded){
        if (damages.containsKey(damageType)){
            int[] damageValues = damages.get(damageType);
            damageValues[0] = (int) (damageValues[0] * (1 + genericPercentAdded/100f ));
            damageValues[1] = (int) (damageValues[1] * (1 + genericPercentAdded/100f ));

            damages.put(damageType, damageValues);
        }
    }
    public static void sumWeaponFlatDamage(Map<DamageTypes, int[]> weaponDamages, DamageTypes type, int[] damageSumVar){
        if (weaponDamages.containsKey(type)){
            int[] weaponDmg = weaponDamages.get(type);
            damageSumVar[0] += weaponDmg[0];
            damageSumVar[1] += weaponDmg[1];
            weaponDamages.put(type,damageSumVar);
        } else {
            weaponDamages.put(type, damageSumVar);
        }
    }

    public void update(Map<DamageTypes, int[]> weaponDamages,
                       int totalStamina,
                       int percentStamina,
                       int staminaRegen,
                       int percentStaminaRegen,
                       int baseCrit,
                       int critChance,
                       int critDamage,
                       int totalAccuracy,
                       int percentAccuracy,
                       int bleedChance,
                       int shred,
                       int maelstrom,
                       int elementalDamage,
                       int lifeOnHit){

        this.stamina = 100 + (int) (totalStamina * (1 + (percentStamina/100f)));
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String staminaRegenToFixed = decimalFormat.format(staminaRegen * (1 + (percentStaminaRegen/100f)));
        this.staminaRegen = Float.parseFloat(staminaRegenToFixed);
        this.critChance = baseCrit * (1 + (critChance/100f));
        this.critDamage = critDamage;
        this.accuracy = totalAccuracy * (1 + (percentAccuracy/100f) );
        this.bleedChance = bleedChance;
        this.shred = shred;
        this.maelstrom = maelstrom;
        this.elementalDamage = elementalDamage;
        this.lifeOnHit = lifeOnHit;

        this.damage = weaponDamages;
        setDps();
    }

    public int getStamina() {
        return stamina;
    }

    public float getStaminaRegen() {
        return staminaRegen;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public float getCritChance() {
        return critChance;
    }

    public int getCritDamage() {
        return critDamage;
    }

    public int getShred() {
        return shred;
    }

    public int getMaelstrom() {
        return maelstrom;
    }

    public float getBleedChance() {
        return bleedChance;
    }

    public float getElementalDamage() {
        return elementalDamage;
    }

    public int getLifeOnHit() {
        return lifeOnHit;
    }
}
