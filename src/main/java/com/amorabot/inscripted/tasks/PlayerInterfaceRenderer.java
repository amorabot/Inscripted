package com.amorabot.inscripted.tasks;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.inscripted.components.Player.stats.PlayerStats;
import com.amorabot.inscripted.components.Items.relic.enums.Keystones;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.components.renderers.CustomUnicodeTable;
import com.amorabot.inscripted.components.renderers.InscriptedPalette;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.skills.casting.GlobalCooldownManager;
import com.amorabot.inscripted.skills.AbilityTypes;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerInterfaceRenderer extends BukkitRunnable {

    private static final Map<UUID, TextDisplay> hpDisplays = new HashMap<>();

    private static final PlayerInterfaceRenderer INSTANCE = new PlayerInterfaceRenderer();
    private PlayerInterfaceRenderer(){
    }
    @Override
    public void run() {
        for (Player currentPlayer : Bukkit.getOnlinePlayers()){
            Profile playerProfile = JSONProfileManager.getProfile(currentPlayer.getUniqueId());
            HealthComponent healthComponent = playerProfile.getHealthComponent();

            TextDisplay hpDisplay = getHPDisplayFor(currentPlayer);
//            String negSpace = ""+CustomUnicodeTable.M7 + CustomUnicodeTable.M32;
//            String negSpace = "";
//            hpDisplay.text(Component.text(negSpace).append(healthComponent.getHealthBarComponent()).appendNewline());
            hpDisplay.text(healthComponent.getHealthBarComponent().appendNewline().appendNewline());

            float maxHealth = healthComponent.getMaxHealth();
            float curHealth = healthComponent.getCurrentHealth();
            String healthHex = InscriptedPalette.HEALTH.getColorString();
            float maxWard = healthComponent.getMaxWard();
            float curWard = healthComponent.getCurrentWard();
            String wardHex = InscriptedPalette.WARD.getColorString();
            float dps = playerProfile.getDamageComponent().getHitData().getDPS();
//            int staminaValue = 100;
            int staminaValue = (int) playerProfile.getStatsComponent().getPlayerStats().getFinalValueFor(PlayerStats.STAMINA,false);
//            int staminaValue = (int) StatCompiler.readFinalFlatValueFrom(,PlayerStats.STAMINA); //TODO: put a cached value for this in some player profile component
//            char facing = currentPlayer.getFacing().toString().charAt(0);

            Audience playerAudience = Audience.audience(currentPlayer);

            currentPlayer.setLevel(staminaValue);

            TextComponent div = Component.text(" | ")
                    .decorate(TextDecoration.BOLD)
                    .color(NamedTextColor.WHITE);
            TextComponent health = Component.text((int) curHealth + "/" + (int) maxHealth)
                            .append(Component.text(DefenceTypes.HEALTH.getSpecialChar()))
                            .color(TextColor.fromHexString(healthHex));
            String wardValues = ((int) curWard + "/" + (int) maxWard) + DefenceTypes.WARD.getSpecialChar();
            TextComponent ward;
            if (playerProfile.hasKeystone(Keystones.FORBIDDEN_PACT)){
                ward = Component.text(wardValues)
                        .color(TextColor.fromHexString(InscriptedPalette.ABYSSAL.getColorString()))
                        .decorate(TextDecoration.BOLD);
            } else {
                ward = Component.text(wardValues).color(TextColor.fromHexString(wardHex));
            }


            String infoSection = "&7&l Lv12[&6&l|||&8|||||||&7&l] ";
            TextComponent infoComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(infoSection);
            String cooldownSection = "";
            if (dps > 1){
                cooldownSection+= "&a\uD83D\uDDE1 ";
            }
            Long remainingMovementCD = GlobalCooldownManager.fetchAbilityRemainingCooldown(currentPlayer.getUniqueId(), AbilityTypes.MOVEMENT);
            if (remainingMovementCD > 0){
                if (remainingMovementCD<4000){
                    cooldownSection += "&8"+ remainingMovementCD/1000 +"⏳M ";
                } else {
                    cooldownSection += "&8⏳M ";
                }
            } else {
                cooldownSection += "&a⏳M ";
            }
//            cooldownSection += " &7\uD83E\uDDEA12";
            TextComponent cooldownCoomponent = LegacyComponentSerializer.legacyAmpersand().deserialize(cooldownSection);

            TextComponent ABComponent;
            if (curWard == 0){
                ABComponent = health.append(div).append(infoComponent).append(div).append(cooldownCoomponent);
            } else {
                ABComponent = health.append(Component.text(" ").append(ward).append(div).append(infoComponent).append(div).append(cooldownCoomponent));
            }
            playerAudience.sendActionBar(ABComponent);
            //Health display renderer above player
        }
    }

    public static TextDisplay getHPDisplayFor(LivingEntity entity){
        UUID entityID = entity.getUniqueId();
        if (!hpDisplays.containsKey(entityID)){
            return createHPDisplayFor(entity);
        }
        return hpDisplays.get(entityID);
    }

    public static TextDisplay createHPDisplayFor(LivingEntity entity){
        TextDisplay display = Inscripted.getPlugin().getWorld().spawn(entity.getLocation().clone().add(0,2.5,0), TextDisplay.class, textDisplay -> {
            textDisplay.setBillboard(Display.Billboard.CENTER);
            textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);
            textDisplay.setTextOpacity((byte) (255*0.70));
            textDisplay.setLineWidth(900);
            textDisplay.setBackgroundColor(Color.fromARGB(10,30,10,10));
            textDisplay.setPersistent(false);
            textDisplay.text(Component.text("Not initialized!"));
        });
        entity.addPassenger(display);
        if (entity instanceof Player){
            ((Player)entity).hideEntity(Inscripted.getPlugin(),display);
            hpDisplays.put(entity.getUniqueId(),display);
        }
        return display;
    }
    public static void destroyHPDisplayFor(LivingEntity entity){
        TextDisplay hpDisplay = getHPDisplayFor(entity);
        entity.removePassenger(hpDisplay);
        hpDisplay.remove();
        hpDisplays.remove(entity.getUniqueId());
        Utils.log("Sucessfully removed hp display!");
    }

    public static void reloadHPDisplays(){
        //Assumes the only entity riding the player is their hp display
        for (Player currentPlayer : Bukkit.getOnlinePlayers()){
            try{
                Entity display = currentPlayer.getPassengers().get(0);
                currentPlayer.removePassenger(display);
                display.remove();
                createHPDisplayFor(currentPlayer);
            } catch (IndexOutOfBoundsException ex){
                Utils.error("Invalid passenger removal attempt");
            }
        }
    }

    public static PlayerInterfaceRenderer getInstance() {
        return INSTANCE;
    }
}
