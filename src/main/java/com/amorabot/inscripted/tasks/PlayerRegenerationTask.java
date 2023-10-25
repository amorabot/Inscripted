package com.amorabot.inscripted.tasks;

import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerRegenManager;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PlayerRegenerationTask extends BukkitRunnable {

    private final UUID playerID;

    public PlayerRegenerationTask(UUID playerID){
        this.playerID = playerID;
    }

    @Override
    public void run() {
        if (this.isCancelled()){
            Utils.log("??????");
        }

        Player player = Bukkit.getPlayer(playerID);
        Profile playerProfile = JSONProfileManager.getProfile(playerID);
        HealthComponent HPComponent = playerProfile.getHealthComponent();
        if (HPComponent.getCurrentHealth() != HPComponent.getMaxHealth()){
            int HPS = HPComponent.getHealthRegen();

            double mappedHealth = HPComponent.getMappedHealth(20);
            if ((mappedHealth - player.getHealth())>=0.5D){
                player.setHealth(mappedHealth);
            }
            player.sendMessage(Utils.color("&a&l+" + HPS));
            HPComponent.regenHealth(HPS);
        }

        if (HPComponent.getCurrentWard() != HPComponent.getMaxWard() && (PlayerRegenManager.canRegenWard(playerID))){
            HPComponent.regenWard();
            player.sendMessage(Utils.color("&3&l+" + HPComponent.getWardRegenTick()));
            return;
        }
    }
}
