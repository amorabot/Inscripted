package com.amorabot.rpgelements.components.Items.Weapon;

import com.amorabot.rpgelements.events.FunctionalItemAccessInterface;
import com.amorabot.rpgelements.components.Items.Abstract.Item;
import com.amorabot.rpgelements.components.Items.Abstract.ItemRenderer;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.*;
import com.amorabot.rpgelements.components.Items.DataStructures.Modifier;
import com.amorabot.rpgelements.components.Items.Interfaces.ItemModifier;
import com.amorabot.rpgelements.components.Items.UnidentifiedRenderer;
import com.amorabot.rpgelements.utils.CraftingUtils;
import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.*;

//!!!!!!!!!!!!!Kill counter -> upgrade after N kills?!!!!!!!!!!!!!!! glow + enchant (veiled)
//Similar ao sistema de potions, kills validas maiores que o ilvl do item, acumulam num contador interno

//TODO: Set centralized serializers and de-serializers for all functional items in the plugin (weapons, armors, pouches, runes, ...) and for basic nbt tags for containers
//TODO: Use multiple event priorities to order eventlisteners listening to the same event
public class Weapon extends Item {
    private final WeaponTypes type;
    private Map<DamageTypes, int[]> baseDmg = new HashMap<>();
    private List<Modifier<WeaponModifiers>> modifiers = new ArrayList<>();
    //Define stat require for weapons

    public Weapon(int ilvl, WeaponTypes type, ItemRarities rarity, boolean identified, boolean corrupted){
        super(ilvl, rarity, identified, corrupted);
        mapTier();
        this.type = type;
        setImplicit(defineImplicit(getType().toString()));
        this.name = type.getRandomName(getTier());
        mapBase();
    }
    public Weapon(int ilvl, ItemRarities rarity, boolean identified, boolean corrupted){ //Random generic weapon constructor
        super(ilvl, rarity, identified, corrupted);
        mapTier();
        //Do the rest...
        WeaponTypes[] weapons = WeaponTypes.values();
        int weaponIndex = CraftingUtils.getRandomNumber(0, weapons.length-1);
        this.type = weapons[weaponIndex];
        setImplicit(defineImplicit(getType().toString()));
        this.name = getType().getRandomName(getTier());
        mapBase();
    }
    @Override
    protected void mapBase(){
        baseDmg.put(DamageTypes.PHYSICAL, type.mapDamage(getIlvl()));
        vanillaMaterial = type.mapWeaponBase(getTier());
    }
    //-------------------------------------------------------------------------
    @Override
    public <ModifierType extends Enum<ModifierType> & ItemModifier> void addModifier(Modifier<ModifierType> mod) {
        if (mod.getModifier() instanceof WeaponModifiers){
            this.modifiers.add(mod.castTo(WeaponModifiers.class));
        }
    }
    public void deleteModifier(){}
    public List<Modifier<WeaponModifiers>> getModifiers(){
        return this.modifiers;
    }

    //-------------------------------------------------------------------------
    public void updateBaseDamageFromModifiers(){ //Once a weapon is created, the damage map needs to be updated to contain any possible new damages
        for (Modifier<WeaponModifiers> mod : modifiers){
            WeaponModifiers weaponModifier = mod.getModifier();

            if (weaponModifier == (WeaponModifiers.ADDED_PHYSICAL)){
                int[] physDmg = baseDmg.get(DamageTypes.PHYSICAL);
                physDmg[0] = physDmg[0] + mod.getValue()[0];
                physDmg[1] = physDmg[1] + mod.getValue()[0];
                baseDmg.put(DamageTypes.PHYSICAL, physDmg);
            }
            if (weaponModifier == (WeaponModifiers.ADDED_FIRE)){
                baseDmg.put(DamageTypes.FIRE, mod.getValue());
            }
            if (weaponModifier == (WeaponModifiers.ADDED_ABYSSAL)){
                baseDmg.put(DamageTypes.ABYSSAL, mod.getValue());
            }
            if (weaponModifier == (WeaponModifiers.ADDED_LIGHTNING)){
                baseDmg.put(DamageTypes.LIGHTNING, mod.getValue());
            }
            if (weaponModifier == (WeaponModifiers.ADDED_COLD)){
                baseDmg.put(DamageTypes.COLD, mod.getValue());
            }
            if (weaponModifier == (WeaponModifiers.PERCENT_PHYSICAL)){
                int percentPhys = mod.getValue()[0];
                int[] physDmg = baseDmg.get(DamageTypes.PHYSICAL);
                float phys1 = physDmg[0] * (1 + (float) percentPhys/100);
                float phys2 = physDmg[1] * (1 + (float) percentPhys/100);
                physDmg[0] = (int)phys1;
                physDmg[1] = (int)phys2;
                baseDmg.put(DamageTypes.PHYSICAL, physDmg);
            }
        }
    }
    public Map<DamageTypes, int[]> getBaseDamage(){
        return this.baseDmg;
    }
    public WeaponTypes getType() {
        return type;
    }
    //-------------------------------------------------------------------------
    @Override
    public void imprint(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        List<String> lore = new ArrayList<>();
        ItemRenderer currentRenderer = getRenderer();

        currentRenderer.renderMainStat(this, lore);
        currentRenderer.renderMods(this, lore);
        currentRenderer.renderDescription(this, lore, type);
        currentRenderer.renderTag(this, lore);

        currentRenderer.placeDivs(lore);

        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);

        String displayName = "";
        switch (getRarity()){
            case COMMON -> displayName = "&f";
            case MAGIC -> displayName = "&9";
            case RARE -> displayName = "&e";
        }
        if (isIdentified()){
            displayName += getName();
        } else {
            displayName += "Unidentified " + this.type.toString().toLowerCase();
        }
        currentRenderer.setDisplayName(displayName, item);
    }
    @Override
    public ItemStack getItemForm(RPGElements plugin) {
        ItemStack weaponItem = new ItemStack(this.vanillaMaterial);
        imprint(weaponItem);

        serializeContainers(plugin, this, weaponItem);
        return weaponItem;
    }
    @Override
    public void serializeContainers(RPGElements plugin, Item itemData, ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        FunctionalItemAccessInterface.serializeWeapon(this, dataContainer);
//        dataContainer.set(new NamespacedKey(plugin, "item-data"), new GenericItemContainerDataType<>(Weapon.class), this);
        item.setItemMeta(itemMeta);
    }

    @Override
    public int getStarRating() { //Voltar pra acesso protected, so pra uso interno
        float percentileSum = 0;
        for (Modifier<WeaponModifiers> mod : modifiers){
            percentileSum += mod.getBasePercentile();
        }
        if (modifiers.size() > 0){
            return (int) (percentileSum/modifiers.size());
        }
        return 0;
    }
    public ItemRenderer getRenderer(){
        switch (this.renderer){
            case UNIDENTIFIED -> {
                return new UnidentifiedRenderer();
            }
            case BASIC -> {
                return new BasicWeaponRenderer();
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
