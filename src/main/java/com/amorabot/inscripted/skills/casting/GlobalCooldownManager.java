package com.amorabot.inscripted.skills.casting;

import com.amorabot.inscripted.components.Items.relic.enums.Effects;
import com.amorabot.inscripted.skills.AbilityTypes;
import com.amorabot.inscripted.skills.PlayerAbilities;
import com.amorabot.inscripted.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GlobalCooldownManager {

    private static final boolean debugMode = false;

    private static final Map<AbilityTypes, Map<UUID, GlobalCooldown>> abilitiesGDC = new HashMap<>();
    private static final Map<Effects, Map<UUID, Long>> effectCastTimes = new HashMap<>();


    public static boolean skillcastBy(UUID playerID, PlayerAbilities abilityUsed, int cooldownModifier){
        if (abilityUsed.getCooldownInSeconds()==0){return true;}
        AbilityTypes type = abilityUsed.getType();
        int cooldown = (int) (abilityUsed.getCooldownInSeconds()*1000);
//        Utils.log("Original CD:" + cooldown);
        if (cooldownModifier!=0){
            cooldown = (int) Utils.applyPercentageTo(cooldown,cooldownModifier);
        }
//        Utils.log("New CD:" + cooldown);

        long castTime = System.currentTimeMillis();
        if (!abilitiesGDC.containsKey(type)){
            Map<UUID, GlobalCooldown> playerCDMap = new HashMap<>();
            playerCDMap.put(playerID, new GlobalCooldown(cooldown, castTime));
            abilitiesGDC.put(type,playerCDMap);
            return true;
        }
        //The ability being cast is accessible via the GCD map
        if (!abilitiesGDC.get(type).containsKey(playerID)){
            //Instantiate a new GCD object to be managed for that player
            abilitiesGDC.get(type).put(playerID, new GlobalCooldown(cooldown, castTime));
            return true;
        }
        //A GCD object is accessible
        GlobalCooldown playerGCD = abilitiesGDC.get(type).get(playerID);
        if (playerGCD.canBeCast()){
            playerGCD.setBaseGCD(cooldown);
            playerGCD.setLastCastTime(castTime);
            return true;
        }
        return false;
    }

    public static Long fetchAbilityRemainingCooldown(UUID playerID, AbilityTypes abilityType){
        Map<UUID, GlobalCooldown> GCDMap = abilitiesGDC.get(abilityType);
        if (GCDMap == null){
            return 0L;
        }

        if (!GCDMap.containsKey(playerID)){
            return 0L;
        }
        //The player already used a skill before, so fetch the GDC in the map
        GlobalCooldown playerGCD = abilitiesGDC.get(abilityType).get(playerID);
        return getRemainingCD(playerGCD.getLastCastTime(), playerGCD.getBaseGCD());
    }

    public static boolean effecTriggered(UUID entityID, Effects triggeredEffect){
        if (triggeredEffect.getCooldownInSeconds()==0){return true;}
        if (debugMode){debugEffectCooldowns();}
        long castTime = System.currentTimeMillis();
        if (!effectCastTimes.containsKey(triggeredEffect)){
            if (debugMode) {
                Utils.log("Initializing " + triggeredEffect);
            }
            Map<UUID, Long> effectCDMap = new HashMap<>();
            effectCDMap.put(entityID, castTime);
            effectCastTimes.put(triggeredEffect, effectCDMap);
            return true;
        }
        if (!effectCastTimes.get(triggeredEffect).containsKey(entityID)){
            if (debugMode) {
                Utils.log("First " + triggeredEffect + " cast for " + entityID);
            }
            effectCastTimes.get(triggeredEffect).put(entityID,castTime);
            return true;
        }
        long lastCastTime = effectCastTimes.get(triggeredEffect).get(entityID);
        long cooldownInMs = triggeredEffect.getCooldownInSeconds()*1000;
        long remainingCD = getRemainingCD(lastCastTime, cooldownInMs);
        if (debugMode) {
            Utils.log(triggeredEffect + " remaining CD: " + remainingCD);
            Utils.log("Last cast time: " + lastCastTime + " || Effect Cooldown: " + cooldownInMs);
            Utils.log("Remaining cooldown (ms): " + remainingCD);
        }
        if (remainingCD > 0){
            return false;
        }
        //Can be triggered!
        effectCastTimes.get(triggeredEffect).put(entityID,castTime);
        return true;
    }
    private static Long getRemainingCD(long lastCastTime, long cooldownTime){
        long timeElapsed = System.currentTimeMillis() - lastCastTime;
//        Utils.error("time elapsed: " + timeElapsed);
        if (timeElapsed > cooldownTime){
            return 0L;
        } else {
            return cooldownTime - timeElapsed;
        }
    }
    public static Long getRemainingCDFor(UUID playerID, Effects effect){
        if (!effectCastTimes.get(effect).containsKey(playerID)){return -1L;}
        return getRemainingCD(effectCastTimes.get(effect).get(playerID),effect.getCooldownInSeconds()*1000);
    }
    private static void debugEffectCooldowns(){
        effectCastTimes.keySet().forEach( effect -> {
            Map<UUID, Long> cdMap = effectCastTimes.get(effect);
            cdMap.keySet().forEach( uuid ->{
                Utils.error("Effect: " + effect + " CD -> " + cdMap.get(uuid) + "("+uuid+")");
            });
        });
    }
}
