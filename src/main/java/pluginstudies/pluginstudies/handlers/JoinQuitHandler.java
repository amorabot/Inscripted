package pluginstudies.pluginstudies.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pluginstudies.pluginstudies.RPGElements;
import pluginstudies.pluginstudies.managers.JSONProfileManager;
import pluginstudies.pluginstudies.managers.ProfileManager;

import java.io.IOException;
import java.util.UUID;

import static pluginstudies.pluginstudies.utils.Utils.log;

public class JoinQuitHandler implements Listener {

    private RPGElements plugin;
    private ProfileManager profileManager;

    public JoinQuitHandler(RPGElements plugin){
        this.plugin = plugin;
        this.profileManager = plugin.getProfileManager();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){

        Player player = event.getPlayer();

        if (profileManager.isNewPlayer(player)){
            profileManager.createNewProfile(player); //cria e instancia o perfil
            log("O perfil para o player " + player.getDisplayName() + " foi criado.");
        } else {
            profileManager.loadProfileFromConfig(player);
            log("Bem vindo de volta " + player.getDisplayName() + "!");
        }

        if (JSONProfileManager.isNewPlayer(player.getUniqueId())){ //If new player:
            JSONProfileManager.createProfile(player.getUniqueId().toString()); //Creates and instantiates the profile.
            log("O perfil para o player " + player.getDisplayName() + " foi criado. (JSON)");
        } else {
            JSONProfileManager.loadProfileFromJSON(player.getUniqueId().toString()); //Loads specific profile into memory
            log("Bem vindo de volta " + player.getDisplayName() + "! (JSON)");
        }
        //Agora vamos checar se o player que logou ja existe na DB (se existe é retornado, senão, o hashMap retorna null)
//        Profile profile = profileManager.getPlayerProfile(player.getUniqueId()); //null se não existe
//        if (profile == null){ //aqui é o caso onde o player não existe na DB, portanto, vamos registrá-lo
//            profile = profileManager.createNewProfile(player); //Criamos o perfil
//            profileManager.loadProfileFromConfig(player); //E logo pegamos ele do config file para ser usado
//            Utils.log("O perfil para o player " + player.getDisplayName() + " foi criado");
//        }// A partir desse momento, temos a certeza de que profile contém um perfil válido (criado ou pré-existente)
//        else{
//            profileManager.loadProfileFromConfig(player);
//            Utils.log("Bem vindo de volta " + player.getDisplayName() + "!");
//        }
//
//        Player player = (Player) event.getEntity();
//        new DelayedTask(() -> {
//            player.getInventory().addItem(new ItemStack(Material.DIAMOND));
//        }, 20 * 5);
///* caso seja desejado cancelar a taks, podemos cancela-la dessa forma, pegando o id e passando para o scheduler cancelar
//
//        DelayedTask task = new DelayedTask(() -> {
//            player.getInventory().addItem(new ItemStack(Material.DIAMOND));
//        }, 20 * 5);
//
//        Bukkit.getScheduler().cancelTask(task.getId());
//*/
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        UUID uuid = event.getPlayer().getUniqueId();
        profileManager.saveProfile(uuid);
        profileManager.unloadProfile(uuid);
        log(event.getPlayer().getDisplayName() + " has quit. Saving profile...");

        try {
            JSONProfileManager.saveToJSON();
        } catch (IOException exception){
            log("Error accessing and saving profiles");
        }
    }
}
