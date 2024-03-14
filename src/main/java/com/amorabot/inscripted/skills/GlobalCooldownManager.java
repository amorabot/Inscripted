package com.amorabot.inscripted.skills;

import com.amorabot.inscripted.APIs.MessageAPI;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GlobalCooldownManager {

    private static Map<UUID, Long> playerCooldown = new HashMap<>();
    private static Map<SkillTypes, Map<UUID, Long>> skillGDCData = new HashMap<>();

    public static void setup(){
        for (SkillTypes skills : SkillTypes.values()){
            skillGDCData.put(skills, new HashMap<>());
        }
    }


    public static boolean skillcastBy(UUID playerID, SkillTypes skillTypeUsed, long GCDcooldown){
        Long remainingCD = fetchRemainingCooldownFor(playerID, skillTypeUsed);
        if (remainingCD > 0){
            return false;
        }
        skillGDCData.get(skillTypeUsed).put(playerID, GCDcooldown*1000);
        playerCooldown.put(playerID, System.currentTimeMillis());
        return true;
    }

    public static Long fetchRemainingCooldownFor(UUID playerID, SkillTypes skill){
        Map<UUID, Long> skillGDCMap = skillGDCData.get(skill);
        if (!skillGDCMap.containsKey(playerID)){
            return 0L;
        }
        //The player already used a skill before, so fetch the GDC in the map
        Long skillGCD = skillGDCData.get(skill).get(playerID);
        long timeElapsed = System.currentTimeMillis() - playerCooldown.get(playerID);
        if (timeElapsed > skillGCD){
            return 0L;
        } else {
            return skillGCD - timeElapsed;
        }
    }

}
