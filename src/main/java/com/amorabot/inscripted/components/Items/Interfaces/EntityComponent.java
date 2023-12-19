package com.amorabot.inscripted.components.Items.Interfaces;

import com.amorabot.inscripted.components.Player.*;

import java.io.Serializable;

public interface EntityComponent extends Serializable {
    void reset();
    void update(Profile profileData);
}
