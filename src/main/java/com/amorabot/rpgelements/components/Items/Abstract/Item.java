package com.amorabot.rpgelements.components.Items.Abstract;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.*;
import com.amorabot.rpgelements.CustomDataTypes.RPGElementsContainer;
import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.Items.DataStructures.Modifier;
import com.amorabot.rpgelements.components.Items.Interfaces.ItemModifier;
import com.amorabot.rpgelements.components.Items.UnidentifiedRenderer;
import com.amorabot.rpgelements.components.Items.Weapon.BasicWeaponRenderer;
import com.amorabot.rpgelements.components.Items.Weapon.WeaponModifiers;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class Item implements RPGElementsContainer {

    private boolean identified;
    private final int ilvl;
    private ItemRarities rarity;
    private Tiers tier;
    private Implicit implicit;
    protected RendererTypes renderer;

    protected String name;
    protected Material vanillaMaterial;
    protected boolean corrupted;

    public Item(int ilvl, ItemRarities rarity, boolean identified, boolean corrupted){
        this.identified = identified;
        this.ilvl = ilvl;
        this.rarity = rarity;
        this.corrupted = corrupted;
        setInitialRenderer();
    }
    //-------------------------------------------------------------------------
    public abstract void imprint(ItemStack item);
    public abstract ItemStack getItemForm(RPGElements plugin);
    protected abstract void serializeContainers(RPGElements plugin, Item itemData, ItemStack item);
    //-------------------------------------------------------------------------
    protected abstract int getStarRating();
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
    //add SetName
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
    public abstract ItemRenderer getRenderer();
    public void setRenderer(RendererTypes rendererType){
        this.renderer = rendererType;
    }
    public Tiers getTier() {
        return tier;
    }
    protected void setTier(Tiers tier) {
        this.tier = tier;
    }
    private void setInitialRenderer(){
        if (isIdentified()){
            setRenderer(RendererTypes.BASIC);
        } else if (getRarity() == ItemRarities.COMMON){
            identify();
        } else {
            setRenderer(RendererTypes.UNIDENTIFIED);
        }
    }
    protected abstract void mapBase();
    protected void mapTier(){
        if (ilvl<=Tiers.T1.getMaxLevel()){
            setTier(Tiers.T1);
            return;
        } else if (ilvl<=Tiers.T2.getMaxLevel()){
            setTier(Tiers.T2);
            return;
        } else if (ilvl<=Tiers.T3.getMaxLevel()){
            setTier(Tiers.T3);
            return;
        } else if (ilvl<=Tiers.T4.getMaxLevel()){
            setTier(Tiers.T4);
            return;
        } else if (ilvl<=Tiers.T5.getMaxLevel()) {
            setTier(Tiers.T5);
            return;
        }
        //If ilvl is greater than T5 threshold, return t5
        setTier(Tiers.T5);
    }
    public abstract  <ModifierType extends Enum<ModifierType> & ItemModifier> void addModifier(Modifier<ModifierType> mod);
}
