package com.amorabot.inscripted.skills.archetypes.wand;

import com.amorabot.inscripted.skills.PlayerAbilities;
import com.amorabot.inscripted.skills.SteeringBehaviors;
import com.amorabot.inscripted.skills.attackInstances.projectile.Projectile;
import com.amorabot.inscripted.skills.attackInstances.projectile.ProjectileCollision;
import com.amorabot.inscripted.skills.attackInstances.projectile.ProjectilePatterns;
import com.amorabot.inscripted.skills.attackInstances.projectile.ProjectileTrail;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class WandBasicAttacks {

    public static void standardWandAttackBy(Player player, PlayerAbilities mappedAbility, SteeringBehaviors behavior, int projectiles){
        int maxRange = 13;
        double projSpeed = (double) 20 / 20; //Blocks/s (Scales with proj speed stat) //randomize per pellet?
        double detectionRange = 0.9;

        Location loc = player.getLocation().clone().add(0,1.7,0);
        Vector targetpos = Projectile.getRaytracedMaxDistance(loc, loc.getDirection().clone(), maxRange);
        ProjectilePatterns.SHOTGUN.instantiate(player, mappedAbility,projectiles,maxRange,projSpeed,detectionRange, behavior, false, false, false,
                ProjectileTrail::basicWand, ProjectileCollision::standard, 16, new Vector[]{targetpos});

    }
}
