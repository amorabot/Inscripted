package com.amorabot.rpgelements.events;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.Items.Armor.Armor;
import com.amorabot.rpgelements.components.Items.DataStructures.GenericItemContainerDataType;
import com.amorabot.rpgelements.components.Items.Weapon.Weapon;
import com.amorabot.rpgelements.components.Player.Profile;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class FunctionalItemAccessInterface {

    private static final RPGElements rpgElementsPlugin = RPGElements.getPlugin();
    private static final String weaponKey = "weapon";
    private static final String weaponDataKey = weaponKey + "-data";
    private static final String armorKey = "armor";
    private static final String armorDataKey = armorKey + "-data";
    private FunctionalItemAccessInterface(){

    }
    //Checks if the item's container has a weapon tag
    public static boolean isWeapon(PersistentDataContainer dataContainer){
        if (dataContainer==null){return false;}
        return dataContainer.has(new NamespacedKey(rpgElementsPlugin, weaponKey), new PersistentDataType.BooleanPersistentDataType()); //If its a weapon, it has this container
    }

    //Checks wether this item has a weapon tag and if it's identified
    public static boolean isEquipableWeapon(PersistentDataContainer dataContainer){
        if (isWeapon(dataContainer)) {
            return Boolean.TRUE.equals(dataContainer.get(new NamespacedKey(rpgElementsPlugin, weaponKey), new PersistentDataType.BooleanPersistentDataType()));
        }
        return false;
    }

    //Both containers are serialized at once. "weapon-data" contains the Weapon class data and "weapon" is the quick-access container for equipability checks
    public static void serializeWeapon(Weapon weaponData, PersistentDataContainer dataContainer){
        dataContainer.set(new NamespacedKey(rpgElementsPlugin, weaponDataKey), new GenericItemContainerDataType<>(Weapon.class), weaponData);
        dataContainer.set(new NamespacedKey(rpgElementsPlugin, weaponKey), new PersistentDataType.BooleanPersistentDataType(), weaponData.isIdentified());
    }

    //Gets weapon class data from the item's container
    public static Weapon deserializeWeapon(PersistentDataContainer dataContainer){ // Optional
        if (isWeapon(dataContainer)){
            Weapon weaponData = dataContainer.get(new NamespacedKey(rpgElementsPlugin, weaponDataKey), new GenericItemContainerDataType<>(Weapon.class));
            if (weaponData == null){ Utils.error("FunctionalItemHandler error: Null weapon deserialization"); }
            return weaponData;
        }
        Utils.error("FunctionalItemHandler error: Not a weapon");
        return null;
    }
    //When identifying weapons, this method should be called
    private static void identifyWeapon(PersistentDataContainer dataContainer){
        if (isWeapon(dataContainer)){
            dataContainer.set(new NamespacedKey(rpgElementsPlugin, weaponKey), new PersistentDataType.BooleanPersistentDataType(), true);
        }
    }


    //Armor methods are analog to weapon's
    public static boolean isArmor(PersistentDataContainer dataContainer){
        if (dataContainer==null){return false;}
        return dataContainer.has(new NamespacedKey(rpgElementsPlugin, armorKey), new PersistentDataType.BooleanPersistentDataType());
    }
    public static boolean isEquipableArmor(PersistentDataContainer dataContainer){
        if (isArmor(dataContainer)) {
            return Boolean.TRUE.equals(dataContainer.get(new NamespacedKey(rpgElementsPlugin, armorKey), new PersistentDataType.BooleanPersistentDataType()));
        }
        return false;
    }
    public static boolean isEquipableArmorByPlayer(Armor armorData, Profile playerProfile){
        //Todo: implement level requirement for player to equip
        return true;
    }
    public static void serializeArmor(Armor armorData, PersistentDataContainer dataContainer){
        dataContainer.set(new NamespacedKey(rpgElementsPlugin, armorDataKey), new GenericItemContainerDataType<>(Armor.class), armorData);
        dataContainer.set(new NamespacedKey(rpgElementsPlugin, armorKey), new PersistentDataType.BooleanPersistentDataType(), armorData.isIdentified());
    }
    public static Armor deserializeArmor(PersistentDataContainer dataContainer){
        if (isArmor(dataContainer)){
            Armor armorData = dataContainer.get(new NamespacedKey(rpgElementsPlugin, armorDataKey), new GenericItemContainerDataType<>(Armor.class));
            if (armorData == null){ Utils.error("FunctionalItemHandler error: Null weapon deserialization"); }
            return armorData;
        }
        Utils.error("FunctionalItemHandler error: Not a weapon");
        return null;
    }
    private static void identifyArmor(PersistentDataContainer dataContainer){
        if (isWeapon(dataContainer)){
            dataContainer.set(new NamespacedKey(rpgElementsPlugin, armorKey), new PersistentDataType.BooleanPersistentDataType(), true);
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
