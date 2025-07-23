package com.amorabot.inscripted.displays;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.utils.DelayedTask;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;

import java.util.function.Function;

@Getter
public class DisplayBlock {

    private final BlockDisplay block;

    public DisplayBlock(Vector pos, World world, Material blockMaterial, int duration, boolean fadeout){
        this.block = world.spawn(pos.toLocation(world), BlockDisplay.class);
        if (!blockMaterial.isBlock()){
            getBlock().remove();
            return;
        }
        block.setBlock(blockMaterial.createBlockData());
        block.setPersistent(false);

        if (duration>0){
            scheduleDeleter(this,duration);
            if (fadeout){
                final int fadeoutTime = (int)(duration*0.9);
                scheduleFadeout(this,duration,fadeoutTime);
            }
        }
    }

    public void teleportTo(Location loc){
        getBlock().teleport(loc);
//        setTpLerp(10);
    }
    public void lerpToKeyframe(Matrix4f transformationMatrix, int delay, int duration){
        getBlock().setTransformationMatrix(transformationMatrix);
        setLerpValues(delay,duration);
    }
    public void animateKeyframes(Matrix4f baseMatrix, int frameDuration, Function<Matrix4f,Matrix4f> animationFunction){
        Bukkit.getScheduler().runTaskTimer(Inscripted.getPlugin(), task -> {
            BlockDisplay display = getBlock();
            if (!display.isValid()) { // display was removed from the world, abort task
                task.cancel();
                return;
            }
            lerpToKeyframe(animationFunction.apply(baseMatrix),0,frameDuration);
        }, 1, frameDuration);
    }

    public void scale(double s){
        Transformation blockTransformation = getTransformation();
        blockTransformation.getScale().set(s);
        setTransformation(blockTransformation);
    }

    public void setLerpValues(int lerpDelay, int lerpDuration){
        getBlock().setInterpolationDelay(lerpDelay);
        getBlock().setInterpolationDuration(lerpDuration);
    }
    public void setTpLerp(int lerpDuration){
        getBlock().setTeleportDuration(lerpDuration);
    }
    public Transformation getTransformation(){
        return getBlock().getTransformation();
    }
    public void setTransformation(Transformation transformation){
        getBlock().setTransformation(transformation);
    }

    private static void scheduleDeleter(DisplayBlock block, int duration){
        new DelayedTask(new BukkitRunnable() {
            @Override
            public void run() {
                Display display = block.getBlock();
                if (display.isValid()){
                    display.remove();
                }
            }
        },duration);
    }
    //TODO: forceFadeout(), save fadeout taskID for later cancelling(if already removed wont do anything, so its fine)
    private static void scheduleFadeout(DisplayBlock block, int totalDuration, int fadeoutDelay){
        new DelayedTask(new BukkitRunnable() {
            @Override
            public void run() {
                Display display = block.getBlock();
                if (display.isValid()){
                    block.scale(0);
                    block.setLerpValues(0,totalDuration-fadeoutDelay);
                }
            }
        },fadeoutDelay);
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
