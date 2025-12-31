package com.amorabot.inscripted.player.profile.parsing;

import com.amorabot.inscripted.combat.buffs.Buffs;
import com.amorabot.inscripted.combat.buffs.categories.stat.Stat;
import com.amorabot.inscripted.item.inscription.Inscription;
import com.amorabot.inscripted.item.inscription.ProceduralInscription;
import com.amorabot.inscripted.item.inscription.UniqueInscription;
import com.amorabot.inscripted.item.inscription.definition.*;
import com.amorabot.inscripted.item.inscription.language.ValueType;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.equipment.PlayerEquipment;
import com.amorabot.inscripted.player.profile.BaseStats;
import com.amorabot.inscripted.player.profile.Profile;
import com.amorabot.inscripted.player.profile.component.SpecialInscriptionsComponent;
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
        SpecialInscriptionsComponent prevInscriptionsSnapshot = equipment.getSpecialInscriptions().snapshot();

        //Getting keystones before re-compilation:
        Set<KeystoneIDs> oldKeystones = new HashSet<>(prevInscriptionsSnapshot.getKeystones());
        //Updating cache for keystones, effects, ..., based on equipment
        equipment.updateSpecialInscriptions();
        SpecialInscriptionsComponent updatedSpecialInscriptions = equipment.getSpecialInscriptions();
        handlePlayerKeystoneStates(oldKeystones,updatedSpecialInscriptions,playerData);

        //Getting global stats
        StatPool globalStatPool = compileEquipmentStats(equipment); //Raw equipment global stats


        //Handle instantiation/state of keystone tasks

        //Early Keystones trigger
        globalStatPool.applyKeystoneRules(TriggerTimes.EARLY,updatedSpecialInscriptions.getKeystones(),playerData);

        //Procedurally group external stats (Buffs, auras, conditional aura buffs...) to insert in the global pool
        StatPool externalStats = gatherExternalStats(playerData);
        globalStatPool.merge(externalStats);

        // Late Keystones trigger (Stat Overrides, rules, ...). Those have the final say on the player's profile state
        // Ensure LATE rules are applied so meta-stats can have a accurate representation of base values
        globalStatPool.applyKeystoneRules(TriggerTimes.LATE,updatedSpecialInscriptions.getKeystones(),playerData,
                "Pre-Meta Stat rule enforcing (Sync'ing mechanism)");

        /*
        Meta stats are based on the main stat pool. If a rule must be applied over a base stat, overriding it, for example,
        And a meta stat alters that base stat, then we have a de-sync problem.
        Example:
            A Forbidden Pact sets the player life to 1. Then STR bonuses grant that player extra life.
            Negative STR would result in negative life, and Positive STR would result in more than 1 life (should be fact)

        On the other end, those rules cant be applied AFTER all meta-stats since the referenced converted stat would not be
        wouldn't be in it's correct state yet.
        Example: Abyssal DMG per 400 Soul
            If, let's say, a Keystone sets a player's Soul value to 1 invariably and it happens after the meta conversion.
            Then the player would have Abyssal DMG coming from a virtual, "would-be", value that's not representative of
            the 1 Soul Keystone rule.

        So, we must guarantee the referenced stat pool is always updated
        */

        //After all stat changes, apply meta stat changes -> attribute bonuses and meta-conversions
        applyAttributeBonuses(globalStatPool);
        convertMetaStats(updatedSpecialInscriptions.getMetaInscriptions(),globalStatPool);

        // Ensures that stats affected by rules AND meta-stats are now definitively following stat rules
        globalStatPool.applyKeystoneRules(TriggerTimes.LATE,updatedSpecialInscriptions.getKeystones(),playerData,"Final Stat rule enforcing");

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

    private static StatPool gatherExternalStats(PlayerDataContainer playerData){
        StatPool externalStats = new StatPool();
        //Buffs
        for (Buffs activePlayerBuff : playerData.getActiveBuffs().keySet()){
            Utils.error("BUFF: " + activePlayerBuff);
            if (!activePlayerBuff.isStatBuff()){continue;}
            Stat buffStatsAnnot = (Stat) activePlayerBuff.getBuffAnnotationData();
            Stats buffedStat = buffStatsAnnot.targetStat();
            ValueType type = buffStatsAnnot.valueType();
            int[] value = buffStatsAnnot.amount();
            Utils.error(buffedStat+": " + type + ", " + Arrays.toString(value));
            externalStats.insertValue(buffedStat,type,value);
        }
        //Whatever external sources
        //...

//        externalStats.debug("External buffs");
        return externalStats;
    }

    public static void handlePlayerKeystoneStates(Set<KeystoneIDs> oldKeystones, SpecialInscriptionsComponent specialInscriptions, PlayerDataContainer playerData){
        Set<KeystoneIDs> updatedKeystones = specialInscriptions.getKeystones();
        debugKeystoneSet(oldKeystones,"Old Keystones");
        if (oldKeystones.isEmpty()){
            // Apply all new keystones
            for (KeystoneIDs newKeystone : updatedKeystones){
                if (!newKeystone.isRule()){
                    newKeystone.castKeystoneSkill(playerData.getPlayerID());
                }
            }
        } else { // Then a diff check is needed
            if (!oldKeystones.equals(updatedKeystones)){ // old and updated are different
                Set<KeystoneIDs> striclyNewKeystones = new HashSet<>(updatedKeystones);
                striclyNewKeystones.removeAll(oldKeystones); // ( (A) U (B) ) - (B) =>
                debugKeystoneSet(striclyNewKeystones,"New Keystones:");
                for (KeystoneIDs nKey : striclyNewKeystones){
                    if (!nKey.isRule()){
                        nKey.castKeystoneSkill(playerData.getPlayerID());
                    }
                }
                // Uninstantiate any state from to-be-removed keystones
                oldKeystones.removeAll(updatedKeystones); //Remove all current keystones from the old snapshot
                uninstantiateKeystones(playerData,oldKeystones);
            } else { // They were the same set to begin with -> No changes
                if (DEBUG_MODE){Utils.error("No keystone state changes.");}
            }
        }
    }

    public static void uninstantiateKeystones(PlayerDataContainer playerData, Set<KeystoneIDs> keystones){
        for (KeystoneIDs oldKeystone : keystones){
            if (!oldKeystone.isRule()){ //Its a Aura, let's toggle it
                if (DEBUG_MODE){
                    Utils.log("Uninstantiating keystone: " + oldKeystone);
                }
                oldKeystone.castKeystoneSkill(playerData.getPlayerID());
            }
        }
    }
    public static void debugKeystoneSet(Set<KeystoneIDs> keystones, String message){
        if (!DEBUG_MODE){return;}
        if (keystones.isEmpty()){
            Utils.error(message+":");
            Utils.error("No keystones!\n");
            return;
        }
        Utils.error("DEBUG: " +message+"  ====---- - - -   -   -   -");
        for (KeystoneIDs debuggedKeystone : keystones){
            Utils.error(debuggedKeystone.name());
        }
        Utils.error("=======------------------ - - -   -   -   -");
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
    private static void convertMetaStats(Set<Inscription> metaInscriptions, StatPool globalStatPool){
        for (Inscription metaInsc : metaInscriptions){
            if (DEBUG_MODE){Utils.log("Compiling meta Inscription " + metaInsc.getInscriptionDefinition().getDisplayName());}
            if (!metaInsc.isMeta()){continue;}
            if (!(metaInsc.getInscriptionDefinition() instanceof InscriptionDefinition.Meta metaInscriptionDef)){
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
    public static Set<Inscription> filterMetaInscriptions(List<Inscription> itemInscriptions){
        Set<Inscription> metaSet = new HashSet<>();
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
