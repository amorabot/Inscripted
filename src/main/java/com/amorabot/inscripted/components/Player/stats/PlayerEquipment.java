package com.amorabot.inscripted.components.Player.stats;

import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.modifiers.Inscription;
import com.amorabot.inscripted.components.Items.modifiers.data.KeystoneData;
import com.amorabot.inscripted.components.Items.modifiers.data.ModifierData;
import com.amorabot.inscripted.components.Items.modifiers.data.UniqueEffectData;
import com.amorabot.inscripted.components.Items.modifiers.unique.Effects;
import com.amorabot.inscripted.components.Items.modifiers.unique.Keystones;
import com.amorabot.inscripted.components.Player.ItemSlotData;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerEquipment {

    //TODO: make a super-object to encapsulate all equipment data per slot
    private final Map<ItemTypes, ItemSlotData> slots;
    private final Map<ItemTypes, StatPool> slotStats = new HashMap<>();
    @Setter
    @Getter
    private Map<ItemTypes, Set<Keystones>> slotKeystones = new HashMap<>();
    @Setter
    @Getter
    private Map<ItemTypes, Set<Inscription>> metaInscriptions = new HashMap<>();
    @Setter
    @Getter
    private Map<ItemTypes, Set<Effects>> slotEffects = new HashMap<>();

    public PlayerEquipment(){
        this.slots = new HashMap<>();
        for (ItemTypes slotType : ItemTypes.values()){
            this.slots.put(slotType, new ItemSlotData());
        }
    }


    public boolean setSlot(ItemTypes slotType, Item itemData){
        if (slotType == null){
            Utils.error("Invalid slotType trying to be equipped (null)");
            return false;
        }
        //Slot type is set, data can be null
        ItemSlotData selectedSlot = slots.get(slotType);
        if (itemData == null){
            selectedSlot.setItemHash(null);
            return true;
        }
        //Data is not null and can be further checked
        if (!slotType.equals(itemData.getCategory())){
            Utils.error("Slot type: " + slotType + " doesn't match item: " + itemData.getCategory());
            return false;
        }
        boolean shouldRecompileItem = selectedSlot.setItemHash(itemData);
        if (shouldRecompileItem){
            StatPool itemStats = new StatPool(); //Each slot will store a stat pool that can be later merged
            StatCompiler.addLocalStatsTo(itemStats, itemData);
            StatCompiler.compileItemInscriptionStats(itemStats, itemData);

            slotKeystones.remove(slotType);
            metaInscriptions.remove(slotType);
            slotEffects.remove(slotType);

            Set<Keystones> itemKeystones = new HashSet<>();
            Set<Effects> itemEffects = new HashSet<>();
            Set<Inscription> metaInsc = new HashSet<>();
            for (Inscription inscription : itemData.getInscriptionList()){
                ModifierData inscData = inscription.getInscription().getData();
                if (inscData.isKeystone()){
                    KeystoneData keystoneData = (KeystoneData) inscData;
                    itemKeystones.add(keystoneData.keystone());
                }
                if(inscData.isUniqueEffect()){
                    UniqueEffectData effectData = (UniqueEffectData) inscData;
                    itemEffects.add(effectData.uniqueEffect());
                }
                if (inscription.getInscription().isMeta()){
                    metaInsc.add(inscription);
                }
            }

            slotKeystones.put(slotType, itemKeystones);
            slotEffects.put(slotType, itemEffects);
            metaInscriptions.put(slotType, metaInsc);

            slotStats.put(slotType, itemStats);
        }
        return true;
    }

    public ItemSlotData getSlot(ItemTypes itemSlot){
        return slots.get(itemSlot);
    }
    public StatPool getSlotStats(ItemTypes itemSlot){
        return slotStats.get(itemSlot);
    }

}
