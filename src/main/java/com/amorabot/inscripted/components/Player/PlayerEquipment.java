package com.amorabot.inscripted.components.Player;

import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Armor.Armor;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class PlayerEquipment {

    private final Map<ItemTypes, EquipmentSlot> slots;

    public PlayerEquipment(){
        //Construtor vazio
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
            selectedSlot.setItemData(null);
            return true;
        }
        //Data is not null and can be further checked
        if (!slotType.equals(itemData.getCategory())){
            Utils.error("Slot type: " + slotType + " doesn't match item: " + itemData.getCategory());
            return false;
        }
        selectedSlot.setItemData(itemData);
        return true;
    }

    public EquipmentSlot getSlot(ItemTypes itemSlot){
        return slots.get(itemSlot);
    }

    public Weapon getWeaponData(){
        return (Weapon) getSlot(ItemTypes.WEAPON).getItemData();
    }


    public Armor[] getArmorSet() {
        Armor[] armorSet = new Armor[4];
        armorSet[3] = ( Armor ) slots.get(ItemTypes.HELMET).getItemData();
        armorSet[2] = ( Armor ) slots.get(ItemTypes.CHESTPLATE).getItemData();
        armorSet[1] = ( Armor ) slots.get(ItemTypes.LEGGINGS).getItemData();
        armorSet[0] = ( Armor ) slots.get(ItemTypes.BOOTS).getItemData();

        return armorSet;
    }
    public Armor getHelmet(){
        return (Armor) slots.get(ItemTypes.HELMET).getItemData();
    }
    public Armor getChestplate(){
        return (Armor) slots.get(ItemTypes.CHESTPLATE).getItemData();
    }
    public Armor getLeggings(){
        return (Armor) slots.get(ItemTypes.LEGGINGS).getItemData();
    }
    public Armor getBoots(){
        return (Armor) slots.get(ItemTypes.BOOTS).getItemData();
    }

    public void setArmorSet(Armor helmet, Armor chestplate, Armor leggings, Armor boots){
        setSlot(ItemTypes.HELMET, helmet);
        setSlot(ItemTypes.CHESTPLATE, chestplate);
        setSlot(ItemTypes.LEGGINGS, leggings);
        setSlot(ItemTypes.BOOTS, boots);
    }
}
