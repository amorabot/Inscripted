package com.amorabot.inscripted.skill.archetypes.mace;

import com.amorabot.inscripted.combat.buffs.PlayerBuffManager;
import com.amorabot.inscripted.particle.ParticlePlotter;
import com.amorabot.inscripted.skill.type.subroutines.DurationSubroutine;
import com.amorabot.inscripted.tasks.base.Skillcast;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class MaceUtility {
    public static void cleanse(Skillcast skillcastInstance){
        Player caster = skillcastInstance.getPlayer();
        PlayerBuffManager.clearDebuffsFor(caster.getUniqueId());

        DurationSubroutine cleanseSubroutine = new DurationSubroutine(skillcastInstance);
        cleanseSubroutine.setRoutine(new BukkitRunnable() {
            final float radius = 0.8f;
            final Location centerLoc = skillcastInstance.getPlayer().getLocation();
            double currentPhase = Math.random()*Math.PI/3;
            final double angleStep = Math.PI/18;
            int frame = 0;
            final double verticalStep = 0.3;
            @Override
            public void run() {
                if (this.isCancelled()) return;
                cleanseSubroutine.cancelIfInvalid(); // Will cancel this task and preven further runs
                // Routine
                Vector currentPoint = centerLoc.clone().add(Math.sin(currentPhase)*radius,
                        frame*verticalStep, Math.cos(currentPhase)*radius).toVector();
                Vector oppositePoint = centerLoc.clone().add(Math.sin(currentPhase+Math.PI)*radius,
                        frame*verticalStep, Math.cos(currentPhase+Math.PI)*radius).toVector();

                ParticlePlotter.spawnParticleAt(currentPoint, centerLoc.getWorld(), Particle.WITCH);
                ParticlePlotter.spawnColoredParticleAt(oppositePoint, centerLoc.getWorld(), 240, 140, 240, 1.2f, 1);
                ParticlePlotter.plotCircleAt(centerLoc.toVector(), centerLoc.getWorld(), Particle.END_ROD, radius, 10);

                currentPhase += angleStep;
                frame++;
                // Update elapsed ticks since the task started
                cleanseSubroutine.addElapsedTime();
            }
        });

        cleanseSubroutine.startSubroutine(0);

    }
}
