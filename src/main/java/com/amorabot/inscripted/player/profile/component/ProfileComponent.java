package com.amorabot.inscripted.player.profile.component;

import com.amorabot.inscripted.item.inscription.definition.Stats;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProfileComponent {
    void updateComponent(UUID playerID,Map<Stats, double[]> finalStats);
    List<Component> asTextComponent();

    default int getSingleValueFrom(Stats stat, Map<Stats, double[]> finalStats){
        double[] statValues = finalStats.get(stat);
        if (statValues==null){return 0;}
        return (int) statValues[0];
    }
}
