package com.amorabot.inscripted.skills.item;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.modifiers.unique.Keystones;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.skills.ParticlePlotter;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

public class StatToggleMonitors {

    public static int initializeBerserkToggleMonitorFor(Player keystoneHolder){
        return new BukkitRunnable() {
            @Override
            public void run() {
                UUID playerID = keystoneHolder.getUniqueId();
                Location loc = keystoneHolder.getLocation();
                Profile playerProfile = JSONProfileManager.getProfile(playerID);
                HealthComponent HPComponent = playerProfile.getHealthComponent();
                boolean isActive = playerProfile.getStatsComponent().isStatKeystonePresent(Keystones.BERSERK);
                        /*
                        Truth table
                        LL  ACTIVE  (toggle)
                        T     T       F
                        T     F       T
                        F     T       T
                        F     F       F
                        */
                boolean needsToggle = (HPComponent.isLowLife() ^ isActive);
                if (isActive){
                    ParticlePlotter.spawnParticleAt(loc.toVector().clone().add(new Vector(0,1.5D, 0)), loc.getWorld(), Particle.ANGRY_VILLAGER);
                }

                if (needsToggle){
                    if (isActive){ //De-activate
                        Utils.log("Toggling berserk! (OFF)");
                        playerProfile.getStatsComponent().removeActiveStatKeystone(playerID, Keystones.BERSERK, false);
                    } else { //Activate
                        Utils.log("Toggling berserk! (ON)");
                        playerProfile.getStatsComponent().addActiveStatKeystone(playerID, Keystones.BERSERK);
                    }
                }
            }


        }.runTaskTimer(Inscripted.getPlugin(), 20, 5).getTaskId();
    }
}
