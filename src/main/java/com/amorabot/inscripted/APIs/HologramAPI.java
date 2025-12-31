package com.amorabot.inscripted.APIs;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.player.profile.component.AttackData;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Vector;

public class HologramAPI {

    public static TextDisplay createAlignedHologram(Component displayText, Location location, double dist, boolean left){
        double distDirection = dist;
        if (left){
            distDirection *= -1;
        }

        Vector locationYaw = location.clone().getDirection().setY(0).normalize(); //Horizontal direction
        final Vector dirVec = locationYaw.getCrossProduct(new Vector(0, 1, 0)).normalize().multiply(distDirection).add(new Vector(0, 3, 0));
        Location holoLocation = location.clone().add(dirVec);

        return Inscripted.getPlugin().getWorld().spawn(holoLocation, TextDisplay.class, textDisplay -> {
            textDisplay.text(displayText);
            textDisplay.setBillboard(Display.Billboard.CENTER);
            textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);
            textDisplay.setTextOpacity((byte) 240);

            //Move to Depleter routine
            textDisplay.setInterpolationDelay(1);
            textDisplay.setInterpolationDuration(14);

            textDisplay.setPersistent(false);
        });
    }
    public static TextDisplay createRegenHologramAt(Location location, String regenString){
        return createAlignedHologram(Component.text(regenString), location, 0.65D, true);
    }
    public static TextDisplay createDamageHologramAt(Location location, int[] damageArray){
        return createAlignedHologram(AttackData.getDamageAsString(damageArray), location, 1D, false);
    }

    public static TextDisplay createDodgeIndicatorAt(Location location){
        return createAlignedHologram(Component.text("\uD83C\uDF0ADodge\uD83C\uDF0A"), location, 1.5F, true);
    }
}
