package com.amorabot.inscripted.components.Items.Weapon;

import org.bukkit.Material;

public enum RangeCategory {
    MELEE(Material.SHEARS),
    RANGED(Material.BOW);

    private final Material item;

    RangeCategory(Material baseItem){
        this.item = baseItem;
    }

    public Material getItem() {
        return item;
    }
}
