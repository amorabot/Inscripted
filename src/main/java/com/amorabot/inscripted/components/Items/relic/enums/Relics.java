package com.amorabot.inscripted.components.Items.relic.enums;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Armor.Armor;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.relic.RelicArmorDAO;
import com.amorabot.inscripted.components.Items.Files.RelicEditor;
import com.amorabot.inscripted.components.Items.relic.RelicWeaponDAO;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Items.modifiers.Inscription;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum Relics {

    TRAINING_DUMMY_ARMOR(ItemTypes.CHESTPLATE, "lol"),
    BLEEDING_HEART(ItemTypes.CHESTPLATE, "This severed, yet","pulsating heart","gives you a unending","desire for carnage."),
    CORRUPTORS_WRAPPINGS(ItemTypes.CHESTPLATE, "This old tunic","once belonged to","a powerful sage","that stared too","long into the abyss."),
    OMINOUS_TWIG(ItemTypes.WEAPON,"Amidst a wasteland,","a single tree remains.","From it's wretched","form, a single", "branch lives on."),
    APPROACHING_WINTER(ItemTypes.BOOTS,"The winter's cold","embrace is slowly","approaching."),
    BLIND_RAGE(ItemTypes.HELMET,"This cursed helmet","once belonged to","a mysterious ske-","letal and silent","protector."),
    EYE_OF_THE_STORM(ItemTypes.CHESTPLATE,"This heavenly curse","can be a powerful","boon in the right", "hands."),
    TIN_FOIL_HELMET(ItemTypes.HELMET,"Can protect you","against conspiracies,","but certainly not", "against electricity!"),
    QUEEN_OF_THE_FOREST(ItemTypes.LEGGINGS,"Become one with nature.","","To hunt is to live."),
    SCARLET_DANCER(ItemTypes.WEAPON,"Oh, the thrill of dancing","to a battle's rhythm...","","The thrill of flirting","with death..."),
    IMMORTAL_FLESH(ItemTypes.CHESTPLATE,"The cycle of carnage","is the only constant","in life."),
    FEATHERED_BOW(ItemTypes.WEAPON,"Reflecting the local","fauna's elegance,","the user can't","help but to feel","graceful aswell."),
    THE_BODY(ItemTypes.CHESTPLATE,"The resilience of","a body can reach","impressive heights.","","But alone... it can't","be whole."),
    THE_MIND(ItemTypes.HELMET,"The strength of","mind can bring","enlightenment.","","But alone... it can't","be whole."),
    THE_SOUL(ItemTypes.BOOTS,"The freedom of a","soul can bring","inner peace.","","But alone... it can't","be whole."),
    TRINITY(ItemTypes.LEGGINGS,"One must seek balance","at all costs."),
    EXECUTIONERS_MASK(ItemTypes.HELMET,"The merciful face","of death shall","remain concealed."),
    HEADSMAN_BLADE(ItemTypes.WEAPON,"No victim is","unworthy of mercy.","Thy death ","shall be quick","and painless."),
    MAD_BUTCHER(ItemTypes.WEAPON,"dihgubsduygahsuidy","","- A once wise and sane", "butcher."),
    ELUSIVE_SHADOW(ItemTypes.WEAPON,"Check every corner.","","Every. Shadow."),
    BROKEN_FAITH(ItemTypes.WEAPON,"Whatever watches","over us must be","completely ignorant","or blind to the","horrors of this land..."),
////    UNWAVERING_FAITH(ItemTypes.WEAPON,"Check every corner.","","Every. Shadow."),
    DRUIDIC_PELTS(ItemTypes.CHESTPLATE,"Nature's beautiful","cycle...","To be wounded","To be healed"),
    HELLFORGE(ItemTypes.WEAPON,"This glowing-hot","cleaver seems","to sap it's user's", "might to grow even", "stronger...");

    @Getter
    private static final Map<Relics, RelicArmorDAO> relicArmorsData = RelicEditor.loadAllArmors();
    @Getter
    private static final Map<Relics, RelicWeaponDAO> relicWeaponsData = RelicEditor.loadAllWeapons();

//    private static Map<Relics, Map<InscriptionID, int[]>> relicStatValues = new HashMap<>();

    private final ItemTypes slot;
    private final String[] flavorText;

    Relics(ItemTypes slot, String... flavorText){
        this.slot = slot;
        this.flavorText = flavorText;
    }

    public ItemStack getItemForm(){
        Item relicItem = generate();
        ItemStack relicItemStack = relicItem.getItemForm();
        ItemMeta relicItemMeta = relicItemStack.getItemMeta();
        StringBuilder flavorTextBuilder = new StringBuilder();
        List<String> rawFlavorText = getFlavorText();

        for (String s : rawFlavorText){
            flavorTextBuilder.append(s).append("<br>");
        }
        relicItemMeta.getPersistentDataContainer().set(
                new NamespacedKey(Inscripted.getPlugin(),"flavor"),
                PersistentDataType.STRING,
                flavorTextBuilder.toString()
        );

        relicItemStack.setItemMeta(relicItemMeta);
        return relicItemStack;
    }

    //TODO: relic constructors should deserialize data from the relic definition file
    private Item generate(){
        if (slot.equals(ItemTypes.WEAPON)){
            RelicWeaponDAO weaponDAO = getRelicWeaponsData().get(this);
            List<InscriptionID> relicInscriptionIDs = new ArrayList<>(weaponDAO.genericData().specialInscriptions());
            relicInscriptionIDs.addAll(weaponDAO.genericData().inscriptions());
            List<Inscription> newlyGeneratedInscriptions = generateNewRelicInscriptionList(relicInscriptionIDs);
            return new Weapon(weaponDAO, newlyGeneratedInscriptions);
        } else { //Relic Armor generation
            RelicArmorDAO armorDAO = getRelicArmorsData().get(this);
            List<InscriptionID> relicInscriptionIDs =  new ArrayList<>(armorDAO.genericData().specialInscriptions());
            relicInscriptionIDs.addAll(armorDAO.genericData().inscriptions());
            List<Inscription> newlyGeneratedInscriptions = generateNewRelicInscriptionList(relicInscriptionIDs);
            return new Armor(armorDAO,newlyGeneratedInscriptions);
        }
    }
    private List<Inscription> generateNewRelicInscriptionList(List<InscriptionID> inscIDs){
        List<Inscription> newlyGeneratedInscriptions = new ArrayList<>();
        for (InscriptionID ID : inscIDs){

            newlyGeneratedInscriptions.add(new Inscription(ID));
        }
        return newlyGeneratedInscriptions;
    }

    private List<String> getFlavorText(){
        if (relicWeaponsData.containsKey(this)){
            return relicWeaponsData.get(this).genericData().flavorText();
        }
        if (relicArmorsData.containsKey(this)){
            return relicArmorsData.get(this).genericData().flavorText();
        }
        return new ArrayList<>();
    }

}
