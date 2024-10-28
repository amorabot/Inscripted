package com.amorabot.inscripted.skills.archetypes.axe;

import com.amorabot.inscripted.skills.PlayerAbilities;
import com.amorabot.inscripted.skills.attackInstances.slash.Slash;
import com.amorabot.inscripted.skills.attackInstances.slash.SlashConfigDTO;
import com.amorabot.inscripted.skills.attackInstances.slash.SlashSegment;
//import org.bukkit.Particle;
import org.bukkit.entity.Player;
//import static com.amorabot.inscripted.skills.AbilityRoutines.newSwipeAttackBy;

public class AxeBasicAttacks {
    public static void standardAxeSlashBy(Player player, PlayerAbilities mappedAbility){
        boolean isMirrored = Math.random() > 0.5;
        boolean isInverted = Math.random() > 0.5;
        SlashConfigDTO slashConfig = new SlashConfigDTO(
                24,120,2.2,-0.3, 0.3,
                0.4,1.4, new int[]{176, 126, 111}, 0.85F, 0.45
        );

        Slash slash = new Slash(player, mappedAbility,slashConfig,
                isMirrored,isInverted,true, SlashSegment::standardAxe, 10,40);

        slash.execute();
    }
}
