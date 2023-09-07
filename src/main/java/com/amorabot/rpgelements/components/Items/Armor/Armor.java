package com.amorabot.rpgelements.components.Items.Armor;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.Items.Abstract.Item;
import com.amorabot.rpgelements.components.Items.Abstract.ItemRenderer;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.RendererTypes;
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

    private final ItemTypes armorSlot;
    private final ArmorTypes type; //Trim materials and patterns are inferred
//    private final ArmorTrim trim;
    private Map<DefenceTypes, Integer> defencesMap = new HashMap<>();

    //Validation for armorSlot arg. is done prior to instantiation
    public Armor(ItemTypes armorSlot, int ilvl, ArmorTypes type, ItemRarities rarity, boolean identified){
        super(ilvl, rarity, identified);
        this.armorSlot = armorSlot;
        this.type = type;
        this.name = "TestName";
        if (isIdentified()){
            setRenderer(RendererTypes.BASIC);
        } else if (getRarity() == ItemRarities.COMMON){
            identify();
        } else {
            setRenderer(RendererTypes.UNIDENTIFIED);
        }
        mapBase(armorSlot,type, ilvl);
        defencesMap.put(DefenceTypes.HEALTH, 40); //FOR TESTING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }
    public Armor(ItemTypes armorSlot, int ilvl, ItemRarities rarity, boolean identified) { //Random generation constructor
        super(ilvl, rarity, identified);
        ArmorTypes[] armorTypes = ArmorTypes.values();
        int typeIndex = CraftingUtils.getRandomNumber(0, armorTypes.length-1);
        ArmorTypes selectedType = armorTypes[typeIndex];
        this.armorSlot = armorSlot;
        this.type = selectedType;
        this.name = "TestName";
        if (isIdentified()){
            setRenderer(RendererTypes.BASIC);
        } else if (getRarity() == ItemRarities.COMMON){
            identify();
        } else {
            setRenderer(RendererTypes.UNIDENTIFIED);
        }
        mapBase(armorSlot,selectedType, ilvl);
        defencesMap.put(DefenceTypes.HEALTH, 40); //FOR TESTING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }

    private void mapBase(ItemTypes armorSlot, ArmorTypes type, int ilvl){
        this.vanillaMaterial = type.mapArmorBase(ilvl, armorSlot);
        type.mapBaseStats(ilvl, armorSlot, defencesMap);
    }
    public ArmorTypes getType() {
        return type;
    }
    public ItemTypes getArmorSlot() {
        return armorSlot;
    }

    public Map<DefenceTypes, Integer> getDefencesMap() {
        return defencesMap;
    }

    @Override
    public void imprint(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        List<String> lore = new ArrayList<>();

        ItemRenderer itemRenderer = getRenderer();
        itemRenderer.renderMainStat(this, lore);
        //...
        //...
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
            displayName += "Unidentified " + this.type.toString().toLowerCase() + getArmorSlot().toString().toLowerCase();
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
