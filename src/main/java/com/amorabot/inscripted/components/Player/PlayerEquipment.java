package com.amorabot.inscripted.components.Player;

import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.PlayerStats;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;
import com.amorabot.inscripted.components.Items.modifiers.Inscription;
import com.amorabot.inscripted.components.Items.modifiers.unique.Keystones;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerEquipment {

    private final Map<ItemTypes, EquipmentSlot> slots;
    private final Map<ItemTypes, Map<PlayerStats, Map<ValueTypes, int[]>>> slotStats = new HashMap<>();
    @Setter
    @Getter
    private Map<ItemTypes, Set<Keystones>> slotKeystones = new HashMap<>();
    @Setter
    @Getter
    private Map<ItemTypes, Set<Inscription>> metaInscriptions = new HashMap<>();

    public PlayerEquipment(){
        this.slots = new HashMap<>();
        for (ItemTypes slotType : ItemTypes.values()){
            this.slots.put(slotType, new EquipmentSlot());
        }
    }


    public boolean setSlot(ItemTypes slotType, Item itemData){
        if (slotType == null){
            Utils.error("Invalid slotType trying to be equipped (null)");
            return false;
        }
        //Slot type is set, data can be null
        EquipmentSlot selectedSlot = slots.get(slotType);
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
            Map<PlayerStats, Map<ValueTypes, int[]>> itemStats = new HashMap<>();
            StatCompiler.addLocalStatsTo(itemStats, itemData);
            StatCompiler.compileItem(itemStats, itemData);

            ItemTypes slot = itemData.getCategory();

            slotKeystones.remove(slot);
            metaInscriptions.remove(slot);
            Set<Keystones> itemKeystones = new HashSet<>();
            Set<Inscription> metaInsc = new HashSet<>();
            for (Inscription inscription : itemData.getInscriptionList()){
                StatCompiler.addKeystonesFrom(inscription, itemKeystones);
                if (inscription.getInscription().isMeta()){
                    metaInsc.add(inscription);
                }
            }

            slotKeystones.put(slot, itemKeystones);
            slotStats.put(slot, itemStats);
            metaInscriptions.put(slot, metaInsc);
        }
        return true;
    }

    public EquipmentSlot getSlot(ItemTypes itemSlot){
        return slots.get(itemSlot);
    }
    public Map<PlayerStats, Map<ValueTypes, int[]>> getSlotStats(ItemTypes itemSlot){
        return slotStats.get(itemSlot);
    }

}
