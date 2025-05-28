package com.amorabot.inscripted.item.structure.Weapon;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Player.archetypes.Archetypes;
import com.amorabot.inscripted.item.render.ItemRenderer;
import com.amorabot.inscripted.item.render.ItemVisitor;
import com.amorabot.inscripted.item.structure.EquipmentSlots;
import com.amorabot.inscripted.item.structure.Item;
import com.amorabot.inscripted.item.structure.ItemRarities;
import com.amorabot.inscripted.item.structure.ItemSubtype;
import com.amorabot.inscripted.item.structure.io.InscriptedItem;
import com.amorabot.inscripted.item.structure.io.ItemSerializer;
import com.amorabot.inscripted.utils.CraftingUtils;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class Weapon extends Item {

    public static final NamespacedKey DATA_CONTAINER_KEY = new NamespacedKey(Inscripted.getPlugin(),"WPN_DATA");

    @Getter
    private final WeaponTypes weaponType;
    private final int[] baseDamage;
    @Getter
    private final WeaponAttackSpeeds atkSpeed;
    @Getter
    private final RangeCategory range;
    private final int damageVariance;

    public Weapon(int ilvl, WeaponTypes type, ItemRarities rarity, boolean identified, boolean corrupted){
        super(ilvl, rarity,identified,corrupted, EquipmentSlots.WEAPON);
        this.weaponType = type;
        this.baseDamage = type.mapBaseDamage(getTier());

        setupInternalItemData();

        this.damageVariance = getRandomVariance();
        this.atkSpeed = getWeaponType().getBaseAttackSpeed();
        this.range = getWeaponType().getRange();
    }



    public LocalDamage getDamage(){
        return new LocalDamage(this);
    }
    public int[] getBaseDamage() {
        int[] basePhys = baseDamage.clone();
        return Arrays.stream(basePhys).map(currValue -> (int) ((1+((float)damageVariance/100))*currValue)).toArray();
    }
    public int getRandomVariance(){
        return CraftingUtils.getRandomNumber(-WeaponTypes.weaponDamageVariance, WeaponTypes.weaponDamageVariance);
    }

    @Override
    public List<Component> renderMainStat(ItemVisitor<List<Component>> visitor) {
        return visitor.visitWeapon(this);
    }
    @Override
    public ItemSubtype getGenericSubtype() {
        return weaponType;
    }
    @Override
    protected void setupInternalItemData() {
        this.name = getWeaponType().getTierName(getTier());
        setImplicit(Archetypes.mapImplicitFor(getWeaponType(), getTier(), isCorrupted()));
        mapItemBase();
    }
    @Override
    protected void mapItemBase() {
        this.vanillaMaterial = getWeaponType().mapWeaponBase();
    }

    @Override
    public ItemStack getItemForm() {
        ItemStack weaponItem = new ItemStack(this.vanillaMaterial);
        setWeaponModel(weaponItem);
        InscriptedItem.tag(weaponItem);

        ItemRenderer.imprintLore(weaponItem,this,ItemRenderer.render(this),isIdentified());

        serializeDataContainerInto(weaponItem);
        return weaponItem;
    }

    @Override
    public NamespacedKey getKey() {
        return DATA_CONTAINER_KEY;
    }

    @Override
    public void serializeDataContainerInto(ItemStack itemStack) {
        ItemSerializer serializer = new ItemSerializer();
        serializer.visitWeapon(itemStack,this);
    }

    private void setWeaponModel(ItemStack item){
        int modelID = getWeaponType().mapWeaponTierModel(getTier());
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setCustomModelData(modelID);
        item.setItemMeta(itemMeta);
    }
}
