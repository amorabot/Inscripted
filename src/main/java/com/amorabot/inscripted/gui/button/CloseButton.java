package com.amorabot.inscripted.gui.button;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class CloseButton extends Button{
    private static final ItemStack closeIcon;
    static {
        ItemStack closeItemStack = new ItemStack(Material.BARRIER);
        closeItemStack.editMeta(
                itemMeta -> {
                    itemMeta.displayName(Component.text("Close").color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
                }
        );
        closeIcon = closeItemStack;
    }

//    public CloseButton(int desiredSlot, ItemStack buttonIcon) {
//        super(desiredSlot, buttonIcon);
//    }
    public CloseButton(int desiredSlot){
        super(desiredSlot,closeIcon);
        setLeftClickFunction((player, gui) -> player.closeInventory());
    }
}
