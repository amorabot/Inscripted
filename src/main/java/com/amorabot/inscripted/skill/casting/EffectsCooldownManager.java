//package com.amorabot.inscripted.skill.casting;
//
//import com.amorabot.inscripted.components.Items.relic.enums.Effects;
//import com.amorabot.inscripted.utils.Utils;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//public class EffectsCooldownManager {
//
//    private static final boolean debugMode = false;
//
//    private static final Map<Effects, Map<UUID, Long>> effectCastTimes = new HashMap<>();
//
//    public static boolean effecTriggered(UUID entityID, Effects triggeredEffect){
//        if (triggeredEffect.getCooldownInSeconds()==0){return true;}
//        if (debugMode){debugEffectCooldowns();}
//        long castTime = System.currentTimeMillis();
//        if (!effectCastTimes.containsKey(triggeredEffect)){
//            if (debugMode) {
//                Utils.log("Initializing " + triggeredEffect);
//            }
//            Map<UUID, Long> effectCDMap = new HashMap<>();
//            effectCDMap.put(entityID, castTime);
//            effectCastTimes.put(triggeredEffect, effectCDMap);
//            return true;
//        }
//        if (!effectCastTimes.get(triggeredEffect).containsKey(entityID)){
//            if (debugMode) {
//                Utils.log("First " + triggeredEffect + " cast for " + entityID);
//            }
//            effectCastTimes.get(triggeredEffect).put(entityID,castTime);
//            return true;
//        }
//        long lastCastTime = effectCastTimes.get(triggeredEffect).get(entityID);
//        long cooldownInMs = triggeredEffect.getCooldownInSeconds()*1000;
//        long remainingCD = getRemainingCD(lastCastTime, cooldownInMs);
//        if (debugMode) {
//            Utils.log(triggeredEffect + " remaining CD: " + remainingCD);
//            Utils.log("Last cast time: " + lastCastTime + " || Effect Cooldown: " + cooldownInMs);
//            Utils.log("Remaining cooldown (ms): " + remainingCD);
//        }
//        if (remainingCD > 0){
//            return false;
//        }
//        //Can be triggered!
//        effectCastTimes.get(triggeredEffect).put(entityID,castTime);
//        return true;
//    }
//    private static Long getRemainingCD(long lastCastTime, long cooldownTime){
//        long timeElapsed = System.currentTimeMillis() - lastCastTime;
////        Utils.error("time elapsed: " + timeElapsed);
//        if (timeElapsed > cooldownTime){
//            return 0L;
//        } else {
//            return cooldownTime - timeElapsed;
//        }
//    }
//    public static Long getRemainingCDFor(UUID playerID, Effects effect){
//        if (!effectCastTimes.get(effect).containsKey(playerID)){return -1L;}
//        return getRemainingCD(effectCastTimes.get(effect).get(playerID),effect.getCooldownInSeconds()*1000);
//    }
//    private static void debugEffectCooldowns(){
//        effectCastTimes.keySet().forEach( effect -> {
//            Map<UUID, Long> cdMap = effectCastTimes.get(effect);
//            cdMap.keySet().forEach( uuid ->{
//                Utils.error("Effect: " + effect + " CD -> " + cdMap.get(uuid) + "("+uuid+")");
//            });
//        });
//    }
//}
