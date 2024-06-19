package com.amorabot.inscripted.APIs;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Attack;
import com.amorabot.inscripted.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;

import static com.amorabot.inscripted.utils.Utils.getRandomOffset;

public class HologramAPI {

//    private static void setupTextDisplayData(TextDisplay display, String displayText, byte baseOpacity){
//        display.setText(displayText);
//        display.setBillboard(Display.Billboard.CENTER);
//        display.setAlignment(TextDisplay.TextAlignment.CENTER);
//        display.setTextOpacity(baseOpacity);
//    }

//    public static TextDisplay createOffsetTextHologram(String displayText, Location location, double xOff, double yOff, double zOff){
////        TextDisplay hologram = createOffsetHologram(location, xOff, yOff, zOff);
//        TextDisplay hologram = createAlignedHologram("asdasdasd",location, 3F, true);
//        setupTextDisplayData(
//                hologram,
//                ColorUtils.translateColorCodes(displayText),
//                (byte) 255
//                );
//        return hologram;
//    }
    public static TextDisplay createAlignedHologram(String displayText, Location location, double dist, boolean left){
        double distDirection = dist;
        if (left){
            distDirection *= -1;
        }

        Vector locationYaw = location.clone().getDirection().setY(0).normalize(); //Horizontal direction
        final Vector dirVec = locationYaw.getCrossProduct(new Vector(0, 1, 0)).normalize().multiply(distDirection).add(new Vector(0, 3, 0));
        Location holoLocation = location.clone().add(dirVec);

        return Inscripted.getPlugin().getWorld().spawn(holoLocation, TextDisplay.class, textDisplay -> {
            textDisplay.setText(ColorUtils.translateColorCodes(displayText));
            textDisplay.setBillboard(Display.Billboard.CENTER);
            textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);
            textDisplay.setTextOpacity((byte) 240);

            textDisplay.setInterpolationDelay(1);
            textDisplay.setInterpolationDuration(14);

            textDisplay.setPersistent(false);
        });
    }
//    private static TextDisplay createOffsetHologram(Location location, double xOff, double yOff, double zOff){
//        Location hologramLocation = location.clone().add(xOff, yOff, zOff);
//        return Inscripted.getPlugin().getWorld().spawn(hologramLocation, TextDisplay.class);
//    }
    public static TextDisplay createRegenHologramAt(Location location, String regenString){
        return createAlignedHologram(regenString, location, 0.65D, true);
    }
    public static TextDisplay createDamageHologramAt(Location location, int[] damageArray){
        return createAlignedHologram(Attack.getDamageString(damageArray), location, 1D, false);
    }

    public static TextDisplay createDodgeIndicatorAt(Location location){
        return createAlignedHologram("\uD83C\uDF0ADodge\uD83C\uDF0A", location, 1.5F, true);
    }
}
