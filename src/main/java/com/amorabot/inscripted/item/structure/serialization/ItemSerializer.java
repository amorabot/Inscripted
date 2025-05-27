package com.amorabot.inscripted.item.structure.serialization;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.item.structure.Armor.Armor;
import com.amorabot.inscripted.item.structure.Weapon.Weapon;
import com.amorabot.inscripted.item.structure.Item;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ItemSerializer implements ItemDataContainerVisitor<Item> {

    // Item-state keys
    public static final NamespacedKey IDENTIFIED = new NamespacedKey(Inscripted.getPlugin(),"IDed");
    //TODO: add corrupted state

    @Override
    public Item visitWeapon(ItemStack itemForm, Weapon weaponData) {
        ItemMeta itemMeta = itemForm.getItemMeta();
        PersistentDataContainer itemPDC = itemMeta.getPersistentDataContainer();
        itemPDC.set(weaponData.getKey(),new GenericItemContainerDataType<>(Weapon.class),weaponData);
        itemForm.setItemMeta(itemMeta);

        setIdentifiedState(itemForm,weaponData.isIdentified());

        return weaponData;
    }

    @Override
    public Item visitArmor(ItemStack itemForm, Armor armorData) {
        ItemMeta itemMeta = itemForm.getItemMeta();
        PersistentDataContainer itemPDC = itemMeta.getPersistentDataContainer();
        itemPDC.set(armorData.getKey(),new GenericItemContainerDataType<>(Armor.class),armorData);
        itemForm.setItemMeta(itemMeta);

        setIdentifiedState(itemForm,armorData.isIdentified());

        return armorData;
    }

    public static void setIdentifiedState(ItemStack item, boolean identified){
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer itemPDC = itemMeta.getPersistentDataContainer();
        itemPDC.set(IDENTIFIED, new PersistentDataType.BooleanPersistentDataType(), identified);
        item.setItemMeta(itemMeta);
    }
    public static boolean isIdentified(ItemStack item){
        if (!ItemDeserializer.checkDataContainer(item,IDENTIFIED)){
            return false;
        }
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer itemPDC = itemMeta.getPersistentDataContainer();
        return Boolean.TRUE.equals(itemPDC.get(IDENTIFIED, new PersistentDataType.BooleanPersistentDataType()));
    }
}
