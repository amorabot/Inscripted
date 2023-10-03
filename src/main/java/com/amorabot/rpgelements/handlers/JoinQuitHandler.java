package com.amorabot.rpgelements.handlers;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.managers.JSONProfileManager;
import com.amorabot.rpgelements.tasks.PlayerInterfaceRenderer;
import com.amorabot.rpgelements.utils.Utils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

import static com.amorabot.rpgelements.tasks.PlayerInterfaceRenderer.startupBossBars;

public class JoinQuitHandler implements Listener {

    private RPGElements plugin;

    public JoinQuitHandler(RPGElements plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){

        Player player = event.getPlayer();

        if (JSONProfileManager.isNewPlayer(player.getUniqueId())){ //If new player:
            JSONProfileManager.createProfile(player.getUniqueId().toString()); //Creates and instantiates the profile.
            Utils.log("O perfil para o player " + player.getDisplayName() + " foi criado. (JSON)");

            startupBossBars(player);

            return;
        }
        JSONProfileManager.loadProfileFromJSON(player.getUniqueId()); //Loads specific profile into memory
        Utils.log("Bem vindo de volta " + player.getDisplayName() + "! (JSON)");

        startupBossBars(player);
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        Utils.log(player.getDisplayName() + " has quit. Saving profile and removing from cache.");
        UUID playerUUID = player.getUniqueId();
        JSONProfileManager.saveProfileOnQuitToJSON(playerUUID, JSONProfileManager.getProfile(playerUUID));

        //Un-instantiate bossbars
        PlayerInterfaceRenderer.deleteBossBars(player);
    }
}
