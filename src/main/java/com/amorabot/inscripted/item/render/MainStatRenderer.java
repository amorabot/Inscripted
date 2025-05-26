package com.amorabot.inscripted.item.render;

import com.amorabot.inscripted.item.structure.Armor.Armor;
import com.amorabot.inscripted.item.structure.Weapon.Weapon;
import net.kyori.adventure.text.Component;

import java.util.List;

public class MainStatRenderer implements ItemVisitor<List<Component>> {

    @Override
    public List<Component> visitWeapon(Weapon weapon) {
        return List.of(); //Do rendering logic
    }

    @Override
    public List<Component> visitArmor(Armor armor) {
        return List.of();
    }
}
