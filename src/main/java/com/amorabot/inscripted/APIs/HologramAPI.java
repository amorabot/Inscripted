package com.amorabot.inscripted.APIs;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Attack;
import com.amorabot.inscripted.utils.ColorUtils;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;

import static com.amorabot.inscripted.utils.Utils.getRandomOffset;

public class HologramAPI {

    private static void setupTextDisplayData(TextDisplay display, String displayText, byte baseOpacity){
        display.setText(displayText);
        display.setBillboard(Display.Billboard.CENTER);
        display.setAlignment(TextDisplay.TextAlignment.CENTER);
        display.setTextOpacity(baseOpacity);
    }

    public static TextDisplay createOffsetTextHologram(String displayText, Location location, double xOff, double yOff, double zOff){
        TextDisplay hologram = createOffsetHologram(location, xOff, yOff, zOff);
        setupTextDisplayData(
                hologram,
                ColorUtils.translateColorCodes(displayText),
                (byte) 255
                );
        return hologram;
    }
    private static TextDisplay createOffsetHologram(Location location, double xOff, double yOff, double zOff){
        Location hologramLocation = location.clone().add(xOff, yOff, zOff);
        return Inscripted.getPlugin().getWorld().spawn(hologramLocation, TextDisplay.class);
    }
    public static TextDisplay createDamageHologramAt(Location location, int[] damageArray){
        TextDisplay damageHologram = createOffsetHologram(location, getRandomOffset(), 2.5 + getRandomOffset(), getRandomOffset());
        setupTextDisplayData(
                damageHologram,
                Attack.getDamageString(damageArray),
                (byte) 255
        );
        return damageHologram;
    }

    public static TextDisplay createDodgeIndicatorAt(Location location){
        TextDisplay dodgeHologram = createOffsetHologram(location, getRandomOffset(), 2.5 + getRandomOffset(), getRandomOffset());
        setupTextDisplayData(
                dodgeHologram,
                "\uD83C\uDF0ADodge\uD83C\uDF0A",
                (byte) 255
        );
        return dodgeHologram;
    }
}
