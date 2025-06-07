package com.amorabot.inscripted.item.structure.Armor;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.player.Archetypes;
import com.amorabot.inscripted.item.generation.InscriptionGenerator;
import com.amorabot.inscripted.item.inscription.definition.Stats;
import com.amorabot.inscripted.item.render.ItemRenderer;
import com.amorabot.inscripted.item.render.ItemVisitor;
import com.amorabot.inscripted.item.structure.EquipmentSlots;
import com.amorabot.inscripted.item.structure.Item;
import com.amorabot.inscripted.item.structure.ItemRarities;
import com.amorabot.inscripted.item.structure.ItemSubtype;
import com.amorabot.inscripted.item.structure.io.InscriptedItem;
import com.amorabot.inscripted.item.structure.io.ItemSerializer;
import com.amorabot.inscripted.math.MathUtils;
import com.amorabot.inscripted.player.profile.parsing.StatPool;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Armor extends Item {

    public static final NamespacedKey DATA_CONTAINER_KEY = new NamespacedKey(Inscripted.getPlugin(),"RMR_DATA");

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
        InscriptionGenerator.generateInscriptionSetFor(this);
    }

    public int getHealth(){
        return (int) ( baseHealth * ( 1 + ( (float) variance/100 ) ) );
    }
    private int getRandomHealthVariance(){
        return MathUtils.getRandomNumber(-ArmorTypes.BASE_VARIANCE, ArmorTypes.BASE_VARIANCE);
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
        InscriptedItem.tag(armorItem);

        ArmorMeta armorMeta = (ArmorMeta) armorItem.getItemMeta();
        assert armorMeta != null;
        armorMeta.setTrim(defineArmorTrim());
        armorMeta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
        armorItem.setItemMeta(armorMeta);

        ItemRenderer.imprintLore(armorItem,this,ItemRenderer.render(this),isIdentified());

        serializeDataContainerInto(armorItem);
        return armorItem;
    }
    @Override
    public NamespacedKey getKey() {
        return DATA_CONTAINER_KEY;
    }

    @Override
    public void serializeDataContainerInto(ItemStack itemStack) {
        ItemSerializer serializer = new ItemSerializer();
        serializer.visitArmor(itemStack,this);
    }

    @Override
    public Map<Stats, int[]> getLocalStats() {
        Map<Stats, int[]> defStats = new HashMap<>();
        LocalDefence localDefences = getDefences();
        localDefences.getArmorDefences().forEach(
                (defence, value) -> defStats.put(defence.getStat(),new int[]{value})
        );
        return defStats;
    }
    @Override
    public StatPool compile() {
        Map<Stats, int[]> localStats = getLocalStats();
        Set<Integer> blockedStats = LocalDefence.getLocallyCompiledStatIDs();
        return StatPool.getItemStats(getImplicit(),getInscriptions(),localStats, blockedStats);
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
