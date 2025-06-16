package com.amorabot.inscripted.skill.archetypes.axe;

import com.amorabot.inscripted.skill.PlayerAbilities;
import com.amorabot.inscripted.skill.attackInstances.slash.Slash;
import com.amorabot.inscripted.skill.attackInstances.slash.SlashPresets;
import com.amorabot.inscripted.skill.type.Attack;
import com.amorabot.inscripted.tasks.base.Skillcast;
import org.bukkit.entity.Player;

public class AxeBasicAttacks {
    public static void standardAxeSlashBy(Player player, PlayerAbilities mappedAbility){
        boolean isMirrored = Math.random() > 0.5;
        boolean isInverted = Math.random() > 0.5;
//        Slash slash = new Slash(player, SlashPresets.STANDARD_AXE.getSlashConfigData(), false,
//                isMirrored,isInverted,true, 10,40);
//        slash.execute();
    }

    public static void standardAxeSlashBy(Skillcast skillcastInstance){
        if (!(skillcastInstance instanceof Attack.Basic basicAttackInstance)){return;}
        boolean isMirrored = Math.random() > 0.5;
        boolean isInverted = Math.random() > 0.5;
        new Slash(skillcastInstance, basicAttackInstance.getAttackData(), SlashPresets.STANDARD_AXE.getSlashConfigData(), false,
                skillcastInstance.getPlayer().getLocation(),isMirrored,isInverted,true, 10,40);
    }
}
