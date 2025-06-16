package com.amorabot.inscripted.player;

import com.amorabot.inscripted.handlers.Inventory.PlayerEquipmentHandler;
import com.amorabot.inscripted.player.profile.Profile;
import com.amorabot.inscripted.player.equipment.PlayerEquipment;
import com.amorabot.inscripted.player.profile.ProfileEvents;
import com.amorabot.inscripted.player.profile.parsing.StatParser;
import com.amorabot.inscripted.skill.AbilityTypes;
import com.amorabot.inscripted.skill.casting.GlobalCooldown;
import com.amorabot.inscripted.tasks.RegenerationTask;
import com.amorabot.inscripted.tasks.base.PlayerboundTask;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class PlayerDataContainer implements Observer {

    @Getter
    private static final Map<UUID, PlayerDataContainer> onlinePlayerData = new HashMap<>();


    private final UUID playerID;
    private final Profile profile;
    private final PlayerEquipment equipment;
    private final Map<Integer,PlayerboundTask> playerboundTasks = new HashMap<>();
    private final Map<AbilityTypes, GlobalCooldown> skillCooldowns = new HashMap<>();

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
    public void onNotify(ProfileEvents event) {
        switch (event){
            case STAT_CHANGE -> {
                Utils.log("Equipment change notification!");
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

    // Player task methods
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

}
