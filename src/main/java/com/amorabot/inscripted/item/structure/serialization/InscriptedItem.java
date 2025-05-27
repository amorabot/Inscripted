package com.amorabot.inscripted.item.structure.serialization;

import com.amorabot.inscripted.Inscripted;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public interface InscriptedItem {
    ItemStack getItemForm();
    NamespacedKey getKey();
    void serializeDataContainerInto(ItemStack itemStack);
//    InscriptedItem deserializeGenericData(ItemStack itemStack);

    default void tag(ItemStack itemToTag){
        if (!hasInscriptedTag(itemToTag)){
            return;
        }
        setUsableState(itemToTag,true);
    }
    default boolean hasInscriptedTag(ItemStack item){
        NamespacedKey inscriptedTag = new NamespacedKey(Inscripted.getPlugin(),"TAG");
        return item.getItemMeta().getPersistentDataContainer().has(inscriptedTag, new PersistentDataType.BooleanPersistentDataType());
    }
    default void setUsableState(ItemStack item, boolean usable){
        NamespacedKey inscriptedTag = new NamespacedKey(Inscripted.getPlugin(),"TAG");
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer itemPDC = itemMeta.getPersistentDataContainer();
        // Can be used as a "isUsable" value | true by default on tag(),
        itemPDC.set(inscriptedTag, new PersistentDataType.BooleanPersistentDataType(), usable);

        item.setItemMeta(itemMeta);
    }
}
