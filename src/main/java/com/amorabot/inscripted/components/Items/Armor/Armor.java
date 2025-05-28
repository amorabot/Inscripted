package com.amorabot.inscripted.components.Items.Armor;

import com.amorabot.inscripted.components.Items.modifiers.Inscription;
import com.amorabot.inscripted.components.Items.modifiers.data.StatDefinition;
import com.amorabot.inscripted.components.Player.stats.PlayerStats;
//import com.amorabot.inscripted.events.FunctionalItemAccessInterface;
import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.*;
import com.amorabot.inscripted.item.structure.Armor.ArmorTypes;
import com.amorabot.inscripted.item.structure.ItemRarities;
import com.amorabot.inscripted.item.structure.Tiers;
import com.amorabot.inscripted.utils.CraftingUtils;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.HashMap;
import java.util.Map;

public class Armor extends Item {

    private final ArmorTypes type;
//    private final int baseHealth;
//    private final int baseHealthVariance;

    public Armor(ItemTypes armorSlot, int ilvl, ArmorTypes type, ItemRarities rarity, boolean identified, boolean corrupted){
        super(ilvl, rarity, identified, corrupted, armorSlot);
        this.type = type;
        setup();
//        this.baseHealth = getSubype().getBaseHealthValue(getTier(),armorSlot);
//        this.baseHealthVariance = CraftingUtils.getRandomNumber(-ArmorTypes.BASE_VARIANCE, ArmorTypes.BASE_VARIANCE);
    }
    public Armor(ItemTypes armorPiece, int ilvl, ItemRarities rarity, boolean identified, boolean corrupted) { //Random generation constructor
        super(ilvl, rarity, identified, corrupted, armorPiece);
        ArmorTypes[] armorTypes = ArmorTypes.values();
        int typeIndex = CraftingUtils.getRandomNumber(0, armorTypes.length-1);
        this.type = armorTypes[typeIndex];
        setup();
//        this.baseHealth = getSubype().mapHealthValue(this);
//        this.baseHealthVariance = CraftingUtils.getRandomNumber(-ArmorTypes.BASE_VARIANCE, ArmorTypes.BASE_VARIANCE);
    }

    @Override
    protected void setup(){
        setTier(Tiers.mapItemLevel(getIlvl()));
        setName(getSubype().getTierName(getTier()) + " " + getCategory().toString().toLowerCase());
//        setImplicit(Archetypes.mapImplicitFor(getSubype(), getTier(), isCorrupted()));
        mapBase();
    }

    @Override
    protected void mapBase(){
//        this.vanillaMaterial = getSubype().mapArmorBase(getTier(), getCategory());
    }

    public ArmorTypes getSubype() {
        return type;
    }
    @Override
    public ItemStack getItemForm() {
//        ItemStack armorItem = new ItemStack(this.vanillaMaterial);
//
//        imprint(armorItem, type);
//
//        //Assuming its always a valid item (A set can be created for all possible armortypes and support custom ones)
//        ArmorMeta armorMeta = (ArmorMeta) armorItem.getItemMeta();
//        assert armorMeta != null;
////        armorMeta.setTrim(defineArmorTrim());
//        armorMeta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
//        armorItem.setItemMeta(armorMeta);
//        serializeContainers(this, armorItem);
//        return armorItem;
        return null;
    }

//    @Override
//    protected void serializeContainers(Item itemData, ItemStack item) {
////        FunctionalItemAccessInterface.serializeItem(item,this);
//    }

    public Map<DefenceTypes, Integer> getLocalDefences(){ //Once a weapon is created, the damage map needs to be updated to contain any possible new damages
        return null;
    }
}
