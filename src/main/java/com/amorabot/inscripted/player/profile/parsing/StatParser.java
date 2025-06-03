package com.amorabot.inscripted.player.profile.parsing;

import com.amorabot.inscripted.item.inscription.Inscription;
import com.amorabot.inscripted.item.inscription.definition.EffectIDs;
import com.amorabot.inscripted.item.inscription.definition.InscriptionIDs;
import com.amorabot.inscripted.item.inscription.definition.KeystoneIDs;
import com.amorabot.inscripted.utils.Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StatParser {


    //TODO: getGlobalStats(), getting data from

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
