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
import com.amorabot.inscripted.components.Items.modifiers.unique.Keystones;
import com.amorabot.inscripted.components.Items.modifiers.unique.TriggerTimes;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.utils.Utils;

import java.util.*;

public class StatCompiler {

    private final Profile targetProfile;
    private final UUID playerID;

    public StatCompiler(UUID playerProfileID){
        targetProfile = JSONProfileManager.getProfile(playerProfileID);
        playerID = playerProfileID;
    }


    public void updateProfile(){
        Utils.log("-----STARTING COMPILATION FOR-----");
        PlayerEquipment equipment = targetProfile.getEquipmentComponent();

        Map<PlayerStats, Map<ValueTypes, int[]>> compiledStats = new HashMap<>();
        addBaseStatsTo(compiledStats);
        Set<Keystones> playerKeystones = compileKeystones(equipment);

        //Apply early compilation keystones
        for (Keystones earlyKeystone : playerKeystones){
            if (!earlyKeystone.getCompilationTime().equals(TriggerTimes.EARLY)){continue;}
            Utils.log("Applying " + earlyKeystone);
            earlyKeystone.apply(targetProfile);
        }


        //Compiling Armor data
        Armor[] equippedArmorSet = equipment.getArmorSet();
        for (Armor equippedArmor : equippedArmorSet){
            if (equippedArmor == null){
                continue;
            }
            Utils.log(equippedArmor.getName());

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
            Map<DefenceTypes, Integer> localDefences = equippedArmor.getLocalDefences();
            addStat(compiledStats, PlayerStats.HEALTH, ValueTypes.FLAT, new int[]{localDefences.get(DefenceTypes.HEALTH)}); //Life should be always present
            addStat(compiledStats, PlayerStats.WARD, ValueTypes.FLAT, new int[]{localDefences.getOrDefault(DefenceTypes.WARD,0)});
            addStat(compiledStats, PlayerStats.ARMOR, ValueTypes.FLAT, new int[]{localDefences.getOrDefault(DefenceTypes.ARMOR,0)});
            addStat(compiledStats, PlayerStats.DODGE, ValueTypes.FLAT, new int[]{localDefences.getOrDefault(DefenceTypes.DODGE,0)});

            compileItem(compiledStats, equippedArmor);

        }
        //Compiling Weapon data
        EquipmentSlot weaponSlot = equipment.getSlot(ItemTypes.WEAPON);
        if (!weaponSlot.isIgnorable()){ //If the weapon data is not ignorable:
            Weapon equippedWeapon = equipment.getWeaponData();
            Utils.log(equippedWeapon.getName());

            Map<DamageTypes, int[]> localDamage = equippedWeapon.getLocalDamage();
            addStat(compiledStats, PlayerStats.PHYSICAL_DAMAGE, ValueTypes.FLAT, localDamage.get(DamageTypes.PHYSICAL));
            if (localDamage.containsKey(DamageTypes.FIRE)){
                addStat(compiledStats, PlayerStats.FIRE_DAMAGE, ValueTypes.FLAT, localDamage.get(DamageTypes.FIRE));
            }
            if (localDamage.containsKey(DamageTypes.LIGHTNING)){
                addStat(compiledStats, PlayerStats.LIGHTNING_DAMAGE, ValueTypes.FLAT, localDamage.get(DamageTypes.LIGHTNING));
            }
            if (localDamage.containsKey(DamageTypes.COLD)){
                addStat(compiledStats, PlayerStats.COLD_DAMAGE, ValueTypes.FLAT, localDamage.get(DamageTypes.COLD));
            }
            if (localDamage.containsKey(DamageTypes.ABYSSAL)){
                addStat(compiledStats, PlayerStats.ABYSSAL_DAMAGE, ValueTypes.FLAT, localDamage.get(DamageTypes.ABYSSAL));
            }


            compileItem(compiledStats, equippedWeapon);
        }

        //Compiled stats are now available for all components to consume in the build step
        targetProfile.getStats().setPlayerStats(compiledStats);
        targetProfile.setKeystones(playerKeystones);

        buildProfile();

        //Apply late compilation keystones
        for (Keystones lateKeystone : playerKeystones){
            if (!lateKeystone.getCompilationTime().equals(TriggerTimes.LATE)){continue;}
            Utils.log("Applying " + lateKeystone);
            lateKeystone.apply(targetProfile);
        }
    }

    private Set<Keystones> compileKeystones(PlayerEquipment equipment) {
        Set<Keystones> playerKeystones = new HashSet<>();

        Armor[] armorSet = equipment.getArmorSet();
        for (Armor equippedArmor : armorSet){
            if (equippedArmor == null){
                continue;
            }
            List<Inscription> armorInscriptions = equippedArmor.getInscriptionList();
            for (Inscription inscription : armorInscriptions){
                addKeystoneFrom(inscription, playerKeystones);
            }
        }

        if (!equipment.getSlot(ItemTypes.WEAPON).isIgnorable()){
            for (Inscription weaponInsc : equipment.getWeaponData().getInscriptionList()){
                addKeystoneFrom(weaponInsc, playerKeystones);
            }
        }

        return playerKeystones;
    }
    private void addKeystoneFrom(Inscription inscription, Set<Keystones> keystoneSet){
        ModifierData inscData = inscription.getInscription().getData();
        if (inscData.isKeystone()){
            KeystoneData keystoneData = (KeystoneData) inscData;
            keystoneSet.add(keystoneData.keystone());
        }
    }

    private void addBaseStatsTo(Map<PlayerStats, Map<ValueTypes, int[]>> compiledStats) {
        putSingleStatIn(compiledStats, PlayerStats.HEALTH, ValueTypes.FLAT, BaseStats.HEALTH.getValue());
        putSingleStatIn(compiledStats, PlayerStats.HEALTH_REGEN, ValueTypes.FLAT, BaseStats.HEALTH_REGEN.getValue());
        putSingleStatIn(compiledStats, PlayerStats.WARD_RECOVERY_RATE, ValueTypes.PERCENT, BaseStats.WARD_RECOVERY_RATE.getValue());
        putSingleStatIn(compiledStats, PlayerStats.STAMINA, ValueTypes.FLAT, BaseStats.STAMINA.getValue());
        putSingleStatIn(compiledStats, PlayerStats.STAMINA_REGEN, ValueTypes.FLAT, BaseStats.STAMINA_REGEN.getValue());
        putSingleStatIn(compiledStats, PlayerStats.WALK_SPEED, ValueTypes.FLAT, BaseStats.WALK_SPEED.getValue());
    }
    private void putSingleStatIn(Map<PlayerStats, Map<ValueTypes, int[]>> compiledStats, PlayerStats targetStat, ValueTypes type, int value){
        if (compiledStats.containsKey(targetStat)){
            compiledStats.get(targetStat).put(type, new int[]{value});
            return;
        }
        Map<ValueTypes, int[]> newStatMap = new HashMap<>();
        newStatMap.put(type, new int[]{value});
        compiledStats.put(targetStat, newStatMap);
    }


    private void buildProfile(){
        Attributes attributes = targetProfile.getAttributes();
        Stats stats = targetProfile.getStats();
        DefenceComponent defences = targetProfile.getDefenceComponent();
        HealthComponent health = targetProfile.getHealthComponent();
        DamageComponent damage = targetProfile.getDamageComponent();

        //The update order is defined here
        attributes.update(playerID); //Relies on the current state of the Stats object
        stats.update(playerID); //Stats is accessed directly, has a non-conventional update() and is the basis for other components states
        defences.update(playerID);
        health.update(playerID);
        damage.update(playerID);

        stats.debug();
    }

    private void addStat(Map<PlayerStats, Map<ValueTypes, int[]>> compiledStats, PlayerStats stat, ValueTypes type, int[] values){
        if (!compiledStats.containsKey(stat)){
            Map<ValueTypes, int[]> newValueTypeMap = new HashMap<>();
            newValueTypeMap.put(type, values);
            compiledStats.put(stat, newValueTypeMap);
            return;
        }
        if (!compiledStats.get(stat).containsKey(type)){
            compiledStats.get(stat).put(type, values);
        }
        int[] storedValues = compiledStats.get(stat).get(type).clone();
        int[] updatedValues = Utils.vectorSum(storedValues, values);
        compiledStats.get(stat).put(type, updatedValues);
    }
    private void compileItem(Map<PlayerStats, Map<ValueTypes, int[]>> compiledStats, Item item){
        List<Inscription> inscriptions = item.getInscriptionList();
        for (Inscription inscription : inscriptions){
            if (!inscription.getInscription().isGlobal()){continue;}
            compileIndividualInscriptionInto(compiledStats, inscription);
        }
        compileIndividualInscriptionInto(compiledStats, item.getImplicit());
    }
    private void compileIndividualInscriptionInto(Map<PlayerStats, Map<ValueTypes, int[]>> compiledStats, Inscription inscription){
        InscriptionID inscID = inscription.getInscription();
        ModifierData inscData = inscID.getData();
        if (inscData.isStandard()){
            InscriptionData regularInsc = (InscriptionData) inscData;
            StatDefinition def = regularInsc.getDefinitionData();
            PlayerStats targetStat = def.stat();
            ValueTypes type = def.valueType();
            int[] mappedInscriptionValues = inscription.getMappedFinalValue();
            Utils.error("Compiling " + targetStat + ": " + Arrays.toString(mappedInscriptionValues));

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
        //TODO: Unique inscriptions de-serialization here
    }
}
//            int[] tableValues = InscriptionID.fetchValuesFor(inscription);
//            int[] mappedInscriptionValues = RangeTypes.mapFinalValuesFor(rangeType, tableValues, inscription.getBasePercentile());