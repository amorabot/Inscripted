package com.amorabot.inscripted.components.Items.Abstract;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.*;
import com.amorabot.inscripted.components.Items.DataStructures.Modifier;
import com.amorabot.inscripted.components.Items.DataStructures.ModifierIDs;
import com.amorabot.inscripted.components.Items.Interfaces.ItemSubtype;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.Serializable;
import java.util.*;

public abstract class Item implements Serializable {

    private final int ilvl;
    protected final ItemTypes category;
    private boolean identified;
    private ItemRarities rarity;
    private Tiers tier;
    private int quality; //Caps at +10
    private Implicits implicit;
    //TODO: Factory / Separate class for handling rendering
    protected RendererTypes renderer;

    protected String name;
    protected Material vanillaMaterial;
    protected boolean corrupted;
    private List<Modifier> modifiers = new ArrayList<>();

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
        setInitialRenderer();
    }
    protected abstract void setup();
    //-------------------------------------------------------------------------
    public <subType extends Enum<subType> & ItemSubtype> void imprint(ItemStack item, subType subType){
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        List<String> lore = new ArrayList<>();
        ItemRenderer currentRenderer = getRenderer();

        currentRenderer.renderAllCustomLore(this, lore, subType);

        itemMeta.setLore(lore);
        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(itemMeta);

        if (isIdentified()){
            currentRenderer.setDisplayName(getRarity(), getName(), item, isCorrupted(), this.quality);
            return;
        }
        currentRenderer.setDisplayName(getRarity(), "Unidentified " + subType.toString().toLowerCase(), item, false, 0);
    }
    public abstract ItemStack getItemForm(Inscripted plugin);
    protected abstract void serializeContainers(Item itemData, ItemStack item);
    public ItemTypes getCategory(){
        return this.category;
    }
    //-------------------------------------------------------------------------
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
    public void setName(String newName){
        this.name = newName;
    }
    public String getName(){
        return this.name;
    }
    public boolean isCorrupted() {
        return corrupted;
    }
    public void corrupt(){
        if (isCorrupted()){
            return;
        }
        this.corrupted = true;
    }
    public Implicits getImplicit() {
        return implicit;
    }
    public void setImplicit(Implicits implicit) {
        this.implicit = implicit;
    }
    public Tiers getTier() {
        return tier;
    }
    protected void setTier(Tiers tier) {
        this.tier = tier;
    }
    public abstract ItemRenderer getRenderer();
    public void setRenderer(RendererTypes rendererType){
        this.renderer = rendererType;
    }
    private void setInitialRenderer(){ //Sets upon item creation
        if (isIdentified()){
            setRenderer(RendererTypes.BASIC);
        } else if (getRarity() == ItemRarities.COMMON){
            identify(); //
        } else {
            setRenderer(RendererTypes.UNIDENTIFIED);
        }
    }
    protected abstract void mapBase();
    public List<Modifier> getModifierList(){
        return this.modifiers;
    }
    public Set<ModifierIDs> getModIDs(){
        Set<ModifierIDs> auxSet = new HashSet<>();
        for (Modifier mod : this.modifiers){
            auxSet.add(mod.getModifierID());
        }
        return auxSet;
    }
    public void addModifier(Modifier newMod) {
        getModifierList().add(newMod);
    }
    public double getStarRating() { //Voltar pra acesso protected, so pra uso interno
        double percentileSum = 0;
        for (Modifier mod : getModifierList()){
            percentileSum += mod.getBasePercentile();
        }
        if (!getModifierList().isEmpty()){
            return (percentileSum/ getModifierList().size());
        }
        return 0;
    }
    public int getQuality() {
        return quality;
    }
    public void setQuality(int newQuality){
        this.quality = newQuality;
    }
    //-1, 0, 1 Return values (Fail, neutral, success)
    public int improveQuality(){
        if (getQuality() >=10){return -1;}
        setQuality(getQuality() + 1);
        return 0;
    }

    @Override
    public int hashCode(){
        return Objects.hash(ilvl, category, name, modifiers, implicit, corrupted, quality);
    }
}
