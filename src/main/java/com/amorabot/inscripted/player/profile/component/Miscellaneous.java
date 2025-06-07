package com.amorabot.inscripted.player.profile.component;

import com.amorabot.inscripted.item.inscription.definition.Stats;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Miscellaneous implements ProfileComponent {
    @Override
    public void updateComponent(UUID playerID, Map<Stats, double[]> finalStats) {

    }

    @Override
    public List<Component> asTextComponent() {
        return List.of();
    }
    /*
    The final walkSpeed stat reflects the % multiplier that is applied to the base player's movement speed
    Ex:  100 (Base) MS = 0.2  player speed
         169 (100 + 54) * 1.1 => 169% base MS,   1,69 multiplier overall to the base 0.2 MS => 0.3388

    Input ->  min -1 | max 1
    Default speed value for players: 0.2 (EMPIRIC FUCKING VALUE)  (https://minecraft.wiki/w/Attribute)
     */
}
