package com.amorabot.inscripted.item.relic;

import com.amorabot.inscripted.file.item.RelicEditor;
import com.amorabot.inscripted.item.inscription.definition.InscriptionDefinition;
import com.amorabot.inscripted.item.inscription.language.DefinitionScanner;
import com.amorabot.inscripted.item.render.ItemRenderer;
import com.amorabot.inscripted.item.structure.Armor.Armor;
import com.amorabot.inscripted.item.structure.EquipmentSlots;
import com.amorabot.inscripted.item.structure.Item;
import com.amorabot.inscripted.item.structure.Weapon.Weapon;
import com.amorabot.inscripted.item.structure.io.ItemSerializer;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Function;

public enum Relics {
// Weapons
    OMINOUS_TWIG(),
    SCARLET_DANCER(),
    FEATHERED_BOW(),
    HEADSMAN_BLADE(),
    MAD_BUTCHER(),
    ELUSIVE_SHADOW(),
    BROKEN_FAITH(),
    HELLFORGED(),
//    UNWAVERING_FAITH(ItemTypes.WEAPON,"Check every corner.","","Every. Shadow."), //TO BE DESIGNED

// Armors
    TRAINING_DUMMY_ARMOR(),
    BLEEDING_HEART(),
    CORRUPTORS_WRAPPINGS(),
    APPROACHING_WINTER(),
    BLIND_RAGE(),
    EYE_OF_THE_STORM(),
    TIN_FOIL_HELMET(),
    QUEEN_OF_THE_FOREST(),
    IMMORTAL_FLESH(),
    THE_BODY(),
    THE_MIND(),
    THE_SOUL(),
    TRINITY(),
    EXECUTIONERS_MASK(),
    DRUIDIC_PELTS(),
    INCANDESCENT_HEART();

    @Getter
    private static final Map<Relics, RelicArmorData> relicArmorsData;
    @Getter
    private static final Map<Relics, RelicWeaponData> relicWeaponsData;
    private static final boolean DEBUG_MODE;

    static {
        DEBUG_MODE = false;
        if (DEBUG_MODE){Utils.log("Initalizing Relics class");}
        relicArmorsData = RelicEditor.loadAllArmors();
        relicWeaponsData = RelicEditor.loadAllWeapons();
    }

    @Getter
    private Map<Integer, ParsedUniqueInscription> parsedInscriptions;
    @Getter
    private String flavorText;

    public ItemStack getItemForm(){
        Item relicItem = generate();
        assert relicItem != null;
        ItemStack relicItemStack = relicItem.getItemForm();
        ItemSerializer.setRelicData(relicItemStack,this);
        ItemRenderer.appendRelicFlavorText(relicItemStack,this);
        return relicItemStack;
    }
    public Item generate(){
        if (isIArmor()){
            RelicArmorData relicData = Relics.getRelicArmorsData().get(this);
            GenericRelicData genericData = relicData.data();
            return new Armor(this, genericData.itemLevel(),relicData.type(),true,relicData.armorSlot());
        }
        if (isIWeapon()){
            RelicWeaponData relicData = Relics.getRelicWeaponsData().get(this);
            GenericRelicData genericData = relicData.data();
            return new Weapon(this, genericData.itemLevel(), relicData.type(),true, EquipmentSlots.WEAPON);
        }
        Utils.error("Invalid relic...");
        return null;
    }
    public boolean isIArmor(){
        return relicArmorsData.containsKey(this);
    }
    public boolean isIWeapon(){
        return relicWeaponsData.containsKey(this);
    }

    private static String parseFlavorText(List<String> rawFlavorText){
        StringBuilder flavorTextBuilder = new StringBuilder();
        for (String s : rawFlavorText){
            flavorTextBuilder.append(s).append("<br>");
        }
        return flavorTextBuilder.toString();
    }

    public static void init() {
        if (DEBUG_MODE){Utils.log("Initializing Relic internal data");}
        for (Relics relic : Relics.values()){
            if (DEBUG_MODE){Utils.log(relic + " initialized.");}
            Map<Integer, ParsedUniqueInscription> parsedInscr = new HashMap<>();
            List<UniqueInscriptionDTO> rawInscriptionData = new ArrayList<>();
            List<String> rawFlavorText = new ArrayList<>();
            if (relic.isIArmor()){
                RelicArmorData relicData = Relics.getRelicArmorsData().get(relic);
                rawInscriptionData = relicData.data().inscriptions();
                rawFlavorText = relicData.data().flavorText();
            }
            if (relic.isIWeapon()){ //Feão
                RelicWeaponData relicData = Relics.getRelicWeaponsData().get(relic);
                rawInscriptionData = relicData.data().inscriptions();
                rawFlavorText = relicData.data().flavorText();
            }
            for (UniqueInscriptionDTO rawInsc : rawInscriptionData){
                DefinitionScanner scanner = new DefinitionScanner(rawInsc.definition());
                InscriptionDefinition parsedDef = scanner.run();
                parsedInscr.put(parsedDef.hashCode(),new ParsedUniqueInscription(parsedDef,rawInsc.values()));
            }
            relic.parsedInscriptions = parsedInscr;

            relic.flavorText = parseFlavorText(rawFlavorText);
        }
    }
    public static boolean isArmor(Relics relic){
        return relicArmorsData.containsKey(relic);
    }
    public static boolean isWeapon(Relics relic){
        return relicWeaponsData.containsKey(relic);
    }

    public static List<Relics> filterRelics(Function<Relics,Boolean> filter){
        List<Relics> filteredRelics = new ArrayList<>();
        for (Relics relic : Relics.values()){
            if (filter.apply(relic)){
                filteredRelics.add(relic);
            }
        }
        return filteredRelics;
    }
}
