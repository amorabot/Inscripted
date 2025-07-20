package com.amorabot.inscripted.events;

import com.amorabot.inscripted.APIs.MessageAPI;
import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.combat.CombatEffects;
import com.amorabot.inscripted.combat.EntityStateManager;
import com.amorabot.inscripted.combat.buffs.PlayerBuffManager;
import com.amorabot.inscripted.utils.DelayedTask;
import io.papermc.paper.entity.TeleportFlag;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;

public class DeathEvent {
    private static final int invulnerabilityPeriod = 60; //ticks

    public static void execute(Player deadPlayer){
        EntityStateManager.setDead(deadPlayer,true);

        Location respawnLoc = new Location(deadPlayer.getWorld(),0,126,-326);
        deadPlayer.teleport(respawnLoc, TeleportFlag.EntityState.RETAIN_PASSENGERS);
        PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, 30, 10, true, false, false);
        blindness.apply(deadPlayer);
        PlayerBuffManager.clearAllBuffsFor(deadPlayer.getUniqueId());
        CombatEffects.deathEffect(deadPlayer);

        String killerName = "???";

        Player killer = deadPlayer.getKiller();
        if (killer != null) {
            Audience audience = Audience.audience(deadPlayer, deadPlayer.getKiller());
            SoundAPI.playDeathSoundFor(audience, deadPlayer.getLocation());
            MessageAPI.broadcast(MessageAPI.deathMessage(deadPlayer.getKiller(), deadPlayer));
            killerName = killer.getName();
        } else {
            MessageAPI.broadcast(Component.text(deadPlayer.getName() + " ☠").color(NamedTextColor.RED));
        }

        final Component mainTitleText = Component.text("You Died").color(NamedTextColor.RED);
        final Component subtitleText = Component.text("to " + killerName).color(NamedTextColor.RED);

        final Title title = Title.title(
                mainTitleText, subtitleText,
                Title.Times.times(Duration.ofMillis(1000), Duration.ofMillis(2000), Duration.ofMillis(2000)) );
        deadPlayer.showTitle(title);

        new DelayedTask(new BukkitRunnable() {
            @Override
            public void run() {
                EntityStateManager.setDead(deadPlayer,false);
            }
        }, invulnerabilityPeriod
        );
    }
}
