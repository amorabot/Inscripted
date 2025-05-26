package com.amorabot.inscripted.item.structure.Armor;

import com.amorabot.inscripted.components.Player.archetypes.Archetypes;
import com.amorabot.inscripted.item.render.ItemRenderer;
import com.amorabot.inscripted.item.render.ItemVisitor;
import com.amorabot.inscripted.item.structure.EquipmentSlots;
import com.amorabot.inscripted.item.structure.Item;
import com.amorabot.inscripted.item.structure.ItemRarities;
import com.amorabot.inscripted.item.structure.ItemSubtype;
import com.amorabot.inscripted.utils.CraftingUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.List;

public class Armor extends Item {

    @Getter
    private final ArmorTypes armorType;
    private final int baseHealth;
    private final int variance;

    public Armor(int ilvl, ArmorTypes type, ItemRarities rarity, boolean identified, boolean corrupted, EquipmentSlots slot){
        super(ilvl,rarity,identified,corrupted,slot);
        this.armorType = type;
        // Some internal attributes can only be defined at this stage
        setupInternalItemData();

        this.baseHealth = type.getBaseHealthValue(getTier(),slot);
        this.variance = getRandomHealthVariance();
    }

    public int getHealth(){
        return (int) ( baseHealth * ( 1 + ( (float) variance/100 ) ) );
    }
    private int getRandomHealthVariance(){
        return CraftingUtils.getRandomNumber(-ArmorTypes.BASE_VARIANCE, ArmorTypes.BASE_VARIANCE);
    }
    public LocalDefence getDefences(){
        return new LocalDefence(this);
    }

    @Override
    public List<Component> renderMainStat(ItemVisitor<List<Component>> visitor) {
        return visitor.visitArmor(this);
    }
    @Override
    public ItemSubtype getGenericSubtype() {
        return armorType;
    }
    @Override
    protected void setupInternalItemData() {
        setName(getArmorType().getTierName(getTier()) + " " + getSlot().toString().toLowerCase());
        setImplicit(Archetypes.mapImplicitFor(getArmorType(), getTier(), isCorrupted()));
        mapItemBase();
    }
    @Override
    protected void mapItemBase() {
        this.vanillaMaterial = getArmorType().mapArmorBase(getTier(), getSlot());
    }

    @Override
    public ItemStack getItemForm() {
        ItemStack armorItem = new ItemStack(this.vanillaMaterial);

        ArmorMeta armorMeta = (ArmorMeta) armorItem.getItemMeta();
        assert armorMeta != null;
        armorMeta.setTrim(defineArmorTrim());
        armorMeta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
        armorItem.setItemMeta(armorMeta);

        ItemRenderer.imprintLore(armorItem,this,ItemRenderer.render(this),isIdentified());

//        serializeContainers(this, armorItem);
        return armorItem;
    }
    private ArmorTrim defineArmorTrim(){
        TrimPattern pattern;
        TrimMaterial material = getArmorType().getTrimMaterial();
        switch (getSlot()){
            case HELMET -> pattern = TrimPattern.HOST;
            case CHESTPLATE -> pattern = TrimPattern.SHAPER;
            case LEGGINGS -> pattern = TrimPattern.SILENCE;
            case BOOTS -> pattern = TrimPattern.HOST;
            default -> pattern = TrimPattern.EYE; //Signals error
        }
        return new ArmorTrim(material, pattern);
    }
}
