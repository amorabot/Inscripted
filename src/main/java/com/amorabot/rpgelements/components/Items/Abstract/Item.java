package com.amorabot.rpgelements.components.Items.Abstract;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.rpgelements.CustomDataTypes.RPGElementsContainer;
import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.RendererTypes;
import org.bukkit.inventory.ItemStack;

public abstract class Item implements RPGElementsContainer {

    private boolean identified;
    private final int ilvl;
    private ItemRarities rarity;
//    private Renderer renderer;
    protected RendererTypes renderer;

    public Item(int ilvl, ItemRarities rarity){
        this.identified = true;
        this.ilvl = ilvl;
        this.rarity = rarity;
    }
    //-------------------------------------------------------------------------
    public ItemTypes getItemType(){
        return ItemTypes.valueOf(this.getClass().getName().toUpperCase());
    }
    public abstract void render(ItemStack item, ItemRenderer itemRenderer);
    public abstract ItemStack getItemForm(RPGElements plugin);
    protected abstract void serializeContainers(RPGElements plugin, Item itemData, ItemStack item);
    //-------------------------------------------------------------------------
    public boolean isIdentified() {
        return identified;
    }
    public void identify() {
        this.identified = true;
    }
    //-------------------------------------------------------------------------
    public int getIlvl() {
        return ilvl;
    }
    //-------------------------------------------------------------------------
    public ItemRarities getRarity() {
        return rarity;
    }
    public void setRarity(ItemRarities rarity) {
        this.rarity = rarity;
    }
    //-------------------------------------------------------------------------
    public abstract ItemRenderer getRenderer();
    public void setRenderer(RendererTypes rendererType){
        this.renderer = rendererType;
    }
//    public Renderer getRenderer() {
//        return renderer;
//    }
//    public void setRenderer(Renderer renderer) {
//        this.renderer = renderer;
//    }
}
