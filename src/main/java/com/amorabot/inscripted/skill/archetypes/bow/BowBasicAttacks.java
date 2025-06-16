package com.amorabot.inscripted.skill.archetypes.bow;

import com.amorabot.inscripted.skill.PlayerAbilities;
import com.amorabot.inscripted.skill.SteeringBehaviors;
import com.amorabot.inscripted.skill.attackInstances.projectile.ProjectileCollision;
import com.amorabot.inscripted.skill.attackInstances.projectile.ProjectilePatterns;
import com.amorabot.inscripted.skill.attackInstances.projectile.ProjectileTrail;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BowBasicAttacks {

    public static void standardBowAttackBy(Player player, PlayerAbilities mappedAbility, SteeringBehaviors behavior, int projectiles){
        int maxRange = 25;
        double projSpeed = (double) 30 / 20; //Blocks/s (Scales with proj speed stat) //randomize per pellet?
        double detectionRange = 0.8;

//        ProjectilePatterns.CONE.instantiate(player, mappedAbility,projectiles,maxRange,projSpeed,detectionRange, behavior, false, false, true,
//                ProjectileTrail::basicArrow, ProjectileCollision::standard, 90, new Vector[]{null});
    }
}
