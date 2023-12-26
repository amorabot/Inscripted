package com.amorabot.inscripted.tasks;

import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.Utils;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.nametag.UnlimitedNameTagManager;
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
            float dps = playerProfile.getDamageComponent().getHitData().getDPS();
            String stamina = String.valueOf(playerProfile.getMiscellaneous().getStamina());
            char facing = currentPlayer.getFacing().toString().charAt(0);

            Audience playerAudience = Audience.audience(currentPlayer);
            playerAudience.sendActionBar(LegacyComponentSerializer.legacyAmpersand().deserialize("&8&l " + facing + "  &7" + dps + "     &2&l"+ stamina));

            Component playerStatComponent = Component.text()
                    .append(Component.text("[1]War 67%   ").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                    .build();

            Component healthText = Component.text()
                    .append(Component.text((int) curHealth + "/" + (int) maxHealth + " ")
                            .decorate(TextDecoration.BOLD))
                    .append(Component.text(DefenceTypes.HEALTH.getSpecialChar()))
                    .color(TextColor.fromHexString(healthHex))
                    .append(Component.text("   "))
                    .build();

            Component wardText = Component.text()
                    .append(Component.text((int) curWard + "/" + (int) maxWard)
                            .color(TextColor.fromHexString(wardHex))
                            .decorate(TextDecoration.BOLD))
                    .append(Component.text(DefenceTypes.WARD.getSpecialChar()))
                    .color(TextColor.fromHexString(wardHex))
                    .build();

            Component mainText = playerStatComponent.append(healthText).append(wardText);

            if (TabAPI.getInstance().getNameTagManager() instanceof UnlimitedNameTagManager){
                UnlimitedNameTagManager unm = (UnlimitedNameTagManager) TabAPI.getInstance().getNameTagManager();

                String healthString = ColorUtils.translateColorCodes(DefenceTypes.HEALTH.getTextColor() + (int) curHealth + DefenceTypes.HEALTH.getSpecialChar());
                String wardString = ColorUtils.translateColorCodes(DefenceTypes.WARD.getTextColor() + (int) curWard + DefenceTypes.WARD.getSpecialChar());
                String finalString;
                if (curWard == 0){
                    finalString = healthString;
                } else {
                    finalString = healthString + " " + wardString;
                }

                TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(currentPlayer.getUniqueId());
                unm.setLine(tabPlayer, "abovename", finalString);
            }

            BossBar HPBossbar = PlayerInterfaceRenderer.getHPBossbar(currentPlayer);
            if (HPBossbar != null){
                HPBossbar.name(mainText);
                if (healthComponent.getCurrentWard()>0){
                    HPBossbar.color(BossBar.Color.BLUE);
                    HPBossbar.progress(healthComponent.getNormalizedWard());
                } else {
                    HPBossbar.color(BossBar.Color.RED);
                    HPBossbar.progress(healthComponent.getNormalizedHP());
                }
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
        String hpKey = player.getUniqueId()+HP_KEY;
        if (PLAYERS_BOSS_BARS.containsKey(hpKey)){
            BossBar hpBar = PLAYERS_BOSS_BARS.get(hpKey);
            player.hideBossBar(hpBar);
        }
    }

    public static void shutdownAllBars(){
        for (Player currentPlayer : Bukkit.getOnlinePlayers()){
            hideHPBossBar(currentPlayer);
            deleteBossBars(currentPlayer);
        }
    }

    public static void createBossbars(Player player){
        UUID playerID = player.getUniqueId();
        //Boss bar
        Component bossBarText = Component.text("Setting bossbars...");
        BossBar HPBossBar = BossBar.bossBar(bossBarText, 0.5F, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        PLAYERS_BOSS_BARS.put(playerID+HP_KEY, HPBossBar);
        Utils.log("Bossbars created for: " + playerID);
    }

    public static void deleteBossBars(Player player){
        UUID playerID = player.getUniqueId();
        PLAYERS_BOSS_BARS.remove(playerID+HP_KEY);
    }

    public static void startupBossBars(Player player){
        PlayerInterfaceRenderer.createBossbars(player);
        Audience playerAudience = Audience.audience(player);
        BossBar HPBossbar = PlayerInterfaceRenderer.getHPBossbar(player);
        if (HPBossbar != null){
            playerAudience.showBossBar(HPBossbar);
        }
    }


    public static PlayerInterfaceRenderer getInstance() {
        return INSTANCE;
    }
}
