package com.amorabot.inscripted.handlers.misc;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerPassivesManager;
import com.amorabot.inscripted.managers.PlayerRegenManager;
import com.amorabot.inscripted.tasks.CombatLogger;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;


public class JoinQuitHandler implements Listener {

    private Inscripted plugin;

    public JoinQuitHandler(Inscripted plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){

        Player player = event.getPlayer();
        player.setMaximumNoDamageTicks(5);
        UUID playerID = player.getUniqueId();
        if (JSONProfileManager.isNewPlayer(playerID)){ //If new player:
            JSONProfileManager.createProfile(playerID.toString()); //Creates and instantiates the profile.
            Utils.log("O perfil para o player " + player.getDisplayName() + " foi criado. (JSON)");

            showTitleTo(player, "<Welcome, "+ player.getName() + ">", "to the Inscripted Alpha!");
            initializePlayer(player);

            return;
        }
        JSONProfileManager.loadProfileFromJSON(player.getUniqueId()); //Loads specific profile into memory

        showTitleTo(player, "<Welcome back, " + player.getName() + "!>", "Enjoy the alpha!");
        initializePlayer(player);
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        Utils.log(player.getDisplayName() + " has quit. Saving profile and removing from cache.");
        UUID playerUUID = player.getUniqueId();
        JSONProfileManager.saveProfileOnQuitToJSON(playerUUID, JSONProfileManager.getProfile(playerUUID));

        destroyPlayerData(player);
    }

    private void initializePlayer(Player player){
        PlayerRegenManager.addPlayer(player.getUniqueId());
        //                                                          min 0  |  max 1
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(1);
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_ABSORPTION)).setBaseValue(20);
        player.setSaturatedRegenRate(3000);

    }
    private void destroyPlayerData(Player player){
        //Un-instantiate bossbars
        PlayerRegenManager.removePlayer(player.getUniqueId());
        PlayerPassivesManager.removePlayer(player.getUniqueId());
        if (CombatLogger.isInCombat(player)) {
            if (player.hasMetadata(CombatLogger.getPvpTag())){
                Utils.error(player.getName() + " logged out during PVP... crack his skull");
            }
            else if (player.hasMetadata(CombatLogger.getCombatTag())){
                Utils.error(player.getName() + " logged out during regular combat");
            }
            CombatLogger.removeFromCombat(player.getUniqueId());
        }
    }

    private void showTitleTo(Player player, String mainTitle, String subtitle){
        final Component mainTitleText = Component.text(mainTitle);
        final Component subtitleText = Component.text(subtitle);

        final Title title = Title.title(
                mainTitleText, subtitleText,
                Title.Times.times(Duration.ofMillis(1500), Duration.ofMillis(3000), Duration.ofMillis(200)) );
        player.showTitle(title);
    }
}
