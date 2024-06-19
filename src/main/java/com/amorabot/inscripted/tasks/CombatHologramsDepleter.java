package com.amorabot.inscripted.tasks;

import com.amorabot.inscripted.APIs.HologramAPI;
import com.amorabot.inscripted.utils.DelayedTask;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.joml.Matrix4f;

import java.util.*;

import static com.amorabot.inscripted.utils.Utils.getRandomOffset;

public class CombatHologramsDepleter extends BukkitRunnable {

    //Instance really needed?
    private static final CombatHologramsDepleter instance = new CombatHologramsDepleter();
    private static final Matrix4f transf = new Matrix4f().translate(0F, 1.2F, 0F);

    private static final Map<TextDisplay, Integer> damageIndicators = new HashMap<>(); //Map de entity e duração do indicator
    private static final Set<TextDisplay> hologramsToRemove = new HashSet<>();
    private static final int persistTime = 15; //Ticks

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
                dmgHolo.setTextOpacity((byte) (255*(ticksLeft/persistTime)));
                dmgHolo.setTransformationMatrix(transf);

                ticksLeft --;
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

    private void putHologram(TextDisplay holo, int duration){
        new DelayedTask(new BukkitRunnable() {
            @Override
            public void run() {
                holo.setTransformationMatrix(transf);
                damageIndicators.put(holo, duration);
            }
        }, 1L);
    }

    public void instantiateDamageHologramAt(Location loc, int[] incomingDamage){
        TextDisplay dmgHologram = HologramAPI.createDamageHologramAt(loc, incomingDamage);
        putHologram(dmgHologram, persistTime);
    }

    public void instantiateDodgeHologramAt(Location loc){
        TextDisplay dodgeHolo = HologramAPI.createDodgeIndicatorAt(loc);
        putHologram(dodgeHolo, persistTime);
    }

    public void instantiateRegenHologram(Location loc, String regenString){
        TextDisplay regenHologram = HologramAPI.createRegenHologramAt(loc, regenString);
        putHologram(regenHologram, persistTime/2);
    }

}
