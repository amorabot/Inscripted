package com.amorabot.inscripted.tasks;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.utils.Utils;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.nametag.NameTagManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CombatLogger extends BukkitRunnable {

    //Make separate maps for NEUTRAL and CHAOTIC tracking

    private static final int COMBAT_TIMER = 10;
    private static final String COMBAT_TAG = "onCombat";

    private static final int PVP_COMBAT_TIMER = 30;
    private static final String PVP_TAG = "PvP";

    private static final CombatLogger instance = new CombatLogger();
    private static final ConcurrentHashMap<UUID, Integer> COMBAT = new ConcurrentHashMap<>();

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

    public static void addToCombat(Player player){
        if (player == null ){return;}
        UUID playerID = player.getUniqueId();
        if (COMBAT.containsKey(playerID)){
//            Utils.log( player.getName() + " already in combat, resetting the timer");
            COMBAT.put(playerID, COMBAT_TIMER);
            return;
        }
        COMBAT.put(playerID, COMBAT_TIMER); //COMBAT_TIMER seconds for combat to deplete
        player.setMetadata(COMBAT_TAG, Inscripted.getPlugin().getMetadataTag());
        setCombatTag(player, false);
    }

    public static void addToPvPCombat(Player attacker, Player defender){
        if ((attacker == null) || (defender == null) ){return;}
        //If any of the players involved are already in combat, just reset their combat timers
        //And check if they aready got the pvp tag
        if (attacker.hasMetadata(PVP_TAG) && defender.hasMetadata(PVP_TAG)){
            resetPvPStatusFor(attacker);
            resetPvPStatusFor(defender);
            return;
        }

        if (!defender.hasMetadata(PVP_TAG)){
            resetPvPStatusFor(attacker);
            //Not needed since defender already took a hit and got tagged via DamageAPI
//            addToCombat(defender);
        } else {
            addToCombat(attacker);
            resetPvPStatusFor(defender);
        }

    }

    public static void removeFromCombat(UUID playerID){
        Player player = Bukkit.getPlayer(playerID);
        if (player == null ){return;}
        Utils.log("removing " + playerID + " from combat");
        COMBAT.remove(playerID);
        player.removeMetadata(COMBAT_TAG, Inscripted.getPlugin());
        if (player.hasMetadata(PVP_TAG)){
            player.removeMetadata(PVP_TAG, Inscripted.getPlugin());
        }

//        UnlimitedNameTagManager unm = (UnlimitedNameTagManager) TabAPI.getInstance().getNameTagManager();
        TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(playerID);
        NameTagManager ntm = TabAPI.getInstance().getNameTagManager();
        assert tabPlayer != null;
        assert ntm != null;
        ntm.setSuffix(tabPlayer, "");
//        assert unm != null;
//        unm.setSuffix(tabPlayer,unm.getOriginalSuffix(tabPlayer));
//        unm.setName(tabPlayer, unm.getOriginalName(tabPlayer));
    }

    public static boolean isInCombat(Player player){
        UUID PUUID = player.getUniqueId();
        return COMBAT.containsKey(PUUID);
    }

    private static void setCombatTag(Player player, boolean isPvP){
//        UnlimitedNameTagManager unm = (UnlimitedNameTagManager) TabAPI.getInstance().getNameTagManager();
//        TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
//        assert unm != null;
//        //TODO: check if changes are actually needed on this call
//        unm.setSuffix(tabPlayer," &f⚔");
        TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
        NameTagManager ntm = TabAPI.getInstance().getNameTagManager();
        assert tabPlayer != null;
        assert ntm != null;
        if (isPvP){
            ntm.setSuffix(tabPlayer," &e⚔");
            return;
        }
        ntm.setSuffix(tabPlayer," &f⚔");
    }
//    private static void setNeutralNametag(Player player){
////        UnlimitedNameTagManager unm = (UnlimitedNameTagManager) TabAPI.getInstance().getNameTagManager();
////        TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
////        assert unm != null;
////        unm.setName(tabPlayer,"&e"+unm.getOriginalName(tabPlayer));
//        TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
//        NameTagManager ntm = TabAPI.getInstance().getNameTagManager();
//        assert tabPlayer != null;
//        assert ntm != null;
//        ntm.setSuffix(tabPlayer," &e⚔");
//        //If the name has not changed, change it
//    }

    private static void resetPvPStatusFor(Player player){
        COMBAT.put(player.getUniqueId(), PVP_COMBAT_TIMER);
        if (!player.hasMetadata(PVP_TAG)){
            player.setMetadata(PVP_TAG, Inscripted.getPlugin().getMetadataTag());
        }
        if (!player.hasMetadata(COMBAT_TAG)){
            player.setMetadata(COMBAT_TAG, Inscripted.getPlugin().getMetadataTag());
        }
        //Adjusting the pvp player's nametag
        setCombatTag(player, true);
//        setNeutralNametag(player);
    }

    public static CombatLogger getInstance() {
        return instance;
    }
    public static String getCombatTag(){
        return COMBAT_TAG;
    }
    public static String getPvpTag(){
        return PVP_TAG;
    }
}
