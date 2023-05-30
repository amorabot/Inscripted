package com.amorabot.rpgelements.handlers;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.managers.JSONProfileManager;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

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
        } else {
            JSONProfileManager.loadProfileFromJSON(player.getUniqueId().toString()); //Loads specific profile into memory
            Utils.log("Bem vindo de volta " + player.getDisplayName() + "! (JSON)");
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        Utils.log(player.getDisplayName() + " has quit. Saving profile...");
        UUID playerUUID = player.getUniqueId();
        JSONProfileManager.saveProfileToJSON(playerUUID, JSONProfileManager.getProfile(playerUUID.toString()));
    }
}
