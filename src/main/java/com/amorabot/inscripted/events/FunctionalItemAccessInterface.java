package com.amorabot.inscripted.events;

import com.amorabot.inscripted.Inscripted;
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
    private static final NamespacedKey WEAPON_TAG = new NamespacedKey(INSCRIPTED_PLUGIN, "IS_WEAPON");
    private static final NamespacedKey WEAPON_DATA_KEY = new NamespacedKey(INSCRIPTED_PLUGIN, "WEAPON-DATA");
    private static final NamespacedKey ARMOR_TAG = new NamespacedKey(INSCRIPTED_PLUGIN, "IS_ARMOR");
    private static final NamespacedKey ARMOR_DATA_KEY = new NamespacedKey(INSCRIPTED_PLUGIN, "ARMOR_DATA");
    private FunctionalItemAccessInterface(){

    }
    //Checks if the item's container has a weapon tag
    public static boolean isWeapon(PersistentDataContainer dataContainer){
        if (dataContainer==null){return false;}
        return dataContainer.has(WEAPON_TAG, new PersistentDataType.BooleanPersistentDataType()); //If its a weapon, it has this container
    }

    //Checks wether this item has a weapon tag and if it's identified
    public static boolean isEquipableWeapon(PersistentDataContainer dataContainer){
        if (isWeapon(dataContainer)) {
            return Boolean.TRUE.equals(dataContainer.get(WEAPON_TAG, new PersistentDataType.BooleanPersistentDataType()));
        }
        return false;
    }

    //Both containers are serialized at once. "weapon-data" contains the Weapon class data and "weapon" is the quick-access container for equipability checks
    public static void serializeWeapon(Weapon weaponData, PersistentDataContainer dataContainer){
        dataContainer.set(WEAPON_DATA_KEY, new GenericItemContainerDataType<>(Weapon.class), weaponData);
        dataContainer.set(WEAPON_TAG, new PersistentDataType.BooleanPersistentDataType(), weaponData.isIdentified());
    }

    //Gets weapon class data from the item's container
    public static Weapon deserializeWeapon(PersistentDataContainer dataContainer){ // Optional
        if (isWeapon(dataContainer)){
            Weapon weaponData = dataContainer.get(WEAPON_DATA_KEY, new GenericItemContainerDataType<>(Weapon.class));
            if (weaponData == null){ Utils.error("FunctionalItemHandler error: Null weapon deserialization"); }
            return weaponData;
        }
        Utils.error("FunctionalItemHandler error: Not a weapon");
        return null;
    }
    //When identifying weapons, this method should be called
    private static void identifyWeapon(PersistentDataContainer dataContainer){
        if (isWeapon(dataContainer)){
            dataContainer.set(WEAPON_TAG, new PersistentDataType.BooleanPersistentDataType(), true);
        }
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
    public static void serializeArmor(Armor armorData, PersistentDataContainer dataContainer){
        dataContainer.set(ARMOR_DATA_KEY, new GenericItemContainerDataType<>(Armor.class), armorData);
        dataContainer.set(ARMOR_TAG, new PersistentDataType.BooleanPersistentDataType(), armorData.isIdentified());
    }
    public static Armor deserializeArmor(PersistentDataContainer dataContainer){
        if (isArmor(dataContainer)){
            Armor armorData = dataContainer.get(ARMOR_DATA_KEY, new GenericItemContainerDataType<>(Armor.class));
            if (armorData == null){ Utils.error("FunctionalItemHandler error: Null weapon deserialization"); }
            return armorData;
        }
        Utils.error("FunctionalItemHandler error: Not a weapon");
        return null;
    }
    private static void identifyArmor(PersistentDataContainer dataContainer){
        if (isWeapon(dataContainer)){
            dataContainer.set(ARMOR_TAG, new PersistentDataType.BooleanPersistentDataType(), true);
        }
    }



    public static void identifyItem(ItemStack item){
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        if (isWeapon(dataContainer)){
            Weapon weapon = FunctionalItemAccessInterface.deserializeWeapon(dataContainer);
            assert weapon != null;
            weapon.identify();
            identifyWeapon(dataContainer);
            FunctionalItemAccessInterface.serializeWeapon(weapon, dataContainer);
            item.setItemMeta(itemMeta);
            weapon.imprint(item);
            return;
        }
        if (isArmor(dataContainer)){
            Armor armor = FunctionalItemAccessInterface.deserializeArmor(dataContainer);
            assert armor != null;
            armor.identify();
            identifyArmor(dataContainer);
            FunctionalItemAccessInterface.serializeArmor(armor, dataContainer);
            item.setItemMeta(itemMeta);
            armor.imprint(item);
            return;
        }
    }
}
