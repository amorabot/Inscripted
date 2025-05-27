package com.amorabot.inscripted.item.structure.serialization;

import com.amorabot.inscripted.item.structure.Armor.Armor;
import com.amorabot.inscripted.item.structure.Weapon.Weapon;
import org.bukkit.inventory.ItemStack;

public interface ItemDataContainerVisitor<R> {
    R visitWeapon(ItemStack itemForm, Weapon weaponData);
    R visitArmor(ItemStack itemForm, Armor armorData);
}
