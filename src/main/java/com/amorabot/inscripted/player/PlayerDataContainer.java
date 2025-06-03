package com.amorabot.inscripted.player;

import com.amorabot.inscripted.player.profile.Profile;
import com.amorabot.inscripted.player.equipment.PlayerEquipment;

public class PlayerDataContainer {

    private final Profile profile;
    private final PlayerEquipment equipment;

    public PlayerDataContainer(){
        this.profile = new Profile();
        this.equipment = new PlayerEquipment();
    }
}
