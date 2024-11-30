package com.amorabot.inscripted.handlers.Combat;

import com.amorabot.inscripted.APIs.MessageAPI;
import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.APIs.damageAPI.CombatEffects;
import com.amorabot.inscripted.APIs.damageAPI.EntityStateManager;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Player.stats.StatCompiler;
import com.amorabot.inscripted.events.death.InscriptedPlayerDeathEvent;
import com.amorabot.inscripted.managers.PlayerBuffManager;
import com.amorabot.inscripted.utils.DelayedTask;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;

public class InscriptedPlayerDeathEventListener implements Listener {

    private static final int invulnerabilityPeriod = 60; //ticks

    public InscriptedPlayerDeathEventListener(){
        Bukkit.getPluginManager().registerEvents(this, Inscripted.getPlugin());
    }

    @EventHandler
    public void onCustomPlayerDeath(InscriptedPlayerDeathEvent event){
        Player deadPlayer = event.getDeadPlayer();
        Location respawnLoc = new Location(deadPlayer.getWorld(),-1399,65,-622);
        deadPlayer.teleport(respawnLoc);
        PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, 30, 10, true, false, false);
        blindness.apply(deadPlayer);
        PlayerBuffManager.clearAllBuffsFor(deadPlayer);
        CombatEffects.deathEffect(deadPlayer);
        if (deadPlayer.getKiller() != null) {
            Audience audience = Audience.audience(deadPlayer, deadPlayer.getKiller());
            SoundAPI.playDeathSoundFor(audience, deadPlayer.getLocation());
            MessageAPI.broadcast(MessageAPI.deathMessage(deadPlayer.getKiller(), deadPlayer));
        } else {
            MessageAPI.broadcast(Component.text(deadPlayer.getName() + " ☠").color(NamedTextColor.RED));
        }

        final Component mainTitleText = Component.text("You Died").color(NamedTextColor.RED);
        final Component subtitleText = Component.text("to " + deadPlayer.getKiller().getName()).color(NamedTextColor.RED);

        final Title title = Title.title(
                mainTitleText, subtitleText,
                Title.Times.times(Duration.ofMillis(1000), Duration.ofMillis(2000), Duration.ofMillis(2000)) );
        deadPlayer.showTitle(title);

        HealthComponent.replenishHitPoints(deadPlayer);
        StatCompiler.updateProfile(deadPlayer.getUniqueId());

        new DelayedTask(new BukkitRunnable() {
            @Override
            public void run() {
                EntityStateManager.setDead(deadPlayer,false);
            }
        }, invulnerabilityPeriod
        );
    }
}
