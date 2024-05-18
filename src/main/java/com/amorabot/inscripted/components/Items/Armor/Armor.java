package com.amorabot.inscripted.components.Items.Armor;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Abstract.ItemCategory;
import com.amorabot.inscripted.components.Items.modifiers.Inscription;
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
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.List;
import java.util.Map;

public class Armor extends Item implements ItemCategory {

    private final ArmorTypes type;
    private final int baseHealth;
    private final int baseHealthVariance;

    public Armor(ItemTypes armorSlot, int ilvl, ArmorTypes type, ItemRarities rarity, boolean identified, boolean corrupted){
        super(ilvl, rarity, identified, corrupted, armorSlot);
        this.type = type;
        setup();
        this.baseHealth = getSubype().mapHealthValue(this);
        this.baseHealthVariance = CraftingUtils.getRandomNumber(-ArmorTypes.percentHealthVariance, ArmorTypes.percentHealthVariance);
    }
    public Armor(ItemTypes armorPiece, int ilvl, ItemRarities rarity, boolean identified, boolean corrupted) { //Random generation constructor
        super(ilvl, rarity, identified, corrupted, armorPiece);
        ArmorTypes[] armorTypes = ArmorTypes.values();
        int typeIndex = CraftingUtils.getRandomNumber(0, armorTypes.length-1);
        this.type = armorTypes[typeIndex];
        setup();
        this.baseHealth = getSubype().mapHealthValue(this);
        this.baseHealthVariance = CraftingUtils.getRandomNumber(-ArmorTypes.percentHealthVariance, ArmorTypes.percentHealthVariance);
    }
    @Override
    protected void setup(){
        setTier(Tiers.mapItemLevel(getIlvl()));
        setName(getSubype().getTierName(getTier()) + " " + getCategory().toString().toLowerCase());
//        setImplicit(Implicits.getImplicitFor(getSubype(), isCorrupted()));
        mapBase();
    }

    @Override
    protected void mapBase(){
        this.vanillaMaterial = getSubype().mapArmorBase(getTier(), getCategory());
    }

    public ArmorTypes getSubype() {
        return type;
    }
    public int getBaseHealth() {
        return (int) ( baseHealth * ( 1 + ( (float) baseHealthVariance/100 ) ) );
    }
    private ArmorTrim defineArmorTrim(){
        TrimPattern pattern;
        TrimMaterial material;
        switch (getSubype()){
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
    public ItemStack getItemForm(Inscripted plugin) {
        ItemStack armorItem = new ItemStack(this.vanillaMaterial);

        imprint(armorItem, type);

        //Assuming its always a valid item (A set can be created for all possible armortypes and support custom ones)
        ArmorMeta armorMeta = (ArmorMeta) armorItem.getItemMeta();
        assert armorMeta != null;
        armorMeta.setTrim(defineArmorTrim());
        armorMeta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
        armorItem.setItemMeta(armorMeta);
        serializeContainers(this, armorItem);
        return armorItem;
    }

    @Override
    protected void serializeContainers(Item itemData, ItemStack item) {
        FunctionalItemAccessInterface.serializeItem(item,this);
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

    public Map<DefenceTypes, Integer> getLocalDefences(){
        List<Inscription> mods = getInscriptionList();
        Map<DefenceTypes, Integer> defMap = getSubype().mapBaseStats(this); //Newly reset defMap
        int qualityBaseStatIncrease = 5 * getQuality();
        //All explicitly defined for clarity's sake
        int healthSum = defMap.get(DefenceTypes.HEALTH);
        int healthPercentModifier = qualityBaseStatIncrease;

        int wardSum = defMap.getOrDefault(DefenceTypes.WARD, 0);
        int wardPercentModifier = qualityBaseStatIncrease;

        int armorSum = defMap.getOrDefault(DefenceTypes.ARMOR, 0);
        int armorPercentModifier = qualityBaseStatIncrease;

        int dodgeSum = defMap.getOrDefault(DefenceTypes.DODGE, 0);
        int dodgePercentModifier = qualityBaseStatIncrease; //Not implemented yet via modifiers

//        for (DefenceTypes def : defMap.keySet()){
//            switch (def){
//                case HEALTH -> {
//                    Set<ModifierIDs> healthMods = ModifierIDs.getLocalModsFor(PlayerStats.HEALTH);
//                    //Add all hybrid mods that target it
//                    healthMods.add(ModifierIDs.ARMOR_HEALTH);
//                    healthMods.add(ModifierIDs.DODGE_HEALTH);
//                    healthMods.add(ModifierIDs.WARD_HEALTH);
//                    for (Modifier mod : mods){
//                        if (!healthMods.contains(mod.getModifierID())){
//                            continue;
//                        }
//                        int[] hpValue = ModifierManager.getMappedFinalValueFor(mod);
//                        switch (mod.getModifierID()){
//                            case HEALTH -> healthSum += hpValue[0];
//                            case ARMOR_HEALTH, DODGE_HEALTH, WARD_HEALTH -> healthSum += hpValue[1];
//                            case HEALTH_PERCENT -> healthPercentModifier += hpValue[0];
//                        }
//                    }
//                }
//                case WARD -> {
//                    Set<ModifierIDs> wardMods = ModifierIDs.getLocalModsFor(PlayerStats.WARD);
//                    //Add all hybrid mods that target it
//                    wardMods.add(ModifierIDs.WARD_HEALTH);
//                    wardMods.add(ModifierIDs.ARMOR_WARD);
//                    wardMods.add(ModifierIDs.DODGE_WARD);
//                    for (Modifier mod : mods){
//                        if (!wardMods.contains(mod.getModifierID())){
//                            continue;
//                        }
//                        int[] wardValue = ModifierManager.getMappedFinalValueFor(mod);
//                        switch (mod.getModifierID()){
//                            case WARD -> wardSum += wardValue[0];
//                            case ARMOR_WARD, DODGE_WARD -> wardSum += wardValue[1];
//                            case WARD_HEALTH -> wardSum += wardValue[0];
//                            case WARD_PERCENT -> wardPercentModifier += wardValue[0];
//                        }
//                    }
//                }
//                case ARMOR ->{
//                    Set<ModifierIDs> armorMods = ModifierIDs.getLocalModsFor(PlayerStats.ARMOR);
//                    //Add all hybrid mods that target it
//                    armorMods.add(ModifierIDs.ARMOR_WARD);
//                    armorMods.add(ModifierIDs.ARMOR_DODGE);
//                    armorMods.add(ModifierIDs.ARMOR_HEALTH);
//                    for (Modifier mod : mods){
//                        if (!armorMods.contains(mod.getModifierID())){
//                            continue;
//                        }
//                        int[] armorValue = ModifierManager.getMappedFinalValueFor(mod);
//                        switch (mod.getModifierID()){
//                            case ARMOR -> armorSum += armorValue[0];
//                            case ARMOR_WARD, ARMOR_DODGE, ARMOR_HEALTH -> armorSum += armorValue[0];
//                            case ARMOR_PERCENT -> armorPercentModifier += armorValue[0];
//                        }
//                    }
//                }
//                case DODGE -> {
//                    Set<ModifierIDs> dodgeMods = ModifierIDs.getLocalModsFor(PlayerStats.DODGE);
//                    //Add all hybrid mods that target it
//                    dodgeMods.add(ModifierIDs.DODGE_HEALTH);
//                    dodgeMods.add(ModifierIDs.DODGE_WARD);
//                    dodgeMods.add(ModifierIDs.ARMOR_DODGE);
//                    for (Modifier mod : mods){
//                        if (!dodgeMods.contains(mod.getModifierID())){
//                            continue;
//                        }
//                        int[] dodgeValue = ModifierManager.getMappedFinalValueFor(mod);
//                        switch (mod.getModifierID()){
//                            case DODGE -> dodgeSum += dodgeValue[0];
//                            case DODGE_HEALTH, DODGE_WARD -> dodgeSum += dodgeValue[0];
//                            case ARMOR_DODGE -> dodgeSum += dodgeValue[1];
////                            case DODGE_PERCENT -> {
////                                dodgePercentModifier += ModifierManager.getMappedFinalValueFor(mod)[0];
////                            }
//                        }
//                    }
//                }
//                default -> Utils.error("Wrong local defence input (Stat compiler)");
//            }
//        }
//        float healthPercent = (float)(healthPercentModifier)/100;
//        float wardPercent = (float)(wardPercentModifier)/100;
//        float armorPercent = (float)(armorPercentModifier)/100;
//        float dodgePercent = (float)(dodgePercentModifier)/100;
//
//        //All local mods have been searched: Update all defence values
//        defMap.put(DefenceTypes.HEALTH, (int) (healthSum * ( 1f + healthPercent )));
//        defMap.put(DefenceTypes.WARD, (int) (wardSum * ( 1f + wardPercent )));
//        defMap.put(DefenceTypes.ARMOR, (int) (armorSum * ( 1f + armorPercent )));
//        defMap.put(DefenceTypes.DODGE, (int) (dodgeSum * ( 1f + dodgePercent )));
//
//        return defMap;
        return null;
    }

    @Override
    public void applyQuality() {

    }

    @Override
    public void getCorruptedImplicit() {

    }
}
