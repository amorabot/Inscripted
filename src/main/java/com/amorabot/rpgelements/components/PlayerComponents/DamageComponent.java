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
    private float accuracy;
//    private float critChance;
    private float bleedChance;

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
        int accuracySum = 0;
        int percentAccuracy = 0;
//        int percentCrit = 0;
        int bleedSum = 0;

//        int[] addedFire = new int[2];
//        int percentFire = 0;

        if (weaponData != null){ //Quando a arma for null e solicitarmos um update, significa que ela foi desequipada, portanto basta re-compilar os status da armadura sozinha
            damageMap = weaponData.getBaseDamage();

            //Getting weapon stats
            for (Modifier<WeaponModifiers> mod : weaponData.getModifiers()){
                TargetStats targetStat = mod.getModifier().getTargetStat();
                ValueTypes valueType = mod.getModifier().getValueType();
                int[] modValue = mod.getValue();
                switch (targetStat){
                    case STAMINA -> {
                        if (valueType == ValueTypes.FLAT) {
                            staminaSum += modValue[0];
                        }
                        //In case of more diverse stamina stats, add here within a switch()
                        continue;
                    }
                    case ACCURACY -> {
                        switch (valueType){
                            case FLAT -> accuracySum += modValue[0];
                            case PERCENT_ADDED -> percentAccuracy += modValue[0];
                        }
                        continue;
                    }
                    case BLEED -> {
                        //There's only %bleed
                        bleedSum += modValue[0];
                        continue;
                    }
                }
                if (damageMap.containsKey(DamageTypes.FIRE)){
//                if (mod.getModifier().getTargetStat() == TargetStats.FIRE_DAMAGE){
//                    //Apply the stat to fire damage
//                    //In case of weapon stats, only check for %fire and % more fire
//                }
                }
            }
        }

        //Getting armor/trinket stats...

        this.damage = damageMap;
        this.stamina = staminaSum;
        this.accuracy = accuracySum * (1 + (float)(percentAccuracy/100) );
        this.bleedChance = bleedSum;
        setDps();
    }
}
