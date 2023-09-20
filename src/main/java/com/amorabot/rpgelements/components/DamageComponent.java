package com.amorabot.rpgelements.components;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.Implicit;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.TargetStats;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ValueTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Modifier;
import com.amorabot.rpgelements.components.Items.Interfaces.EntityComponent;
import com.amorabot.rpgelements.components.Items.Weapon.Weapon;
import com.amorabot.rpgelements.components.Items.Weapon.WeaponModifiers;
import com.amorabot.rpgelements.components.Player.Profile;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        int percentCritChance = 0;
        int percentCritDamage = 0;

        int bleedSum = 0;

        int shredSum = 0;
        int maelstromSum = 0;

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

        /* If the weapon slot is null and an update was sent, it means the weapon was unequiped
           In this case, all other stats are still compiled and the weapon, ignored */
        if (weaponData != null){
            //Applying weapon implicits
            Implicit weaponImplicit = weaponData.getImplicit();
            switch (weaponImplicit.getTargetStat()){
                case SHRED -> shredSum += weaponImplicit.getValue()[0]; //Theres only % Shred
                case ACCURACY -> {
                    switch (weaponImplicit.getValueType()){
                        case FLAT -> accuracySum += weaponImplicit.getValue()[0];
                        case PERCENT_ADDED -> percentAccuracy += weaponImplicit.getValue()[0];
                    }
                }
//              case DODGE -> {de-serialize dodge info in defenceComponent}
                case CRITICAL_DAMAGE -> percentCritDamage += weaponImplicit.getValue()[0]; //Theres only % Crit DMG
                case MAELSTROM -> maelstromSum += weaponImplicit.getValue()[0]; //Theres only % Crit DMG
                case ELEMENTAL_DAMAGE -> percentElemental += weaponImplicit.getValue()[0]; //Theres only % Ele DMG
            }

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
                    case STAMINA -> { //Updating Stamina Stats
                        switch (valueType){
                            case FLAT -> staminaSum += modValue[0];
                            case PERCENT_ADDED -> staminaRegenPercent += modValue[0];
                        }
                    }
                    case ACCURACY -> { //Updating Accuracy Stats
                        switch (valueType){
                            case FLAT -> accuracySum += modValue[0];
                            case PERCENT_ADDED -> percentAccuracy += modValue[0];
                        }
                    }
                    case BLEED -> bleedSum += modValue[0]; //There's only %bleed
                    case CRITICAL -> { //Updating Crit. Stats
                        if (valueType == ValueTypes.PERCENT_ADDED) {
                            percentCritChance += modValue[0];
                        }
                    }
                    case SHRED -> shredSum += modValue[0]; //There's only %shred
                    case ELEMENTAL_DAMAGE -> percentElemental += modValue[0]; //There's only %eleDMG
                    case LIFE_ON_HIT -> lifeOnHit += modValue[0];
                    //ADD SPECIFIC ELEMENT MODS (fire light cold)
                    //Add VS MOBS, VS PLAYERS
                }
            }
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
        //Getting armor/trinket stats...
        //...
//        DecimalFormat formatter = new DecimalFormat("#.##"); //Will format numbers for display (STRING)
        this.stamina = 100 + staminaSum;
        this.staminaRegen = baseStaminaRegen * (1 + (staminaRegenPercent/100f));
        this.critChance = baseCrit * (1 + (percentCritChance/100f));
        this.critDamage = percentCritDamage;
        this.accuracy = accuracySum * (1 + (percentAccuracy/100f) );
        this.bleedChance = bleedSum;
        this.shred = shredSum;
        this.maelstrom = maelstromSum;
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
