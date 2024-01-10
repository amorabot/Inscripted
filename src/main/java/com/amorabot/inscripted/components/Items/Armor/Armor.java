package com.amorabot.inscripted.components.Items.Armor;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.events.FunctionalItemAccessInterface;
import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Abstract.ItemRenderer;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.*;
import com.amorabot.inscripted.components.Items.UnidentifiedRenderer;
import com.amorabot.inscripted.utils.CraftingUtils;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Armor extends Item {

    private final ArmorTypes type;
    private final int baseHealth;
    private Map<DefenceTypes, Integer> defencesMap = new HashMap<>();
    //Define level limitations for armors

    public Armor(ItemTypes armorSlot, int ilvl, ArmorTypes type, ItemRarities rarity, boolean identified, boolean corrupted){
        super(ilvl, rarity, identified, corrupted, armorSlot);
        this.type = type;
        setup();
        this.baseHealth = getType().mapBaseStats(this);
    }
    public Armor(ItemTypes armorPiece, int ilvl, ItemRarities rarity, boolean identified, boolean corrupted) { //Random generation constructor
        super(ilvl, rarity, identified, corrupted, armorPiece);
        ArmorTypes[] armorTypes = ArmorTypes.values();
        int typeIndex = CraftingUtils.getRandomNumber(0, armorTypes.length-1);
        this.type = armorTypes[typeIndex];
        setup();
        this.baseHealth = getType().mapBaseStats(this);
    }
    @Override
    protected void setup(){
        setTier(Tiers.mapItemLevel(getIlvl()));
        setName(getType().getTierName(getTier()) + " " + getCategory().toString().toLowerCase());
        setImplicit(defineImplicit(getType().toString()));
        mapBase();
    }

    @Override
    protected void mapBase(){
        this.vanillaMaterial = getType().mapArmorBase(getTier(), getCategory());
    }

    public ArmorTypes getType() {
        return type;
    }
    public Map<DefenceTypes, Integer> getDefencesMap() {
        return defencesMap;
    }
    public int getBaseHealth() {
        return baseHealth;
    }
    private ArmorTrim defineArmorTrim(){
        TrimPattern pattern;
        TrimMaterial material;
        switch (getType()){
            case HEAVY_PLATING -> material = TrimMaterial.REDSTONE;
            case CARVED_PLATING -> material = TrimMaterial.GOLD;
            case LIGHT_CLOTH -> material = TrimMaterial.EMERALD;
            case RUNIC_LEATHER -> material = TrimMaterial.NETHERITE;
            case ENCHANTED_SILK -> material = TrimMaterial.LAPIS;
            case RUNIC_STEEL -> material = TrimMaterial.AMETHYST;
            default -> material = TrimMaterial.QUARTZ; //Signals error
        }
        switch (getCategory()){
            case HELMET -> pattern = TrimPattern.HOST;
            case CHESTPLATE -> pattern = TrimPattern.SHAPER;
            case LEGGINGS -> pattern = TrimPattern.SILENCE;
            case BOOTS -> pattern = TrimPattern.HOST;
            default -> pattern = TrimPattern.EYE; //Signals error
        }
        return new ArmorTrim(material, pattern);
    }
    @Override
    public void imprint(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        List<String> lore = new ArrayList<>();
        ItemRenderer itemRenderer = getRenderer();

        itemRenderer.renderAllCustomLore(this, lore, type);

        itemMeta.setLore(lore);
        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
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
            displayName += "Unidentified " + getCategory().toString().toLowerCase();
        }
        itemRenderer.setDisplayName(displayName, item);
    }

    @Override
    public ItemStack getItemForm(Inscripted plugin) {
        ItemStack armorItem = new ItemStack(this.vanillaMaterial);
        imprint(armorItem);

        //Assuming its always a valid item (A set can be created for all possible armortypes and support custom ones)
        ArmorMeta armorMeta = (ArmorMeta) armorItem.getItemMeta();
        assert armorMeta != null;
        armorMeta.setTrim(defineArmorTrim());
        armorMeta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
        armorItem.setItemMeta(armorMeta);
        serializeContainers(plugin, this, armorItem);
        return armorItem;
    }

    @Override
    protected void serializeContainers(Inscripted plugin, Item itemData, ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        FunctionalItemAccessInterface.serializeArmor(this, dataContainer);
        item.setItemMeta(itemMeta);
    }

    @Override
    public ItemRenderer getRenderer() {
        switch (this.renderer){
            case UNIDENTIFIED -> {
                return new UnidentifiedRenderer();
            }
            case BASIC -> {
                return new ArmorRenderer();
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
