package com.amorabot.inscripted.tasks;

import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.PlayerStats;
import com.amorabot.inscripted.components.Items.modifiers.unique.Keystones;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.components.Player.stats.StatCompiler;
import com.amorabot.inscripted.components.renderers.InscriptedPalette;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.skills.casting.GlobalCooldownManager;
import com.amorabot.inscripted.skills.AbilityTypes;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerInterfaceRenderer extends BukkitRunnable {

    private static final PlayerInterfaceRenderer INSTANCE = new PlayerInterfaceRenderer();
    private PlayerInterfaceRenderer(){
    }
    @Override
    public void run() {
        for (Player currentPlayer : Bukkit.getOnlinePlayers()){
            Profile playerProfile = JSONProfileManager.getProfile(currentPlayer.getUniqueId());
            HealthComponent healthComponent = playerProfile.getHealthComponent();
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


            String infoSection = "&7&l Lv19[&6&l|||&8|||||||&7&l] ";
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
            cooldownSection += " &7\uD83E\uDDEA12";
            TextComponent cooldownCoomponent = LegacyComponentSerializer.legacyAmpersand().deserialize(cooldownSection);

            TextComponent ABComponent;
            if (curWard == 0){
                ABComponent = health.append(div).append(infoComponent).append(div).append(cooldownCoomponent);
            } else {
                ABComponent = health.append(Component.text(" ").append(ward).append(div).append(infoComponent).append(div).append(cooldownCoomponent));
            }
            playerAudience.sendActionBar(ABComponent);
            //Health display renderer above player
            renderCustomNametagsFor(currentPlayer, (int) curHealth, (int) curWard);

        }
    }

    private void renderCustomNametagsFor(Player player,int curHealth, int curWard){
//        TabAPI.getInstance().getNameTagManager();
//        if (TabAPI.getInstance().getNameTagManager() instanceof UnlimitedNameTagManager){
//            UnlimitedNameTagManager unm = (UnlimitedNameTagManager) TabAPI.getInstance().getNameTagManager();
//
//            String healthString = ColorUtils.translateColorCodes(DefenceTypes.HEALTH.getTextColorTag() + curHealth + DefenceTypes.HEALTH.getSpecialChar());
//            String wardString = ColorUtils.translateColorCodes(DefenceTypes.WARD.getTextColorTag() + curWard + DefenceTypes.WARD.getSpecialChar());
//            String finalString;
//            if (curWard == 0){
//                finalString = healthString;
//            } else {
//                finalString = healthString + " " + wardString;
//            }
//
//            TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
//            unm.setLine(tabPlayer, "abovename", finalString);
//        }
    }

    public static PlayerInterfaceRenderer getInstance() {
        return INSTANCE;
    }
}
