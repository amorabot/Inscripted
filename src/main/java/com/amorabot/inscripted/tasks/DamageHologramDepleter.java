package com.amorabot.inscripted.tasks;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.CraftingUtils;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.amorabot.inscripted.utils.Utils.getRandomOffset;

public class DamageHologramDepleter extends BukkitRunnable {

    private static final DamageHologramDepleter instance = new DamageHologramDepleter();

    private static final Map<TextDisplay, Integer> damageIndicators = new HashMap<>(); //Map de entity e duração do indicator
    private Set<TextDisplay> hologramsToRemove = new HashSet<>();
    private static final int persistTime = 20; //Ticks

    private DamageHologramDepleter(){
    }
    @Override
    public void run() {
        if (!damageIndicators.isEmpty()){
            for (TextDisplay dmgHolo : damageIndicators.keySet()){
                int ticksLeft = damageIndicators.get(dmgHolo);
                if (ticksLeft == 0){
                    dmgHolo.remove();
                    hologramsToRemove.add(dmgHolo);
                    continue;
                }
                int opactiyStep = 255 / persistTime;
                dmgHolo.setTextOpacity((byte) (dmgHolo.getTextOpacity()-opactiyStep));
                ticksLeft--;
                damageIndicators.put(dmgHolo, ticksLeft);
            }
            damageIndicators.keySet().removeAll(hologramsToRemove);
        }
    }

    public static DamageHologramDepleter getInstance(){
        return instance;
    }

    public void createDamageHologram(int[] incomingDamage, Entity entity){
        Location hologramLocation = entity.getLocation().clone().add(getRandomOffset(), 2.5 + getRandomOffset(), getRandomOffset());
        TextDisplay damageHologram = Inscripted.getPlugin().getWorld().spawn(hologramLocation, TextDisplay.class);
        damageHologram.setText(getDamageString(incomingDamage));
        damageHologram.setBillboard(Display.Billboard.CENTER);
        damageHologram.setAlignment(TextDisplay.TextAlignment.CENTER);
        damageHologram.setTextOpacity((byte) (255));
//        return damageHologram;
        damageIndicators.put(damageHologram, persistTime);
    }
    public static @NotNull String getDamageString(int[] damagesArray){
        StringBuilder dmgString = new StringBuilder();
        addDamageToString(dmgString, damagesArray[0], DamageTypes.PHYSICAL);
        addDamageToString(dmgString, damagesArray[1], DamageTypes.FIRE);
        addDamageToString(dmgString, damagesArray[2], DamageTypes.LIGHTNING);
        addDamageToString(dmgString, damagesArray[3], DamageTypes.COLD);
        addDamageToString(dmgString, damagesArray[4], DamageTypes.ABYSSAL);
        return dmgString.toString().trim();
    }

    private static void addDamageToString(StringBuilder builder, int damage, DamageTypes damageType){
        if (damage > 0){
            String damageIcon = damageType.getCharacter();
            String damageColor = damageType.getColor();
            builder.append(ColorUtils.translateColorCodes(damageColor + damage + damageIcon))
                    .append(" ");
        }
    }

}
