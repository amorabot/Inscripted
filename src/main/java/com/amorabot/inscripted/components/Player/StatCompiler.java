package com.amorabot.inscripted.components.Player;

import com.amorabot.inscripted.components.DamageComponent;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.HealthComponent;
//import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Armor.Armor;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.*;
//import com.amorabot.inscripted.components.Items.DataStructures.Modifier;
//import com.amorabot.inscripted.components.Items.DataStructures.ModifierIDs;
//import com.amorabot.inscripted.components.Items.DataStructures.ModifierManager;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class StatCompiler {

    private final Profile targetProfile;
    private final UUID playerID;

    public StatCompiler(UUID playerProfileID){
        targetProfile = JSONProfileManager.getProfile(playerProfileID);
        playerID = playerProfileID;
    }

    private void resetProfile(){
        targetProfile.getAttributes().reset();
        targetProfile.getDefenceComponent().reset();
        targetProfile.getHealthComponent().reset();
        targetProfile.getMiscellaneous().reset();
        //No problem if null
        Weapon weaponSlot = targetProfile.getEquipmentComponent().getWeaponData();
        targetProfile.getDamageComponent().reset(weaponSlot);
    }

    private void buildProfile(){
        Attributes attributes = targetProfile.getAttributes();
        DefenceComponent defences = targetProfile.getDefenceComponent();
        HealthComponent health = targetProfile.getHealthComponent();
        Miscellaneous miscellaneous = targetProfile.getMiscellaneous();
        DamageComponent damage = targetProfile.getDamageComponent();

        //The update order is defined here
        attributes.update(playerID);
        defences.update(playerID);
        health.update(playerID);
        miscellaneous.update(playerID);
        damage.update(playerID);
    }


    public void updateProfile(){
        Utils.log("-----STARTING COMPILATION FOR-----");
        PlayerEquipment equipment = targetProfile.getEquipmentComponent();

        resetProfile();

        Map<String, int[]> compiledStats = new HashMap<>();

        int healthSum = 0;

        int wardSum = 0;
        int armorSum = 0;
        int dodgeSum = 0;
        //Compiling Armor data
        Armor[] equippedArmorSet = equipment.getArmorSet();
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

            /*For each armor piece:

            1:Reconstruct the local defences (base stats + mods)
            2:Compile every defence modifier into the accumulators
            3:Comiple the rest of the global mods into the general stat pool

            */
            Map<DefenceTypes, Integer> updatedArmorDefences = equippedArmor.getLocalDefences();
            healthSum += updatedArmorDefences.get(DefenceTypes.HEALTH);
            wardSum += updatedArmorDefences.getOrDefault(DefenceTypes.WARD, 0);
            armorSum += updatedArmorDefences.getOrDefault(DefenceTypes.ARMOR,0);
            dodgeSum += updatedArmorDefences.getOrDefault(DefenceTypes.DODGE, 0);
            //....

//            compileItem(compiledStats, equippedArmor); //Compile the global stats
//        }
//        updateModValue(compiledStats, ModifierIDs.HEALTH.getModifierKey(), new int[]{healthSum});
//        updateModValue(compiledStats, ModifierIDs.WARD.getModifierKey(), new int[]{wardSum});
//        updateModValue(compiledStats, ModifierIDs.ARMOR.getModifierKey(), new int[]{armorSum});
//        updateModValue(compiledStats, ModifierIDs.DODGE.getModifierKey(), new int[]{dodgeSum});
//
//        //Compiling Weapon data
//        EquipmentSlot weaponSlot = equipment.getSlot(ItemTypes.WEAPON);
//        if (!weaponSlot.isIgnorable()){ //If the weapon data is not ignorable:
//            Weapon equippedWeapon = equipment.getWeaponData();
//
//            Utils.log(equippedWeapon.getName());
//
//            compileItem(compiledStats, equippedWeapon);
//        }

        Utils.log("-----COMPILED STATS-----");
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
        Utils.log("--------------------------");

        //Lets see what stats were compiled through the compiledStats map's keySet:

        //Lets first see whats stats were subsequently generated by attributes or other special mods!
        //33 STR -> 11 global flat health(HEALTH_FLAT stat)!
        //TODO: remove the stat mapping from attributes within profile and moving them here
        //===================================================================

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
            /*Tokenization explanation:
            Single target stat example:
            PHYSICALDAMAGE_INCREASED
            Token1 = PHYSICALDAMAGE
            Token2 = INCREASED

            The tokens before the last one define the global stat in the profile: PHYSICALDAMAGE
            The type of value is defined by the last token: INCREASED

            Hybrid mod example:
            STRENGTH_INTELLIGENCE_FLAT
            Token1 = STRENGTH
            Token2 = INTELLIGENCE
            Token3 = FLAT

            Target stats: Strength and Intelligence, Type of value: Flat
            They will be redirected to the Flat attribute section of the profile, for example
            ...*/
            //Global stat compilation
            try {
                switch (tokens){
                    case 2:
                        //Non-Hybrid Mods
                        String modToken = modTokens[0];
                        String methodKey = "set"+modToken;
                        Method method = this.getClass().getDeclaredMethod(methodKey, String.class,int[].class);
                        method.invoke(this, typeToken, compiledStats.get(modKey));
                        break;
                    case 3:
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

                        //The hybrid mod structure assumes a mod like:
                        //DEX_INT_FLAT -> [13, 15]. That is, a single int for each value
                        //In this example, 13 FLAT DEX and 15 FLAT INT
                        method1.invoke(this, typeToken, new int[]{compiledStats.get(modKey)[0]});
                        method2.invoke(this, typeToken, new int[]{compiledStats.get(modKey)[1]});
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

        buildProfile();
    }

//    private void compileItem(Map<String, int[]> compiledStats, Item equippedItem){
//        //Compiling mods
//        List<Modifier> itemMods = equippedItem.getInscriptionList();
//        for (Modifier mod : itemMods){
//
//            if (mod.getModifierID().isLocal()){
//                continue;
//            }
//            ModifierIDs modifierID = mod.getModifierID();
//
//            /*
//            The value that needs to be compiled comes directly from the modifiers.yml table.
//            We need to fetch the current modifier's tier from the table and map the value ranges based on the mod's
//            base percentile (0-1)
//            Ex:
//                ARMOR_WARD Tier 3, Item's BP = 0.95
//                      - 10    | ARMOR RANGE
//                      - 14    |
//                      - 18          | WARD RANGE
//                      - 20          |
//                ArmorValue = A value thats 95% of the way between 10 - 14 = roundedParametricValue(10, 14, 0.95) = 14
//                WardValue = roundedParametricValue(18,20, 0.95)
//
//                int[] wantedFinalArray = [ArmorValue, WardValue]
//             */
//            compileStat(compiledStats, modifierID.getModifierKey(), ModifierManager.getMappedFinalValueFor(mod));
//        }
//
//        //Compiling implicit
//        //Implicits use the old system of fixed values for mods
//        Implicits implicitID = equippedItem.getImplicit();
//        compileStat(compiledStats, implicitID.getModifierKey(), implicitID.getValue().clone());
//    }

//    private void compileStat(Map<String, int[]> compiledStats, String modKey, int[] values){
//        updateModValue(compiledStats, modKey, values);
//    }
//
//    private void updateModValue(Map<String, int[]> compiledStats, String modKey, int[] value){
//        if (compiledStats.containsKey(modKey)){
//            int[] previousValue = compiledStats.get(modKey).clone();
//            for (int i = 0; i < previousValue.length; i++){
//                value[i] += previousValue[i];
//            }
//            compiledStats.put(modKey, value);
//        } else {
//            compiledStats.put(modKey, value);
//        }
//    }






    /*
    Compiler methods use the "add" method for each stat because that stat can, hipothetically,
    come from all different sources. If that is the case, the previous stat would be
    overriden and not accumulated, as it should

    Apart from using the add method, at the end of the compilation step all components
    should be updated to signal this. The update() method does all final calculations
    and cross-stat modifications needed for each component.

    The compilation process, basically, is:

    Reset all player components -> Compile all stats -> Fill all the fresh components -> Update/Build all
    */

    /*
    Every mod's value is contained within a int[] argument. Single value mods will be handled within the correspondent
    method, and more complex mods like hybrid ones will take advantage of the multiple values that
     can be (and are) stored, also handling them within their methods
     */
//    private void setHEALTH(String typeToken, int[] healthValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case FLAT -> targetProfile.getHealthComponent().addBaseHealth(healthValue[0]);
//            case INCREASED -> targetProfile.getHealthComponent().addIncreasedHealth(healthValue[0]); //Global
//            //No multiplier mods for HEALTH
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in HEALTH setter");
//        }
//    }
//    private void setHEALTHREGEN(String typeToken, int[] healthRegenValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case FLAT -> targetProfile.getHealthComponent().addBaseHealthRegen(healthRegenValue[0]);
//            //No %Added health regen mods
//            //No multiplier mods for HEALTHREGEN
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in HEALTHREGEN setter");
//        }
//    }
//    private void setWARD(String typeToken, int[] wardValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case FLAT -> targetProfile.getHealthComponent().addBaseWard(wardValue[0]);
//            case INCREASED -> targetProfile.getHealthComponent().addIncreasedWard(wardValue[0]); //Global
//            //No multiplier mods for WARD
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in WARD setter");
//        }
//    }
//    private void setSTRENGTH(String typeToken, int[] strengthValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case FLAT -> targetProfile.getAttributes().addBaseStrength(strengthValue[0]);
//            case MULTIPLIER -> targetProfile.getAttributes().addBaseStrMulti(strengthValue[0]);
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in STRENGTH setter");
//        }
//    }
//    private void setDEXTERITY(String typeToken, int[] dexterityValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case FLAT -> targetProfile.getAttributes().addBaseDexterity(dexterityValue[0]);
//            case MULTIPLIER -> targetProfile.getAttributes().addBaseDexMulti(dexterityValue[0]);
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in DEXTERITY setter");
//        }
//    }
//    private void setINTELLIGENCE(String typeToken, int[] intelligenceValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case FLAT -> targetProfile.getAttributes().addBaseIntelligence(intelligenceValue[0]);
//            case MULTIPLIER -> targetProfile.getAttributes().addBaseIntMulti(intelligenceValue[0]);
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in INTELLIGENCE setter");
//        }
//    }
//    private void setDODGE(String typeToken, int[] dodgeValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case FLAT -> targetProfile.getDefenceComponent().addBaseDodge(dodgeValue[0]);
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in DODGE setter");
//        }
//    }
//    private void setARMOR(String typeToken, int[] armorValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case FLAT -> targetProfile.getDefenceComponent().addBaseArmor(armorValue[0]);
//            case INCREASED -> targetProfile.getDefenceComponent().addBaseIncreasedArmor(armorValue[0]); //Global
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in ARMOR setter");
//        }
//    }
//    private void setFIRERESISTANCE(String typeToken, int[] fireResValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case PERCENT -> targetProfile.getDefenceComponent().addBaseFireRes(fireResValue[0]);
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in FIRE RESISTANCE setter");
//        }
//    }
//    private void setLIGHTNINGRESISTANCE(String typeToken, int[] lightResValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case PERCENT -> targetProfile.getDefenceComponent().addBaseLightningResistance(lightResValue[0]);
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in LIGHTNING RESISTANCE setter");
//        }
//    }
//    private void setCOLDRESISTANCE(String typeToken, int[] coldResValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case PERCENT -> targetProfile.getDefenceComponent().addBaseColdResistance(coldResValue[0]);
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in COLD RESISTANCE setter");
//        }
//    }
//    private void setABYSSALRESISTANCE(String typeToken, int[] abyssResValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case PERCENT -> targetProfile.getDefenceComponent().addBaseAbyssalResistance(abyssResValue[0]);
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in ABYSS RESISTANCE setter");
//        }
//    }
//    private void setSTAMINA(String typeToken, int[] staminaValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case FLAT -> targetProfile.getMiscellaneous().addBaseStamina(staminaValue[0]);
//            case INCREASED -> targetProfile.getMiscellaneous().addBasePercentStamina(staminaValue[0]);
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in STAMINA setter");
//        }
//    }
//    private void setSTAMINAREGEN(String typeToken, int[] staminaRegenValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case FLAT -> targetProfile.getMiscellaneous().addBaseStaminaRegen(staminaRegenValue[0]);
//            case INCREASED -> targetProfile.getMiscellaneous().addBasePercentStaminaRegen(staminaRegenValue[0]);
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in STAMINA REGEN setter");
//        }
//    }
//    private void setWALKSPEED(String typeToken, int[] walkSpeedValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case FLAT -> targetProfile.getMiscellaneous().addBaseWalkSpeed(walkSpeedValue[0]);
//            case INCREASED -> targetProfile.getMiscellaneous().addBasePercentWalkSpeed(walkSpeedValue[0]);
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in WALK SPEED setter");
//        }
//    }
//    private void setLIFEONHIT(String typeToken, int[] lifeOnHitValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case FLAT -> targetProfile.getDamageComponent().addBaseLifeOnHit(lifeOnHitValue[0]);
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in LIFE ON HIT setter");
//        }
//    }
//    private void setSHRED(String typeToken, int[] shredValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case PERCENT -> targetProfile.getDamageComponent().getHitData().addBaseShred(shredValue[0]);
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in SHRED setter");
//        }
//    }
//    private void setMAELSTROM(String typeToken, int[] maelstromValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case PERCENT -> targetProfile.getDamageComponent().getHitData().addBaseMaelstrom(maelstromValue[0]);
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in MAELSTROM setter");
//        }
//    }
//    private void setBLEED(String typeToken, int[] bleedValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case PERCENT -> targetProfile.getDamageComponent().getHitData().addBaseBleedChance(bleedValue[0]);
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in BLEED CHANCE setter");
//        }
//    }
//    private void setPHYSICALDAMAGE(String typeToken, int[] physicalValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case FLAT -> targetProfile.getDamageComponent().getHitData().addFlatDamage(DamageTypes.PHYSICAL,physicalValue);
//            case INCREASED -> targetProfile.getDamageComponent().addBaseIncreasedPhysicalDamage(physicalValue[0]);  //Global
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in PHYS DAMAGE setter");
//        }
//    }
//    private void setFIREDAMAGE(String typeToken, int[] fireValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case FLAT -> targetProfile.getDamageComponent().getHitData().addFlatDamage(DamageTypes.FIRE,fireValue);
//            case INCREASED -> targetProfile.getDamageComponent().addBaseIncreasedFireDamage(fireValue[0]); //Global
//            //MULTI mapping shoud be added soon
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in FIRE DAMAGE setter");
//        }
//    }
//    private void setLIGHTNINGDAMAGE(String typeToken, int[] lightningValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case FLAT -> targetProfile.getDamageComponent().getHitData().addFlatDamage(DamageTypes.LIGHTNING,lightningValue);
//            case INCREASED -> targetProfile.getDamageComponent().addBaseIncreasedLightningDamage(lightningValue[0]);  //Global
//            //MULTI mapping shoud be added soon
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in LIGHTNING DAMAGE setter");
//        }
//    }
//    private void setCOLDDAMAGE(String typeToken, int[] coldValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case FLAT -> targetProfile.getDamageComponent().getHitData().addFlatDamage(DamageTypes.COLD,coldValue);
//            case INCREASED -> targetProfile.getDamageComponent().addBaseIncreasedColdDamage(coldValue[0]);
//            //MULTI mapping shoud be added soon
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in COLD DAMAGE setter");
//        }
//    }
//    private void setABYSSALDAMAGE(String typeToken, int[] abyssValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case FLAT -> targetProfile.getDamageComponent().getHitData().addFlatDamage(DamageTypes.ABYSSAL,abyssValue);
//            case INCREASED -> targetProfile.getDamageComponent().addBaseIncreasedAbyssalDamage(abyssValue[0]);
//            //MULTI mapping shoud be added soon
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in ABYSSAL DAMAGE setter");
//        }
//    }
//    private void setELEMENTALDAMAGE(String typeToken, int[] elementalValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case INCREASED -> targetProfile.getDamageComponent().addBaseIncreasedElementalDamage(elementalValue[0]);
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in ELE DAMAGE setter");
//        }
//    }
//    private void setACCURACY(String typeToken, int[] accuracyValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case FLAT -> targetProfile.getDamageComponent().getHitData().addBaseAccuracy(accuracyValue[0]);
//            case INCREASED -> targetProfile.getDamageComponent().addBaseIncreasedAccuracy(accuracyValue[0]);
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in ACCURACY setter");
//        }
//    }
//    private void setCRITICALCHANCE(String typeToken, int[] critChanceValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            //a FLAT mapping could be extra base crit chance!
//            case INCREASED -> targetProfile.getDamageComponent().addBaseIncreasedCritChance(critChanceValue[0]);
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in CRIT CHANCE setter");
//        }
//    }
//    private void setCRITICALDAMAGE(String typeToken, int[] critDamageValue){
//        ValueTypes type = mapValueType(typeToken);
//        if (type == null){return;}
//        switch (type){
//            case INCREASED -> targetProfile.getDamageComponent().getHitData().addBaseCritDamage(critDamageValue[0]);
//            default -> Utils.error("TypeToken not handled: " + typeToken + " in CRIT DAMAGE setter");
//        }
    }


    //Null if invalid token, ValueType Enum entry if valid token
//    private ValueTypes mapValueType(String valueTypeString){
//        ValueTypes type = null;
//        try {
//            type = ValueTypes.valueOf(valueTypeString);
//        } catch (IllegalArgumentException exception){
//            Utils.log("Invalid typeToken (mapValueType function for "+ valueTypeString +")");
//        }
//        return type;
//    }
}
