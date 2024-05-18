package com.amorabot.inscripted.components.Items.Abstract;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.*;
import com.amorabot.inscripted.components.Items.Interfaces.ItemSubtype;
import com.amorabot.inscripted.components.Items.modifiers.Inscription;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
    //TODO: Factory / Separate class for handling rendering
    @Setter
    protected RendererTypes renderer;

    @Setter
    protected String name;
    protected Material vanillaMaterial;
    protected boolean corrupted;
    private final List<Inscription> modifiers = new ArrayList<>();

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
        setInitialRenderer();
    }
    protected abstract void setup();
    //-------------------------------------------------------------------------
    public <subType extends Enum<subType> & ItemSubtype> void imprint(ItemStack item, subType subType){
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        List<String> lore = new ArrayList<>();
        ItemRenderer currentRenderer = getRenderer();

        currentRenderer.renderAllCustomLore(this, lore, subType);

        itemMeta.setLore(lore);
        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(itemMeta);

        if (isIdentified()){
            currentRenderer.setDisplayName(getRarity(), getName(), item, isCorrupted(), this.quality);
            return;
        }
        currentRenderer.setDisplayName(getRarity(), "Unidentified " + subType.toString().toLowerCase(), item, false, 0);
    }
    public abstract ItemStack getItemForm(Inscripted plugin);
    protected abstract void serializeContainers(Item itemData, ItemStack item);
    //-------------------------------------------------------------------------

    public void identify() {
        if (isIdentified()){
            return;
        }
        this.identified = true;
        setRenderer(RendererTypes.BASIC);
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
    public abstract ItemRenderer getRenderer();
    private void setInitialRenderer(){ //Sets upon item creation
        if (isIdentified()){
            setRenderer(RendererTypes.BASIC);
        } else if (getRarity() == ItemRarities.COMMON){
            identify(); //
        } else {
            setRenderer(RendererTypes.UNIDENTIFIED);
        }
    }
    protected abstract void mapBase();
    public List<Inscription> getInscriptionList(){
        return this.modifiers;
    }
    public Set<InscriptionID> getInscriptions(){
        Set<InscriptionID> auxSet = new HashSet<>();
        for (Inscription mod : this.modifiers){
            auxSet.add(mod.getInscription());
        }
        return auxSet;
    }
    public void addInscription(Inscription newMod) {
        getInscriptionList().add(newMod);
    }
    public double getStarRating() { //Voltar pra acesso protected, so pra uso interno
        double percentileSum = 0;
        for (Inscription mod : getInscriptionList()){
            percentileSum += mod.getBasePercentile();
        }
        if (!getInscriptionList().isEmpty()){
            return (percentileSum/ getInscriptionList().size());
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
