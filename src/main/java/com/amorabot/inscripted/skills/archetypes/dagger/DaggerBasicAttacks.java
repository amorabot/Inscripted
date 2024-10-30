package com.amorabot.inscripted.skills.archetypes.dagger;

import com.amorabot.inscripted.skills.PlayerAbilities;
import com.amorabot.inscripted.skills.attackInstances.slash.Slash;
import com.amorabot.inscripted.skills.attackInstances.slash.SlashConfigDTO;
import com.amorabot.inscripted.skills.attackInstances.slash.SlashSegment;
import org.bukkit.entity.Player;
//import static com.amorabot.inscripted.skills.AbilityRoutines.newSwipeAttackBy;

public class DaggerBasicAttacks {

    public static void standardDaggerSlashBy(Player player, PlayerAbilities mappedAbility){
        boolean isMirrored = Math.random() > 0.5;
        boolean isInverted = Math.random() > 0.5;
        SlashConfigDTO slashConfig = new SlashConfigDTO(
                12,90,1.6,0, 0,
                1.1,1.1, new int[]{140,140,140}, null, 0.5F, 0.1
        );

        Slash slash = new Slash(player, mappedAbility,slashConfig,
                isMirrored,isInverted,false, SlashSegment::standardSword, 45);

        slash.execute();
    }
}
