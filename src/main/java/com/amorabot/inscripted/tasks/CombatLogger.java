package com.amorabot.inscripted.tasks;

import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class CombatLogger extends BukkitRunnable {

    private static final CombatLogger instance = new CombatLogger();
    private static final Map<UUID, Integer> COMBAT = new HashMap<>();
    private static final Team RED_TEAM = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getTeam(ChatColor.RED + "team");

    private CombatLogger(){
    }

    @Override
    public void run() { // Task should be run 1 times/second (May cause almost-instant 9-second CD combat in some instances, but its ok)
        for (Map.Entry<UUID, Integer> playerCombatCD : COMBAT.entrySet()){
            if (playerCombatCD.getValue() <= 0){
                removeFromCombat(playerCombatCD.getKey());
                continue;
            }
            COMBAT.put(playerCombatCD.getKey(), playerCombatCD.getValue() -1);
        }
    }

    public static void addToCombat(UUID playerID){
        Player player = Bukkit.getPlayer(playerID);
        if (player == null ){return;}
        if (COMBAT.containsKey(playerID)){
            Utils.log("Player already in combat, resetting the timer");
            COMBAT.put(playerID, 10);
            return;
        }
        COMBAT.put(playerID, 10); //10 seconds for combat to deplete
        player.setGlowing(true);
        assert RED_TEAM != null;
        RED_TEAM.addEntry(player.getDisplayName());
    }
    public static void removeFromCombat(UUID playerID){
        Player player = Bukkit.getPlayer(playerID);
        if (player == null ){return;}
        COMBAT.remove(playerID);
        player.setGlowing(false);
        assert RED_TEAM != null;
        RED_TEAM.removeEntry(player.getDisplayName());
    }

    public static CombatLogger getInstance() {
        return instance;
    }
}
