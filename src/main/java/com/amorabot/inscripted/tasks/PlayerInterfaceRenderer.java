package com.amorabot.inscripted.tasks;

import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerInterfaceRenderer extends BukkitRunnable {

    private static final PlayerInterfaceRenderer INSTANCE = new PlayerInterfaceRenderer();
    private static final Map<String, BossBar> PLAYERS_BOSS_BARS = new HashMap<>();
    private static final String HP_KEY = "_HEALTH";
    private static final String WARD_KEY = "_WARD";

    private PlayerInterfaceRenderer(){
    }
    @Override
    public void run() {
        for (Player currentPlayer : Bukkit.getOnlinePlayers()){
            Profile playerProfile = JSONProfileManager.getProfile(currentPlayer.getUniqueId());
            HealthComponent healthComponent = playerProfile.getHealthComponent();
            float maxHealth = healthComponent.getMaxHealth();
            float curHealth = healthComponent.getCurrentHealth();
            String healthHex = DefenceTypes.HEALTH.getTextColor().replace("&", "");
            float maxWard = healthComponent.getMaxWard();
            float curWard = healthComponent.getCurrentWard();
            String wardHex = DefenceTypes.WARD.getTextColor().replace("&", "");
            float dps = playerProfile.getDamageComponent().getDPS();
            String stamina = String.valueOf(playerProfile.getDamageComponent().getStamina());
            char facing = currentPlayer.getFacing().toString().charAt(0);

            Audience playerAudience = Audience.audience((Audience) currentPlayer);
            playerAudience.sendActionBar(LegacyComponentSerializer.legacyAmpersand().deserialize("&8&l " + facing + "  &7" + dps + "     &2&l"+ stamina));

            Component mainText = Component.text()
                    .append(Component.text("[1]War 67%   ").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                    .append(Component.text((int) curHealth + "/" + (int) maxHealth + " ")
                            .decorate(TextDecoration.BOLD))
                    .append(Component.text(DefenceTypes.HEALTH.getSpecialChar()))
                    .color(TextColor.fromHexString(healthHex))
                    .build();

            Component wardText = Component.text()
                    .append(Component.text((int) curWard + "/" + (int) maxWard)
                            .color(TextColor.fromHexString(wardHex))
                            .decorate(TextDecoration.BOLD))
                    .append(Component.text(DefenceTypes.WARD.getSpecialChar()))
                    .color(TextColor.fromHexString(wardHex))
                    .build();


            BossBar HPBossbar = PlayerInterfaceRenderer.getHPBossbar(currentPlayer);
            BossBar WardBossbar = PlayerInterfaceRenderer.getWardBossbar(currentPlayer);
            if (HPBossbar != null){
                HPBossbar.name(mainText);
                HPBossbar.progress(healthComponent.getNormalizedHP());
            }
            if (WardBossbar != null){
                WardBossbar.name(wardText);
            }
        }
    }

    public static BossBar getHPBossbar(Player player){
        if (PLAYERS_BOSS_BARS.containsKey(player.getUniqueId()+HP_KEY)){
            return PLAYERS_BOSS_BARS.get(player.getUniqueId()+HP_KEY);
        }
        return null;
    }
    public static void hideHPBossBar(Player player){
        Audience playerAudience = (Audience) player;
        String hpKey = player.getUniqueId()+HP_KEY;
        if (PLAYERS_BOSS_BARS.containsKey(hpKey)){
            BossBar hpBar = PLAYERS_BOSS_BARS.get(hpKey);
            playerAudience.hideBossBar(hpBar);
        }
    }
    public static void hideWardBossBar(Player player){
        Audience playerAudience = (Audience) player;
        String wardKey = player.getUniqueId()+WARD_KEY;
        if (PLAYERS_BOSS_BARS.containsKey(wardKey)){
            BossBar wardBar = PLAYERS_BOSS_BARS.get(wardKey);
            playerAudience.hideBossBar(wardBar);
        }
    }
    public static BossBar getWardBossbar(Player player){
        if (PLAYERS_BOSS_BARS.containsKey(player.getUniqueId()+WARD_KEY)){
            return PLAYERS_BOSS_BARS.get(player.getUniqueId()+WARD_KEY);
        }
        return null;
    }

    public static void shutdownAllBars(){
        for (Player currentPlayer : Bukkit.getOnlinePlayers()){
            hideHPBossBar(currentPlayer);
            hideWardBossBar(currentPlayer);
            deleteBossBars(currentPlayer);
        }
    }

    public static void createBossbars(Player player){
        UUID playerID = player.getUniqueId();
        //Boss bar
        Component bossBarText = Component.text("Setting bossbars...");
        BossBar HPBossBar = BossBar.bossBar(bossBarText, 0.5F, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        PLAYERS_BOSS_BARS.put(playerID+HP_KEY, HPBossBar);
        BossBar WardBossBar = BossBar.bossBar(bossBarText, 0.5F, BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_6);
        PLAYERS_BOSS_BARS.put(playerID+WARD_KEY, WardBossBar);
        Utils.log("Bossbars created for: " + playerID);
    }

    public static void deleteBossBars(Player player){
        UUID playerID = player.getUniqueId();
        PLAYERS_BOSS_BARS.remove(playerID+HP_KEY);
        PLAYERS_BOSS_BARS.remove(playerID+WARD_KEY);
    }

    public static void startupBossBars(Player player){
        PlayerInterfaceRenderer.createBossbars(player);
        Audience playerAudience = Audience.audience((Audience) player);
        BossBar HPBossbar = PlayerInterfaceRenderer.getHPBossbar(player);
        BossBar WardBossBar = PlayerInterfaceRenderer.getWardBossbar(player);
        if (HPBossbar != null){
            playerAudience.showBossBar(HPBossbar);
        }
        if (WardBossBar != null){
            playerAudience.showBossBar(WardBossBar);
        }
    }


    public static PlayerInterfaceRenderer getInstance() {
        return INSTANCE;
    }
}
