package com.amorabot.inscripted.components.Player.archetypes;

import com.amorabot.inscripted.item.inscription.Inscription;
import com.amorabot.inscripted.item.inscription.definition.InscriptionIDs;
import com.amorabot.inscripted.item.structure.Armor.ArmorTypes;
import com.amorabot.inscripted.item.structure.Tiers;
import com.amorabot.inscripted.item.structure.ItemSubtype;
import com.amorabot.inscripted.item.structure.Weapon.WeaponTypes;
import com.amorabot.inscripted.item.render.InscriptedPalette;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;

@Getter
public enum Archetypes {
    NONE(InscriptedPalette.TINTED_BEIGE, null,null),
    MARAUDER(InscriptedPalette.MARAUDER, WeaponTypes.AXE, ArmorTypes.ARMORED),
    GLADIATOR(InscriptedPalette.GLADIATOR, WeaponTypes.SWORD, ArmorTypes.ORNATE),
    MERCENARY(InscriptedPalette.MERCENARY, WeaponTypes.BOW, ArmorTypes.CLOTH),
    ROGUE(InscriptedPalette.ROGUE, WeaponTypes.DAGGER, ArmorTypes.PELT),
    SORCERER(InscriptedPalette.SORCERER, WeaponTypes.WAND, ArmorTypes.SILK),
    TEMPLAR(InscriptedPalette.TEMPLAR, WeaponTypes.MACE, ArmorTypes.RUNISTEEL);

    private final InscriptedPalette colorOnPalette;
    private final WeaponTypes weaponType;
    private final ArmorTypes armorType;

    Archetypes(InscriptedPalette colorOnPalette, WeaponTypes weaponType, ArmorTypes armorMaterial){
        this.colorOnPalette = colorOnPalette;

        this.weaponType = weaponType;
        this.armorType = armorMaterial;
    }
    public static <subType extends Enum<subType> & ItemSubtype> Inscription mapImplicitFor(subType itemSubtype, Tiers tier, boolean corrupted){
        Archetypes subtypeArchetype = itemSubtype.mapArchetype();
        if (subtypeArchetype.equals(NONE)){Utils.error("No valid archetype for"+ itemSubtype.name()+" @" + Archetypes.class.getSimpleName());}
        return subtypeArchetype.getImplicitFor(itemSubtype, tier, corrupted);
    }
    private <subType extends Enum<subType> & ItemSubtype> Inscription getImplicitFor(subType itemSubtype, Tiers tier, boolean corrupted){
        String implicitID = this + "_" + itemSubtype.name();
        try {
            InscriptionIDs implicit = InscriptionIDs.valueOf(implicitID);
            return new Inscription(implicit, tier.ordinal(), Tiers.values().length-1);
        } catch (IllegalArgumentException exception){
            Utils.error("Invalid implicit fetch for: " + implicitID);
        }
        return null;
    }

}
