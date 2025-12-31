package com.amorabot.inscripted.player.profile.component;

import com.amorabot.inscripted.item.inscription.Inscription;
import com.amorabot.inscripted.item.inscription.definition.EffectIDs;
import com.amorabot.inscripted.item.inscription.definition.KeystoneIDs;
import com.amorabot.inscripted.item.structure.EquipmentSlots;
import com.amorabot.inscripted.player.equipment.EquimentSlotData;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@NoArgsConstructor
public class SpecialInscriptionsComponent {
    private final Set<EffectIDs> effects = new HashSet<>();
    private final Set<KeystoneIDs> keystones = new HashSet<>();
    private final Set<Inscription> metaInscriptions = new HashSet<>();

    public SpecialInscriptionsComponent(Map<EquipmentSlots, EquimentSlotData> playerEquipment){
        update(playerEquipment);
    }
    public SpecialInscriptionsComponent(Set<EffectIDs> effects,Set<KeystoneIDs> keystones,Set<Inscription> metaInscriptions){
        this.effects.addAll(effects);
        this.keystones.addAll(keystones);
        this.metaInscriptions.addAll(metaInscriptions);
    }

    public SpecialInscriptionsComponent snapshot(){
        return new SpecialInscriptionsComponent(effects,keystones,metaInscriptions);
    }
    public void clear(){
        this.effects.clear();
        this.keystones.clear();
        this.metaInscriptions.clear();
    }

    public void update(Map<EquipmentSlots, EquimentSlotData> playerEquipment){
        updateEquipmenEffects(playerEquipment);
        updateEquipmenKeystones(playerEquipment);
        updateEquipmentMetaInscriptions(playerEquipment);
    }

    private void updateEquipmentMetaInscriptions(Map<EquipmentSlots, EquimentSlotData> playerEquipment){
        metaInscriptions.clear();
        playerEquipment.forEach(
                (slot, slotData) -> metaInscriptions.addAll(slotData.getItemMetaInscriptions())
        );
    }
    private void updateEquipmenEffects(Map<EquipmentSlots, EquimentSlotData> playerEquipment){
        effects.clear();
        playerEquipment.forEach(
                (slot, slotData) -> effects.addAll(slotData.getItemEffects())
        );
    }
    private void updateEquipmenKeystones(Map<EquipmentSlots, EquimentSlotData> playerEquipment){
        keystones.clear();
        playerEquipment.forEach(
                (slot, slotData) -> keystones.addAll(slotData.getItemKeystones())
        );
    }
}
