package com.amorabot.inscripted.item.structure.io;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.item.inscription.definition.Stats;
import com.amorabot.inscripted.player.profile.parsing.StatPool;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public interface InscriptedItem {
    ItemStack getItemForm();
    NamespacedKey getKey();
    void serializeDataContainerInto(ItemStack itemStack);

    static void tag(ItemStack itemToTag){
        if (hasInscriptedTag(itemToTag)){
            return;
        }
        setUsableState(itemToTag,true);
    }
    static boolean hasInscriptedTag(ItemStack item){
        NamespacedKey inscriptedTag = new NamespacedKey(Inscripted.getPlugin(),"TAG");
        return item.getItemMeta().getPersistentDataContainer().has(inscriptedTag, new PersistentDataType.BooleanPersistentDataType());
    }
    static void setUsableState(ItemStack item, boolean usable){
        NamespacedKey inscriptedTag = new NamespacedKey(Inscripted.getPlugin(),"TAG");
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer itemPDC = itemMeta.getPersistentDataContainer();
        // Can be used as a "isUsable" value | true by default on tag(),
        itemPDC.set(inscriptedTag, new PersistentDataType.BooleanPersistentDataType(), usable);

        item.setItemMeta(itemMeta);
    }

    Map<Stats, int[]> getLocalStats();
    StatPool compile();
}
