package com.amorabot.inscripted.skills.dagger;

import com.amorabot.inscripted.skills.PlayerAbilities;
import org.bukkit.entity.Player;
import static com.amorabot.inscripted.skills.AbilityRoutines.newSwipeAttackBy;

public class DaggerBasicAttacks {


    public static void newDaggerBasicAttackBy(Player player, PlayerAbilities mappedAbility){
        boolean isMirrored = Math.random() > 0.5;
        boolean isInverted = Math.random() > 0.5;

        newSwipeAttackBy(player,mappedAbility,90, 12, 4, 1.6,
                1.1,1.1,0,0,
                160,160,160,null,0.1, 0.4,0.6,
                isMirrored, isInverted,false, 45);
        //TODO: if making a dual strike-like mirrored and inverted are key
    }
}
