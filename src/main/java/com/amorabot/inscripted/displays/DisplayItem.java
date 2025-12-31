package com.amorabot.inscripted.displays;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class DisplayItem extends InscriptedDisplay{
    public DisplayItem(Vector pos, World world, Material itemMaterial, int duration, boolean fadeout) {
        super(world.spawn(pos.toLocation(world), ItemDisplay.class), duration, fadeout);
        ItemDisplay itemDisplay = (ItemDisplay) getDisplayEntity();
        ItemStack itemToDisplay = new ItemStack(itemMaterial);
        itemDisplay.setItemStack(itemToDisplay);
    }
}
