package com.amorabot.inscripted.handlers.Combat;

import com.amorabot.inscripted.APIs.MessageAPI;
import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.managers.JSONProfileManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class DamageHandler implements Listener {

    private Inscripted plugin;
//    private DecimalFormat formatter = new DecimalFormat("#.##");

    public DamageHandler(Inscripted p){
        plugin = p;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event){
        //new way to handle damage:
        /*
        player.attack();
        player.setKiller();
        any custom routines are executed, any needed damage is done within them, and after all, the event damage is cancelled

        or -> custom death event: handling items to keep, no losing xp, death teleports player to last established spawnpoint(hearthstone), ...
         */
        com.amorabot.inscripted.APIs.damageAPI.DamageHandler.handleDamageEntityDamageEvents(event);
    }

    @EventHandler
    public void onEntityDeath(PlayerDeathEvent event){
        event.setShouldPlayDeathSound(false);
        if (event.getEntity().getKiller() != null) {
            Audience audience = Audience.audience(event.getEntity(), event.getEntity().getKiller());
            SoundAPI.playDeathSoundFor(audience, event.getEntity().getLocation());
            event.deathMessage(MessageAPI.deathMessage(event.getEntity().getKiller(), event.getPlayer()));
        } else {
            event.deathMessage(Component.text(event.getEntity().getName() + " ☠").color(NamedTextColor.RED));
        }
        Profile playerProfile = JSONProfileManager.getProfile((event.getEntity()).getUniqueId());
        HealthComponent playerHealth = playerProfile.getHealthComponent();
        playerHealth.replenishHitPoints();

        Player killedPlayer = event.getEntity();
        Location loc = killedPlayer.getEyeLocation();
        World world = event.getEntity().getWorld();
        ItemStack itemCrackData = new ItemStack(Material.NETHER_WART_BLOCK);
        for (int i = 0; i < 20; i++){
            world.spawnParticle(Particle.ITEM_CRACK, loc.x(),loc.y(),loc.z(), 2,
                    0.5,
                    0.5,
                    0.5,
                    itemCrackData);
        }
    }
}
