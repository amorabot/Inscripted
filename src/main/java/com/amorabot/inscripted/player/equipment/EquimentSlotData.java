package com.amorabot.inscripted.player.equipment;

import com.amorabot.inscripted.item.inscription.Inscription;
import com.amorabot.inscripted.item.inscription.ProceduralInscription;
import com.amorabot.inscripted.item.inscription.definition.EffectIDs;
import com.amorabot.inscripted.item.inscription.definition.KeystoneIDs;
import com.amorabot.inscripted.item.structure.Item;
import com.amorabot.inscripted.player.profile.parsing.StatParser;
import com.amorabot.inscripted.player.profile.parsing.StatPool;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class EquimentSlotData {

    private StatPool equipmentStats;
    private Set<EffectIDs> itemEffects = new HashSet<>();
    private Set<KeystoneIDs> itemKeystones = new HashSet<>();
    private Set<ProceduralInscription> metaInscriptions = new HashSet<>();

    private int itemHash;
    private boolean ignore;

    public EquimentSlotData(){
        this.itemHash = 0;
        this.ignore = true;
        this.equipmentStats = null;
    }
    public EquimentSlotData(Item newItem){
        this.itemHash = newItem.hashCode();
        this.ignore = false;
        parseItem(newItem);
    }

    public void update(Item newItem){
        if (newItem == null){ // Newly equipment item is invalid, ignore that slot
            ignore=true;
            return;
        }
        final int newHash = newItem.hashCode();
        if (itemHash == 0){ // No previously stored data
            this.itemHash = newHash;
            this.ignore = false;
            parseItem(newItem);
            return;
        }
        // Already has a stored item hash
        if (itemHash == newHash){ // We are trying to equip an already cached item
            // Lets reset the ignored state
            this.ignore = false;
        } else { // Its a new incoming item, let's update the internal data
            this.itemHash = newHash;
            this.ignore = false;
            parseItem(newItem);
        }
    }

    public int getItemHash(){
        if (ignore){ return 0; }
        return this.itemHash;
    }
    public boolean isIgnorable(){
        return this.ignore;
    }

    private void parseItem(Item itemData){
        this.equipmentStats = itemData.compile();
        List<Inscription> itemInscriptions = itemData.getInscriptions();
        this.itemEffects = StatParser.getEffects(itemInscriptions);
        this.itemKeystones = StatParser.getKeystones(itemInscriptions);
        this.metaInscriptions = StatParser.filterMetaInscriptions(itemInscriptions);
    }
    public void clear(){
        this.equipmentStats = null;
        this.itemEffects.clear();
        this.itemKeystones.clear();
        this.metaInscriptions.clear();
    }
}
