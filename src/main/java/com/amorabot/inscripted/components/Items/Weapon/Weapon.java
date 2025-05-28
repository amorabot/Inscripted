package com.amorabot.inscripted.components.Items.Weapon;

import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.*;
import com.amorabot.inscripted.item.structure.ItemRarities;
import com.amorabot.inscripted.item.structure.Weapon.RangeCategory;
import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.item.structure.Weapon.WeaponTypes;
import com.amorabot.inscripted.utils.CraftingUtils;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Weapon extends Item {
    private final WeaponTypes type;
    private final int[] baseDamage;
    @Getter
    private final WeaponAttackSpeeds atkSpeed;
    @Getter
    private final RangeCategory range;
    private final int percentDamageVariance;

    public Weapon(int ilvl, WeaponTypes type, ItemRarities rarity, boolean identified, boolean corrupted){
        super(ilvl, rarity, identified, corrupted, ItemTypes.WEAPON);
        this.type = type;
        setup();
        baseDamage = type.mapBaseDamage(getTier());
        this.percentDamageVariance = CraftingUtils.getRandomNumber(-WeaponTypes.weaponDamageVariance, WeaponTypes.weaponDamageVariance);
        this.atkSpeed = getSubtype().getBaseAttackSpeed();
        this.range = getSubtype().getRange();
    }
    public Weapon(int ilvl, ItemRarities rarity, boolean identified, boolean corrupted){ //Random generic weapon constructor
        super(ilvl, rarity, identified, corrupted, ItemTypes.WEAPON);
        //Do the rest...
        WeaponTypes[] weapons = WeaponTypes.values();
        int weaponIndex = CraftingUtils.getRandomNumber(0, weapons.length-1);
        this.type = weapons[weaponIndex];
        setup();
        baseDamage = type.mapBaseDamage(getTier());
        this.percentDamageVariance = CraftingUtils.getRandomNumber(-WeaponTypes.weaponDamageVariance, WeaponTypes.weaponDamageVariance);
        this.atkSpeed = getSubtype().getBaseAttackSpeed();
        this.range = getSubtype().getRange();
    }

    @Override
    protected void setup() {
//        setTier(Tiers.mapItemLevel(getIlvl()));
//        setImplicit(Archetypes.mapImplicitFor(getSubtype(), getTier(), isCorrupted()));
        this.name = getSubtype().getTierName(getTier());
        mapBase();
    }

    @Override
    protected void mapBase(){
        vanillaMaterial = type.mapWeaponBase();
    }

    //-------------------------------------------------------------------------
    public Map<DamageTypes, int[]> getLocalDamage(){ //Once a weapon is created, the damage map needs to be updated to contain any possible new damages
        return null;
    }
    public WeaponTypes getSubtype() {
        return type;
    }
    //-------------------------------------------------------------------------
    @Override
    public ItemStack getItemForm() {
        return null;
//        ItemStack weaponItem = new ItemStack(this.vanillaMaterial);
//        imprint(weaponItem,type);
//
//        serializeContainers(this, weaponItem);
//
//        setWeaponModel(weaponItem);
//        return weaponItem;
    }
//    @Override
//    public void serializeContainers(Item itemData, ItemStack item) {
////        FunctionalItemAccessInterface.serializeItem(item, this);
//    }

//    private void setWeaponModel(ItemStack item){
//        int modelID = getSubtype().mapWeaponTierModel(getTier());
//        ItemMeta itemMeta = item.getItemMeta();
//        itemMeta.setCustomModelData(modelID);
//        item.setItemMeta(itemMeta);
//    }
}
