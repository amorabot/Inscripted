package com.amorabot.inscripted.tasks;

import com.amorabot.inscripted.item.render.InscriptedPalette;
import com.amorabot.inscripted.item.structure.Armor.DefenceTypes;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.Profile;
import com.amorabot.inscripted.player.profile.component.HealthComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class ActionBarRenderer extends BukkitRunnable {

    private static final ActionBarRenderer INSTANCE = new ActionBarRenderer();
    private ActionBarRenderer(){
    }
    @Override
    public void run() {
        for (Player currentPlayer : Bukkit.getOnlinePlayers()){
            UUID id = currentPlayer.getUniqueId();
            Profile playerProfile = PlayerDataContainer.getProfile(id);
            HealthComponent healthComponent = playerProfile.getHealthComponent();

            final int currentHP = healthComponent.getHealth();
            final int totalHP = healthComponent.getMaxHealth();
            Component hpSection = Component.text(DefenceTypes.HEALTH.getSpecialChar()+" "+ currentHP + "/" + totalHP).color(InscriptedPalette.HEALTH.getColor());
            Component soulSection = null;
            if (healthComponent.getSoul()>0){
                soulSection = Component.text("  |  ").color(TextColor.color(120,120,120));
                final int currentSoul = healthComponent.getSoul();
                final int totalSoul = healthComponent.getMaxSoul();
                soulSection = soulSection.append(Component.text(DefenceTypes.SOUL.getSpecialChar()+" "+ currentSoul + "/" + totalSoul).color(InscriptedPalette.SOUL.getColor()));
            }
            if (soulSection==null){
                currentPlayer.sendActionBar(hpSection.decoration(TextDecoration.ITALIC,false));
                continue;
            }

            currentPlayer.sendActionBar(hpSection.append(soulSection).decoration(TextDecoration.ITALIC,false));
//            Long remainingMovementCD = GlobalCooldownManager.fetchAbilityRemainingCooldown(currentPlayer.getUniqueId(), AbilityTypes.MOVEMENT);
//            if (remainingMovementCD > 0){
//                if (remainingMovementCD<4000){
//                    cooldownSection += "&8"+ remainingMovementCD/1000 +"⏳M ";
//                } else {
//                    cooldownSection += "&8⏳M ";
//                }
//            } else {
//                cooldownSection += "&a⏳M ";
//            }
////            cooldownSection += " &7\uD83E\uDDEA12";
//            TextComponent cooldownCoomponent = LegacyComponentSerializer.legacyAmpersand().deserialize(cooldownSection);
    }


    }

    public static ActionBarRenderer getInstance() {
        return INSTANCE;
    }
}
