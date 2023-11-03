package com.amorabot.inscripted.handlers.misc;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerRegenManager;
import com.amorabot.inscripted.tasks.PlayerInterfaceRenderer;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

import static com.amorabot.inscripted.tasks.PlayerInterfaceRenderer.startupBossBars;

public class JoinQuitHandler implements Listener {

    private Inscripted plugin;

    public JoinQuitHandler(Inscripted plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){

        Player player = event.getPlayer();
        UUID playerID = player.getUniqueId();
        if (JSONProfileManager.isNewPlayer(playerID)){ //If new player:
            JSONProfileManager.createProfile(playerID.toString()); //Creates and instantiates the profile.
            Utils.log("O perfil para o player " + player.getDisplayName() + " foi criado. (JSON)");

            initializePlayer(player);
//            PlayerRegenManager.addPlayer(playerID);
//            startupBossBars(player);

            return;
        }
        JSONProfileManager.loadProfileFromJSON(player.getUniqueId()); //Loads specific profile into memory
        Utils.log("Bem vindo de volta " + player.getDisplayName() + "! (JSON)");

        initializePlayer(player);
//        PlayerRegenManager.addPlayer(playerID);
//        startupBossBars(player);
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        Utils.log(player.getDisplayName() + " has quit. Saving profile and removing from cache.");
        UUID playerUUID = player.getUniqueId();
        JSONProfileManager.saveProfileOnQuitToJSON(playerUUID, JSONProfileManager.getProfile(playerUUID));

        destroyPlayerData(player);
//        //Un-instantiate bossbars
//        PlayerInterfaceRenderer.deleteBossBars(player);
    }

    private void initializePlayer(Player player){
        PlayerRegenManager.addPlayer(player.getUniqueId());
        startupBossBars(player);
    }
    private void destroyPlayerData(Player player){
        //Un-instantiate bossbars
        PlayerRegenManager.removePlayer(player.getUniqueId());
        PlayerInterfaceRenderer.deleteBossBars(player);
    }
}
