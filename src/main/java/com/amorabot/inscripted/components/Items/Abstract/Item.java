package com.amorabot.inscripted.components.Items.Abstract;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.*;
import com.amorabot.inscripted.item.structure.ItemRarities;
import com.amorabot.inscripted.item.structure.Tiers;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

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
    protected String name;
    protected Material vanillaMaterial;
    protected boolean corrupted;

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
    public abstract ItemStack getItemForm();

    protected void setTier(Tiers tier) {
        this.tier = tier;
    }
    protected abstract void mapBase();
}
