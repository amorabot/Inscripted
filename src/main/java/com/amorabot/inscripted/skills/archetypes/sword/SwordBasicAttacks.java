package com.amorabot.inscripted.skills.archetypes.sword;

import com.amorabot.inscripted.skills.PlayerAbilities;
import com.amorabot.inscripted.skills.attackInstances.slash.Slash;
import com.amorabot.inscripted.skills.attackInstances.slash.SlashConfigDTO;
import com.amorabot.inscripted.skills.attackInstances.slash.SlashSegment;
import org.bukkit.entity.Player;
//import static com.amorabot.inscripted.skills.AbilityRoutines.newSwipeAttackBy;

public class SwordBasicAttacks {

    public static void standardSwordSlashBy(Player player, PlayerAbilities mappedAbility){
        boolean isMirrored = Math.random() > 0.5;
        boolean isInverted = Math.random() > 0.5;
        SlashConfigDTO slashConfig = new SlashConfigDTO(
                20,100,2,-0.2, 0.1,
                0.3,1.2, new int[]{173, 143, 130}, 0.7F, 0.2
        );

        Slash slash = new Slash(player, mappedAbility,slashConfig,
                isMirrored,isInverted,false, SlashSegment::standardSword, 30);

        slash.execute();
    }
}
