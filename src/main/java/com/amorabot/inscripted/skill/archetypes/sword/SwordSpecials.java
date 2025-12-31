package com.amorabot.inscripted.skill.archetypes.sword;

import com.amorabot.inscripted.math.OrientedBoundingBox;
import com.amorabot.inscripted.skill.routine.slash.Slash;
import com.amorabot.inscripted.skill.routine.slash.SlashConfig;
import com.amorabot.inscripted.skill.routine.slash.SlashPresets;
import com.amorabot.inscripted.skill.routine.slash.SlashSegment;
import com.amorabot.inscripted.skill.type.Attack;
import com.amorabot.inscripted.tasks.base.Skillcast;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class SwordSpecials {
    public static void lacerate(Skillcast skillcastInstance) {
        if (!(skillcastInstance instanceof Attack.Basic basicAttackInstance)) {return;}
//        boolean isMirrored = Math.random() > 0.5;
        Slash mainSlash = new Slash(skillcastInstance, basicAttackInstance.getAttackData(), SlashPresets.LACERATE.getSlashConfigData(), false, null,
                false,false,true, false, 45);
        OrientedBoundingBox mainHitbox = mainSlash.getHitbox();
        mainHitbox.expandFromCenter(2,1);
//        mainHitbox.render(mainSlash.getSlashWorld());

        final double clawOffset = 0.5;
        SlashConfig miniClawSlash = new SlashConfig(SlashSegment::bloody,
                15,90,1.8,0.2, -0.7,
                0.2,0.5, new int[]{100, 20, 30}, null, 0.6F, 0.1
        );
        Vector[] mainSlashOrientation = mainHitbox.getOrientation();
        Location upperLoc = skillcastInstance.getPlayer().getLocation().clone()
                .add(mainSlashOrientation[2].clone().multiply(clawOffset)).add(mainSlashOrientation[1].clone().multiply(0.3))
                .add(mainSlashOrientation[0].clone().multiply(0.3));
        Slash upperSlash = new Slash(skillcastInstance, basicAttackInstance.getAttackData(), miniClawSlash, true, upperLoc,
                false,false,false, false, 55);
        Location lowerLoc = skillcastInstance.getPlayer().getLocation().clone()
                .subtract(mainSlashOrientation[2].clone().multiply(clawOffset)).add(mainSlashOrientation[1].clone().multiply(0.3))
                .add(mainSlashOrientation[0].clone().multiply(0.3));
        Slash lowerSlash = new Slash(skillcastInstance, basicAttackInstance.getAttackData(), miniClawSlash, true, lowerLoc,
                false,false,false, false, 35);

        mainSlash.execute(3);
        upperSlash.execute(3);
        lowerSlash.execute(3);
    }
}
