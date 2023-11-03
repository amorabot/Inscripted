package com.amorabot.inscripted.components.Player;

import com.amorabot.inscripted.components.DamageComponent;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.HitComponent;
import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Armor.Armor;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.*;
import com.amorabot.inscripted.components.Items.DataStructures.ModifierIDs;
import com.amorabot.inscripted.components.Items.DataStructures.Modifier;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


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
        attributes.reset();
        defences.reset();
        health.reset();
        miscellaneous.reset();
        damage.reset(stats.getWeaponSlot()); //No problem if null

        Map<String, int[]> compiledStats = new HashMap<>();

        int healthSum = 0;

        int wardSum = 0;
        int armorSum = 0;
        int dodgeSum = 0;
        //Compiling Armor data
        Armor[] equippedArmorSet = stats.getArmorSet();
        for (Armor equippedArmor : equippedArmorSet){
            if (equippedArmor == null){
                continue;
            }
            Utils.log(equippedArmor.getName());
            /*
            After they are generated,
            SINGLE_VALUES stay unchanged, still a standalone int
            SINGLE_RANGE values become a Single value ===>  [12, 35] -> 27
            DOUBLE_RANGE becomes a SINGLE_RANGE array ===>  [12,35,  40,56] -> [20, 46]
            And so on... (In case more complex mods are added)
            So when accessing these MAPPED/ALREADY GENERATED values, assume this structure
            */

            //Redirect base stats
            healthSum += equippedArmor.getBaseHealth();
            Map<DefenceTypes, Integer> defences = equippedArmor.getDefencesMap();
            if (defences.containsKey(DefenceTypes.WARD)){wardSum += defences.get(DefenceTypes.WARD);}
            if (defences.containsKey(DefenceTypes.ARMOR)){armorSum += defences.get(DefenceTypes.ARMOR);}
            if (defences.containsKey(DefenceTypes.DODGE)){dodgeSum += defences.get(DefenceTypes.DODGE);}
            //....

            compileItem(compiledStats, equippedArmor);
        }
        updateModValue(compiledStats, ModifierIDs.HEALTH.getModifierKey(), new int[]{healthSum});

        updateModValue(compiledStats, ModifierIDs.WARD.getModifierKey(), new int[]{wardSum});
        updateModValue(compiledStats, ModifierIDs.ARMOR.getModifierKey(), new int[]{armorSum});
        updateModValue(compiledStats, ModifierIDs.DODGE.getModifierKey(), new int[]{dodgeSum});

        //Compiling Weapon data
        if (hasWeaponEquipped()){
            Weapon equippedWeapon = stats.getWeaponSlot();

            Utils.log(equippedWeapon.getName());

            compileItem(compiledStats, equippedWeapon);
        }

        Utils.log("-----START-----");
        for (Map.Entry<String, int[]> stat : compiledStats.entrySet()){
            int[] values = stat.getValue();
            Utils.log(stat.getKey());
            if (values.length == 1){
                Utils.log("Value: "+values[0]);
            } else if (values.length == 2){
                Utils.log("Value: "+values[0] + " / " + values[1]);
            } else if (values.length == 4){
                Utils.log("Value1: "+values[0] + " / " + values[1]);
                Utils.log("Value2: "+values[2] + " / " + values[3]);
            }
        }
        Utils.log("----------------------");

        //Lets see what stats were compiled through the compiledStats map's keySet:
        for (String modKey : compiledStats.keySet()){
            if (!compiledStats.containsKey(modKey)){
                continue;
            }
            //Tokenization
            //Can be expanded with more complex regex.
            //For now, assumes a standard naming for more complex hybrid mods (See ModifierIDs class for more info)
            String[] modTokens = modKey.split("_");
            String typeToken = modTokens[modTokens.length-1]; //The last string should be the "valueType" key.
            int tokens = modTokens.length;
            //Tokenization explanation:

            //...
            try {
                switch (tokens){
                    case 2:
                        //Non-Hybrid Mods
                        String modToken = modTokens[0];
                        String methodKey = "set"+modToken;
                        Method method = this.getClass().getDeclaredMethod(methodKey, String.class,int[].class);
                        Utils.log("Temos o método set" + modToken + "!!!");
                        method.invoke(this, typeToken, (Object) compiledStats.get(modKey));
                        break;
                    case 3:
                        Utils.log("Hybrid mod");
                        //Non-Hybrid Mods
                        //1st mod token
                        String mod1Token = modTokens[0];
                        //2nd mod token
                        String mod2Token = modTokens[1];

                        //Target methods of each stat
                        String method1Key = "set"+mod1Token;
                        String method2Key = "set"+mod2Token;

                        Method method1 = this.getClass().getDeclaredMethod(method1Key, String.class,int[].class);
                        Method method2 = this.getClass().getDeclaredMethod(method2Key, String.class,int[].class);

                        Utils.log("Temos o método set " + mod1Token + " e " + mod2Token + "!!!");
                        //The hybrid mod structure assumes a mod like:
                        //DEX_INT_FLAT -> [13, 15]. That is, a single int for each value
                        //In this example, 13 FLAT DEX and 15 FLAT INT
                        method1.invoke(this, typeToken, (Object) new int[]{compiledStats.get(modKey)[0]});
                        method2.invoke(this, typeToken, (Object) new int[]{compiledStats.get(modKey)[1]});
                        break;
                    default:
                        Utils.log("Token array size not handled: " + tokens);
                        break;
                }
            } catch (NoSuchMethodException exception){
                Utils.log("No 'Set' method for " + modKey);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        attributes.update(this);
        defences.update(this);
        health.update(this);
        miscellaneous.update(this);
        damage.update(this);
    }

    private void compileItem(Map<String, int[]> compiledStats, Item equippedItem){
        //Compiling mods
        List<Modifier> armorMods = equippedItem.getModifiers();
        for (Modifier mod : armorMods){
            //Ignore local damage mods for weapons
            Set<ModifierIDs> ignoredWeaponMods = Set.of(
                      ModifierIDs.ADDED_PHYSICAL
                    , ModifierIDs.ADDED_FIRE
                    , ModifierIDs.ADDED_LIGHTNING
                    , ModifierIDs.ADDED_COLD
                    , ModifierIDs.ADDED_ABYSSAL
                    , ModifierIDs.PERCENT_PHYSICAL);
            if ((equippedItem instanceof Weapon) && (ignoredWeaponMods.contains(mod.getModifierID())) ){continue;}

            ModifierIDs modifierID = mod.getModifierID();
            compileStat(compiledStats, modifierID, mod.getValue().clone());
        }

        //Compiling implicit
        Implicits implicitID = equippedItem.getImplicit();
        compileStat(compiledStats, implicitID, implicitID.getValue().clone());
    }

    private void compileStat(Map<String, int[]> compiledStats, ModifierIDs modID, int[] values){
        String modKey = modID.getModifierKey();
        updateModValue(compiledStats, modKey, values);
    }
    //TODO: Merge both methods
    private void compileStat(Map<String, int[]> compiledStats, Implicits implicitID, int[] values){
        String modKey = implicitID.getModifierKey();
        updateModValue(compiledStats, modKey, values);
    }

    private void updateModValue(Map<String, int[]> compiledStats, String modKey, int[] value){
        if (compiledStats.containsKey(modKey)){
            int[] previousValue = compiledStats.get(modKey).clone();
            for (int i = 0; i < previousValue.length; i++){
                value[i] += previousValue[i];
            }
            compiledStats.put(modKey, value);
        } else {
            compiledStats.put(modKey, value);
        }
    }

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

    /*
    Compiler methods use the "add" method for each stat because that stat can, hipotetically,
    come from all different sources. If that is the case, the previous stat would be
    overriden and not accumulated, as it should

    Apart from using the add method, at the end of the compilation step all components
    should be updated to signal this. The update() method does all final calculations
    and cross-stat modifications needed for each component.

    The compilation process, basically, is:

    Reset all player components -> Compile all stats -> Fill all the fresh components -> Update all
    */
    private void setHEALTH(String typeToken, int[] healthValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case FLAT -> health.addBaseHealth(healthValue[0]);
            case ADDED -> health.addIncreasedHealth(healthValue[0]);
            //No multiplier mods for HEALTH
            default -> Utils.error("TypeToken not handled: " + typeToken + "in HEALTH setter");
        }
    }
    private void setHEALTHREGEN(String typeToken, int[] healthRegenValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case FLAT -> health.addBaseHealthRegen(healthRegenValue[0]);
            //No %Added health regen mods
            //No multiplier mods for HEALTHREGEN
            default -> Utils.error("TypeToken not handled: " + typeToken + "in HEALTHREGEN setter");
        }
    }
    private void setWARD(String typeToken, int[] wardValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case FLAT -> health.addBaseWard(wardValue[0]);
            case ADDED -> health.addIncreasedWard(wardValue[0]);
            //No multiplier mods for WARD
            default -> Utils.error("TypeToken not handled: " + typeToken + "in WARD setter");
        }
    }
    private void setSTRENGTH(String typeToken, int[] strengthValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case FLAT -> attributes.addBaseStrength(strengthValue[0]);
            case MULTI -> attributes.addBaseStrMulti(strengthValue[0]);
            default -> Utils.error("TypeToken not handled: " + typeToken + "in STRENGTH setter");
        }
    }
    private void setDEXTERITY(String typeToken, int[] dexterityValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case FLAT -> attributes.addBaseDexterity(dexterityValue[0]);
            case MULTI -> attributes.addBaseDexMulti(dexterityValue[0]);
            default -> Utils.error("TypeToken not handled: " + typeToken + "in DEXTERITY setter");
        }
    }
    private void setINTELLIGENCE(String typeToken, int[] intelligenceValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case FLAT -> attributes.addBaseIntelligence(intelligenceValue[0]);
            case MULTI -> attributes.addBaseIntMulti(intelligenceValue[0]);
            default -> Utils.error("TypeToken not handled: " + typeToken + "in INTELLIGENCE setter");
        }
    }
    private void setDODGE(String typeToken, int[] dodgeValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case FLAT -> defences.addBaseDodge(dodgeValue[0]);
            default -> Utils.error("TypeToken not handled: " + typeToken + "in DODGE setter");
        }
    }
    private void setARMOR(String typeToken, int[] armorValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case FLAT -> defences.addBaseArmor(armorValue[0]);
            case ADDED -> defences.addBaseIncreasedArmor(armorValue[0]);
            default -> Utils.error("TypeToken not handled: " + typeToken + "in ARMOR setter");
        }
    }
    private void setFIRERESISTANCE(String typeToken, int[] fireResValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case PERCENT -> defences.addBaseFireRes(fireResValue[0]);
            default -> Utils.error("TypeToken not handled: " + typeToken + "in FIRE RESISTANCE setter");
        }
    }
    private void setLIGHTNINGRESISTANCE(String typeToken, int[] lightResValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case PERCENT -> defences.addBaseLightningResistance(lightResValue[0]);
            default -> Utils.error("TypeToken not handled: " + typeToken + "in LIGHTNING RESISTANCE setter");
        }
    }
    private void setCOLDRESISTANCE(String typeToken, int[] coldResValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case PERCENT -> defences.addBaseColdResistance(coldResValue[0]);
            default -> Utils.error("TypeToken not handled: " + typeToken + "in COLD RESISTANCE setter");
        }
    }
    private void setABYSSALRESISTANCE(String typeToken, int[] abyssResValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case PERCENT -> defences.addBaseAbyssalResistance(abyssResValue[0]);
            default -> Utils.error("TypeToken not handled: " + typeToken + "in ABYSS RESISTANCE setter");
        }
    }
    private void setSTAMINA(String typeToken, int[] staminaValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case FLAT -> miscellaneous.addBaseStamina(staminaValue[0]);
            case ADDED -> miscellaneous.addBasePercentStamina(staminaValue[0]);
            default -> Utils.error("TypeToken not handled: " + typeToken + "in STAMINA setter");
        }
    }
    private void setSTAMINAREGEN(String typeToken, int[] staminaRegenValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case FLAT -> miscellaneous.addBaseStaminaRegen(staminaRegenValue[0]);
            case ADDED -> miscellaneous.addBasePercentStaminaRegen(staminaRegenValue[0]);
            default -> Utils.error("TypeToken not handled: " + typeToken + "in STAMINA REGEN setter");
        }
    }
    private void setWALKSPEED(String typeToken, int[] walkSpeedValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case FLAT -> miscellaneous.addBaseWalkSpeed(walkSpeedValue[0]);
            case ADDED -> miscellaneous.addBasePercentWalkSpeed(walkSpeedValue[0]);
            default -> Utils.error("TypeToken not handled: " + typeToken + "in WALK SPEED setter");
        }
    }
    private void setLIFEONHIT(String typeToken, int[] lifeOnHitValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case FLAT -> damage.addBaseLifeOnHit(lifeOnHitValue[0]);
            default -> Utils.error("TypeToken not handled: " + typeToken + "in LIFE ON HIT setter");
        }
    }
    private void setSHRED(String typeToken, int[] shredValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case PERCENT -> damage.getHitData().addBaseShred(shredValue[0]);
            default -> Utils.error("TypeToken not handled: " + typeToken + "in SHRED setter");
        }
    }
    private void setMAELSTROM(String typeToken, int[] maelstromValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case PERCENT -> damage.getHitData().addBaseMaelstrom(maelstromValue[0]);
            default -> Utils.error("TypeToken not handled: " + typeToken + "in MAELSTROM setter");
        }
    }
    private void setBLEED(String typeToken, int[] bleedValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case PERCENT -> damage.getHitData().addBaseBleedChance(bleedValue[0]);
            default -> Utils.error("TypeToken not handled: " + typeToken + "in BLEED CHANCE setter");
        }
    }
    private void setPHYSICALDAMAGE(String typeToken, int[] physicalValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case FLAT -> damage.getHitData().addFlatDamage(DamageTypes.PHYSICAL,physicalValue);
            case ADDED -> damage.addBaseIncreasedPhysicalDamage(physicalValue[0]);
            default -> Utils.error("TypeToken not handled: " + typeToken + "in PHYS DAMAGE setter");
        }
    }
    private void setFIREDAMAGE(String typeToken, int[] fireValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case FLAT -> damage.getHitData().addFlatDamage(DamageTypes.FIRE,fireValue);
            case ADDED -> damage.addBaseIncreasedFireDamage(fireValue[0]);
            //MULTI mapping shoud be added soon
            default -> Utils.error("TypeToken not handled: " + typeToken + "in FIRE DAMAGE setter");
        }
    }
    private void setLIGHTNINGDAMAGE(String typeToken, int[] lightningValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case FLAT -> damage.getHitData().addFlatDamage(DamageTypes.LIGHTNING,lightningValue);
            case ADDED -> damage.addBaseIncreasedLightningDamage(lightningValue[0]);
            //MULTI mapping shoud be added soon
            default -> Utils.error("TypeToken not handled: " + typeToken + "in LIGHTNING DAMAGE setter");
        }
    }
    private void setCOLDDAMAGE(String typeToken, int[] coldValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case FLAT -> damage.getHitData().addFlatDamage(DamageTypes.COLD,coldValue);
            case ADDED -> damage.addBaseIncreasedColdDamage(coldValue[0]);
            //MULTI mapping shoud be added soon
            default -> Utils.error("TypeToken not handled: " + typeToken + "in COLD DAMAGE setter");
        }
    }
    private void setABYSSALDAMAGE(String typeToken, int[] abyssValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case FLAT -> damage.getHitData().addFlatDamage(DamageTypes.ABYSSAL,abyssValue);
            case ADDED -> damage.addBaseIncreasedAbyssalDamage(abyssValue[0]);
            //MULTI mapping shoud be added soon
            default -> Utils.error("TypeToken not handled: " + typeToken + "in ABYSSAL DAMAGE setter");
        }
    }
    private void setELEMENTALDAMAGE(String typeToken, int[] elementalValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case ADDED -> damage.addBaseIncreasedElementalDamage(elementalValue[0]);
            default -> Utils.error("TypeToken not handled: " + typeToken + "in ELE DAMAGE setter");
        }
    }
    private void setACCURACY(String typeToken, int[] accuracyValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case FLAT -> damage.getHitData().addBaseAccuracy(accuracyValue[0]);
            case ADDED -> damage.addBaseIncreasedAccuracy(accuracyValue[0]);
            default -> Utils.error("TypeToken not handled: " + typeToken + "in ACCURACY setter");
        }
    }
    private void setCRITICALCHANCE(String typeToken, int[] critChanceValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            //a FLAT mapping could be extra base crit chance!
            case ADDED -> damage.addBaseIncreasedCritChance(critChanceValue[0]);
            default -> Utils.error("TypeToken not handled: " + typeToken + "in CRIT CHANCE setter");
        }
    }
    private void setCRITICALDAMAGE(String typeToken, int[] critDamageValue){
        ValueTypes type = mapValueType(typeToken);
        if (type == null){return;}
        switch (type){
            case ADDED -> damage.getHitData().addBaseCritDamage(critDamageValue[0]);
            default -> Utils.error("TypeToken not handled: " + typeToken + "in CRIT DAMAGE setter");
        }
    }


    //Null if invalid token, ValueType Enum entry if valid token
    private ValueTypes mapValueType(String valueTypeString){
        ValueTypes type = null;
        try {
            type = ValueTypes.valueOf(valueTypeString);
        } catch (IllegalArgumentException exception){
            Utils.log("Invalid typeToken (mapValueType function for "+ valueTypeString +")");
        }
        return type;
    }
}
