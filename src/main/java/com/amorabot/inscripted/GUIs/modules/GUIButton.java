package com.amorabot.inscripted.GUIs.modules;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public abstract class GUIButton implements Button {

    private final ItemStack icon;
    private final int slot;

    public GUIButton(int desiredSlot, Material buttonIcon, int quantityDisplay, boolean enchanted, String buttonName, List<String> buttonDescription){
        this.slot = desiredSlot;

        ItemStack iconItem = new ItemStack(buttonIcon);
        iconItem.setAmount(quantityDisplay);
        ItemMeta itemMeta = iconItem.getItemMeta();
        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        if (enchanted){
            itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, false);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemMeta.setDisplayName(buttonName);
        itemMeta.setLore(buttonDescription);

        iconItem.setItemMeta(itemMeta);

        this.icon = iconItem;
    }
    public GUIButton(int desiredSlot, ItemStack iconItem){
        this.slot = desiredSlot;

        this.icon = iconItem;
    }

    public ItemStack getIcon() {
        return icon;
    }
    public int getSlot() {
        return slot;
    }
}
