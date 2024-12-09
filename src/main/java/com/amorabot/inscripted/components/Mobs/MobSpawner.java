package com.amorabot.inscripted.components.Mobs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MobSpawner {

    private final int mobLimit;
    private final Map<UUID, InscriptedMob> mobData = new HashMap<>();


    public MobSpawner(int mobLimit){
        this.mobLimit = mobLimit;
    }
}
