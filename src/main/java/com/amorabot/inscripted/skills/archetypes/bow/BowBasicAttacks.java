package com.amorabot.inscripted.skills.archetypes.bow;

import com.amorabot.inscripted.skills.PlayerAbilities;
import com.amorabot.inscripted.skills.SteeringBehaviors;
import com.amorabot.inscripted.skills.attackInstances.projectile.ProjectileCollision;
import com.amorabot.inscripted.skills.attackInstances.projectile.ProjectilePatterns;
import com.amorabot.inscripted.skills.attackInstances.projectile.ProjectileTrail;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BowBasicAttacks {

    public static void standardBowAttackBy(Player player, PlayerAbilities mappedAbility, SteeringBehaviors behavior, int projectiles){
        int maxRange = 25;
        double projSpeed = (double) 30 / 20; //Blocks/s (Scales with proj speed stat) //randomize per pellet?
        double detectionRange = 0.8;

        ProjectilePatterns.CONE.instantiate(player, mappedAbility,projectiles,maxRange,projSpeed,detectionRange, behavior, false, false, true,
                ProjectileTrail::basicArrow, ProjectileCollision::standard, 90, new Vector[]{null});
    }
}
