package com.amorabot.inscripted.components.Items.DataStructures.Enums;

public enum ItemTypes { //Represent equipable item  slots
    HELMET,
    CHESTPLATE,
    LEGGINGS,
    BOOTS,
    WEAPON

    //TODO:Rename to ItemSlot

    //Only subtypes should hold their parent's data, not otherwise
    //SWORD is a WEAPON, WEAPON does not know about sword directly (Type not defined due to generics)
    //RUNIC_LEATHER is a HELMET, CHESTPLATE, ..., All of them dont know about RUNIC_LEATHER
    //Can be applied to new slots such as BELTS or RINGS, each of them having their own subtypes or not (sharing ArmorTypes for instance)
}
