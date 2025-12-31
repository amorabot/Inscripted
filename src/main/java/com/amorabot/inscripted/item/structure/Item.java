package com.amorabot.inscripted.item.structure;

import com.amorabot.inscripted.item.inscription.Inscription;
import com.amorabot.inscripted.player.Archetypes;
import com.amorabot.inscripted.item.inscription.ProceduralInscription;
import com.amorabot.inscripted.item.inscription.definition.InscriptionIDs;
import com.amorabot.inscripted.item.render.ItemVisitor;
import com.amorabot.inscripted.item.structure.io.InscriptedItem;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

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
    private ProceduralInscription implicit;
    private List<Inscription> inscriptions = new ArrayList<>();

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

    public List<ProceduralInscription> getProceduralInscriptions(){
        List<ProceduralInscription> proceduralSet = new ArrayList<>();
        getInscriptions().forEach(
                inscription -> {
                    if (inscription instanceof ProceduralInscription proceduralInscription){
                        proceduralSet.add(proceduralInscription);
                    }
                }
        );
        return proceduralSet;
    }

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
    public double getStarRating() {
        double percentileSum = 0;
        for (Inscription inscription : getInscriptions()){
            if (inscription.isEffect() || inscription.isKeystone()){
                percentileSum++;
                continue;
            }
            double inscBP = inscription.getBasePercentile();
            percentileSum += inscBP;
        }
        if (!getInscriptions().isEmpty()){
            final int inscriptions = getInscriptions().size();
            return percentileSum / inscriptions;
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
