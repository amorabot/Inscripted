package com.amorabot.rpgelements.components.FunctionalItems;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.Items.DataStructures.GenericItemContainerDataType;
import com.amorabot.rpgelements.components.Items.Weapon.Weapon;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;

public class FunctionalItemHandler {

    private static final RPGElements rpgElementsPlugin = RPGElements.getPlugin();

    private FunctionalItemHandler(){

    }

    /*
     * @return True -> if the given container has valid weapon data and it's identified
     * @return False -> Any other case
     *
     * @param dataContainer -> A generic item's data container
     * */
    public static boolean isEquipableWeapon(PersistentDataContainer dataContainer){
        if (isWeapon(dataContainer)){
            Weapon weapon = deserializeWeapon(dataContainer);
            if (weapon == null){return false;}
            return weapon.isIdentified();
        }
        return false;
    }
    public static boolean isWeapon(PersistentDataContainer dataContainer){
        return dataContainer.has(new NamespacedKey(rpgElementsPlugin, "item-data"), new GenericItemContainerDataType<>(Weapon.class));
    }
    public static void serializeWeapon(Weapon weaponData, PersistentDataContainer dataContainer){
        dataContainer.set(new NamespacedKey(rpgElementsPlugin, "item-data"), new GenericItemContainerDataType<>(Weapon.class), weaponData);
    }
    /*
    * @return Null -> if item doesn't have valid Weapon data
    * */
    public static Weapon deserializeWeapon(PersistentDataContainer dataContainer){
        if (isWeapon(dataContainer)){
            Weapon weaponData = dataContainer.get(new NamespacedKey(rpgElementsPlugin, "item-data"), new GenericItemContainerDataType<>(Weapon.class));
            if (weaponData == null){ Utils.error("FunctionalItemHandler error: Null weapon deserialization"); }
            return weaponData;
        }
        Utils.error("FunctionalItemHandler error: Not a weapon");
        return null;
    }
}
