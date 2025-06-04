package com.amorabot.inscripted.player;

import com.amorabot.inscripted.player.profile.ProfileEvents;

public interface Observer {
    void onNotify(ProfileEvents event);
}
