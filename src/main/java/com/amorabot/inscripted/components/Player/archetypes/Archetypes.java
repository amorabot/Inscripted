package com.amorabot.inscripted.components.Player.archetypes;

import com.amorabot.inscripted.components.Items.Armor.ArmorTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Tiers;
import com.amorabot.inscripted.components.Items.Interfaces.ItemSubtype;
import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;
import com.amorabot.inscripted.components.Items.modifiers.Inscription;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;

@Getter
public enum Archetypes {
    MARAUDER("#b01330", WeaponTypes.AXE, ArmorTypes.HEAVY_PLATING),
    GLADIATOR("#F9B147", WeaponTypes.SWORD, ArmorTypes.CARVED_PLATING),
    MERCENARY("#89AE59", WeaponTypes.BOW, ArmorTypes.LIGHT_CLOTH),
    ROGUE("#18A383", WeaponTypes.DAGGER, ArmorTypes.RUNIC_LEATHER),
    SORCERER("#496FE3", WeaponTypes.WAND, ArmorTypes.ENCHANTED_SILK),
    TEMPLAR("#A735D4", WeaponTypes.MACE, ArmorTypes.RUNIC_STEEL);

    private final String color;
    private final WeaponTypes weaponType;
    private final ArmorTypes armorType;

    Archetypes(String hexArchetypeColor, WeaponTypes weaponType, ArmorTypes armorMaterial){
        this.color = hexArchetypeColor;

        this.weaponType = weaponType;
        this.armorType = armorMaterial;
    }
    public static <subType extends Enum<subType> & ItemSubtype> Inscription mapImplicitFor(subType itemSubtype, Tiers tier, boolean corrupted){
        Archetypes subtypeArchetype = mapArchetypeFor(itemSubtype);
        assert subtypeArchetype != null;
        return subtypeArchetype.getImplicitFor(itemSubtype, tier, corrupted);
    }
    public static <subType extends Enum<subType> & ItemSubtype> Archetypes mapArchetypeFor(subType itemSubtype){
        Archetypes[] archetypes = Archetypes.values();
        if (itemSubtype instanceof WeaponTypes weapon){
            for (Archetypes a : archetypes){
                if (a.getWeaponType().equals(weapon)){return a;}
            }
        }
        if (itemSubtype instanceof ArmorTypes armor){
            for (Archetypes a : archetypes){
                if (a.getArmorType().equals(armor)){return a;}
            }
        }
        Utils.error("Invalid item subtype for archetype mapping @" + Archetypes.class.getSimpleName());
        return null;
    }
    private <subType extends Enum<subType> & ItemSubtype> Inscription getImplicitFor(subType itemSubtype, Tiers tier, boolean corrupted){
        String implicitID = this + "_" + itemSubtype.name();
        try {
            InscriptionID implicit = InscriptionID.valueOf(implicitID);
            return new Inscription(implicit, tier.ordinal(), Tiers.values().length-1);
        } catch (IllegalArgumentException exception){
            Utils.error("Invalid implicit fetch for: " + implicitID);
        }
        return null;
    }

}
