package com.amorabot.inscripted.components.Items.Abstract;

import com.amorabot.inscripted.components.Items.Armor.Armor;
import com.amorabot.inscripted.item.structure.Armor.ArmorTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.*;
import com.amorabot.inscripted.item.structure.ItemSubtype;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.item.structure.Weapon.WeaponTypes;
import com.amorabot.inscripted.components.Items.modifiers.Inscription;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;
import com.amorabot.inscripted.components.Items.modifiers.data.HybridInscriptionData;
import com.amorabot.inscripted.components.Items.modifiers.data.InscriptionData;
import com.amorabot.inscripted.components.renderers.ItemInterfaceRenderer;
import com.amorabot.inscripted.item.structure.ItemRarities;
import com.amorabot.inscripted.item.structure.Tiers;
import com.amorabot.inscripted.utils.Utils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.*;

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
    private Inscription implicit;

    @Setter
    protected String name;
    protected Material vanillaMaterial;
    protected boolean corrupted;
    private final List<Inscription> inscriptions = new ArrayList<>();

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
    //-------------------------------------------------------------------------
    public <subType extends Enum<subType> & ItemSubtype> void imprint(ItemStack item, subType subType){
    }
    public abstract ItemStack getItemForm();
    protected abstract void serializeContainers(Item itemData, ItemStack item);
    //-------------------------------------------------------------------------

    public void identify() {
        if (isIdentified()){
            return;
        }
        this.identified = true;
    }
    public void corrupt(){
        if (isCorrupted()){
            return;
        }
        this.corrupted = true;
    }
    protected void setTier(Tiers tier) {
        this.tier = tier;
    }
    protected abstract void mapBase();
    public List<Inscription> getInscriptionList(){
        return this.inscriptions;
    }
    public Set<InscriptionID> getInscriptions(){
        Set<InscriptionID> auxSet = new HashSet<>();
        for (Inscription mod : this.inscriptions){
            auxSet.add(mod.getInscription());
        }
        return auxSet;
    }
    public void addInscription(Inscription newMod) {
        getInscriptionList().add(newMod);
    }
    public double getStarRating() { //Voltar pra acesso protected, so pra uso interno
        return 0;
    }
    //-1, 0, 1 Return values (Fail, neutral, success)
    public int improveQuality(){
        if (getQuality() >=10){return -1;}
        setQuality(getQuality() + 1);
        return 0;
    }
}
