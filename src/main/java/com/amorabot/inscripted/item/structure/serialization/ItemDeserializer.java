package com.amorabot.inscripted.item.structure.serialization;

import com.amorabot.inscripted.item.structure.Armor.Armor;
import com.amorabot.inscripted.item.structure.Item;
import com.amorabot.inscripted.item.structure.Weapon.Weapon;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Optional;

public class ItemDeserializer implements ItemDataContainerVisitor<Optional<Item>>{
    @Override
    public Optional<Item> visitWeapon(ItemStack itemForm, Weapon weaponData) {
        return Optional.of(getItemDataFromContainer(itemForm,Weapon.DATA_CONTAINER_KEY,Weapon.class));
    }
    @Override
    public Optional<Item> visitArmor(ItemStack itemForm, Armor armorData) {
        return Optional.of(getItemDataFromContainer(itemForm, Armor.DATA_CONTAINER_KEY,Armor.class));
    }

    public static <T extends Item> Item getItemDataFromContainer(ItemStack itemStack, NamespacedKey containerKey, Class<T> type){
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer itemPDC = itemMeta.getPersistentDataContainer();
        return itemPDC.get(containerKey, new GenericItemContainerDataType<>(type));
    }

    public static boolean checkDataContainer(ItemStack itemToCheck, NamespacedKey key){
        ItemMeta itemMeta = itemToCheck.getItemMeta();
        PersistentDataContainer itemPDC = itemMeta.getPersistentDataContainer();
        return itemPDC.has(key);
    }

    // Armor deserialization
    public static boolean isArmor(ItemStack item){
        return checkDataContainer(item, Armor.DATA_CONTAINER_KEY);
    }
    public Armor deserializeArmorData(ItemStack itemStack) {
        Optional<Item> itemData = visitArmor(itemStack,null);
        return (Armor) itemData.orElseThrow();
    }

    // Weapon deserialization
    public static boolean isWeapon(ItemStack item){
        return checkDataContainer(item, Weapon.DATA_CONTAINER_KEY);
    }
    public Weapon deserializeWeaponData(ItemStack itemStack) {
        Optional<Item> itemData = visitWeapon(itemStack, null);
        return (Weapon) itemData.orElseThrow();
    }
}
