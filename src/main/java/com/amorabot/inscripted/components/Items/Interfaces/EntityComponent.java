package com.amorabot.inscripted.components.Items.Interfaces;

import java.io.Serializable;
import java.util.UUID;

public interface EntityComponent extends Serializable {
    void update(UUID profileID);
}
