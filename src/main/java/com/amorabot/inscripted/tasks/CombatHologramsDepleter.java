package com.amorabot.inscripted.tasks;

import com.amorabot.inscripted.APIs.HologramAPI;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static com.amorabot.inscripted.utils.Utils.getRandomOffset;

public class CombatHologramsDepleter extends BukkitRunnable {

    //Instance really needed?
    private static final CombatHologramsDepleter instance = new CombatHologramsDepleter();

    private static final Map<TextDisplay, Integer> damageIndicators = new HashMap<>(); //Map de entity e duração do indicator
    private static Set<TextDisplay> hologramsToRemove = new HashSet<>();
    private static final int persistTime = 20; //Ticks

    private CombatHologramsDepleter(){
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

    public static CombatHologramsDepleter getInstance(){
        return instance;
    }

    public void shutdown(){
        for (TextDisplay textDisplay : damageIndicators.keySet()){
            textDisplay.remove();
        }
        damageIndicators.clear();

        for (TextDisplay removableDisplay : hologramsToRemove){
            removableDisplay.remove();
        }
        damageIndicators.clear();
        if (!isCancelled()){
            cancel();
        }
    }

    public void instantiateDamageHologramAt(Location loc, int[] incomingDamage){
        TextDisplay dmgHologram = HologramAPI.createDamageHologramAt(loc, incomingDamage);
        damageIndicators.put(dmgHologram, persistTime);
    }

    public void instantiateDodgeHologramAt(Location loc){
        TextDisplay dodgeHolo = HologramAPI.createDodgeIndicatorAt(loc);
        damageIndicators.put(dodgeHolo, persistTime);
    }

    public void instantiateRegenHologram(Location loc, String regenString){
        TextDisplay regenHologram = HologramAPI.createOffsetTextHologram(regenString, loc, getRandomOffset(), 2.85, getRandomOffset());
        damageIndicators.put(regenHologram, persistTime/2);
    }

}
