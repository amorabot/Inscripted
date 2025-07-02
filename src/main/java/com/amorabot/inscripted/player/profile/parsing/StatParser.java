package com.amorabot.inscripted.player.profile.parsing;

import com.amorabot.inscripted.item.inscription.Inscription;
import com.amorabot.inscripted.item.inscription.ProceduralInscription;
import com.amorabot.inscripted.item.inscription.UniqueInscription;
import com.amorabot.inscripted.item.inscription.definition.*;
import com.amorabot.inscripted.item.inscription.language.ValueType;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.equipment.PlayerEquipment;
import com.amorabot.inscripted.player.profile.BaseStats;
import com.amorabot.inscripted.player.profile.Profile;
import com.amorabot.inscripted.utils.Utils;

import java.util.*;

public class StatParser {

    //TODO: implement a compiling queue for fast recompilation attempts
    // private static Map<UUID, Boolean> lockedProfiles
    // if locked -> queue & have a running server-wide task to handle queue processing
    private static final boolean DEBUG_MODE = true;

    public static void buildProfile(PlayerDataContainer playerData){
        Utils.log("Building profile!");
        Profile profile = playerData.getProfile();
        PlayerEquipment equipment = playerData.getEquipment();

        //TODO: diff checking for special ACTIVE inscriptions (Aura keystones, etc...)

        //Updating cache for keystones, effects, ... based on equipment
        equipment.updateSpecialInscriptions();

        //Getting global stats
        StatPool globalStatPool = compileEquipmentStats(equipment); //Raw equipment global stats

        Set<KeystoneIDs> keystones = equipment.getKeystones();
        Set<EffectIDs> effects = equipment.getEffects();
        Set<ProceduralInscription> metaInscriptions = equipment.getMetaInscriptions();

        //Handle instantiation/state of keystone tasks

        //Early Keystones trigger
        //...

        //Procedurally group external stats (Buffs, auras, conditional aura buffs...) to insert in the global pool
        //...

        //Late Keystones trigger (Stat Overrides, rules, ...). Those have the final say on the player's profile state
        //...

        //After all stat changes, apply attribute bonuses and meta-conversions
        applyAttributeBonuses(globalStatPool);
        convertMetaStats(metaInscriptions,globalStatPool);

        //Now that all stat changes are applied, get the final profile-level values to be stored
        Map<Stats, double[]> finalStats = globalStatPool.calculateFinalValues();
        if (DEBUG_MODE){
            for (Stats stat : finalStats.keySet()){
                Utils.log(stat.getAlias()+": " + Arrays.toString(finalStats.get(stat)));
            }
        }

        //Update the player's profile
        profile.update(playerData.getPlayerID(),finalStats);
        playerData.setGlobalStats(globalStatPool); // Store the up-to-date pool for things like stat checks for skill damages
    }

    public static StatPool compileEquipmentStats(PlayerEquipment playerEquipment){
        StatPool globalStatPool = new StatPool();
        // Add base stats
        for(BaseStats baseStat : BaseStats.values()){
            globalStatPool.insertValue(baseStat.getTargetStat(),baseStat.getType(), new int[]{baseStat.getValue()});
        }

        playerEquipment.getEquipmentData().forEach(
                (slot, slotData) -> {
                    if (slotData.isIgnorable()){return;}
                    StatPool slotStats = slotData.getEquipmentStats();
                    if (slotStats == null){return;}
                    if (DEBUG_MODE){Utils.log("Compiling " + slot);}
                    globalStatPool.merge(slotStats);
                }
        );
        return globalStatPool;
    }
    private static void convertMetaStats(Set<ProceduralInscription> metaInscriptions, StatPool globalStatPool){
        for (ProceduralInscription metaInsc : metaInscriptions){
            InscriptionIDs inscID = metaInsc.getInscription();
            if (DEBUG_MODE){Utils.log("Compiling meta Inscription " + inscID);}
            if (!metaInsc.isMeta()){continue;}
            if (!(inscID.getDefinitionData() instanceof InscriptionDefinition.Meta metaInscriptionDef)){
                if (DEBUG_MODE){Utils.error("Wtf is this shit of meta insc");}
                continue;
            }
            //Get meta inscription definition data
            //Get metadata
            Stats convertedStat = metaInscriptionDef.getConvertedStat();
            ValueType convertedType = metaInscriptionDef.getMetaValueType();
            final int ratio = metaInscriptionDef.getConversionRate();
            //Get main data
            InscriptionDefinition.BaseInscription baseMetaInsc = metaInscriptionDef.getBaseData();
            boolean positive = metaInscriptionDef.isPositive(); // Not really needed, why tf a meta stat would be negative
            Stats targetStat = baseMetaInsc.stat();
            ValueType targetValueType = baseMetaInsc.type();
            /*
             Get expected final value for convertedStat
                A constant/single-roll converted stat is implicit/assumed, that means accessing the [0] index
                should be always safe
            */
            double baseExpectedValue;
            switch (convertedType){
                //These are affected by multipliers
                case FLAT,PERCENTAGE -> baseExpectedValue = globalStatPool.calculateStatValue(convertedStat)[0];
                case INCREASED -> baseExpectedValue = globalStatPool.getBaseStatValue(convertedStat,convertedType)[0];
                case MULTIPLIER -> baseExpectedValue = globalStatPool.getMultiplier(convertedStat);
                default -> baseExpectedValue = 0D;
            }
            if (baseExpectedValue==0){continue;}
            //Calculate how many times the base stat values are going to be added
            final int stacks = (int) Math.floor(baseExpectedValue/ratio);
            if (stacks==0){continue;}
            int[] metaMappedValues = Arrays.stream(metaInsc.getMappedFinalValues()).map(
                    metaValue -> metaValue * stacks
            ).toArray();
            globalStatPool.insertValue(targetStat,targetValueType,metaMappedValues);
        }
    }

    private static void applyAttributeBonuses(StatPool globalStatPool){
        int[] globalStr = globalStatPool.getBaseStatValue(Stats.STRENGTH, ValueType.FLAT);
        if (globalStr.length==1){
            final int strength = globalStr[0];
            //Add STR bonuses to global pool
            //3 STR -> +1 Base HP
            //10 STR -> 1% Melee DMG
            int extraHP = strength / 3;
            if (extraHP>0){
                globalStatPool.insertValue(Stats.HEALTH,ValueType.FLAT,new int[]{extraHP});
            }
            int extraMeleeDMG = strength / 10;
            if (extraMeleeDMG>0){
                /*
                Melee Damage is inherently a INCREASED value. But storing it as a percentage/flat value makes it
                not ignorable during the stat parsing. That even allows for it to be influenced correctly by
                mods like More Melee DMG

                When RETRIEVING the final value for melee damage or similar stats (inside its corresponding component),
                it can be used, for example, as a % Increase inside skills or wherever else needed.
                */
                globalStatPool.insertValue(Stats.MELEE_DAMAGE,ValueType.PERCENTAGE,new int[]{extraMeleeDMG});
            }
        }

        int[] globalDex = globalStatPool.getBaseStatValue(Stats.DEXTERITY, ValueType.FLAT);
        if (globalDex.length==1){
            final int dexterity = globalDex[0];
            //Add DEX bonuses to global pool
            //3 DEX -> +1 Accuracy
            //10 DEX -> +1 Base stamina
            int extraAccuracy = dexterity / 3;
            if (extraAccuracy>0){
                globalStatPool.insertValue(Stats.ACCURACY,ValueType.FLAT,new int[]{extraAccuracy});
            }
            int extraStamina = dexterity / 10;
            if (extraStamina>0){
                globalStatPool.insertValue(Stats.STAMINA,ValueType.FLAT,new int[]{extraStamina});
            }
        }

        int[] globalInt = globalStatPool.getBaseStatValue(Stats.INTELLIGENCE, ValueType.FLAT);
        if (globalInt.length==1){
            final int intelligence = globalInt[0];
            //Add INT bonuses to global pool
            //3 INT -> +1 Base Soul
            //50 INT -> 1% Soul recovery
            int extraSoul = intelligence / 3;
            if (extraSoul>0){
                globalStatPool.insertValue(Stats.SOUL,ValueType.FLAT,new int[]{extraSoul});
            }
            int extraSoulRecovery = intelligence / 50;
            if (extraSoulRecovery>0){
                globalStatPool.insertValue(Stats.SOUL_RECOVERY_RATE,ValueType.PERCENTAGE,new int[]{extraSoulRecovery});
            }
        }

    }

    public static Set<EffectIDs> getEffects(List<Inscription> itemInscriptions){
        Set<EffectIDs> mappedEffects = new HashSet<>();
        for (Inscription inscription : itemInscriptions){
            if (inscription instanceof UniqueInscription uniqueInsc){
                if (uniqueInsc.isEffect()){
                    mappedEffects.add(((InscriptionDefinition.Effect)uniqueInsc.getInscriptionDefinition()).getEffectID());
                }
            }
        }
        return mappedEffects;
    }
    public static Set<KeystoneIDs> getKeystones(List<Inscription> itemInscriptions){
        Set<KeystoneIDs> mappedKeystones = new HashSet<>();
        for (Inscription inscription : itemInscriptions){
            if (inscription instanceof UniqueInscription uniqueInsc){
                if (uniqueInsc.isKeystone()){
                    mappedKeystones.add(((InscriptionDefinition.Keystone)uniqueInsc.getInscriptionDefinition()).getKeystoneID());
                }
            }
        }
        return mappedKeystones;
    }
    public static Set<ProceduralInscription> filterMetaInscriptions(List<Inscription> itemInscriptions){
        Set<ProceduralInscription> metaSet = new HashSet<>();
        itemInscriptions.forEach(
            inscription -> {
                if (inscription instanceof ProceduralInscription proceduralInscription){
                    if (proceduralInscription.isMeta()){
                        metaSet.add(proceduralInscription);
                    }
                }
            }
        );
        return metaSet;
    }
}
