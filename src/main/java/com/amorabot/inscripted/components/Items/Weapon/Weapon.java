package com.amorabot.inscripted.components.Items.Weapon;

import com.amorabot.inscripted.Inscripted;
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
public class Weapon extends Item {
    private final WeaponTypes type;
    //Define stat requirement for weapons
    //Atk speed

    public Weapon(int ilvl, WeaponTypes type, ItemRarities rarity, boolean identified, boolean corrupted){
        super(ilvl, rarity, identified, corrupted, ItemTypes.WEAPON);
        this.type = type;
        setup();
    }
    public Weapon(int ilvl, ItemRarities rarity, boolean identified, boolean corrupted){ //Random generic weapon constructor
        super(ilvl, rarity, identified, corrupted, ItemTypes.WEAPON);
        //Do the rest...
        WeaponTypes[] weapons = WeaponTypes.values();
        int weaponIndex = CraftingUtils.getRandomNumber(0, weapons.length-1);
        this.type = weapons[weaponIndex];
        setup();
    }

    @Override
    protected void setup() {
        setTier(Tiers.mapItemLevel(getIlvl()));
        setImplicit(defineImplicit(getSubtype().toString()));
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

        int[] basePhys = type.mapDamage(getTier());
//TODO: FIX MOD ORDER
        for (Modifier mod : getModifierList()){
            ModifierIDs weaponModifier = mod.getModifierID();
            if (weaponModifier.equals(ModifierIDs.ADDED_PHYSICAL)){
                int[] addedLocalPhys = ModifierManager.getMappedFinalValueFor(mod);
                basePhys[0] = basePhys[0] + addedLocalPhys[0];
                basePhys[1] = basePhys[1] + addedLocalPhys[1];
            }
            if (weaponModifier.equals(ModifierIDs.ADDED_FIRE)){
                baseDmg.put(DamageTypes.FIRE, ModifierManager.getMappedFinalValueFor(mod));
                continue;
            }
            if (weaponModifier.equals(ModifierIDs.ADDED_ABYSSAL)){
                baseDmg.put(DamageTypes.ABYSSAL, ModifierManager.getMappedFinalValueFor(mod));
                continue;
            }
            if (weaponModifier.equals(ModifierIDs.ADDED_LIGHTNING)){
                baseDmg.put(DamageTypes.LIGHTNING, ModifierManager.getMappedFinalValueFor(mod));
                continue;
            }
            if (weaponModifier.equals(ModifierIDs.ADDED_COLD)){
                baseDmg.put(DamageTypes.COLD, ModifierManager.getMappedFinalValueFor(mod));
                continue;
            }
            if (weaponModifier.equals(ModifierIDs.PERCENT_PHYSICAL)){
                int percentPhys = ModifierManager.getMappedFinalValueFor(mod)[0];
                float phys1 = basePhys[0] * (1 + percentPhys/100F);
                float phys2 = basePhys[1] * (1 + percentPhys/100F);
                basePhys[0] = (int)phys1;
                basePhys[1] = (int)phys2;
            }
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

        serializeContainers(plugin, this, weaponItem);
        return weaponItem;
    }
    @Override //Remove plugin instance parameter
    public void serializeContainers(Inscripted plugin, Item itemData, ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        FunctionalItemAccessInterface.serializeItem(this, dataContainer);
        item.setItemMeta(itemMeta);
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
}
