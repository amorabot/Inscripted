package com.amorabot.inscripted.player;

import com.amorabot.inscripted.combat.buffs.Buffs;
import com.amorabot.inscripted.combat.buffs.categories.BuffData;
import com.amorabot.inscripted.events.DeathEvent;
import com.amorabot.inscripted.handlers.Inventory.PlayerEquipmentHandler;
import com.amorabot.inscripted.item.inscription.definition.EffectIDs;
import com.amorabot.inscripted.item.inscription.definition.KeystoneIDs;
import com.amorabot.inscripted.item.inscription.definition.TriggerTimes;
import com.amorabot.inscripted.item.inscription.definition.TriggerTypes;
import com.amorabot.inscripted.player.profile.Profile;
import com.amorabot.inscripted.player.equipment.PlayerEquipment;
import com.amorabot.inscripted.player.profile.PlayerEvents;
import com.amorabot.inscripted.player.profile.parsing.StatParser;
import com.amorabot.inscripted.player.profile.parsing.StatPool;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.skill.casting.CastType;
import com.amorabot.inscripted.skill.casting.GlobalCooldown;
import com.amorabot.inscripted.skill.type.Aura;
import com.amorabot.inscripted.tasks.RegenerationTask;
import com.amorabot.inscripted.tasks.base.PlayerboundTask;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class PlayerDataContainer implements ProfileObserver {
    private static final boolean DEBUG_MODE = false;

    @Getter
    private static final Map<UUID, PlayerDataContainer> onlinePlayerData = new HashMap<>();


    private final UUID playerID;
    private final Profile profile;
    @Setter
    private StatPool globalStats = new StatPool(); // Fully compiled global player stats cache ( Equipment + Keystones + External stats + ...)
    private final PlayerEquipment equipment;
    private final Map<Integer,PlayerboundTask> playerboundTasks = new HashMap<>();
    private final Map<CastType, GlobalCooldown> skillCooldowns = new HashMap<>();
    private final Map<EffectIDs, Long> effectCastTimes = new HashMap<>();
//    private final Map<CastType, Skillcast.Persistent> persistentSkillInstances = new HashMap<>();
    @Getter
    private final Map<Skills, Aura> activeAuras = new HashMap<>();
    @Getter
    private final Map<Buffs, BuffData> activeBuffs = new HashMap<>();


    public PlayerDataContainer(UUID playerID){
        this.playerID = playerID;
        this.profile = new Profile();
        this.equipment = new PlayerEquipment(this);
    }
    public PlayerDataContainer(UUID playerID,Profile profileData){
        this.playerID = playerID;
        this.profile = profileData;
        this.equipment = new PlayerEquipment(this);
    }


    @Override
    public void onNotify(PlayerEvents event) {
        switch (event){
            case DEATH -> {
                Player player = Bukkit.getPlayer(playerID);
                assert player != null;
                Player killer = player.getKiller();
                DeathEvent.execute(player);

                if (killer!= null){ //Late death trigger (Can be a troll effect :D)
                    onNotify(TriggerTimes.LATE, TriggerTypes.ON_DEATH, player, new int[5]);
                }
            }
            case EQUIPMENT_CHANGE -> {
                Utils.log("Equipment change notification!");
                StatParser.buildProfile(this);
            }
            case EXTERNAL_STAT_CHANGE -> {
                /*
                 Sources of stats should notify the player using this event
                 Since external stats are calculated procedurally by StatsParser,
                 notifying about a external change and triggering a full profile
                 recompilation should be enough but not optimized.
                */
                Utils.log("Stat change!");
                // Event-specific logic
                //...
                StatParser.buildProfile(this);
            }
            case REEVALUATE_ALL_EQUIPMENT -> {
                Player targetPlayer = Bukkit.getPlayer(getPlayerID());
                Utils.log("Reloading all equipment for " + targetPlayer.getDisplayName());
                PlayerEquipmentHandler.reEquipAllSlots(targetPlayer);
            }
            default -> Utils.log("Untreated event");
        }
    }
    @Override
    public void onNotify(TriggerTimes triggerTime, TriggerTypes combatTrigger, Player target, int[] incomingDamage) {
        Player caster = Bukkit.getPlayer(playerID);
        Set<EffectIDs> playerEffects = equipment.getSpecialInscriptions().getEffects();
        triggerEffects(triggerTime,combatTrigger,playerEffects,caster,target,incomingDamage);
    }
    private void triggerEffects(TriggerTimes triggerTime, TriggerTypes triggeredEffectType, Set<EffectIDs> playerEffects,
                                Player caster, Player target, int[] incomingDamage){
        for (EffectIDs effect : playerEffects){
            if (!effect.getTrigger().equals(triggeredEffectType)){continue;}
            if (!triggerTime.equals(effect.getTriggerTime())){return;}
            effect.trigger(caster,target,incomingDamage);
            Utils.error(triggeredEffectType + " triggered: " + effect);
        }
    }


    public static void instantiatePlayer(UUID playerID, Profile profileData){
        if (onlinePlayerData.containsKey(playerID)){return;}
        onlinePlayerData.put(playerID, new PlayerDataContainer(playerID,profileData));
        new RegenerationTask(playerID).start(0, RegenerationTask.regenTimerCooldown);
    }
    public static void instantiatePlayer(UUID playerID){
        if (onlinePlayerData.containsKey(playerID)){return;}
        onlinePlayerData.put(playerID, new PlayerDataContainer(playerID));
    }
    public static PlayerDataContainer clearPlayerMemory(UUID playerID){
        return getOnlinePlayerData().remove(playerID);
    }


    //Profile & Equipment methods
    public static Map<UUID, Profile> getProfiles(){
        Map<UUID, Profile> onlineProfiles = new HashMap<>();
        getOnlinePlayerData().forEach(
                (uuid, playerDataContainer) -> {
                    onlineProfiles.put(uuid, playerDataContainer.getProfile());
                }
        );
        return onlineProfiles;
    }
    public static Profile getProfile(UUID profileID){
        return getDataContainerFor(profileID).getProfile();
    }
    public static PlayerEquipment getPlayerEquipment(UUID playerID){
        return getDataContainerFor(playerID).getEquipment();
    }
    public static PlayerDataContainer getDataContainerFor(UUID uuid){
        if (hasPlayerData(uuid)){
            return getOnlinePlayerData().get(uuid);
        }
        Utils.error("Data not instanced for ID " + uuid + ". Retrieving new data container.");
        return new PlayerDataContainer(uuid);
    }
    public static boolean hasPlayerData(UUID playerID){
        return getOnlinePlayerData().containsKey(playerID);
    }

    // Player task methods (HP & Stamina Regeneration, HP Display renderer, Player state tasks in general)
    public void addTask(PlayerboundTask newTask){
        playerboundTasks.put(newTask.getTaskId(),newTask);
    }
    public PlayerboundTask getTask(int taskID){
        return playerboundTasks.getOrDefault(taskID,null);
    }
    public void removeTask(int taskID){
        PlayerboundTask removedTask = playerboundTasks.remove(taskID);
        if (removedTask != null){
            removedTask.cancel();
        }
    }
    public void clearTasks(){
        playerboundTasks.forEach(
                (id, playerboundTask) -> {
                    playerboundTask.cancel();
                }
        );
        playerboundTasks.clear();
    }

    //Skill casting/cooldown methods
    public boolean skillcastBy(Skills skill, int cooldownModifier){
        final int baseCD = skill.getCooldownInSeconds();
        if (baseCD==0){return true;}
        CastType type = skill.getType();
        int cooldown = (baseCD*1000);
        if (cooldownModifier!=0){
            cooldown = (int) Utils.applyPercentageTo(cooldown,cooldownModifier);
        }

        long castTime = System.currentTimeMillis();
        if (!skillCooldowns.containsKey(type)){
            skillCooldowns.put(type, new GlobalCooldown(cooldown, castTime));
            return true;
        }
        //A GCD object is accessible
        GlobalCooldown skillGCD = skillCooldowns.get(type);
        if (skillGCD.canBeCast()){
            skillGCD.setBaseGCD(cooldown);
            skillGCD.setLastCastTime(castTime);
            return true;
        }
        return false;
    }
    public boolean effecTriggered(EffectIDs triggeredEffect){
        if (triggeredEffect.getCooldowInSeconds()==0){return true;}
        long castTime = System.currentTimeMillis();
        if (!effectCastTimes.containsKey(triggeredEffect)){
            effectCastTimes.put(triggeredEffect, castTime);
            return true;
        }
        long lastCastTime = effectCastTimes.get(triggeredEffect);
        long cooldownInMs = triggeredEffect.getCooldowInSeconds()*1000;
        long remainingCD = getRemainingCD(lastCastTime, cooldownInMs);
        if (DEBUG_MODE) {
            Utils.log(triggeredEffect + " remaining CD: " + remainingCD);
            Utils.log("Last cast time: " + lastCastTime + " || Effect Cooldown: " + cooldownInMs);
            Utils.log("Remaining cooldown (ms): " + remainingCD);
        }
        if (remainingCD > 0){
            return false;
        }
        //Can be triggered!
        effectCastTimes.put(triggeredEffect,castTime);
        return true;
    }

    public Long fetchAbilityRemainingCooldown(CastType type){
        if (!skillCooldowns.containsKey(type)){
            return 0L;
        }
        //The player already used a skill before, so fetch the GDC in the map
        GlobalCooldown playerGCD = skillCooldowns.get(type);
        return getRemainingCD(playerGCD.getLastCastTime(), playerGCD.getBaseGCD());
    }
    private Long getRemainingCD(long lastCastTime, long cooldownTime){
        long timeElapsed = System.currentTimeMillis() - lastCastTime;
        if (timeElapsed > cooldownTime){
            return 0L;
        } else {
            return cooldownTime - timeElapsed;
        }
    }

    public boolean hasKeystone(KeystoneIDs keystone){
        return getEquipment().getSpecialInscriptions().getKeystones().contains(keystone);
    }
    public boolean hasEffect(EffectIDs effect){
        return getEquipment().getSpecialInscriptions().getEffects().contains(effect);
    }
}
