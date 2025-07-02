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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Relics {

    TRAINING_DUMMY_ARMOR(),
//    BLEEDING_HEART(ItemTypes.CHESTPLATE, "This severed, yet","pulsating heart","gives you a unending","desire for carnage.");
//    CORRUPTORS_WRAPPINGS(ItemTypes.CHESTPLATE, "This old tunic","once belonged to","a powerful sage","that stared too","long into the abyss."),
    OMINOUS_TWIG();
//    APPROACHING_WINTER(ItemTypes.BOOTS,"The winter's cold","embrace is slowly","approaching."),
//    BLIND_RAGE(ItemTypes.HELMET,"This cursed helmet","once belonged to","a mysterious ske-","letal and silent","protector."),
//    EYE_OF_THE_STORM(ItemTypes.CHESTPLATE,"This heavenly curse","can be a powerful","boon in the right", "hands."),
//    TIN_FOIL_HELMET(ItemTypes.HELMET,"Can protect you","against conspiracies,","but certainly not", "against electricity!"),
//    QUEEN_OF_THE_FOREST(ItemTypes.LEGGINGS,"Become one with nature.","","To hunt is to live."),
//    SCARLET_DANCER(ItemTypes.WEAPON,"Oh, the thrill of dancing","to a battle's rhythm...","","The thrill of flirting","with death..."),
//    IMMORTAL_FLESH(ItemTypes.CHESTPLATE,"The cycle of carnage","is the only constant","in life."),
//    FEATHERED_BOW(ItemTypes.WEAPON,"Reflecting the local","fauna's elegance,","the user can't","help but to feel","graceful aswell."),
//    THE_BODY(ItemTypes.CHESTPLATE,"The resilience of","a body can reach","impressive heights.","","But alone... it can't","be whole."),
//    THE_MIND(ItemTypes.HELMET,"The strength of","mind can bring","enlightenment.","","But alone... it can't","be whole."),
//    THE_SOUL(ItemTypes.BOOTS,"The freedom of a","soul can bring","inner peace.","","But alone... it can't","be whole."),
//    TRINITY(ItemTypes.LEGGINGS,"One must seek balance","at all costs."),
//    EXECUTIONERS_MASK(ItemTypes.HELMET,"The merciful face","of death shall","remain concealed."),
//    HEADSMAN_BLADE(ItemTypes.WEAPON,"No victim is","unworthy of mercy.","Thy death ","shall be quick","and painless."),
//    MAD_BUTCHER(ItemTypes.WEAPON,"dihgubsduygahsuidy","","- A once wise and sane", "butcher."),
//    ELUSIVE_SHADOW(ItemTypes.WEAPON,"Check every corner.","","Every. Shadow."),
//    BROKEN_FAITH(ItemTypes.WEAPON,"Whatever watches","over us must be","completely ignorant","or blind to the","horrors of this land..."),
////    UNWAVERING_FAITH(ItemTypes.WEAPON,"Check every corner.","","Every. Shadow."),
//    DRUIDIC_PELTS(ItemTypes.CHESTPLATE,"Nature's beautiful","cycle...","To be wounded","To be healed"),
//    HELLFORGE(ItemTypes.WEAPON,"This glowing-hot","cleaver seems","to sap it's user's", "might to grow even", "stronger...");

    @Getter
    private static final Map<Relics, RelicArmorData> relicArmorsData;
    @Getter
    private static final Map<Relics, RelicWeaponData> relicWeaponsData;

    static {
        Utils.error("Initalizing Relics class");
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
        if (isArmor()){
            RelicArmorData relicData = Relics.getRelicArmorsData().get(this);
            GenericRelicData genericData = relicData.data();
            return new Armor(this, genericData.itemLevel(),relicData.type(),true,relicData.armorSlot());
        }
        if (isWeapon()){
            RelicWeaponData relicData = Relics.getRelicWeaponsData().get(this);
            GenericRelicData genericData = relicData.data();
            return new Weapon(this, genericData.itemLevel(), relicData.type(),true, EquipmentSlots.WEAPON);
        }
        Utils.error("Invalid relic...");
        return null;
    }
    public boolean isArmor(){
        return relicArmorsData.containsKey(this);
    }
    public boolean isWeapon(){
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
        for (Relics relic : Relics.values()){
            Map<Integer, ParsedUniqueInscription> parsedInscr = new HashMap<>();
            List<UniqueInscriptionDTO> rawInscriptionData = new ArrayList<>();
            List<String> rawFlavorText = new ArrayList<>();
            if (relic.isArmor()){
                RelicArmorData relicData = Relics.getRelicArmorsData().get(relic);
                rawInscriptionData = relicData.data().inscriptions();
                rawFlavorText = relicData.data().flavorText();
            }
            if (relic.isWeapon()){ //Feão
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
}
