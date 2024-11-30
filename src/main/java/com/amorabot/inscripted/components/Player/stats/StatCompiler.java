package com.amorabot.inscripted.components.Player.stats;

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
import com.amorabot.inscripted.components.Player.Attributes;
import com.amorabot.inscripted.components.Player.ItemSlotData;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.components.Player.StatsComponent;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerBuffManager;
import com.amorabot.inscripted.managers.PlayerPassivesManager;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class StatCompiler {

    public static void updateProfile(UUID playerID){
        Utils.log("----=| Compiling Stats |=----");
        Profile targetProfile = JSONProfileManager.getProfile(playerID);
        PlayerEquipment equipment = targetProfile.getEquipmentComponent();

        //Compile equipment stats then compile external stats
        StatPool playerStatPool = new StatPool();
        addBaseStatsTo(playerStatPool);

        Set<Keystones> playerKeystones = compileKeystones(equipment);
        Set<Effects> playerEffects = compileUniqueEffects(equipment);

        manageKeystoneTasks(playerID, playerKeystones);

        //Apply early compilation keystones
        applyKeystones(playerID, playerKeystones, TriggerTimes.EARLY);


        //After local stats are compiled, group all equipment slots
        groupEquipmentStatsInto(playerStatPool, equipment);

        //Lets reset the external stat container and see what external sources of stats are valid to be recompiled
        targetProfile.getStatsComponent().getExternalStats().getStats().clear();
        //Compiles every external stat into the corresponding statPool
        addExternalStatsFromKeystones(playerID);
        groupExternalStats(playerID);

        //Once most stats were added, lets add the meta stats resulting values
        addMetaStatsFrom(equipment, playerStatPool);

        //Compiled stats are now available for all components to consume in the build step
        targetProfile.getStatsComponent().setPlayerStats(playerStatPool);
        targetProfile.setKeystones(playerKeystones);
        targetProfile.setEffects(playerEffects);

        buildProfile(targetProfile, playerID);

        //Apply late compilation keystones
        applyKeystones(playerID, playerKeystones, TriggerTimes.LATE);
    }

    public static void addLocalStatsTo(StatPool playerStats, Item itemData){
        if (itemData instanceof Weapon weaponData){
            Map<DamageTypes, int[]> localDamage = weaponData.getLocalDamage();
            playerStats.addStat(PlayerStats.PHYSICAL_DAMAGE, ValueTypes.FLAT, localDamage.get(DamageTypes.PHYSICAL));
            if (localDamage.containsKey(DamageTypes.FIRE)){
                playerStats.addStat(PlayerStats.FIRE_DAMAGE, ValueTypes.FLAT, localDamage.get(DamageTypes.FIRE));
            }
            if (localDamage.containsKey(DamageTypes.LIGHTNING)){
                playerStats.addStat(PlayerStats.LIGHTNING_DAMAGE, ValueTypes.FLAT, localDamage.get(DamageTypes.LIGHTNING));
            }
            if (localDamage.containsKey(DamageTypes.COLD)){
                playerStats.addStat(PlayerStats.COLD_DAMAGE, ValueTypes.FLAT, localDamage.get(DamageTypes.COLD));
            }
            if (localDamage.containsKey(DamageTypes.ABYSSAL)){
                playerStats.addStat(PlayerStats.ABYSSAL_DAMAGE, ValueTypes.FLAT, localDamage.get(DamageTypes.ABYSSAL));
            }
        }
        if (itemData instanceof Armor armorData){
            Map<DefenceTypes, Integer> localDefences = armorData.getLocalDefences();
            playerStats.addStat(PlayerStats.HEALTH, ValueTypes.FLAT, new int[]{localDefences.get(DefenceTypes.HEALTH)}); //Life should be always present
            playerStats.addStat(PlayerStats.WARD, ValueTypes.FLAT, new int[]{localDefences.getOrDefault(DefenceTypes.WARD,0)});
            playerStats.addStat(PlayerStats.ARMOR, ValueTypes.FLAT, new int[]{localDefences.getOrDefault(DefenceTypes.ARMOR,0)});
            playerStats.addStat(PlayerStats.DODGE, ValueTypes.FLAT, new int[]{localDefences.getOrDefault(DefenceTypes.DODGE,0)});
        }
    }

    private static void addMetaStatsFrom(PlayerEquipment equipment, StatPool statPool){
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
                    if (statPool.getStats().containsKey(targetConvertedStat)){
                        if (statPool.getStats().get(targetConvertedStat).containsKey(type)){
                            int convertedStat = (int) statPool.getFinalValueFor(targetConvertedStat, false);
                            int[] baseMetaValue = insc.getMappedFinalValue();
                            int[] stackedMetaValue = ID.convert(convertedStat, baseMetaValue);
                            statPool.addStat(finalConvertedStat, finalStatType, stackedMetaValue);
//                            Utils.error("CONVERTED BASE STAT:" + convertedStat);
//                            Utils.log(Arrays.toString(stackedMetaValue));
                        }
                    }
                }
            }
        }
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


    private static void addBaseStatsTo(StatPool statPool) {
        statPool.addStat(PlayerStats.HEALTH, ValueTypes.FLAT, new int[]{BaseStats.HEALTH.getValue()});
        statPool.addStat(PlayerStats.HEALTH_REGEN, ValueTypes.FLAT, new int[]{BaseStats.HEALTH_REGEN.getValue()});
        statPool.addStat(PlayerStats.WARD_RECOVERY_RATE, ValueTypes.PERCENT, new int[]{BaseStats.WARD_RECOVERY_RATE.getValue()});
        statPool.addStat(PlayerStats.STAMINA, ValueTypes.FLAT, new int[]{BaseStats.STAMINA.getValue()});
        statPool.addStat(PlayerStats.STAMINA_REGEN, ValueTypes.FLAT, new int[]{BaseStats.STAMINA_REGEN.getValue()});
        statPool.addStat(PlayerStats.WALK_SPEED, ValueTypes.FLAT, new int[]{BaseStats.WALK_SPEED.getValue()});
    }


    private static void buildProfile(Profile targetProfile, UUID playerID){
        Attributes attributes = targetProfile.getAttributes();
        StatsComponent statsComponent = targetProfile.getStatsComponent();
        DefenceComponent defences = targetProfile.getDefenceComponent();
        HealthComponent health = targetProfile.getHealthComponent();
        DamageComponent damage = targetProfile.getDamageComponent();

        //The update order is defined here
        attributes.update(playerID); //Relies on the current state of the Stats object (Its already been set)
        statsComponent.update(playerID); //Stats is accessed directly, has a non-conventional update() and is the basis for other components states
        defences.update(playerID);
        health.update(playerID);
        damage.update(playerID);

        statsComponent.debug();
    }
    public static void compileItemInscriptionStats(StatPool statPool, Item item){
        List<Inscription> inscriptions = item.getInscriptionList();
        for (Inscription inscription : inscriptions){
            if (!inscription.getInscription().isGlobal()){continue;}
            if (inscription.getInscription().isMeta()){continue;}
            statPool.add(inscription);
        }
        statPool.add(item.getImplicit());
    }

    private static void addExternalStatsFromKeystones(UUID playerID){
        Profile playerProfile = JSONProfileManager.getProfile(playerID);
        StatsComponent playerStatsComponent = playerProfile.getStatsComponent();
        for (Keystones statKeystone : playerStatsComponent.getActiveStatKeystones()){
            if (!statKeystone.isStatKeystone()){continue;}
            statKeystone.addExternalStat(playerID);
        }
    }

    private static void groupEquipmentStatsInto(StatPool statPool, PlayerEquipment playerEquipment){
        for (ItemTypes slot : ItemTypes.values()) {
            if (playerEquipment.getSlot(slot).isIgnorable()){continue;}
            Utils.log("Grouping stats for: " + slot);
            StatPool slotStats = playerEquipment.getSlotStats(slot);
            if (slotStats == null){continue;}
            statPool.merge(slotStats);
//            statPool.debug("After " + slot.toString());
        }
    }
    private static void groupExternalStats(UUID playerID){
        Profile playerProfile = JSONProfileManager.getProfile(playerID);
        StatPool externalStats = playerProfile.getStatsComponent().getExternalStats(); //

        //After grouping any external stats, lets get and add all buff stats
        Utils.log("Grouping buff stats!");
        StatPool buffStats = PlayerBuffManager.getBuffStatsFor(playerID);
        if (!buffStats.getStats().isEmpty()){
            externalStats.merge(buffStats);
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