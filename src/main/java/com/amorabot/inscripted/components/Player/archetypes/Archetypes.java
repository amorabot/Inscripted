package com.amorabot.inscripted.components.Player.archetypes;

import com.amorabot.inscripted.components.Items.Armor.ArmorTypes;
import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;

public enum Archetypes {
    MARAUDER("#b01330", WeaponTypes.AXE, ArmorTypes.HEAVY_PLATING),
    GLADIATOR("#F9B147", WeaponTypes.SWORD, ArmorTypes.CARVED_PLATING),
    MERCENARY("#89AE59", WeaponTypes.BOW, ArmorTypes.LIGHT_CLOTH),
    ROGUE("#18A383", WeaponTypes.DAGGER, ArmorTypes.RUNIC_LEATHER),
    SORCERER("#496FE3", WeaponTypes.WAND, ArmorTypes.ENCHANTED_SILK),
    TEMPLAR("#A735D4", WeaponTypes.SCEPTRE, ArmorTypes.RUNIC_STEEL);

    private final String color;
    private final WeaponTypes weaponType;
    private final ArmorTypes armorType;

    Archetypes(String hexArchetypeColor, WeaponTypes weaponType, ArmorTypes armorMaterial){
        this.color = hexArchetypeColor;

        this.weaponType = weaponType;
        this.armorType = armorMaterial;
    }

    public String getColor() {
        return color;
    }
    public WeaponTypes getWeaponType() {
        return weaponType;
    }
    public ArmorTypes getArmorType() {
        return armorType;
    }
}
