package com.amorabot.inscripted.item.structure;

import com.amorabot.inscripted.components.Player.archetypes.Archetypes;
import com.amorabot.inscripted.item.inscription.Inscription;
import com.amorabot.inscripted.item.inscription.definition.InscriptionIDs;
import com.amorabot.inscripted.item.render.ItemVisitor;
import com.amorabot.inscripted.item.structure.serialization.InscriptedItem;
import com.amorabot.inscripted.utils.Utils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode
public abstract class Item implements Serializable, InscriptedItem {
    private final int ilvl;
    protected final EquipmentSlots slot;
    @Setter
    private boolean identified;
    @Setter
    private ItemRarities rarity;
    private final Tiers tier;
    @Setter
    private int quality; //Caps at +10

    @Setter
    protected String name;
    protected Material vanillaMaterial;
    protected boolean corrupted;

    @Setter
    private Inscription implicit;
    private final List<Inscription> inscriptions = new ArrayList<>();

    public Item(int ilvl, EquipmentSlots itemSlot){
        this.ilvl = ilvl;
        this.slot = itemSlot;
        this.identified = false;
        this.corrupted = false;
        this.tier = Tiers.mapItemLevel(ilvl);
    }
    public Item(int ilvl, ItemRarities rarity, boolean identified, boolean corrupted, EquipmentSlots itemSlot){
        this.identified = identified;
        this.ilvl = ilvl;
        this.rarity = rarity;
        this.corrupted = corrupted;
        this.slot = itemSlot;
        this.tier = Tiers.mapItemLevel(ilvl);
    }
    // The equivalent of accept() method on Visitor pattern
    public abstract List<Component> renderMainStat(ItemVisitor<List<Component>> visitor);

    public abstract ItemSubtype getGenericSubtype();
    protected abstract void setupInternalItemData();
    protected abstract void mapItemBase();

    public Archetypes getArchetype(){
        return getGenericSubtype().mapArchetype();
    }
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
    public Set<InscriptionIDs> getInscriptionSet(){
        Set<InscriptionIDs> auxSet = new HashSet<>();
        for (Inscription insc : this.inscriptions){
            auxSet.add(insc.getInscription());
        }
        return auxSet;
    }
    public double getStarRating() {
        double percentileSum = 0;
        int invalidMods = 0;
        for (Inscription inscription : getInscriptions()){
            if (inscription.isSpecial()){
                invalidMods++;
                continue;
            }
            double inscBP = inscription.getBasePercentile();
            percentileSum += inscBP;
        }
        if (!getInscriptions().isEmpty()){
            double percentileAvg = percentileSum/ (getInscriptions().size()-invalidMods);
            Utils.log("SR: " + percentileAvg);
            return percentileAvg;
        }
        return 0;
    }
    //-1, 0, 1 Return values (Fail, neutral, success)
    public int improveQuality(){
        if (getQuality() >=10){return -1;}
        setQuality(getQuality() + 1);
        return 0;
    }
}
