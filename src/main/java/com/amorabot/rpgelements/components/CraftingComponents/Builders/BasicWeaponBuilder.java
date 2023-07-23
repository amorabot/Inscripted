package com.amorabot.rpgelements.components.CraftingComponents.Builders;

import com.amorabot.rpgelements.CustomDataTypes.RPGElementsContainerDataType;
import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.CraftingComponents.Items.BaseItem;
import com.amorabot.rpgelements.components.CraftingComponents.Items.Weapon;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class BasicWeaponBuilder{

    private final RPGElements plugin;

    private final BaseItem baseItemData;
    private final Weapon weaponData;
    private WeaponAssembler assembler;
    private final WeaponRenderer renderer;

    public BasicWeaponBuilder(RPGElements plugin, BaseItem baseItemData, WeaponAssembler assembler, WeaponRenderer renderer){
        this.plugin = plugin;

        this.baseItemData = baseItemData;
        this.assembler = assembler;
        this.renderer = renderer;
        this.weaponData = assembler.assemble();
    }

    public ItemStack build(){
        ItemStack finalItem = getItemForm();
        serializeContainers(finalItem, baseItemData, weaponData);
        finalItem.setItemMeta(finalItem.getItemMeta());
        renderer.render(baseItemData, weaponData, finalItem);
        return finalItem;
    }
    public ItemStack getItemForm() {
        return new ItemStack(weaponData.getItemMaterial());
    }
    public void serializeContainers(ItemStack item, BaseItem itemData, Weapon weaponData) {
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer itemContainer = itemMeta.getPersistentDataContainer();
        //Item information container
        RPGElementsContainerDataType<BaseItem> itemDataContainer = new RPGElementsContainerDataType<>(BaseItem.class);
        itemContainer.set(new NamespacedKey(plugin, "data"), itemDataContainer, itemData);
        //Weapon information container
        RPGElementsContainerDataType<Weapon> weaponDataContainer = new RPGElementsContainerDataType<>(Weapon.class);
        itemContainer.set(new NamespacedKey(plugin, "stats"), weaponDataContainer, weaponData);
        item.setItemMeta(itemMeta);
        Utils.log("builder serialization complete");
        Utils.log(String.valueOf(item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "data"), new RPGElementsContainerDataType<>(BaseItem.class))));
    }
}
