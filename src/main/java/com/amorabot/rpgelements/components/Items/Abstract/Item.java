package com.amorabot.rpgelements.components.Items.Abstract;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.*;
import com.amorabot.rpgelements.components.Items.DataStructures.NewModifier;
import com.amorabot.rpgelements.components.Items.Interfaces.RPGElementsContainer;
import com.amorabot.rpgelements.RPGElements;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class Item implements RPGElementsContainer {

    private boolean identified;
    private final int ilvl;
    private ItemRarities rarity;
    private Tiers tier;
    private Implicit implicit;
    protected RendererTypes renderer;
    protected final ItemTypes category;

    protected String name;
    protected Material vanillaMaterial;
    protected boolean corrupted;

    public Item(int ilvl, ItemRarities rarity, boolean identified, boolean corrupted, ItemTypes itemCategory){
        this.identified = identified;
        this.ilvl = ilvl;
        this.rarity = rarity;
        this.corrupted = corrupted;
        this.category = itemCategory;
        setInitialRenderer();
    }
    //-------------------------------------------------------------------------
    public abstract void imprint(ItemStack item);
    public abstract ItemStack getItemForm(RPGElements plugin);
    protected abstract void serializeContainers(RPGElements plugin, Item itemData, ItemStack item);
    public ItemTypes getCategory(){
        return this.category;
    }
    protected abstract int getStarRating();
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
        this.corrupted = true;
    }
    public Implicit getImplicit() {
        return implicit;
    }
    protected void setImplicit(Implicit implicit) {
        this.implicit = implicit;
    }
    protected Implicit defineImplicit(String subTypeString){
        Implicit itemImplicit;
        try {
            if (isCorrupted()){
                //...
                itemImplicit = Implicit.valueOf(subTypeString+"_"+ ImplicitType.CORRUPTED);
                //Or alternate corrupted...
            } else {
                itemImplicit = Implicit.valueOf(subTypeString+"_"+ImplicitType.STANDARD);
            }
        } catch (IllegalArgumentException exception){
            exception.printStackTrace();
            itemImplicit = Implicit.AXE_STANDARD;
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
    protected void mapTier(){
        if (getIlvl()<=Tiers.T1.getMaxLevel()){
            setTier(Tiers.T1);
            return;
        } else if (getIlvl()<=Tiers.T2.getMaxLevel()){
            setTier(Tiers.T2);
            return;
        } else if (getIlvl()<=Tiers.T3.getMaxLevel()){
            setTier(Tiers.T3);
            return;
        } else if (getIlvl()<=Tiers.T4.getMaxLevel()){
            setTier(Tiers.T4);
            return;
        } else if (getIlvl()<=Tiers.T5.getMaxLevel()) {
            setTier(Tiers.T5);
            return;
        }
        //If ilvl is greater than T5 threshold, return t5
        setTier(Tiers.T5);
    }
//    public abstract  <ModifierType extends Enum<ModifierType> & ItemModifier> void addModifier(Modifier<ModifierType> mod);
    public abstract void addModifier(NewModifier newMod);
}
