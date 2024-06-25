package com.amorabot.inscripted.components.Player;

import com.amorabot.inscripted.components.DamageComponent;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Armor.Armor;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.*;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Items.modifiers.Inscription;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;
import com.amorabot.inscripted.components.Items.modifiers.data.*;
import com.amorabot.inscripted.components.Items.modifiers.unique.Effects;
import com.amorabot.inscripted.components.Items.modifiers.unique.Keystones;
import com.amorabot.inscripted.components.Items.modifiers.unique.TriggerTimes;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerBuffManager;
import com.amorabot.inscripted.managers.PlayerPassivesManager;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

import static com.amorabot.inscripted.utils.Utils.calculateFinalFlatValue;

public class StatCompiler {

    public static void updateProfile(UUID playerID){
        Utils.log("-----STARTING COMPILATION FOR-----");
        Profile targetProfile = JSONProfileManager.getProfile(playerID);
        PlayerEquipment equipment = targetProfile.getEquipmentComponent();

        Map<PlayerStats, Map<ValueTypes, int[]>> compiledStats = new HashMap<>();
        addBaseStatsTo(compiledStats);

        Set<Keystones> playerKeystones = compileKeystones(equipment);
        Set<Effects> playerEffects = compileUniqueEffects(equipment);

        manageKeystoneTasks(playerID, playerKeystones);

        //Apply early compilation keystones
        applyKeystones(playerID, playerKeystones, TriggerTimes.EARLY);


        //After local stats are compiled, group all equipment slots
        groupEquipmentStats(compiledStats, equipment);

        //Lets reset the external stat container and see what external sources of stats are valid to be recompiled
        targetProfile.getStats().getExternalStats().clear();
        addExternalStatsFromKeystones(playerID);
        groupExternalStats(compiledStats, playerID);

        //Once most stats were added, lets add the meta stat values, based on it's target mod base final value
        addMetaStatsFrom(equipment, compiledStats);

        //Compiled stats are now available for all components to consume in the build step
        targetProfile.getStats().setPlayerStats(compiledStats);
        targetProfile.setKeystones(playerKeystones);
        targetProfile.setEffects(playerEffects);

        buildProfile(targetProfile, playerID);

        //Apply late compilation keystones
        applyKeystones(playerID, playerKeystones, TriggerTimes.LATE);
    }

    public static void addLocalStatsTo(Map<PlayerStats, Map<ValueTypes, int[]>> statMap, Item itemData){
        if (itemData instanceof Weapon weaponData){
            Map<DamageTypes, int[]> localDamage = weaponData.getLocalDamage();
            addStat(statMap, PlayerStats.PHYSICAL_DAMAGE, ValueTypes.FLAT, localDamage.get(DamageTypes.PHYSICAL));
            if (localDamage.containsKey(DamageTypes.FIRE)){
                addStat(statMap, PlayerStats.FIRE_DAMAGE, ValueTypes.FLAT, localDamage.get(DamageTypes.FIRE));
            }
            if (localDamage.containsKey(DamageTypes.LIGHTNING)){
                addStat(statMap, PlayerStats.LIGHTNING_DAMAGE, ValueTypes.FLAT, localDamage.get(DamageTypes.LIGHTNING));
            }
            if (localDamage.containsKey(DamageTypes.COLD)){
                addStat(statMap, PlayerStats.COLD_DAMAGE, ValueTypes.FLAT, localDamage.get(DamageTypes.COLD));
            }
            if (localDamage.containsKey(DamageTypes.ABYSSAL)){
                addStat(statMap, PlayerStats.ABYSSAL_DAMAGE, ValueTypes.FLAT, localDamage.get(DamageTypes.ABYSSAL));
            }
        }
        if (itemData instanceof Armor armorData){
            Map<DefenceTypes, Integer> localDefences = armorData.getLocalDefences();
            addStat(statMap, PlayerStats.HEALTH, ValueTypes.FLAT, new int[]{localDefences.get(DefenceTypes.HEALTH)}); //Life should be always present
            addStat(statMap, PlayerStats.WARD, ValueTypes.FLAT, new int[]{localDefences.getOrDefault(DefenceTypes.WARD,0)});
            addStat(statMap, PlayerStats.ARMOR, ValueTypes.FLAT, new int[]{localDefences.getOrDefault(DefenceTypes.ARMOR,0)});
            addStat(statMap, PlayerStats.DODGE, ValueTypes.FLAT, new int[]{localDefences.getOrDefault(DefenceTypes.DODGE,0)});
        }
    }

    private static void addMetaStatsFrom(PlayerEquipment equipment, Map<PlayerStats, Map<ValueTypes, int[]>> compiledStats){
        Map<ItemTypes, Set<Inscription>> metaInscPerSlot = equipment.getMetaInscriptions();
        for (ItemTypes slot : metaInscPerSlot.keySet()){
            if (equipment.getSlot(slot).isIgnorable()){continue;}
            Set<Inscription> metaInscriptions = metaInscPerSlot.get(slot);
            if (metaInscriptions.isEmpty()){continue;}
            for (Inscription insc : metaInscriptions){
                InscriptionID ID = insc.getInscription();
                if (ID.getData().isStandard()){
                    InscriptionData inscData = (InscriptionData) ID.getData();
                    StatDefinition definitionData = inscData.getDefinitionData();

                    Meta metaData = ID.getMetaAnnotationData();
                    PlayerStats targetConvertedStat = metaData.convertedStat();
                    ValueTypes type = metaData.convertedValueType(); //To make things simple, meta stats will only work with flat values for now

                    PlayerStats finalConvertedStat = definitionData.stat();
                    ValueTypes finalStatType = definitionData.valueType();
                    if (compiledStats.containsKey(targetConvertedStat)){
                        if (compiledStats.get(targetConvertedStat).containsKey(type)){
                            int convertedStat = (int) readFinalFlatValueFrom(compiledStats, targetConvertedStat);
                            int[] baseMetaValue = insc.getMappedFinalValue();
                            int[] stackedMetaValue = ID.convert(convertedStat, baseMetaValue);
                            addStat(compiledStats, finalConvertedStat, finalStatType, stackedMetaValue);
//                            Utils.error("CONVERTED BASE STAT:" + convertedStat);
//                            Utils.log(Arrays.toString(stackedMetaValue));
                        }
                    }
                }
            }
        }
    }
    public static float readFinalFlatValueFrom(Map<PlayerStats, Map<ValueTypes, int[]>> compiledStats, PlayerStats stat){
        if (!compiledStats.containsKey(stat)){return 0F;} //Invalid query
        int baseValue = compiledStats.get(stat).getOrDefault(ValueTypes.FLAT, new int[1])[0];
        int increasedMod = compiledStats.get(stat).getOrDefault(ValueTypes.INCREASED, new int[1])[0];
        int multiplierMod = compiledStats.get(stat).getOrDefault(ValueTypes.MULTIPLIER, new int[1])[0];

        return calculateFinalFlatValue(baseValue, increasedMod, multiplierMod);
    }

    private static Set<Keystones> compileKeystones(PlayerEquipment equipment) {
        Set<Keystones> playerKeystones = new HashSet<>();

        for (ItemTypes slot : ItemTypes.values()){
            ItemSlotData equipmentSlot = equipment.getSlot(slot);
            if (equipmentSlot.isIgnorable()){continue;}
            if (!equipment.getSlotKeystones().containsKey(slot)){continue;}
            Set<Keystones> slotKeystones = equipment.getSlotKeystones().get(slot);
            playerKeystones.addAll(slotKeystones);
        }
        return playerKeystones;
    }
    private static Set<Effects> compileUniqueEffects(PlayerEquipment equipment) {
        Set<Effects> playerEffects = new HashSet<>();

        for (ItemTypes slot : ItemTypes.values()){
            ItemSlotData equipmentSlot = equipment.getSlot(slot);
            if (equipmentSlot.isIgnorable()){continue;}
            if (!equipment.getSlotEffects().containsKey(slot)){continue;}
            Set<Effects> slotEffects = equipment.getSlotEffects().get(slot);
            playerEffects.addAll(slotEffects);
        }
        return playerEffects;
    }

    public static void manageKeystoneTasks(UUID playerID, Set<Keystones> compiledPlayerKeystones){
        Utils.log("Managing keystones for: " + playerID);

        /*
        If a recompilation is called on player quit(removing active tasks -> triggering), for instance,
        their gear might still have a stat keystone, which could result in the instantiation of a new
        stat monitor task for a offline player(by #manageKeystoneTasks() method), resulting in a null
        pointer to that player.

        That can be remedied by:
            Blocking task instantiation for offline players OR
            Supressing any resulting compilations from a player logging off
        */
        Player player = Bukkit.getPlayer(playerID);
        boolean invalidCall = player == null || !player.isOnline();
        if (invalidCall){
            Utils.log("That player is not online, aborting task instantiation attempts.");
            return;
        }

        if (!PlayerPassivesManager.getPassiveTaskMap().containsKey(playerID)){
            for (Keystones keystone : compiledPlayerKeystones){
                if (!keystone.isPassiveTask()){continue;}
                keystone.apply(playerID);
            }
            return;
        }
        Map<Keystones, Integer> playerPassives = PlayerPassivesManager.getPassiveTaskMap().get(playerID);
        //Cleaning step:
        //If, after a recompilation, the player still has a specific passive Keystone, don't remove it
        Set<Keystones> keystonesToRemove = new HashSet<>();
        for (Keystones keystone : playerPassives.keySet()){
            if (!compiledPlayerKeystones.contains(keystone)){
                keystonesToRemove.add(keystone);
            }
        }
        for (Keystones removedKeystone : keystonesToRemove){
            Utils.log("Removing keystone: " + removedKeystone);
            PlayerPassivesManager.removePassiveTask(playerID,removedKeystone);
        }

        //Now only the passives that are common between the 2 recompilations remains
        //Lets instantiate any possible new passive tasks originated from this compilation
        Set<Keystones> persistentKeystones = playerPassives.keySet();
        for (Keystones currentKeystone : compiledPlayerKeystones){
            if (!currentKeystone.isPassiveTask()){continue;}
            if (persistentKeystones.contains(currentKeystone)){continue;} //If the keystone is already there, ignore
            currentKeystone.apply(playerID);
        }

    }

    private static void applyKeystones(UUID playerID, Set<Keystones> playerKeystones, TriggerTimes timing){
        for (Keystones keystone : playerKeystones){
            if (keystone.isPassiveTask() || keystone.isStatKeystone()){continue;} //Ignore keystone with associated tasks
            if (!keystone.getCompilationTime().equals(timing)){continue;}
            Utils.log("Applying " + keystone);
            keystone.apply(playerID);
        }
    }

//    public static void addKeystonesFrom(Inscription inscription, Set<Keystones> keystoneSet){
//        ModifierData inscData = inscription.getInscription().getData();
//        if (inscData.isKeystone()){
//            KeystoneData keystoneData = (KeystoneData) inscData;
//            keystoneSet.add(keystoneData.keystone());
//        }
//    }


    private static void addBaseStatsTo(Map<PlayerStats, Map<ValueTypes, int[]>> compiledStats) {
        putSingleValueIn(compiledStats, PlayerStats.HEALTH, ValueTypes.FLAT, BaseStats.HEALTH.getValue());
        putSingleValueIn(compiledStats, PlayerStats.HEALTH_REGEN, ValueTypes.FLAT, BaseStats.HEALTH_REGEN.getValue());
        putSingleValueIn(compiledStats, PlayerStats.WARD_RECOVERY_RATE, ValueTypes.PERCENT, BaseStats.WARD_RECOVERY_RATE.getValue());
        putSingleValueIn(compiledStats, PlayerStats.STAMINA, ValueTypes.FLAT, BaseStats.STAMINA.getValue());
        putSingleValueIn(compiledStats, PlayerStats.STAMINA_REGEN, ValueTypes.FLAT, BaseStats.STAMINA_REGEN.getValue());
        putSingleValueIn(compiledStats, PlayerStats.WALK_SPEED, ValueTypes.FLAT, BaseStats.WALK_SPEED.getValue());
    }
    public static void putSingleValueIn(Map<PlayerStats, Map<ValueTypes, int[]>> compiledStats, PlayerStats targetStat, ValueTypes type, int value){
        if (compiledStats.containsKey(targetStat)){
            compiledStats.get(targetStat).put(type, new int[]{value});
            return;
        }
        Map<ValueTypes, int[]> newStatMap = new HashMap<>();
        newStatMap.put(type, new int[]{value});
        compiledStats.put(targetStat, newStatMap);
    }


    private static void buildProfile(Profile targetProfile, UUID playerID){
        Attributes attributes = targetProfile.getAttributes();
        Stats stats = targetProfile.getStats();
        DefenceComponent defences = targetProfile.getDefenceComponent();
        HealthComponent health = targetProfile.getHealthComponent();
        DamageComponent damage = targetProfile.getDamageComponent();

        //The update order is defined here
        attributes.update(playerID); //Relies on the current state of the Stats object (Its already been set)
        stats.update(playerID); //Stats is accessed directly, has a non-conventional update() and is the basis for other components states
        defences.update(playerID);
        health.update(playerID);
        damage.update(playerID);

        Stats.debugStatMap(stats.getPlayerStats(), "MISC STATS (Profile build step)");
    }

    public static void addStat(Map<PlayerStats, Map<ValueTypes, int[]>> compiledStats, PlayerStats stat, ValueTypes type, int[] values){
        if (!compiledStats.containsKey(stat)){
            Map<ValueTypes, int[]> newValueTypeMap = new HashMap<>();
            newValueTypeMap.put(type, values);
            compiledStats.put(stat, newValueTypeMap);
            return;
        }
        if (!compiledStats.get(stat).containsKey(type)){
            compiledStats.get(stat).put(type, values);
            return;
        }
        int[] storedValues = compiledStats.get(stat).get(type).clone();
        int[] updatedValues = Utils.vectorSum(storedValues, values);
//        Utils.log("Prev: " + Arrays.toString(storedValues) + "/ Updt: " + Arrays.toString(updatedValues));

        compiledStats.get(stat).put(type, updatedValues);
    }
    public static void compileItem(Map<PlayerStats, Map<ValueTypes, int[]>> compiledStats, Item item){
        List<Inscription> inscriptions = item.getInscriptionList();
        for (Inscription inscription : inscriptions){
            if (!inscription.getInscription().isGlobal()){continue;}
            if (inscription.getInscription().isMeta()){continue;}
            compileIndividualInscriptionInto(compiledStats, inscription);
        }
        compileIndividualInscriptionInto(compiledStats, item.getImplicit());
    }
    private static void compileIndividualInscriptionInto(Map<PlayerStats, Map<ValueTypes, int[]>> compiledStats, Inscription inscription){
        InscriptionID inscID = inscription.getInscription();
        ModifierData inscData = inscID.getData();
        if (inscData.isStandard()){
            InscriptionData regularInsc = (InscriptionData) inscData;
            StatDefinition def = regularInsc.getDefinitionData();
            PlayerStats targetStat = def.stat();
            ValueTypes type = def.valueType();
            int[] mappedInscriptionValues = inscription.getMappedFinalValue();
//            Utils.error("Compiling " + targetStat + ": " + Arrays.toString(mappedInscriptionValues) + " (" + type + ")");

            addStat(compiledStats, targetStat, type, mappedInscriptionValues);
            return;
        }
        if (inscData.isHybrid()){
            HybridInscriptionData hybridInsc = (HybridInscriptionData) inscData;
            StatDefinition[] definitions = hybridInsc.getStatDefinitions();
            for (int d = 0; d < definitions.length; d++){
                StatDefinition currentDef = definitions[d];
                PlayerStats targetStat = currentDef.stat();
                ValueTypes type = currentDef.valueType();
                int[] mappedInscriptionValues = inscription.getMappedFinalValue(d);

                addStat(compiledStats, targetStat, type, mappedInscriptionValues);
            }
            return;
        }
    }

    private static void addExternalStatsFromKeystones(UUID playerID){
        Profile playerProfile = JSONProfileManager.getProfile(playerID);
        Stats playerStats = playerProfile.getStats();
        for (Keystones statKeystone : playerStats.getActiveStatKeystones()){
            if (!statKeystone.isStatKeystone()){continue;}
            statKeystone.addExternalStat(playerID);
        }
    }

    private static void groupEquipmentStats(Map<PlayerStats, Map<ValueTypes, int[]>> compiledStats, PlayerEquipment playerEquipment){
        for (ItemTypes slot : ItemTypes.values()) {
            if (playerEquipment.getSlot(slot).isIgnorable()){continue;}
            Utils.log("Grouping stats for: " + slot);
            Map<PlayerStats, Map<ValueTypes, int[]>> slotStats = playerEquipment.getSlotStats(slot);
            if (slotStats == null){continue;}
            for (PlayerStats stat : slotStats.keySet()){
                Map<ValueTypes, int[]> valueTypeMap = slotStats.get(stat);
                for (ValueTypes type : valueTypeMap.keySet()){
                    int[] currentValues = valueTypeMap.get(type).clone();
//                    Utils.log("Adding " + stat + " " + type + ": " + Arrays.toString(currentValues));
                    addStat(compiledStats, stat, type, currentValues);
                }
            }
        }
    }
    private static void groupExternalStats(Map<PlayerStats, Map<ValueTypes, int[]>> compiledStats, UUID playerID){
        Profile playerProfile = JSONProfileManager.getProfile(playerID);
        Map<PlayerStats, Map<ValueTypes, int[]>> externalStats = playerProfile.getStats().getExternalStats();
        mergeStatMaps(compiledStats, externalStats);

        //After grouping any external stats, lets get and add all buff stats
        Utils.log("Grouping buff stats!");
        Map<PlayerStats, Map<ValueTypes, int[]>> buffStats = PlayerBuffManager.getBuffStatsFor(playerID);
        if (!buffStats.isEmpty()){
            mergeStatMaps(compiledStats, buffStats);
        }
    }

    public static void mergeStatMaps(Map<PlayerStats, Map<ValueTypes, int[]>> mainStatPool, Map<PlayerStats, Map<ValueTypes, int[]>> addedStats){
        Utils.log("Merging statmaps!");
        for (PlayerStats stat : addedStats.keySet()){
            Map<ValueTypes, int[]> valueTypeMap = addedStats.get(stat);
            for (ValueTypes type : valueTypeMap.keySet()){
                int[] currentValues = valueTypeMap.get(type).clone();
                Utils.log("Grouping external stat! " + stat + " " + type + ": " + Arrays.toString(currentValues));
                addStat(mainStatPool, stat, type, currentValues);
            }
        }
    }
}

        /*
        The value that needs to be compiled comes directly from the modifiers.yml table.
        We need to fetch the current modifier's tier from the table and map the value ranges based on the mod's
        base percentile (0-1)
        Ex:
            ARMOR_WARD Tier 3, Item's BP = 0.95
                  - 10    | ARMOR RANGE
                  - 14    |
                  - 18          | WARD RANGE
                  - 20          |
            ArmorValue = A value thats 95% of the way between 10 - 14 = roundedParametricValue(10, 14, 0.95) = 14
            WardValue = roundedParametricValue(18,20, 0.95)

            int[] wantedFinalArray = [ArmorValue, WardValue]


        For each item:
            1:Get all local stats
            2:Compile them directly into the map/pool
            3:Comiple the rest of the global mods into the general stat pool

        */