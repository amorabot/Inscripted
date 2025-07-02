package com.amorabot.inscripted.player.equipment;

import com.amorabot.inscripted.item.inscription.ProceduralInscription;
import com.amorabot.inscripted.item.inscription.definition.EffectIDs;
import com.amorabot.inscripted.item.inscription.definition.KeystoneIDs;
import com.amorabot.inscripted.item.structure.EquipmentSlots;
import com.amorabot.inscripted.item.structure.Item;
import com.amorabot.inscripted.player.Observer;
import com.amorabot.inscripted.player.Subject;
import com.amorabot.inscripted.player.profile.ProfileEvents;
import com.amorabot.inscripted.utils.DelayedTask;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class PlayerEquipment {

    private final Subject profileSubject = new Subject();
    private boolean locked = true;
    private final Map<EquipmentSlots, EquimentSlotData> equipmentData = new HashMap<>();

    private final Set<EffectIDs> effects = new HashSet<>();
    private final Set<KeystoneIDs> keystones = new HashSet<>();
    private final Set<ProceduralInscription> metaInscriptions = new HashSet<>();

    public PlayerEquipment(Observer observer){
        for (EquipmentSlots slot : EquipmentSlots.values()){
            equipmentData.put(slot,new EquimentSlotData());
        }
        profileSubject.addObserver(observer);
    }

    public void updateEquimentSlot(EquipmentSlots slot, Item itemData){
        if (locked){
            locked = false;
            //Update internal data & trigger a delayed equipment change
            if (slot == null){
                locked = true;
                return;
            }
            //This aims to prevent very fast equipment recompilations
            //Check if it's necessary to trigger a equipment change
            EquimentSlotData currentSlotData = equipmentData.get(slot);
            if (itemData==null){
                Utils.log("Unequipping " + slot.name());
                if (currentSlotData.isIgnorable()){
                    /*
                     Already unequipped/ignored, this prevents a un-needed recompilation trigger
                     since the armorSlot state didn't change and wouldn't be compiled anyway
                    */
                    locked = true;
                    return;
                }
            }
            if (itemData==null && currentSlotData.isIgnorable()){
                //If the incoming item is not equippable and the stored data is already ignored
                locked = true;
                return;
            }
            currentSlotData.update(itemData);
            new DelayedTask(new BukkitRunnable() {
                @Override
                public void run() {
                    //After 3Ticks, apply changes
                    profileSubject.notifyListeners(ProfileEvents.EQUIPMENT_CHANGE);
                    //Re-lock so it can be accessed later
                    locked=true;
                }
            }, 2L);
            return;
        }
        Utils.error("Equipment currently being accessed, changes will be applied soon...");
        //Only update internal data
        EquimentSlotData currentSlotData = equipmentData.get(slot);
        //A unnecessary update here would only reset 'ignored' to true, so its fine
        currentSlotData.update(itemData);
    }

    public void updateSpecialInscriptions(){
        updateEquipmenEffects();
        updateEquipmenKeystones();
        updateEquipmentMetaInscriptions();
    }

    private void updateEquipmentMetaInscriptions(){
        metaInscriptions.clear();
        getEquipmentData().forEach(
                (slot, slotData) -> metaInscriptions.addAll(slotData.getMetaInscriptions())
        );
    }
    private void updateEquipmenEffects(){
        effects.clear();
        getEquipmentData().forEach(
                (slot, slotData) -> effects.addAll(slotData.getItemEffects())
        );
    }
    private void updateEquipmenKeystones(){
        effects.clear();
        getEquipmentData().forEach(
                (slot, slotData) -> keystones.addAll(slotData.getItemKeystones())
        );
    }
}
