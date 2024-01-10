package com.amorabot.inscripted.components.Items.Abstract;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.*;
import com.amorabot.inscripted.components.Items.DataStructures.Modifier;
import com.amorabot.inscripted.components.Items.DataStructures.ModifierIDs;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.*;

public abstract class Item implements Serializable {

    private final int ilvl;
    protected final ItemTypes category;
    private boolean identified;
    private ItemRarities rarity;
    private Tiers tier;
    private Implicits implicit;
    //TODO: Factory / Separate class for handling rendering
    protected RendererTypes renderer;

    protected String name;
    protected Material vanillaMaterial;
    protected boolean corrupted;
    private List<Modifier> modifiers = new ArrayList<>();

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
    public abstract void imprint(ItemStack item);
    public abstract ItemStack getItemForm(Inscripted plugin);
    protected abstract void serializeContainers(Inscripted plugin, Item itemData, ItemStack item);
    public ItemTypes getCategory(){
        return this.category;
    }
    //-------------------------------------------------------------------------
    public boolean isIdentified() {
        return identified;
    }
    public void identify() {
        if (isIdentified()){
            return;
        }
        this.identified = true;
        setRenderer(RendererTypes.BASIC);
    }
    public int getIlvl() {
        return ilvl;
    }
    public ItemRarities getRarity() {
        return rarity;
    }
    public void setRarity(ItemRarities rarity) {
        this.rarity = rarity;
    }
    public void setName(String newName){
        this.name = newName;
    }
    public String getName(){
        return this.name;
    }
    public boolean isCorrupted() {
        return corrupted;
    }
    public void corrupt(){
        if (isCorrupted()){
            return;
        }
        this.corrupted = true;
    }
    public Implicits getImplicit() {
        return implicit;
    }
    protected void setImplicit(Implicits implicit) {
        this.implicit = implicit;
    }
    protected Implicits defineImplicit(String subTypeString){
        Implicits itemImplicit;
        try {
            if (isCorrupted()){
                //...
                itemImplicit = Implicits.valueOf(subTypeString+"_"+ ImplicitType.CORRUPTED);
                //Or alternate corrupted...
            } else {
                itemImplicit = Implicits.valueOf(subTypeString+"_"+ImplicitType.STANDARD);
            }
        } catch (IllegalArgumentException exception){
            exception.printStackTrace();
            itemImplicit = Implicits.AXE_STANDARD;
        }
        return itemImplicit;
    }
    public Tiers getTier() {
        return tier;
    }
    protected void setTier(Tiers tier) {
        this.tier = tier;
    }
    public abstract ItemRenderer getRenderer();
    public void setRenderer(RendererTypes rendererType){
        this.renderer = rendererType;
    }
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
    public List<Modifier> getModifierList(){
        return this.modifiers;
    }
    public Set<ModifierIDs> getModIDs(){
        Set<ModifierIDs> auxSet = new HashSet<>();
        for (Modifier mod : this.modifiers){
            auxSet.add(mod.getModifierID());
        }
        return auxSet;
    }
    public void addModifier(Modifier newMod) {
        getModifierList().add(newMod);
    }
    public double getStarRating() { //Voltar pra acesso protected, so pra uso interno
        double percentileSum = 0;
        for (Modifier mod : getModifierList()){
            percentileSum += mod.getBasePercentile();
        }
        if (!getModifierList().isEmpty()){
            return (percentileSum/ getModifierList().size());
        }
        return 0;
    }

    @Override
    public int hashCode(){
        return Objects.hash(ilvl, category, name, modifiers);
    }
}
