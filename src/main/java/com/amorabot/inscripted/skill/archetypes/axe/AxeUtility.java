package com.amorabot.inscripted.skill.archetypes.axe;

import com.amorabot.inscripted.combat.buffs.Buffs;
import com.amorabot.inscripted.combat.buffs.PlayerBuffManager;
import com.amorabot.inscripted.combat.buffs.categories.stat.StatBuff;
import com.amorabot.inscripted.displays.InscriptedDisplay;
import com.amorabot.inscripted.displays.Models;
import com.amorabot.inscripted.particle.ParticlePlotter;
import com.amorabot.inscripted.skill.type.subroutines.DurationSubroutine;
import com.amorabot.inscripted.tasks.base.Skillcast;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class AxeUtility {

    public static void warBanner(Skillcast skillcastInstance){
        DurationSubroutine warBannerSubroutine = new DurationSubroutine(skillcastInstance);
        warBannerSubroutine.setRoutine(new BukkitRunnable() {
            final float radius = 6.5f;
            final int points = 40;
            final Location centerLoc = skillcastInstance.getPlayer().getLocation();
            InscriptedDisplay[] banner = Models.instantiateWarBanner(centerLoc.toVector(),centerLoc.getWorld(),warBannerSubroutine.getTotalDuration());
            final StatBuff bannerFortify = new StatBuff(Buffs.FORTIFY, skillcastInstance.getPlayer());
            @Override
            public void run() {
                if (this.isCancelled()) return;
                warBannerSubroutine.cancelIfInvalid(); // Will cancel this task and preven further runs
                // Routine
                List<Player> affectedPlayers = (List<Player>) centerLoc.getNearbyPlayers(radius);
                for (Player p : affectedPlayers){
                    PlayerBuffManager.addBuffToPlayer(bannerFortify,p.getUniqueId());
                }

                ParticlePlotter.plotColoredCircleAt(centerLoc.toVector(), centerLoc.getWorld(), 255, 40, 50, 1.2f, radius, points);
                ParticlePlotter.plotCircleAt(centerLoc.toVector(), centerLoc.getWorld(), Particle.CRIT, radius-0.25f,points);
                ParticlePlotter.plotColoredCircleAt(centerLoc.toVector(), centerLoc.getWorld(), 255, 40, 50, 1.2f, radius-0.5f, points);
                // Update elapsed ticks since the task started
                warBannerSubroutine.addPeriodToElapsedTime();
            }
        });

        warBannerSubroutine.startSubroutine(0);
    }
}
