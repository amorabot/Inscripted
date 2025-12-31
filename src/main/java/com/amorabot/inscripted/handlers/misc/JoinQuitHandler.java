package com.amorabot.inscripted.handlers.misc;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.combat.EntityStateManager;
import com.amorabot.inscripted.file.profile.ProfileDatabase;
import com.amorabot.inscripted.handlers.Inventory.PlayerEquipmentHandler;
import com.amorabot.inscripted.player.PlayerDataContainer;
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

    public JoinQuitHandler(){
        Bukkit.getPluginManager().registerEvents(this, Inscripted.getPlugin());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        player.setMaximumNoDamageTicks(5);
        UUID playerID = player.getUniqueId();

        if (ProfileDatabase.isNewPlayer(playerID)){
            PlayerDataContainer.instantiatePlayer(playerID);
            Utils.log("Instantiating new profile for " + player.getName());

            showTitleTo(player, "<Welcome, "+ player.getName() + ">", "to the Inscripted Alpha!");
            initializePlayer(player);
            return;
        }

        PlayerDataContainer.instantiatePlayer(playerID,ProfileDatabase.loadProfile(playerID));
        PlayerEquipmentHandler.weaponEquip(player,player.getInventory().getItemInMainHand());
        initializePlayer(player);

        showTitleTo(player, "<Welcome back, " + player.getName() + "!>", "Enjoy the alpha!");
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        Utils.log(player.getDisplayName() + " has quit. Saving profile and removing from cache.");
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_JUMP_STRENGTH)).setBaseValue(0.42); //Resetting JumpStrength on quit
        UUID playerUUID = player.getUniqueId();

        ProfileDatabase.saveProfile(playerUUID);
        PlayerDataContainer.clearPlayerMemory(playerUUID);
    }

    private void initializePlayer(Player player){
        EntityStateManager.setPlayerMetadata(player);
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(1);
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_ABSORPTION)).setBaseValue(20);
        player.setSaturatedRegenRate(3000);
    }

    private void showTitleTo(Player player, String mainTitle, String subtitle){
        final Component mainTitleText = Component.text(mainTitle);
        final Component subtitleText = Component.text(subtitle);

        final Title title = Title.title(
                mainTitleText, subtitleText,
                Title.Times.times(Duration.ofMillis(1500), Duration.ofMillis(3000), Duration.ofMillis(200)) );
        player.showTitle(title);
    }

    private void combatLog(Player player){
        if (CombatLogger.isInCombat(player)) {
            if (player.hasMetadata(CombatLogger.getPvpTag())){
                Utils.error(player.getName() + " logged out during PVP... crack his skull");
//                EventAPI.entityDeath(player);
            }
            else if (player.hasMetadata(CombatLogger.getCombatTag())){
                Utils.error(player.getName() + " logged out during regular combat");
            }
            CombatLogger.removeFromCombat(player.getUniqueId());
        }
    }
}
