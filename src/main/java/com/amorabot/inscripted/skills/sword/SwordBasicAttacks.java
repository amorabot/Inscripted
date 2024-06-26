package com.amorabot.inscripted.skills.sword;

import com.amorabot.inscripted.skills.PlayerAbilities;
import org.bukkit.entity.Player;
import static com.amorabot.inscripted.skills.AbilityRoutines.newSwipeAttackBy;

public class SwordBasicAttacks {

    public static void newSwordBasicAttackBy(Player player, PlayerAbilities mappedAbility){
        boolean isMirrored = Math.random() > 0.5;
        boolean isInverted = Math.random() > 0.5;

        newSwipeAttackBy(player,mappedAbility,
                100, 20, 4,
                2,0.3,1.2,
                -0.2, 0.1,
                173, 143, 130, null, 0.2, 0.7,1,
                isMirrored, isInverted,false, 30);
    }
}
