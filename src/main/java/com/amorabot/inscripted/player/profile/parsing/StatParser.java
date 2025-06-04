package com.amorabot.inscripted.player.profile.parsing;

import com.amorabot.inscripted.item.inscription.Inscription;
import com.amorabot.inscripted.item.inscription.definition.EffectIDs;
import com.amorabot.inscripted.item.inscription.definition.InscriptionIDs;
import com.amorabot.inscripted.item.inscription.definition.KeystoneIDs;
import com.amorabot.inscripted.item.inscription.definition.Stats;
import com.amorabot.inscripted.player.equipment.PlayerEquipment;
import com.amorabot.inscripted.player.profile.Profile;
import com.amorabot.inscripted.utils.Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StatParser {

    private static final boolean DEBUG_MODE = true;

    public static void buildProfile(Profile profile, PlayerEquipment equipment){
        Utils.log("Building profile!");
        //Getting global stats
        StatPool globalStatPool = compileEquipmentStats(equipment); //Raw global stats
        Set<EffectIDs> effects = getEquipmenEffects(equipment);
        Set<KeystoneIDs> keystones = getEquipmenKeystones(equipment);

    }

    public static StatPool compileEquipmentStats(PlayerEquipment playerEquipment){
        StatPool globalStatPool = new StatPool();
        playerEquipment.getEquipmentData().forEach(
                (slot, slotData) -> {
                    if (slotData.isIgnorable()){return;}
                    StatPool slotStats = slotData.getEquipmentStats();
                    if (slotStats == null){return;}
                    if (DEBUG_MODE){Utils.log("Compiling " + slot);}
                    globalStatPool.merge(slotStats);
                }
        );
        //TODO: Sort meta inscriptions for predictability?
        Set<Inscription> metaInscriptions = getEquipmentMetaInscriptions(playerEquipment);
        for (Inscription metaInsc : metaInscriptions){
            /*
            Snapshot converted value
            convert it
            add the final stat to the global map via insertValue()
            */
        }
        return globalStatPool;
    }
    public static Set<Inscription> getEquipmentMetaInscriptions(PlayerEquipment playerEquipment){
        Set<Inscription> metaInscriptions = new HashSet<>();
        playerEquipment.getEquipmentData().forEach(
                (slot, slotData) -> metaInscriptions.addAll(slotData.getMetaInscriptions())
        );
        return metaInscriptions;
    }
    public static Set<EffectIDs> getEquipmenEffects(PlayerEquipment playerEquipment){
        Set<EffectIDs> effects = new HashSet<>();
        playerEquipment.getEquipmentData().forEach(
                (slot, slotData) -> effects.addAll(slotData.getItemEffects())
        );
        return effects;
    }
    public static Set<KeystoneIDs> getEquipmenKeystones(PlayerEquipment playerEquipment){
        Set<KeystoneIDs> keystones = new HashSet<>();
        playerEquipment.getEquipmentData().forEach(
                (slot, slotData) -> keystones.addAll(slotData.getItemKeystones())
        );
        return keystones;
    }

    public static Set<EffectIDs> getEffects(List<Inscription> itemInscriptions){
        Set<EffectIDs> mappedEffects = new HashSet<>();
        for (Inscription inscription : itemInscriptions){
            if (!inscription.isSpecial()){continue;}
            // Map Insc.IDs -> EffectIDs
            InscriptionIDs insc = inscription.getInscription();
            try {
                EffectIDs mappedEffect = EffectIDs.valueOf(insc.name());
                mappedEffects.add(mappedEffect);
            } catch (IllegalArgumentException ex){
                Utils.error("Unable to parse Effect: " + insc.name());
            }
        }
        return mappedEffects;
    }
    public static Set<KeystoneIDs> getKeystones(List<Inscription> itemInscriptions){
        Set<KeystoneIDs> mappedKeystones = new HashSet<>();
        for (Inscription inscription : itemInscriptions){
            if (!inscription.isSpecial()){continue;}
            // Map Insc.IDs -> KeystoneIDs
            InscriptionIDs insc = inscription.getInscription();
            try {
                KeystoneIDs mappedKeystone = KeystoneIDs.valueOf(insc.name());
                mappedKeystones.add(mappedKeystone);
            } catch (IllegalArgumentException ex){
                Utils.error("Unable to parse Keystone: " + insc.name());
            }
        }
        return mappedKeystones;
    }
    public static Set<Inscription> filterMetaInscriptions(List<Inscription> itemInscriptions){
        return itemInscriptions.stream().filter(
                inscription -> {
                    return inscription.getInscription().hasMetadata();
                }
        ).collect(Collectors.toSet());
    }
}
