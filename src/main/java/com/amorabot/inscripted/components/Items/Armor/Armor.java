package com.amorabot.inscripted.components.Items.Armor;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Abstract.ItemCategory;
import com.amorabot.inscripted.components.Items.modifiers.Inscription;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;
import com.amorabot.inscripted.components.Items.modifiers.data.HybridInscriptionData;
import com.amorabot.inscripted.components.Items.modifiers.data.InscriptionData;
import com.amorabot.inscripted.components.Items.modifiers.data.Modifier;
import com.amorabot.inscripted.components.Items.modifiers.data.StatDefinition;
import com.amorabot.inscripted.components.Player.archetypes.Archetypes;
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

import java.util.Arrays;
import java.util.HashMap;
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
        setImplicit(Archetypes.mapImplicitFor(getSubype(), getTier(), isCorrupted()));
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
        TrimMaterial material = getSubype().getTrimMaterial();
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

    public Map<DefenceTypes, Integer> getLocalDefences(){ //Once a weapon is created, the damage map needs to be updated to contain any possible new damages
        Map<DefenceTypes, Integer> defMap = getSubype().mapBaseStats(this); //Newly reset defMap
        Map<DefenceTypes, Integer> incPecentages = new HashMap<>();

        int qualityIncrease = 5*getQuality();
        if (qualityIncrease>0){
            for (DefenceTypes dmg : DefenceTypes.values()){
                incPecentages.put(dmg, qualityIncrease);
            }
        }

        for (Inscription mod : getInscriptionList()){
            //Local mod mapping
            InscriptionID armorMod = mod.getInscription();
            if (armorMod.isGlobal()){continue;}
            Modifier modData = armorMod.getData();
            if (modData instanceof InscriptionData inscriptionData){
                int[] mappedValues = mod.getMappedFinalValue();
                StatDefinition statDef = inscriptionData.getDefinitionData();
                mapLocalMods(defMap, incPecentages, statDef, mappedValues);
            } else if (modData instanceof HybridInscriptionData hybridInscriptionData) {
                StatDefinition[] defs = hybridInscriptionData.getStatDefinitions();
                for (int d = 0; d < defs.length; d++){
                    int[] currentMappedVal = mod.getMappedFinalValue(d);
                    mapLocalMods(defMap, incPecentages, defs[d], currentMappedVal);
                }
            } else {
                continue;
            }
        }

        for (DefenceTypes dmg : DefenceTypes.values()){
            int currDefence = defMap.getOrDefault(dmg,0);
            int currIncValue = incPecentages.getOrDefault(dmg,0);
            int updatedDefence = (int) Utils.applyPercentageTo(currDefence, currIncValue);
            if (updatedDefence == 0){continue;}
            defMap.put(dmg, updatedDefence);
        }

        return defMap;
    }
    private void mapLocalMods(Map<DefenceTypes, Integer> baseDefs, Map<DefenceTypes, Integer> percentages, StatDefinition statDef, int[] mappedValues){
        PlayerStats targetStat = statDef.stat();
        ValueTypes valueType = statDef.valueType();
        switch (targetStat){
            case ARMOR -> redirectValue(valueType, DefenceTypes.ARMOR, baseDefs, percentages, mappedValues);
            case DODGE -> redirectValue(valueType, DefenceTypes.DODGE, baseDefs, percentages, mappedValues);
            case WARD -> redirectValue(valueType, DefenceTypes.WARD, baseDefs, percentages, mappedValues);
            case HEALTH -> redirectValue(valueType, DefenceTypes.HEALTH, baseDefs, percentages, mappedValues);
        }
    }
    private void redirectValue
            (
                    ValueTypes valueType,
                    DefenceTypes defType,
                    Map<DefenceTypes, Integer> baseDefs,
                    Map<DefenceTypes, Integer> percentages,
                    int[] mappedValues
            )
    {
        switch (valueType){
            case FLAT -> addDefTypeToMap(defType, mappedValues[0], baseDefs);
            case INCREASED -> addDefTypeToMap(defType, mappedValues[0], percentages);
        }
    }
    private void addDefTypeToMap(DefenceTypes def, int value, Map<DefenceTypes, Integer> statMap){
        if (!statMap.containsKey(def)){
            statMap.put(def, value);
            return;
        }
        int selectedStat = statMap.get(def);
        statMap.put(def, (selectedStat + value));
    }

    @Override
    public void applyQuality() {

    }
}
