package pluginstudies.pluginstudies.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pluginstudies.pluginstudies.PluginStudies;
import pluginstudies.pluginstudies.components.Profile;
import pluginstudies.pluginstudies.managers.ProfileManager;
import pluginstudies.pluginstudies.utils.DelayedTask;
import pluginstudies.pluginstudies.utils.Utils;

import java.util.LinkedList;
import java.util.List;

public class PlayerHandler implements Listener {

    private PluginStudies plugin;
    private ProfileManager profileManager;

    public PlayerHandler(PluginStudies plugin){
        this.plugin = plugin;
        this.profileManager = plugin.getProfileManager();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){

        Player player = event.getPlayer();
        //Agora vamos checar se o player que logou ja existe na DB (se existe é retornado, senão, o hashMap retorna null)
        Profile profile = profileManager.getPlayerProfile(player.getUniqueId()); //null se não existe
        if (profile == null){ //aqui é o caso onde o player não existe na DB, portanto, vamos registrá-lo
            profile = profileManager.createNewProfile(player);
            Utils.log("O perfil para o player " + player.getDisplayName() + " foi criado");
        }// A partir desse momento, temos a certeza de que profile contém um perfil válido (criado ou pré-existente)
        else{
            Utils.log("Bem vindo de volta " + player.getDisplayName() + "!");
        }


//        Player player = event.getPlayer();
//
//        ItemStack item = new ItemStack(Material.GHAST_TEAR, 10);
//        Inventory inv = player.getInventory();
//
//        ItemMeta meta = item.getItemMeta();
//        meta.setDisplayName("Fragment");
//        if (meta.hasDisplayName()){
//            List <String> lore = new LinkedList<>();
//            lore.add("This item has been renamed by someone else...");
//            lore.add("try coming up with your own name");
//            meta.setLore(lore);
//        }
//        item.setItemMeta(meta);
//
//        inv.addItem(item);
//        inv.setItem(8, item);
    }

//    @EventHandler
//    public void onEntityDamage(EntityDamageEvent event){
//        if (!(event.getEntity() instanceof Player) && (event.getCause() == EntityDamageEvent.DamageCause.FALL)){
//            return;
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
//    }
}
