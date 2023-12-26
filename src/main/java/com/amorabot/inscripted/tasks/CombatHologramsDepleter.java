package com.amorabot.inscripted.tasks;

import com.amorabot.inscripted.APIs.HologramAPI;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.utils.ColorUtils;
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

    public void instantiateDamageHologram(int[] incomingDamage, Entity entity){
        TextDisplay dmgHologram = HologramAPI.createDamageHologramAt(entity.getLocation(), incomingDamage);
        damageIndicators.put(dmgHologram, persistTime);
    }

}
