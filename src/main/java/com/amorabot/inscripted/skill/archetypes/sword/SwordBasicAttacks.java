package com.amorabot.inscripted.skill.archetypes.sword;

import com.amorabot.inscripted.skill.PlayerAbilities;
import com.amorabot.inscripted.skill.routine.slash.Slash;
import com.amorabot.inscripted.skill.routine.slash.SlashPresets;
import com.amorabot.inscripted.skill.type.Attack;
import com.amorabot.inscripted.tasks.base.Skillcast;
import org.bukkit.entity.Player;

public class SwordBasicAttacks {

    public static void standardSwordSlashBy(Player player, PlayerAbilities mappedAbility){
    }
    public static void standardSwordSlash(Skillcast skillcastInstance){
        if (!(skillcastInstance instanceof Attack.Basic basicAttackInstance)){return;}
        boolean isMirrored = Math.random() > 0.5;
        boolean isInverted = Math.random() > 0.5;
        new Slash(skillcastInstance, basicAttackInstance.getAttackData(), SlashPresets.STANDARD_SWORD.getSlashConfigData(), false,
                skillcastInstance.getPlayer().getLocation(),isMirrored,isInverted,false, 30);
    }

}
