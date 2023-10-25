package com.amorabot.inscripted.components.Player;

import com.amorabot.inscripted.components.DamageComponent;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.HitComponent;
import com.amorabot.inscripted.components.Items.Armor.Armor;
//import com.amorabot.rpgelements.components.Items.Armor.ArmorModifiers;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.*;
//import com.amorabot.rpgelements.components.Items.DataStructures.Modifier;
import com.amorabot.inscripted.components.Items.DataStructures.ModifierList;
import com.amorabot.inscripted.components.Items.DataStructures.NewModifier;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.utils.Utils;
//import com.amorabot.rpgelements.components.Items.Weapon.WeaponModifiers;

import java.util.HashMap;
import java.util.Map;

//import static com.amorabot.inscripted.components.DamageComponent.applyPercentDamage;
//import static com.amorabot.inscripted.components.DamageComponent.sumWeaponFlatDamage;

public class Profile {
    private HealthComponent health;
    private DefenceComponent defences;
    private DamageComponent damage;
    private Attributes attributes;
    private Miscellaneous miscellaneous;
    private Stats stats;
    public Profile(Attributes attributes, Stats stats){
        this.attributes = attributes;
        this.stats = stats;
    }
    public Profile(HealthComponent hp, DefenceComponent def, DamageComponent dmg, Attributes att, Stats stats){
        this.health = hp;
        this.defences = def;
        this.damage = dmg;
        this.attributes = att;
        this.miscellaneous = new Miscellaneous();
        this.stats = stats;
    }
    public Attributes getAttributes(){
        return this.attributes;
    }
    public void setAttributes(Attributes attributes){
        this.attributes = attributes;
    }

    public Miscellaneous getMiscellaneous() {
        return miscellaneous;
    }
    public void setMiscellaneous(Miscellaneous miscellaneous) {
        this.miscellaneous = miscellaneous;
    }

    public Stats getStats() {
        return this.stats;
    }
    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public HealthComponent getHealthComponent(){
        return this.health;
    }
    public DefenceComponent getDefenceComponent(){
        return this.defences;
    }
    public DamageComponent getDamageComponent(){
        return this.damage;
    }
    private void updateProfile(){

        int strSum = 0;
        int dexSum = 0;
        int intSum = 0;

        int strMultiSum = 0;
        int dexMultiSum = 0;
        int intMultiSum = 0;

        int flatStaminaSum = 0;
        int percentStaminaSum = 0;

        int flatHealthSum = 0;
        int percentHealthSum = 0;
        int healthRegenSum = 0;
//        int percenthealthRegenSum = 0;

        int flatWardSum = 0;
        int percentWardSum = 0;

        int flatArmorSum = 0;
        int percentArmorSum = 0;

        int flatDodgeSum = 0;

        int walkSpeedSum = 0; //MISC

        int coldResSum = 0;
        int fireResSum = 0;
        int lightResSum = 0;
        int abyssResSum = 0;



        Map<DamageTypes, int[]> damageMap = new HashMap<>();
//        int baseStaminaRegen = 2;
        int staminaRegenPercentSum = 0;

        int accuracySum = 0;
        int percentAccuracySum = 0;

//        int baseCrit = 1;
        int percentCritChanceSum = 0;
        int percentCritDamageSum = 0;

        int bleedSum = 0;

        int shredSum = 0;
        int maelstromSum = 0;

        int percentElementalDamageSum = 0;

        int lifeOnHitSum = 0;

        int[] addedPhys = new int[2];
        int percentPhys = 0;

        int[] addedFire = new int[2];
        int percentFire = 0;

        int[] addedLightning = new int[2];
        int percentLightning = 0;

        int[] addedCold = new int[2];
        int percentCold = 0;

        int[] addedAbyssal = new int[2];


        //Getting stats from armor
        for (Armor armorPiece : getStats().getArmorSet()){
            if (armorPiece == null){
                continue;
            }
            //Adding the armors base health
            flatHealthSum += armorPiece.getBaseHealth();

            //Mapping baseStats
            Map<DefenceTypes, Integer> defenceMap = armorPiece.getDefencesMap();
            for (DefenceTypes def : defenceMap.keySet()){
                switch (def){
                    case WARD -> flatWardSum += defenceMap.get(DefenceTypes.WARD);
                    case ARMOR -> flatArmorSum += defenceMap.get(DefenceTypes.ARMOR);
                    case DODGE -> flatDodgeSum += defenceMap.get(DefenceTypes.DODGE);
                }
            }

            //Mapping implicit
            Implicits armorImplicit = armorPiece.getImplicit();
            int[] armorImplicitValue = armorImplicit.getValue();
            switch (armorImplicit.getTargetStat()){
                case STRENGTH -> {
                    int addedAttribute = armorImplicitValue[0];
                    strSum += addedAttribute;
                }
                case DEXTERITY -> {
                    int addedAttribute = armorImplicitValue[0];
                    dexSum += addedAttribute;
                }
                case INTELLIGENCE -> {
                    int addedAttribute = armorImplicitValue[0];
                    intSum += addedAttribute;
                }
                case DEXTERITY_INTELLIGENCE -> {
                    //Only available in implicits for now
                    //Only flat-hybrid attributes (in case of attributes, both share the same value
                    int addedAttribute = armorImplicitValue[0];
                    dexSum += addedAttribute;
                    intSum += addedAttribute;
                }
                case STRENGTH_DEXTERITY -> {
                    int addedAttribute = armorImplicitValue[0];
                    dexSum += addedAttribute;
                    strSum += addedAttribute;
                }
                case INTELLIGENCE_STRENGTH -> {
                    int addedAttribute = armorImplicitValue[0];
                    strSum += addedAttribute;
                    intSum += addedAttribute;
                }
            }

            //Mapping modifiers to stats
//            for (Modifier<ArmorModifiers> armorMod : armorPiece.getModifiers()){
            for (NewModifier armorMod : armorPiece.getModifiers()){
                //TODO: Encapsular o somatório nas variáveis
                ModifierList mod = armorMod.getModifier();
                switch (mod.getTargetStat()){
                    case HEALTH -> {
                        switch (mod.getValueType()){
                            case FLAT -> {
                                flatHealthSum += armorMod.getValue()[0];
                            }
                            case PERCENT_ADDED -> {
                                percentHealthSum += armorMod.getValue()[0];
                            }
                            case PERCENT_MULTI -> {
                                //Not available yet
                            }
                        }
                    }
                    case HEALTH_REGEN -> {
                        //Only flat life regen
                        healthRegenSum += armorMod.getValue()[0];
                    }
                    case STAMINA -> {
                        if (mod.getValueType() == ValueTypes.FLAT){
                            flatStaminaSum += armorMod.getValue()[0];
                        } else {
                            //ValueTypes.PERCENT_ADDED
                            percentStaminaSum += armorMod.getValue()[0];
                        }
                    }
                    case STRENGTH -> {
                        if (mod.getValueType() == ValueTypes.FLAT){
                            strSum += armorMod.getValue()[0];
                        } else {
                            //ValueTypes.PERCENT_MULTI
                            strMultiSum += armorMod.getValue()[0];
                        }
                    }
                    case DEXTERITY -> {
                        if (mod.getValueType() == ValueTypes.FLAT){
                            dexSum += armorMod.getValue()[0];
                        } else {
                            //ValueTypes.PERCENT_MULTI
                            dexMultiSum += armorMod.getValue()[0];
                        }
                    }
                    case INTELLIGENCE -> {
                        if (mod.getValueType() == ValueTypes.FLAT){
                            intSum += armorMod.getValue()[0];
                        } else {
                            //ValueTypes.PERCENT_MULTI
                            intMultiSum += armorMod.getValue()[0];
                        }
                    }
                    case WARD -> {
                        if (mod.getValueType() == ValueTypes.FLAT){
                            flatWardSum += armorMod.getValue()[0];
                        } else {//PERCENT_ADDED
                            percentWardSum += armorMod.getValue()[0];
                        }
                    }
                    case ARMOR -> {
                        if (mod.getValueType() == ValueTypes.FLAT){
                            flatArmorSum+= armorMod.getValue()[0];
                        } else {//PERCENT_ADDED
                            percentArmorSum += armorMod.getValue()[0];
                        }
                    }
                    //Hybrid mod section => double range (2 single ranges)
                    case ARMOR_HEALTH -> {
                        if (mod.getValueType().equals(ValueTypes.FLAT)){
                            int[] modValues = armorMod.getValue();
                            flatArmorSum += modValues[0];
                            flatHealthSum += modValues[1];
                        }
                    }
                    case ARMOR_DODGE -> {
                        if (mod.getValueType().equals(ValueTypes.FLAT)){
                            int[] modValues = armorMod.getValue();
                            flatArmorSum += modValues[0];
                            flatDodgeSum += modValues[1];
                        }
                    }
                    case ARMOR_WARD -> {
                        if (mod.getValueType().equals(ValueTypes.FLAT)){
                            int[] modValues = armorMod.getValue();
                            flatArmorSum += modValues[0];
                            flatWardSum += modValues[1];
                        }
                    }
                    case WARD_HEALTH -> {
                        if (mod.getValueType().equals(ValueTypes.FLAT)){
                            int[] modValues = armorMod.getValue();
                            flatWardSum += modValues[0];
                            flatHealthSum += modValues[1];
                        }
                    }
                    case DODGE_HEALTH -> {
                        if (mod.getValueType().equals(ValueTypes.FLAT)){
                            int[] modValues = armorMod.getValue();
                            flatDodgeSum += modValues[0];
                            flatHealthSum += modValues[1];
                        }
                    }
                    case DODGE_WARD -> {
                        if (mod.getValueType().equals(ValueTypes.FLAT)){
                            int[] modValues = armorMod.getValue();
                            flatDodgeSum += modValues[0];
                            flatWardSum += modValues[1];
                        }
                    }
                    //----------------------------------------------------
                    case DODGE -> {
                        flatDodgeSum+= armorMod.getValue()[0];
                    }
                    case WALK_SPEED -> {
                        walkSpeedSum += armorMod.getValue()[0];
                    }
                    case COLD_RESISTANCE -> {
                        coldResSum += armorMod.getValue()[0];
                    }
                    case FIRE_RESISTANCE -> {
                        fireResSum += armorMod.getValue()[0];
                    }
                    case LIGHTNING_RESISTANCE -> {
                        lightResSum += armorMod.getValue()[0];
                    }
                    case ABYSSAL_RESISTANCE -> {
                        abyssResSum += armorMod.getValue()[0];
                    }
                }
            }
        }
        //Getting stats from weapon
        Weapon weaponData = getStats().getWeaponSlot();
        if (weaponData != null){
            //Mapping base stats from weapon

            //Applying weapon implicits
            Implicits weaponImplicit = weaponData.getImplicit();
            switch (weaponImplicit.getTargetStat()){
                case SHRED -> shredSum += weaponImplicit.getValue()[0]; //Theres only % Shred
                case ACCURACY -> {
                    switch (weaponImplicit.getValueType()){
                        case FLAT -> accuracySum += weaponImplicit.getValue()[0];
                        case PERCENT_ADDED -> percentAccuracySum += weaponImplicit.getValue()[0];
                    }
                }
                case DODGE -> flatDodgeSum += weaponImplicit.getValue()[0];
                case CRITICAL_DAMAGE -> percentCritDamageSum += weaponImplicit.getValue()[0]; //Theres only % Crit DMG
                case MAELSTROM -> maelstromSum += weaponImplicit.getValue()[0]; //Theres only % Crit DMG
                case ELEMENTAL_DAMAGE -> percentElementalDamageSum += weaponImplicit.getValue()[0]; //Theres only % Ele DMG
            }
            //Getting stats from weapon modifiers

            //Getting weapon stats
    //            for (Modifier<WeaponModifiers> mod : weaponData.getModifiers()){
            for (NewModifier mod : weaponData.getModifiers()){
                TargetStats targetStat = mod.getModifier().getTargetStat();
                ValueTypes valueType = mod.getModifier().getValueType();
                int[] modValue = mod.getValue();
                switch (targetStat){
                    case STAMINA -> { //Updating Stamina Stats
                        switch (valueType){
                            case FLAT -> flatStaminaSum += modValue[0];
                            case PERCENT_ADDED -> percentStaminaSum += modValue[0];
                        }
                    }
                    case ACCURACY -> { //Updating Accuracy Stats
                        switch (valueType){
                            case FLAT -> accuracySum += modValue[0];
                            case PERCENT_ADDED -> percentAccuracySum += modValue[0];
                        }
                    }
                    case BLEED -> bleedSum += modValue[0]; //There's only %bleed
                    case CRITICAL -> { //Updating Crit. Stats
                        if (valueType == ValueTypes.PERCENT_ADDED) {
                            percentCritChanceSum += modValue[0];
                        }
                    }
                    case SHRED -> shredSum += modValue[0]; //There's only %shred
                    case MAELSTROM -> maelstromSum += modValue[0];
                    case ELEMENTAL_DAMAGE -> percentElementalDamageSum += modValue[0]; //There's only %eleDMG
                    case LIFE_ON_HIT -> lifeOnHitSum += modValue[0];
                    case STRENGTH -> strSum += modValue[0];
                    case DEXTERITY -> dexSum += modValue[0];
                    case INTELLIGENCE -> intSum += modValue[0];
                    case STAMINA_REGEN -> staminaRegenPercentSum += modValue[0];
                    //ADD SPECIFIC ELEMENT MODS (fire light cold)
                    //Add VS MOBS, VS PLAYERS
                }
            }
            Map<DamageTypes, int[]> originalDamageMap = weaponData.getBaseDamage(); //Will be cloned to be modified
            for (DamageTypes dmgType : originalDamageMap.keySet()){ //Turn into a entrySet
                damageMap.put(dmgType, originalDamageMap.get(dmgType));
            }
        }

        //Setting attributes
        getAttributes().updateStats(strSum, strMultiSum, dexSum, dexMultiSum, intSum, intMultiSum);
        flatHealthSum += getAttributes().getStrength()/3;
        flatWardSum += getAttributes().getIntelligence()/5;

        //Setting defences
        getDefenceComponent().update(fireResSum, coldResSum, lightResSum, abyssResSum, flatArmorSum, percentArmorSum, flatDodgeSum);

        //Setting health
        getHealthComponent().updateHealthComponent(flatHealthSum, percentHealthSum, healthRegenSum, flatWardSum, percentWardSum);

        //Setting misc.
        Miscellaneous miscComponent = getMiscellaneous();
        miscComponent.setWalkSpeed(walkSpeedSum);

        miscComponent.setExtraStamina(flatStaminaSum);
        miscComponent.setPercentStamina(percentStaminaSum);

        miscComponent.setExtraStaminaRegen(0); // #NOT IMPLEMENTED#
        miscComponent.setPercentStaminaRegen(staminaRegenPercentSum);

        //Setting the player's damage
        DamageComponent damageComponent = getDamageComponent();
        Utils.log(""+ percentElementalDamageSum);

        damageComponent.reset(weaponData); //No problem if null
        damageComponent.setLifeOnHit(lifeOnHitSum);
        damageComponent.setLifeSteal(0); // #NOT IMPLEMENTED#
        damageComponent.setAreaDamage(0); // #NOT IMPLEMENTED#
        damageComponent.setIncreasedPhysicalDamage(percentPhys);
        damageComponent.setIncreasedElementalDamage(percentElementalDamageSum);
        damageComponent.setIncreasedFireDamage(percentFire);
        damageComponent.setIncreasedLightningDamage(percentLightning);
        damageComponent.setIncreasedColdDamage(percentCold);
        damageComponent.setIncreasedAbyssalDamage(0); // #NOT IMPLEMENTED#

        //Now that the damage component is set, lets setup the hit component
        HitComponent hitComponent = damageComponent.getHitData();
        hitComponent.setAccuracy(accuracySum);
        hitComponent.setCritChance(percentCritChanceSum);
        hitComponent.setCritDamage(percentCritDamageSum);
        hitComponent.setShred(shredSum);
        hitComponent.setMaelstrom(maelstromSum);
        hitComponent.setBleedChance(bleedSum);

        //Adding any flat damage from items, and then amplifying them
        hitComponent.addFlatDamage(DamageTypes.PHYSICAL, addedPhys);
        hitComponent.addFlatDamage(DamageTypes.FIRE, addedFire);
        hitComponent.addFlatDamage(DamageTypes.LIGHTNING, addedLightning);
        hitComponent.addFlatDamage(DamageTypes.COLD, addedCold);
        hitComponent.addFlatDamage(DamageTypes.ABYSSAL, addedAbyssal);

        damageComponent.applyDamageMods();
    }

    //todo: Arrumar triggers de update no profile
    public void updateMainHand(Weapon weapon){
        getStats().setWeaponSlot(weapon);
        updateProfile();
    }
    public void updateArmorSlot(){
        updateProfile();
    }

    public boolean hasWeaponEquipped(){
        return this.stats.getWeaponSlot() != null;
    }

    private void setDamage(DamageComponent damage) {
        this.damage = damage;
    }
}
