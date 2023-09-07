package com.amorabot.rpgelements.components.Items.Abstract;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.rpgelements.CustomDataTypes.RPGElementsContainer;
import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.RendererTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.Tiers;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class Item implements RPGElementsContainer {

    private boolean identified;
    private final int ilvl;
    private ItemRarities rarity;
    private Tiers tier;
    protected RendererTypes renderer;

    protected String name;
    protected Material vanillaMaterial;
    protected boolean corrupted;

    public Item(int ilvl, ItemRarities rarity, boolean identified){
        this.identified = identified;
        this.ilvl = ilvl;
        this.rarity = rarity;
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
    public String getName(){
        return this.name;
    }
    public boolean isCorrupted() {
        return corrupted;
    }
    public void corrupt(){
        this.corrupted = true;
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
}
