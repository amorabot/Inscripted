package com.amorabot.inscripted.components.Items.Interfaces;

import com.amorabot.inscripted.components.Player.*;

import java.io.Serializable;
import java.util.UUID;

public interface EntityComponent extends Serializable {
    void reset();
    void update(UUID profileID);
}
