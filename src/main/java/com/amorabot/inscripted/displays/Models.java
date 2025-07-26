package com.amorabot.inscripted.displays;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;

public class Models {

    public static InscriptedDisplay[] instantiateWarBanner(Vector pos, World playerWorld, int duration){
        double handleScale = 3;
        DisplayItem bannerHandle = new DisplayItem(pos,playerWorld, Material.STICK,duration,true);
        bannerHandle.scale(handleScale);
        Transformation handleTransform = bannerHandle.getDisplayEntity().getTransformation();
        handleTransform.getLeftRotation().rotateZ((float) Math.toRadians(-60));
        handleTransform.getTranslation().add(0,1.2f,0);
        bannerHandle.setTransformation(handleTransform);
        bannerHandle.setLerpValues(0,3);

        DisplayItem handleCrossing = new DisplayItem(pos,playerWorld, Material.STICK,duration,true);
        handleCrossing.scale(handleScale/2);
        Transformation handleCrossTransform = handleCrossing.getDisplayEntity().getTransformation();
        handleCrossTransform.getLeftRotation().rotateZ((float) Math.toRadians(30));
        handleCrossTransform.getTranslation().add(0.2f,2.4f,-0.1f);
        handleCrossing.setTransformation(handleCrossTransform);
        handleCrossing.setLerpValues(0,3);

        DisplayItem banner = new DisplayItem(pos,playerWorld, Material.RED_BANNER,duration,true);
        banner.scale(1.1);
        banner.setLerpValues(0,5);
        Transformation bannerTransform = banner.getDisplayEntity().getTransformation();
        bannerTransform.getLeftRotation().rotateZ((float) Math.toRadians(-15));
        bannerTransform.getTranslation().add(-0.15f,1.1f,-0.1f);
        banner.setTransformation(bannerTransform);
        banner.setLerpValues(0,3);




        return new InscriptedDisplay[]{
                bannerHandle,handleCrossing,banner
        };
    }
}
