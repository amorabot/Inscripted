package com.amorabot.rpgelements.components.Items.Weapon;

import com.amorabot.rpgelements.components.Items.Abstract.Item;
import com.amorabot.rpgelements.components.Items.Abstract.Renderer;
import com.amorabot.rpgelements.components.Items.DataStructures.Modifier;
import com.amorabot.rpgelements.utils.CraftingUtils;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.WeaponNames;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.WeaponTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.WeaponModifiers;
import com.amorabot.rpgelements.components.Items.DataStructures.GenericItemContainerDataType;
import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.*;

//!!!!!!!!!!!!!Kill counter -> upgrade after N kills?!!!!!!!!!!!!!!! glow + enchant (veiled)
//Similar ao sistema de potions, kills validas maiores que o ilvl do item, acumulam num contador interno
public class Weapon extends Item {

    private final String name;
    private Material material;
    private final WeaponTypes type;
    private boolean corrupted;

    private Map<DamageTypes, int[]> baseDmg = new HashMap<>();
    private List<Modifier<WeaponModifiers>> modifiers = new ArrayList<>();

    public Weapon(int ilvl, WeaponTypes type, ItemRarities rarity){
        super(ilvl, rarity);
        this.type = type;
        this.name = WeaponNames.valueOf(type.toString()).getRandomName();
        mapBase(type, ilvl);
    }
    public Weapon(int ilvl, ItemRarities rarity){ //Random generic weapon constructor
        super(ilvl, rarity);
        //Do the rest...
        WeaponTypes[] weapons = WeaponTypes.values();
        int weaponIndex = CraftingUtils.getRandomNumber(0, weapons.length-1);
        this.type = weapons[weaponIndex];
        this.name = WeaponNames.valueOf(type.toString()).getRandomName();
        mapBase(this.type, ilvl);
    }
    private void mapBase(WeaponTypes type, int ilvl){
        baseDmg.put(DamageTypes.PHYSICAL, type.mapDamage(ilvl));
        material = type.mapWeaponBase(ilvl);
    }
    //-------------------------------------------------------------------------
    public void addModifier(Modifier<WeaponModifiers> mod){
        this.modifiers.add(mod);
    }
    public void deleteModifier(){}
    public List<Modifier<WeaponModifiers>> getModifiers(){
        return this.modifiers;
    }
    //-------------------------------------------------------------------------
    public void updateBaseDamage(){
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
    //-------------------------------------------------------------------------
    @Override
    public void render(ItemStack item, Renderer itemRenderer) {
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        List<String> lore = new ArrayList<>();

        itemRenderer.renderMainStat(this, lore);
        itemRenderer.renderMods(this, lore);
        itemRenderer.renderDescription(this, lore, type);
        itemRenderer.renderTag(this, lore);

        itemRenderer.placeDivs(lore);

        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
    }
    @Override
    public ItemStack getItemForm(RPGElements plugin, Renderer itemRenderer) {
        ItemStack weaponItem = new ItemStack(this.material);
        render(weaponItem, itemRenderer);
        String displayName = "";
        switch (getRarity()){
            case COMMON -> displayName = "&f";
            case MAGIC -> displayName = "&9";
            case RARE -> displayName = "&e";
        }
        displayName += this.name;
        itemRenderer.setDisplayName(displayName, weaponItem);
        serializeContainers(plugin, this, weaponItem);
        return weaponItem;
    }
    @Override
    public void serializeContainers(RPGElements plugin, Item itemData, ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        if (!dataContainer.has(new NamespacedKey(plugin, "item-data"), new GenericItemContainerDataType<>(Weapon.class))){
            dataContainer.set(new NamespacedKey(plugin, "item-data"), new GenericItemContainerDataType<>(Weapon.class), this);
            Utils.log("Successful datacontainerSerialization");
        }
    }
}
