package com.amorabot.rpgelements.components.Items.Armor;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.Items.Abstract.Item;
import com.amorabot.rpgelements.components.Items.Abstract.ItemRenderer;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.*;
import com.amorabot.rpgelements.components.Items.UnidentifiedRenderer;
import com.amorabot.rpgelements.utils.CraftingUtils;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Armor extends Item {

    private final ItemTypes armorPiece;
    private final ArmorTypes type; //Trim materials and patterns are inferred
//    private final ArmorTrim trim;
    private final int baseHealth;
    private Map<DefenceTypes, Integer> defencesMap = new HashMap<>();

    public Armor(ItemTypes armorSlot, int ilvl, ArmorTypes type, ItemRarities rarity, boolean identified, boolean corrupted){
        super(ilvl, rarity, identified, corrupted);
        mapTier();
        this.armorPiece = armorSlot;
        this.type = type;
        this.name = type.toString();
        Implicit itemImplicit;
        try {
            if (isCorrupted()){
                //Decide wether the implicit is corrupted too
                //...
                itemImplicit = Implicit.valueOf(type+"_"+ImplicitType.CORRUPTED);
                //Or alternate corrupted...
            } else {
                itemImplicit = Implicit.valueOf(type+"_"+ImplicitType.STANDARD);
            }
        } catch (IllegalArgumentException exception){
            exception.printStackTrace();
            itemImplicit = Implicit.AXE_STANDARD;
        }
        setImplicit(itemImplicit);
        mapBase();
        this.baseHealth = getType().mapBaseHealth(getTier(), armorPiece);
    }
    public Armor(ItemTypes armorPiece, int ilvl, ItemRarities rarity, boolean identified, boolean corrupted) { //Random generation constructor
        super(ilvl, rarity, identified, corrupted);
        mapTier();
        ArmorTypes[] armorTypes = ArmorTypes.values();
        int typeIndex = CraftingUtils.getRandomNumber(0, armorTypes.length-1);
        ArmorTypes selectedType = armorTypes[typeIndex];
        this.armorPiece = armorPiece;
        this.type = selectedType;
        this.name = type.toString();
        Implicit itemImplicit;
        try {
            if (isCorrupted()){
                //Decide wether the implicit is corrupted too
                //...
                itemImplicit = Implicit.valueOf(type+"_"+ImplicitType.CORRUPTED);
                //Or alternate corrupted...
            } else {
                itemImplicit = Implicit.valueOf(type+"_"+ImplicitType.STANDARD);
            }
        } catch (IllegalArgumentException exception){
            exception.printStackTrace();
            itemImplicit = Implicit.AXE_STANDARD;
        }
        setImplicit(itemImplicit);
        mapBase();
        this.baseHealth = getType().mapBaseHealth(getTier(), armorPiece);
    }

    @Override
    protected void mapBase(){
        this.vanillaMaterial = getType().mapArmorBase(getTier(), getArmorPiece());
        getType().mapBaseDefences(getIlvl(), getArmorPiece(), getDefencesMap());
    }
    public ArmorTypes getType() {
        return type;
    }
    public ItemTypes getArmorPiece() {
        return armorPiece;
    }
    public Map<DefenceTypes, Integer> getDefencesMap() {
        return defencesMap;
    }
    public int getBaseHealth() {
        return baseHealth;
    }

    @Override
    public void imprint(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        List<String> lore = new ArrayList<>();

        ItemRenderer itemRenderer = getRenderer();
        itemRenderer.renderMainStat(this, lore);
        //...
        itemRenderer.renderDescription(this, lore, type);
        itemRenderer.renderTag(this, lore);

        itemRenderer.placeDivs(lore);

        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);

        String displayName = "";
        switch (getRarity()){
            case COMMON -> displayName = "&f";
            case MAGIC -> displayName = "&9";
            case RARE -> displayName = "&e";
        }
        if (isIdentified()){
            displayName += getName();
        } else {
            displayName += "Unidentified " + this.type.toString().toLowerCase() + getArmorPiece().toString().toLowerCase();
        }
        itemRenderer.setDisplayName(displayName, item);
    }

    @Override
    public ItemStack getItemForm(RPGElements plugin) {
        ItemStack armorItem = new ItemStack(this.vanillaMaterial);
        imprint(armorItem);

//        serializeContainers(plugin, this, armorItem);
        return armorItem;
    }

    @Override
    protected void serializeContainers(RPGElements plugin, Item itemData, ItemStack item) {

    }

    @Override
    protected int getStarRating() {
        return 0;
    }

    @Override
    public ItemRenderer getRenderer() {
        switch (this.renderer){
            case UNIDENTIFIED -> {
                return new UnidentifiedRenderer();
            }
            case BASIC -> {
                return new BasicArmorRenderer();
            }
            case CORRUPTED -> {
                Utils.log("No corruptedArmorRenderer");
                return null;
            }
            default -> {
                Utils.log("No renderer defined/No ItemRenderer class implemented for this renderer constant");
                return null;
            }
        }
    }
}
