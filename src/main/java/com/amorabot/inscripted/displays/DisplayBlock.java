package com.amorabot.inscripted.displays;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Vector;


@Getter
public class DisplayBlock extends InscriptedDisplay {

    public DisplayBlock(Vector pos, World world, Material blockMaterial, int duration, boolean fadeout){
        super(world.spawn(pos.toLocation(world), BlockDisplay.class),duration,fadeout);
        BlockDisplay blockDisplay = (BlockDisplay) getDisplayEntity();
        if (!blockMaterial.isBlock()){
            blockDisplay.remove();
            return;
        }
        blockDisplay.setBlock(blockMaterial.createBlockData());
        blockDisplay.setPersistent(false);
    }
    public static DisplayBlock getBlockDisplayAt(Vector pos, World world, double scale, Material defaultMaterial){
        Material blockMaterial = getBlockMaterialUnder(pos.toLocation(world),defaultMaterial);
        DisplayBlock display = new DisplayBlock(pos,world,blockMaterial,40, true);
        display.scale(scale);
        display.setLerpValues(0,5);
        return display;
    }
    public static Material getBlockMaterialUnder(Location originalLocation, Material defaultMaterial){
        Block centerBlock = originalLocation.subtract(0,1,0).getBlock();
        Material centerMaterial = centerBlock.getBlockData().getMaterial();
        if (centerMaterial.isAir() || !centerMaterial.isBlock()){
            return defaultMaterial;
        }
        return centerMaterial;
    }

}
