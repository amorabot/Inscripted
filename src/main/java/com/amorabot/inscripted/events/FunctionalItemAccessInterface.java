package com.amorabot.inscripted.events;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Armor.Armor;
import com.amorabot.inscripted.components.Items.DataStructures.GenericItemContainerDataType;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class FunctionalItemAccessInterface {

    private static final Inscripted INSCRIPTED_PLUGIN = Inscripted.getPlugin();
    public static final NamespacedKey CORRUPTED_TAG = new NamespacedKey(INSCRIPTED_PLUGIN, "CORRUPTED");
    public static final NamespacedKey WEAPON_TAG = new NamespacedKey(INSCRIPTED_PLUGIN, "IS_WEAPON");
    public static final NamespacedKey WEAPON_DATA_KEY = new NamespacedKey(INSCRIPTED_PLUGIN, "WEAPON-DATA");
    public static final NamespacedKey ARMOR_TAG = new NamespacedKey(INSCRIPTED_PLUGIN, "IS_ARMOR");
    public static final NamespacedKey ARMOR_DATA_KEY = new NamespacedKey(INSCRIPTED_PLUGIN, "ARMOR_DATA");
    private FunctionalItemAccessInterface(){

    }

    //Checks for a specific item-type key within the item's PDC
    public static boolean isItemType(NamespacedKey typeKey, PersistentDataContainer dataContainer){
        if (dataContainer==null){return false;}
        return dataContainer.has(typeKey, new PersistentDataType.BooleanPersistentDataType());
    }
    //Checks for the value associated with the item-type key inside the PDC (This value represents the identified flag inside the Item class, for easy access)
    public static boolean isIdentified(NamespacedKey typeKey, PersistentDataContainer dataContainer){
        if (isItemType(typeKey, dataContainer)){//
            return Boolean.TRUE.equals(dataContainer.get(typeKey, new PersistentDataType.BooleanPersistentDataType()));
        }
        return false;
    }
    public static boolean isCorrupted(PersistentDataContainer dataContainer){
        if (!dataContainer.has(CORRUPTED_TAG, new PersistentDataType.BooleanPersistentDataType())){
            return false;
        }
        return Boolean.TRUE.equals(dataContainer.get(CORRUPTED_TAG, new PersistentDataType.BooleanPersistentDataType()));
    }
    public static void serializeItem(ItemStack itemStack, Item itemData){
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        if (itemData instanceof Weapon){
            dataContainer.set(WEAPON_DATA_KEY, new GenericItemContainerDataType<>(Weapon.class), (Weapon) itemData);
            dataContainer.set(WEAPON_TAG, new PersistentDataType.BooleanPersistentDataType(), itemData.isIdentified());
        } else if (itemData instanceof Armor) {
            dataContainer.set(ARMOR_DATA_KEY, new GenericItemContainerDataType<>(Armor.class), (Armor) itemData);
            dataContainer.set(ARMOR_TAG, new PersistentDataType.BooleanPersistentDataType(), itemData.isIdentified());
        }
        dataContainer.set(CORRUPTED_TAG, new PersistentDataType.BooleanPersistentDataType(), itemData.isCorrupted());

        itemStack.setItemMeta(itemMeta);
    }

    //Should only be used when the needed item's data doesnt depend on their subtype
    public static Item deserializeGenericItemData(PersistentDataContainer dataContainer){
        if (isItemType(WEAPON_TAG, dataContainer)){
            return deserializeWeaponData(dataContainer);
        } else if (isItemType(ARMOR_TAG, dataContainer)) {
            return deserializeArmorData(dataContainer);
        } else {
            Utils.error("Generic item deserialization error (FunctionalItemAccessInterface)");
            return null;
        }
    }
//==================================================OLD METHODS=================================================================
    //Gets weapon class data from the item's container
    public static Weapon deserializeWeaponData(PersistentDataContainer dataContainer){ // Optional
        if (isItemType(WEAPON_TAG,dataContainer)){
            Weapon weaponData = dataContainer.get(WEAPON_DATA_KEY, new GenericItemContainerDataType<>(Weapon.class));
            if (weaponData == null){ Utils.error("FunctionalItemHandler error: Null weapon deserialization"); }
            return weaponData;
        }
        Utils.error("FunctionalItemHandler error: Not a weapon");
        return null;
    }

    //Armor methods are analog to weapon's
    public static boolean isArmor(PersistentDataContainer dataContainer){
        if (dataContainer==null){return false;}
        return dataContainer.has(ARMOR_TAG, new PersistentDataType.BooleanPersistentDataType());
    }
    public static boolean isEquipableArmor(PersistentDataContainer dataContainer){
        if (isArmor(dataContainer)) {
            return Boolean.TRUE.equals(dataContainer.get(ARMOR_TAG, new PersistentDataType.BooleanPersistentDataType()));
        }
        return false;
    }
    public static boolean isEquipableArmorByPlayer(Armor armorData, Profile playerProfile){
        //Todo: implement level requirement for player to equip
        return true;
    }
    public static Armor deserializeArmorData(PersistentDataContainer dataContainer){
        if (isArmor(dataContainer)){
            Armor armorData = dataContainer.get(ARMOR_DATA_KEY, new GenericItemContainerDataType<>(Armor.class));
            if (armorData == null){ Utils.error("FunctionalItemHandler error: Null weapon deserialization"); }
            return armorData;
        }
        Utils.error("FunctionalItemHandler error: Not a armor");
        return null;
    }
}
