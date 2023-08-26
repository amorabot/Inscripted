package com.amorabot.rpgelements.components.PlayerComponents;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.TargetStats;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ValueTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Modifier;
import com.amorabot.rpgelements.components.Items.Interfaces.PlayerComponent;
import com.amorabot.rpgelements.components.Items.Weapon.Weapon;
import com.amorabot.rpgelements.components.Items.Weapon.WeaponModifiers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DamageComponent implements PlayerComponent {
    private float DPS;
    private Map<DamageTypes, int[]> damage;
    private int stamina;
    private float staminaRegen;
    private float accuracy;
    private float critChance;
    private float shred;
    private float bleedChance;
    private float elementalDamage;
    private int lifeOnHit;

    public DamageComponent(){
        this.DPS = 1;
    }

    private void setDps(){
        Set<DamageTypes> damageTypes = damage.keySet();
        int lowerDamage = 0;
        int upperDamage = 0;
        for (DamageTypes dmgType : damageTypes){
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
        Weapon weaponData = profileData.getStats().getWeaponSlot();
        Map<DamageTypes, int[]> damageMap = new HashMap<>();
        int staminaSum = 0;
        int baseStaminaRegen = 2;
        int staminaRegenPercent = 2;
        int accuracySum = 0;
        int percentAccuracy = 0;
        int baseCrit = 1;
        int percentCrit = 0;
        int bleedSum = 0;
        int shred = 0;
        int percentElemental = 0;
        int lifeOnHit = 0;

        int[] addedPhys = new int[2];
        int percentPhys = 0;

        int[] addedFire = new int[2];
        int percentFire = 0;

        int[] addedLightning = new int[2];
        int percentLightning = 0;

        int[] addedCold = new int[2];
        int percentCold = 0;

        int[] addedAbyssal = new int[2];

        if (weaponData != null){ //Quando a arma for null e solicitarmos um update, significa que ela foi desequipada, portanto basta re-compilar os status da armadura sozinha
//            damageMap = weaponData.getBaseDamage();
            Map<DamageTypes, int[]> weaponDamages = weaponData.getBaseDamage();
            sumWeaponFlatDamageIfPresent(weaponDamages, DamageTypes.PHYSICAL, addedPhys);
            sumWeaponFlatDamageIfPresent(weaponDamages, DamageTypes.FIRE, addedFire);
            sumWeaponFlatDamageIfPresent(weaponDamages, DamageTypes.COLD, addedCold);
            sumWeaponFlatDamageIfPresent(weaponDamages, DamageTypes.LIGHTNING, addedLightning);
            sumWeaponFlatDamageIfPresent(weaponDamages, DamageTypes.ABYSSAL, addedAbyssal);


            //Getting weapon stats
            for (Modifier<WeaponModifiers> mod : weaponData.getModifiers()){
                TargetStats targetStat = mod.getModifier().getTargetStat();
                ValueTypes valueType = mod.getModifier().getValueType();
                int[] modValue = mod.getValue();
                switch (targetStat){
                    case STAMINA -> {
                        //In case of more diverse stamina stats, add here within a switch()
                        switch (valueType){
                            case FLAT -> staminaSum += modValue[0];
                            case PERCENT_ADDED -> staminaRegenPercent += modValue[0];
                        }
                    }
                    case ACCURACY -> {
                        switch (valueType){
                            case FLAT -> accuracySum += modValue[0];
                            case PERCENT_ADDED -> percentAccuracy += modValue[0];
                        }
                    }
                    case BLEED -> bleedSum += modValue[0]; //There's only %bleed
                    case CRITICAL -> {
                        if (valueType == ValueTypes.PERCENT_ADDED) {
                            percentCrit += modValue[0];
                        }
                    }
                    case SHRED -> shred += modValue[0]; //There's only %shred
                    case ELEMENTAL_DAMAGE -> percentElemental += modValue[0]; //There's only %eleDMG
                    case LIFE_ON_HIT -> lifeOnHit += modValue[0];
                    //ADD SPECIFIC ELEMENT MODS
                }
            }
            //Getting armor/trinket stats...
            //...

            //fire cold and light. are updated individually and then generic elemental is applied to each one
            percentFire += percentElemental;
            percentLightning += percentElemental;
            percentCold += percentElemental;

            updateDamageMap(damageMap, DamageTypes.PHYSICAL, addedPhys, percentPhys);
            updateDamageMap(damageMap, DamageTypes.FIRE, addedFire, percentFire);
            updateDamageMap(damageMap, DamageTypes.LIGHTNING, addedLightning, percentLightning);
            updateDamageMap(damageMap, DamageTypes.COLD, addedCold, percentCold);
            updateDamageMap(damageMap, DamageTypes.ABYSSAL, addedAbyssal, 0);
        }

        this.stamina = 100 + staminaSum;
        this.staminaRegen = baseStaminaRegen * (1 + (staminaRegenPercent/100f));
        this.critChance = baseCrit * (1 + (percentCrit/100f));
        this.accuracy = accuracySum * (1 + (percentAccuracy/100f) );
        this.bleedChance = bleedSum;
        this.shred = shred;
        this.elementalDamage = percentElemental;
        this.lifeOnHit = lifeOnHit;

        this.damage = damageMap;
        setDps();
    }

    private void updateDamageMap(Map<DamageTypes, int[]> damageMap, DamageTypes damageType, int[] addedGenericDamage, int percentGenericDamage){
        int dmg1 = (int) (addedGenericDamage[0] * (1 + percentGenericDamage/100f ));
        int dmg2 = (int) (addedGenericDamage[1] * (1 + percentGenericDamage/100f ));

        damageMap.put(damageType, new int[]{dmg1,dmg2});
    }
    private void sumWeaponFlatDamageIfPresent(Map<DamageTypes, int[]> weaponDamages, DamageTypes type, int[] damageSumVar){
        if (weaponDamages.containsKey(type)){
            int[] weaponDmg = weaponDamages.get(type);
            damageSumVar[0] += weaponDmg[0];
            damageSumVar[1] += weaponDmg[1];
        }
    }
}
