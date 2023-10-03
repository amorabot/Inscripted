package com.amorabot.rpgelements.tasks;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.rpgelements.handlers.Combat.MobDropHandler;
import com.amorabot.rpgelements.managers.JSONProfileManager;
import com.amorabot.rpgelements.utils.ColorUtils;
import com.amorabot.rpgelements.utils.CraftingUtils;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.amorabot.rpgelements.utils.Utils.getRandomOffset;

public class DamageHologramDepleter extends BukkitRunnable {

    private static final DamageHologramDepleter instance = new DamageHologramDepleter();

    private static final Map<TextDisplay, Integer> damageIndicators = new HashMap<>(); //Map de entity e duração do indicator
    private Set<TextDisplay> hologramsToRemove = new HashSet<>();
    private static final int persistTime = 25; //Ticks

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
                dmgHolo.setTextOpacity((byte) (dmgHolo.getTextOpacity()-10));
                ticksLeft--;
                damageIndicators.put(dmgHolo, ticksLeft);
            }
            damageIndicators.keySet().removeAll(hologramsToRemove);
        }
    }

    public static DamageHologramDepleter getInstance(){
        return instance;
    }

    public void createDamageHologram(Player player, LivingEntity entity){
        Location hologramLocation = entity.getLocation().clone().add(getRandomOffset(), 2.5 + getRandomOffset(), getRandomOffset());
        TextDisplay damageHologram = RPGElements.getPlugin().getWorld().spawn(hologramLocation, TextDisplay.class);
        damageHologram.setText(getDamageString(JSONProfileManager.getProfile(player.getUniqueId()).getDamageComponent().getDamage()));
        damageHologram.setBillboard(Display.Billboard.VERTICAL);
        damageHologram.setAlignment(TextDisplay.TextAlignment.CENTER);
        damageHologram.setTextOpacity((byte) (255));
//        return damageHologram;
        damageIndicators.put(damageHologram, persistTime);
    }
    private @NotNull String getDamageString(Map<DamageTypes, int[]> damagesMap){
        StringBuilder dmgString = new StringBuilder();
        if (damagesMap.containsKey(DamageTypes.PHYSICAL)){
            int[] physDmg = damagesMap.get(DamageTypes.PHYSICAL);
            dmgString.append(ColorUtils.translateColorCodes(
                            DamageTypes.PHYSICAL.getColor() + CraftingUtils.getRandomNumber(physDmg[0], physDmg[1])))
                    .append(DamageTypes.PHYSICAL.getCharacter())
                    .append(" ");
        }

        for (DamageTypes dmgType : damagesMap.keySet()){
            if (dmgType.equals(DamageTypes.PHYSICAL)){continue;}
            int[] dmg = damagesMap.get(dmgType);
            if (Arrays.equals(dmg, new int[2])){
                continue;
            }
            dmgString.append(ColorUtils.translateColorCodes(
                    dmgType.getColor() + CraftingUtils.getRandomNumber(dmg[0], dmg[1])+ dmgType.getCharacter() )).append(" ");
        }
        return dmgString.toString().trim();
    }
//    public static void addHologram(TextDisplay hologram){
//
//    }

}
