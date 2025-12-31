package com.amorabot.inscripted.item.render;

import com.amorabot.inscripted.item.structure.Armor.Armor;
import com.amorabot.inscripted.item.structure.Weapon.Weapon;

public interface ItemVisitor<R> {
    R visitWeapon(Weapon weapon);
    R visitArmor(Armor armor);
}
