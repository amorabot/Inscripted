package com.amorabot.inscripted.components.Items.Abstract;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Armor.Armor;
import com.amorabot.inscripted.components.Items.Armor.ArmorTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.*;
import com.amorabot.inscripted.components.Items.Interfaces.ItemSubtype;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;
import com.amorabot.inscripted.components.Items.modifiers.Inscription;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;
import com.amorabot.inscripted.components.Items.modifiers.data.HybridInscriptionData;
import com.amorabot.inscripted.components.Items.modifiers.data.InscriptionData;
import com.amorabot.inscripted.components.renderers.ItemInterfaceRenderer;
import com.amorabot.inscripted.utils.Utils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.*;

@Getter
@EqualsAndHashCode
public abstract class Item implements Serializable {

    private final int ilvl;
    protected final ItemTypes category;
    @Setter
    private boolean identified;
    @Setter
    private ItemRarities rarity;
    private Tiers tier;
    @Setter
    private int quality; //Caps at +10
    @Setter
    private Inscription implicit;

    @Setter
    protected String name;
    protected Material vanillaMaterial;
    protected boolean corrupted;
    private final List<Inscription> inscriptions = new ArrayList<>();

    public Item(int ilvl, ItemTypes category){
        this.ilvl = ilvl;
        this.category = category;
        this.identified = false;
        this.corrupted = false;
    }

    public Item(int ilvl, ItemRarities rarity, boolean identified, boolean corrupted, ItemTypes itemCategory){
        this.identified = identified;
        this.ilvl = ilvl;
        this.rarity = rarity;
        this.corrupted = corrupted;
        this.category = itemCategory;
    }
    protected abstract void setup();
    //-------------------------------------------------------------------------
    public <subType extends Enum<subType> & ItemSubtype> void imprint(ItemStack item, subType subType){
        final int mainStatPadding = 1;
        final int inscriptionsPadding = 3;
        final int inscriptions = this.getInscriptions().size();
        Component emptyLine = Component.text("");
        Component descriptionLine;

        List<Component> imprintedLore = new ArrayList<>();

        if (subType instanceof WeaponTypes type){
            Weapon weaponData = (Weapon) this;
            imprintedLore.add(emptyLine);
            imprintedLore.addAll(ItemInterfaceRenderer.renderDamage(weaponData, mainStatPadding));
            imprintedLore.add(emptyLine);


            descriptionLine = ItemInterfaceRenderer.renderDescription(this, type.toString(),0);
        } else if (subType instanceof ArmorTypes type) {
            Armor armorData = (Armor) this;
            imprintedLore.add(emptyLine);
            imprintedLore.addAll(ItemInterfaceRenderer.renderDefences(armorData,mainStatPadding));
            imprintedLore.add(emptyLine);

            descriptionLine = ItemInterfaceRenderer.renderDescription(this, this.getCategory().toString(),0);
        } else {
            //...
            descriptionLine = ItemInterfaceRenderer.renderDescription(this, "INVALID",0);
        }
//        imprintedLore.add(ItemInterfaceRenderer.renderImplicit(this,mainStatPadding,subType));
//        imprintedLore.add(emptyLine);
//        imprintedLore.addAll(ItemInterfaceRenderer.renderRequirements(this,mainStatPadding));


        ItemRarities rarity = this.getRarity();
        switch (rarity){
            case AUGMENTED,RUNIC -> {
//                imprintedLore.add(emptyLine);
//                imprintedLore.add(ItemInterfaceRenderer.getRunicLine(true, inscriptions, highestLength));
                imprintedLore.add(ItemInterfaceRenderer.getInscriptionHeader(inscriptions,mainStatPadding+1));
//                imprintedLore.add(emptyLine);
                imprintedLore.addAll(ItemInterfaceRenderer.renderInscriptions(this, inscriptionsPadding+1));
//                imprintedLore.add(ItemInterfaceRenderer.getRunicLine(false, inscriptions, highestLength));
//                imprintedLore.add(emptyLine);
                imprintedLore.add(ItemInterfaceRenderer.getInscriptionFooter(mainStatPadding+1));
            }
            case RELIC -> {

            }
            default -> imprintedLore.add(emptyLine);
        }

        imprintedLore.add(emptyLine);
        imprintedLore.add(ItemInterfaceRenderer.renderImplicit(this,mainStatPadding,subType));
        imprintedLore.add(emptyLine);
        imprintedLore.addAll(ItemInterfaceRenderer.renderRequirements(this,mainStatPadding));
        imprintedLore.add(emptyLine);

        imprintedLore.add(descriptionLine);

        item.lore(imprintedLore);

        item.editMeta((itemMeta)-> {
            assert itemMeta != null;
            itemMeta.setUnbreakable(true);
            //TODO: add attributes so they can be hidden (mojank problem)
            itemMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier("tempArmor", 0.1D, AttributeModifier.Operation.ADD_NUMBER));
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        });

        if (isIdentified()){
            ItemInterfaceRenderer.setDisplayName(getName(),item,getRarity(),isCorrupted(),this.quality);
            return;
        }
        ItemInterfaceRenderer.setDisplayName("Unidentified " + subType.toString().toLowerCase(),item,getRarity(),false,0);
    }
    public abstract ItemStack getItemForm(Inscripted plugin);
    protected abstract void serializeContainers(Item itemData, ItemStack item);
    //-------------------------------------------------------------------------

    public void identify() {
        if (isIdentified()){
            return;
        }
        this.identified = true;
    }
    public void corrupt(){
        if (isCorrupted()){
            return;
        }
        this.corrupted = true;
    }
    protected void setTier(Tiers tier) {
        this.tier = tier;
    }
    protected abstract void mapBase();
    public List<Inscription> getInscriptionList(){
        return this.inscriptions;
    }
    public Set<InscriptionID> getInscriptions(){
        Set<InscriptionID> auxSet = new HashSet<>();
        for (Inscription mod : this.inscriptions){
            auxSet.add(mod.getInscription());
        }
        return auxSet;
    }
    public void addInscription(Inscription newMod) {
        getInscriptionList().add(newMod);
    }
    public double getStarRating() { //Voltar pra acesso protected, so pra uso interno
        double percentileSum = 0;
        int invalidMods = 0;
        for (Inscription mod : getInscriptionList()){
            InscriptionID inscID = mod.getInscription();
            if (inscID.getData().isKeystone() || inscID.getData().isUniqueEffect()){
                invalidMods++;
                continue;
            }
            boolean invertValue = !inscID.isPositive();
            RangeTypes range = null;
            if (inscID.getData() instanceof InscriptionData){
                range = ((InscriptionData)inscID.getData()).getDefinitionData().rangeType();
            } else if (inscID.getData() instanceof HybridInscriptionData) {
                range = RangeTypes.SINGLE_VALUE; //Ignore SR calc for Hybrid insc for now
            }
            if (range.equals(RangeTypes.SINGLE_VALUE)){
                //Even if it's negative, it's max rolled so it doesnt count towards SR
                invalidMods+=1;
                continue;
            }
            double inscBP = mod.getBasePercentile();
            if (invertValue){inscBP = (1D-inscBP);}
            percentileSum += inscBP;
        }
        if (!getInscriptionList().isEmpty()){
            double percentileAvg = percentileSum/ (getInscriptionList().size()-invalidMods);
            Utils.log("SR: " + percentileAvg);
            return percentileAvg;
        }
        return 0;
    }
    //-1, 0, 1 Return values (Fail, neutral, success)
    public int improveQuality(){
        if (getQuality() >=10){return -1;}
        setQuality(getQuality() + 1);
        return 0;
    }
}
