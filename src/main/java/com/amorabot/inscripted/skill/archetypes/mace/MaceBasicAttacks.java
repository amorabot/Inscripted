package com.amorabot.inscripted.skill.archetypes.mace;

import com.amorabot.inscripted.skill.routine.slam.Slam;
import com.amorabot.inscripted.skill.routine.slam.SlamConfig;
import com.amorabot.inscripted.skill.routine.slam.SlamRenderers;
import com.amorabot.inscripted.skill.routine.slash.SlashConfig;
import com.amorabot.inscripted.skill.routine.slash.SlashPresets;
import com.amorabot.inscripted.skill.type.Attack;
import com.amorabot.inscripted.tasks.base.Skillcast;

public class MaceBasicAttacks {

    public static void standardMaceSlam(Skillcast skillcastInstance){
        if (!(skillcastInstance instanceof Attack.Basic basicAttackInstance)){return;}
        boolean rightHanded = Math.random() > 0.5;
        SlashConfig swingRendererData = SlashPresets.STANDARD_SLAM_SWING.getSlashConfigData();
        SlamConfig slamConfig = new SlamConfig(
                swingRendererData, rightHanded, 50,1.9,
                2.3, 2, 3);

        Slam slam = new Slam(skillcastInstance, basicAttackInstance.getAttackData(),
                slamConfig, swingRendererData.defaultRenderer(), SlamRenderers::standardMaceImpact);
    }
}
