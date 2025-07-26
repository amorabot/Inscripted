package com.amorabot.inscripted.displays;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.utils.DelayedTask;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;

import java.util.function.Function;

@Getter
public abstract class InscriptedDisplay {

    final protected Display displayEntity;

    protected InscriptedDisplay(Display display, int duration, boolean fadeout){
        this.displayEntity = display;

        if (duration>0){
            scheduleDeleter(this,duration);
            if (fadeout){
//                final int fadeoutTime = (int)(duration*0.9);
                scheduleFadeout(this,duration,10);
            }
        }
    }

    public void teleportTo(Location loc){
        getDisplayEntity().teleport(loc);
    }
    public void setDirection(Vector dir){
        getDisplayEntity().getLocation().setDirection(dir);
    }
    public void lerpToKeyframe(Matrix4f transformationMatrix, int delay, int duration){
        getDisplayEntity().setTransformationMatrix(transformationMatrix);
        setLerpValues(delay,duration);
    }
    public void animateKeyframes(Matrix4f baseMatrix, int frameDuration, Function<Matrix4f,Matrix4f> animationFunction){
        Bukkit.getScheduler().runTaskTimer(Inscripted.getPlugin(), task -> {
            Display display = getDisplayEntity();
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
        getDisplayEntity().setInterpolationDelay(lerpDelay);
        getDisplayEntity().setInterpolationDuration(lerpDuration);
    }
    public void setTpLerp(int lerpDuration){
        getDisplayEntity().setTeleportDuration(lerpDuration);
    }
    public Transformation getTransformation(){
        return getDisplayEntity().getTransformation();
    }
    public void setTransformation(Transformation transformation){
        getDisplayEntity().setTransformation(transformation);
    }

    private static void scheduleDeleter(InscriptedDisplay display, int duration){
        new DelayedTask(new BukkitRunnable() {
            @Override
            public void run() {
                Display disp = display.getDisplayEntity();
                if (disp.isValid()){
                    disp.remove();
                }
            }
        },duration);
    }
    //TODO: forceFadeout(), save fadeout taskID for later cancelling(if already removed wont do anything, so its fine)
    private static void scheduleFadeout(InscriptedDisplay display, int totalDuration, int fadeoutDuration){
        new DelayedTask(new BukkitRunnable() {
            @Override
            public void run() {
                Display disp = display.getDisplayEntity();
                if (disp.isValid()){
                    display.scale(0);
                    display.setLerpValues(0,fadeoutDuration);
                }
            }
        },totalDuration-fadeoutDuration);
    }
}
