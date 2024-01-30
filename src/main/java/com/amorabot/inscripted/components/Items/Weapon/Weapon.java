package com.amorabot.inscripted.components.Items.Weapon;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Abstract.ItemCategory;
import com.amorabot.inscripted.components.Items.DataStructures.ModifierIDs;
import com.amorabot.inscripted.components.Items.DataStructures.Modifier;
import com.amorabot.inscripted.components.Items.DataStructures.ModifierManager;
import com.amorabot.inscripted.events.FunctionalItemAccessInterface;
import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Abstract.ItemRenderer;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.*;
import com.amorabot.inscripted.components.Items.UnidentifiedRenderer;
import com.amorabot.inscripted.utils.CraftingUtils;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.*;

//!!!!!!!!!!!!!Kill counter -> upgrade after N kills?!!!!!!!!!!!!!!! glow + enchant (veiled)
//Similar ao sistema de potions, kills validas maiores que o ilvl do item, acumulam num contador interno //Milestones -> mobKill custom event!

//TODO: Use multiple event priorities to order eventlisteners listening to the same event
public class Weapon extends Item implements ItemCategory {
    private final WeaponTypes type;
    private final int[] baseDamage;
    private final int percentDamageVariance;
    //Define stat requirement for weapons
    //Atk speed

    public Weapon(int ilvl, WeaponTypes type, ItemRarities rarity, boolean identified, boolean corrupted){
        super(ilvl, rarity, identified, corrupted, ItemTypes.WEAPON);
        this.type = type;
        setup();
        baseDamage = type.mapBaseDamage(getTier());
        this.percentDamageVariance = CraftingUtils.getRandomNumber(-WeaponTypes.weaponDamageVariance, WeaponTypes.weaponDamageVariance);
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
    }

    @Override
    protected void setup() {
        setTier(Tiers.mapItemLevel(getIlvl()));
        setImplicit(Implicits.getImplicitFor(getSubtype(), isCorrupted()));
        this.name = getSubtype().getTierName(getTier());
        mapBase();
    }

    @Override
    protected void mapBase(){
        //TODO: Add atk speed mapping, min base stats required...
        vanillaMaterial = type.mapWeaponBase(getTier());
    }

    //-------------------------------------------------------------------------
    public Map<DamageTypes, int[]> getLocalDamage(){ //Once a weapon is created, the damage map needs to be updated to contain any possible new damages
        Map<DamageTypes, int[]> baseDmg = new HashMap<>();

        int[] basePhys = getBaseDamage();
        int qualityIncrease = 5*getQuality();

        boolean hasLocalIncPhys = false;
        Modifier incPhys = null;

        for (Modifier mod : getModifierList()){
            //Local mod mapping
            ModifierIDs weaponModifier = mod.getModifierID();
            if (!weaponModifier.isLocal()){continue;}
            if (weaponModifier.equals(ModifierIDs.PERCENT_PHYSICAL)){
                hasLocalIncPhys = true;
                incPhys = mod;
            }

            switch (weaponModifier){
                case ADDED_PHYSICAL -> {
                    int[] addedLocalPhys = ModifierManager.getMappedFinalValueFor(mod);
                    basePhys[0] = basePhys[0] + addedLocalPhys[0];
                    basePhys[1] = basePhys[1] + addedLocalPhys[1];
                }
                case ADDED_FIRE -> baseDmg.put(DamageTypes.FIRE, ModifierManager.getMappedFinalValueFor(mod));
                case ADDED_LIGHTNING -> baseDmg.put(DamageTypes.LIGHTNING, ModifierManager.getMappedFinalValueFor(mod));
                case ADDED_COLD -> baseDmg.put(DamageTypes.COLD, ModifierManager.getMappedFinalValueFor(mod));
                case ADDED_ABYSSAL -> baseDmg.put(DamageTypes.ABYSSAL, ModifierManager.getMappedFinalValueFor(mod));
            }
        }
        if (hasLocalIncPhys){
            int percentPhys = ModifierManager.getMappedFinalValueFor(incPhys)[0];
            float phys1 = Utils.applyPercentageTo(basePhys[0], percentPhys + qualityIncrease);
            float phys2 = Utils.applyPercentageTo(basePhys[1], percentPhys + qualityIncrease);
            basePhys[0] = (int)phys1;
            basePhys[1] = (int)phys2;
        } else {
            float phys1 = Utils.applyPercentageTo(basePhys[0], qualityIncrease);
            float phys2 = Utils.applyPercentageTo(basePhys[1], qualityIncrease);
            basePhys[0] = (int)phys1;
            basePhys[1] = (int)phys2;
        }

        baseDmg.put(DamageTypes.PHYSICAL, basePhys);

        return baseDmg;
    }
    public WeaponTypes getSubtype() {
        return type;
    }
    //-------------------------------------------------------------------------
    @Override
    public ItemStack getItemForm(Inscripted plugin) {
        ItemStack weaponItem = new ItemStack(this.vanillaMaterial);
        imprint(weaponItem,type);

        serializeContainers(this, weaponItem);
        return weaponItem;
    }
    @Override
    public void serializeContainers(Item itemData, ItemStack item) {
        FunctionalItemAccessInterface.serializeItem(item, this);
    }

    public ItemRenderer getRenderer(){
        switch (this.renderer){
            case UNIDENTIFIED -> {
                return new UnidentifiedRenderer();
            }
            case BASIC -> {
                return new WeaponRenderer();
            }
            case CORRUPTED -> {
                Utils.log("No corruptedWeaponRenderer");
                return null;
            }
            default -> {
                Utils.log("No renderer defined/No ItemRenderer class implemented for this renderer constant");
                return null;
            }
        }
    }

    public int[] getBaseDamage() {
        int[] basePhys = baseDamage;
        int[] finalDmg = new int[2];
        for (int i = 0; i< basePhys.length; i++){
            finalDmg[i] = (int) ((1+((float)percentDamageVariance/100))*basePhys[i]);
        }
        return finalDmg;
    }

    @Override
    public void applyQuality() {
        Utils.log("applying weapon quality");
    }

    @Override
    public void getCorruptedImplicit() {

    }
}
