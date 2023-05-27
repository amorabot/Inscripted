package pluginstudies.pluginstudies.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pluginstudies.pluginstudies.RPGElements;
import pluginstudies.pluginstudies.managers.JSONProfileManager;

import java.io.IOException;

import static pluginstudies.pluginstudies.utils.Utils.log;

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
            log("O perfil para o player " + player.getDisplayName() + " foi criado. (JSON)");
        } else {
            JSONProfileManager.loadProfileFromJSON(player.getUniqueId().toString()); //Loads specific profile into memory
            log("Bem vindo de volta " + player.getDisplayName() + "! (JSON)");
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        log(event.getPlayer().getDisplayName() + " has quit. Saving profile...");

        try {
            JSONProfileManager.saveToJSON();
        } catch (IOException exception){
            log("Error accessing and saving profiles");
        }
    }
}
