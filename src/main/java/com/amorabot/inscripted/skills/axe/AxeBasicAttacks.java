package com.amorabot.inscripted.skills.axe;

import com.amorabot.inscripted.skills.PlayerAbilities;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import static com.amorabot.inscripted.skills.AbilityRoutines.newSwipeAttackBy;

public class AxeBasicAttacks {
    public static void newAxeBasicAttackBy(Player player, PlayerAbilities mappedAbility){
        boolean isMirrored = Math.random() > 0.5;
        boolean isInverted = Math.random() > 0.5;

        newSwipeAttackBy(player,mappedAbility,
                120, 24, 4,
                2.2,0.3,1.4,
                -0.3, 0.3,
                176, 126, 111, Particle.SMOKE, 0.45, 0.7, 1,
                isMirrored, isInverted, true, 10, 40);
    }
}
