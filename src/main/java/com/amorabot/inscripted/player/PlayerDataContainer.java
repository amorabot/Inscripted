package com.amorabot.inscripted.player;

import com.amorabot.inscripted.player.profile.Profile;
import com.amorabot.inscripted.player.equipment.PlayerEquipment;
import com.amorabot.inscripted.player.profile.ProfileEvents;
import com.amorabot.inscripted.player.profile.parsing.StatParser;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PlayerDataContainer implements Observer {

    @Getter
    private static final Map<UUID, PlayerDataContainer> onlinePlayerData = new HashMap<>();


    private final UUID playerID;
    private final Profile profile;
    private final PlayerEquipment equipment;

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

            }
            default -> Utils.log("Untreated event");
        }
    }


    public static void instantiatePlayer(UUID playerID, Profile profileData){
        if (onlinePlayerData.containsKey(playerID)){return;}
        onlinePlayerData.put(playerID, new PlayerDataContainer(playerID,profileData));
    }
    public static void instantiatePlayer(UUID playerID){
        if (onlinePlayerData.containsKey(playerID)){return;}
        onlinePlayerData.put(playerID, new PlayerDataContainer(playerID));
    }
    public static PlayerDataContainer clearPlayerMemory(UUID playerID){
        return getOnlinePlayerData().remove(playerID);
    }



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
        return getOnlinePlayerData().getOrDefault(uuid,new PlayerDataContainer(uuid));
    }
}
