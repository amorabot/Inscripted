package com.amorabot.inscripted.skill.archetypes.axe;

import com.amorabot.inscripted.skill.routine.slash.Slash;
import com.amorabot.inscripted.skill.routine.slash.SlashPresets;
import com.amorabot.inscripted.skill.type.Attack;
import com.amorabot.inscripted.tasks.base.Skillcast;

public class AxeBasicAttacks {
    public static void standardAxeSlash(Skillcast skillcastInstance){
        if (!(skillcastInstance instanceof Attack.Basic basicAttackInstance)){return;}
        boolean isMirrored = Math.random() > 0.5;
        boolean isInverted = Math.random() > 0.5;
        new Slash(skillcastInstance, basicAttackInstance.getAttackData(), SlashPresets.STANDARD_AXE.getSlashConfigData(), false,
                skillcastInstance.getPlayer().getLocation(),isMirrored,isInverted,true, 10,40);
    }
}
