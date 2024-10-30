package com.amorabot.inscripted.managers;

import com.amorabot.inscripted.skills.casting.CasterState;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CasterStateManager {

    private static final Map<UUID, CasterState> playerCasterStateMap = new HashMap<>();


    public static void initializePlayer(Player player){
        UUID playerID = player.getUniqueId();
        if (playerCasterStateMap.containsKey(playerID)){return;}
        playerCasterStateMap.put(playerID, new CasterState());
    }

}
