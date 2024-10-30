package com.amorabot.inscripted.components.Items.modifiers.unique;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Armor.Armor;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.Files.RelicArmorDAO;
import com.amorabot.inscripted.components.Items.Files.RelicEditor;
import com.amorabot.inscripted.components.Items.Files.RelicWeaponDAO;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Items.modifiers.Inscription;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
    HEADSMAN_BLADE(ItemTypes.WEAPON,"No victim is","unworthy of mercy.","Thy death will","shall be quick","and painless."),
    MAD_BUTCHER(ItemTypes.WEAPON,"dihgubsduygahsuidy","","- A once wise and sane", "butcher."),
    ELUSIVE_SHADOW(ItemTypes.WEAPON,"Check every corner.","","Every. Shadow."),
    BROKEN_FAITH(ItemTypes.WEAPON,"Whatever watches","over us must be","completely ignorant","or blind to the","horrors of this land..."),
//    UNWAVERING_FAITH(ItemTypes.WEAPON,"Check every corner.","","Every. Shadow."),
    DRUIDIC_PELTS(ItemTypes.CHESTPLATE,"Nature's beautiful","cycle...","To be wounded","To be healed"),
    HELLFORGE(ItemTypes.WEAPON,"This glowing-hot","cleaver seems","to sap it's user's", "might to grow even", "stronger...");

    @Getter
    private static final Map<Relics, RelicArmorDAO> relicArmorsData = RelicEditor.loadAllArmors();
    @Getter
    private static final Map<Relics, RelicWeaponDAO> relicWeaponsData = RelicEditor.loadAllWeapons();
    private final ItemTypes slot;
    private final String[] flavorText;

    Relics(ItemTypes slot, String... flavorText){
        this.slot = slot;
        this.flavorText = flavorText;
    }

    public ItemStack getItemForm(){
        Item relicItem = generate();
        ItemStack relicItemStack = relicItem.getItemForm(Inscripted.getPlugin());
        ItemMeta relicItemMeta = relicItemStack.getItemMeta();
        List<String> lore = relicItemMeta.getLore();
        assert lore != null;
//        String itemTag = lore.remove(lore.size()-1);
        //TODO: Implement flavor text rendering at a Item (class) level -> item updating doest render the flavor text
        lore.add("");
        lore.add(Utils.color("&8──────✎──────    ").indent(4));
        for (String flavorLine : flavorText){
            String finalLine = ColorUtils.translateColorCodes("&8"+Utils.convertToPrettyString(flavorLine).indent(4));
            lore.add(finalLine);
        }
        lore.add(Utils.color("&8─────────────    ").indent(4));
        lore.add("");
//        lore.add(itemTag);
        relicItemMeta.setLore(lore);
        relicItemStack.setItemMeta(relicItemMeta);
        return relicItemStack;
    }

    //TODO: relic constructors should deserialize data from the relic definition file
    private Item generate(){
        if (slot.equals(ItemTypes.WEAPON)){
            RelicWeaponDAO weaponDAO = getRelicWeaponsData().get(this);
            List<Inscription> newlyGeneratedInscriptions = generateNewInscriptions(weaponDAO.inscriptions());
            return new Weapon(weaponDAO.name(),weaponDAO.ilvl(),weaponDAO.type(),weaponDAO.atkSpeed(),weaponDAO.baseDmg(),newlyGeneratedInscriptions);
        } else { //Relic Armor generation
            RelicArmorDAO armorDAO = getRelicArmorsData().get(this);
            List<Inscription> newlyGeneratedInscriptions = generateNewInscriptions(armorDAO.inscriptions());
            return new Armor(armorDAO.name(),armorDAO.ilvl(),armorDAO.slot(),armorDAO.type(),armorDAO.baseHealth(),newlyGeneratedInscriptions);
        }
    }
    private List<Inscription> generateNewInscriptions(List<InscriptionID> inscIDs){
        List<Inscription> newlyGeneratedInscriptions = new ArrayList<>();
        for (InscriptionID ID : inscIDs){
            newlyGeneratedInscriptions.add(new Inscription(ID));
        }
        return newlyGeneratedInscriptions;
    }


}
