package com.amorabot.inscripted.skill.archetypes.sword;

import com.amorabot.inscripted.combat.buffs.Buffs;
import com.amorabot.inscripted.combat.buffs.PlayerBuffManager;
import com.amorabot.inscripted.combat.buffs.categories.stat.StatBuff;
import com.amorabot.inscripted.displays.DisplayItem;
import com.amorabot.inscripted.math.LinalgMath;
import com.amorabot.inscripted.particle.ParticlePlotter;
import com.amorabot.inscripted.skill.type.subroutines.DurationSubroutine;
import com.amorabot.inscripted.tasks.base.Skillcast;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;

import java.util.List;

public class SwordUtility {
    public static void ringOfBlades(Skillcast skillcastInstance){
        DurationSubroutine robSubroutine = new DurationSubroutine(skillcastInstance);

        final float radius = 4f;
        final int points = 30;
        final Location centerLoc = skillcastInstance.getPlayer().getLocation();
        final int totalDuration = robSubroutine.getTotalDuration();

        plotSword(centerLoc.toVector(),centerLoc.getWorld(),Material.GOLDEN_SWORD,100,0.6f,2.1,totalDuration);
        Vector[] miniSwordPositions = LinalgMath.plotPointsInsideHorizontalCircle(centerLoc.toVector(),radius,4);
        for (Vector swordPos : miniSwordPositions){
            int randRotation = (int) (100 + (Utils.getRandomOffset()*20));
            double randScale = 0.8 + (Math.random()*0.4);
            plotSword(swordPos,centerLoc.getWorld(),Material.IRON_SWORD,randRotation,0.3f,randScale,totalDuration);
        }

        robSubroutine.setRoutine(new BukkitRunnable() {

            @Override
            public void run() {
                if (this.isCancelled()) return;
                robSubroutine.cancelIfInvalid(); // Will cancel this task and preven further runs
                // Routine
                List<Player> affectedPlayers = (List<Player>) centerLoc.getNearbyPlayers(radius); //Currently affects everyone inside
                for (Player p : affectedPlayers){
                    final StatBuff adrenaline = new StatBuff(Buffs.ADRENALINE, p);
                    PlayerBuffManager.addBuffToPlayer(adrenaline,p.getUniqueId());
                }

                ParticlePlotter.plotColoredCircleAt(centerLoc.toVector(), centerLoc.getWorld(), 255, 255, 50, 1.2f, radius, points,true);
                ParticlePlotter.plotCircleAt(centerLoc.toVector(), centerLoc.getWorld(), Particle.CRIT, radius-0.25f,points);
                ParticlePlotter.plotColoredCircleAt(centerLoc.toVector(), centerLoc.getWorld(), 255, 255, 50, 1.2f, radius-0.5f, points,true);
                // Update elapsed ticks since the task started
                robSubroutine.addElapsedTime();
            }
        });

        robSubroutine.startSubroutine(0);
    }

    private static void plotSword(Vector pos, World world, Material swordMaterial, int rotation, float offY, double scale, int totalDuration){
        DisplayItem blade = new DisplayItem(pos,world, swordMaterial,totalDuration,true);
        blade.scale(scale);
        Transformation bladeTransform = blade.getDisplayEntity().getTransformation();
        bladeTransform.getLeftRotation().rotateZ((float) Math.toRadians(rotation));
        bladeTransform.getLeftRotation().rotateX((float) Math.toRadians(90*Utils.getRandomOffset()));
        bladeTransform.getTranslation().add(0,offY,0);
        blade.setTransformation(bladeTransform);
        blade.setLerpValues(0,3);
    }
}
