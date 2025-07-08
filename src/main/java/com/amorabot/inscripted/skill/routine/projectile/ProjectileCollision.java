package com.amorabot.inscripted.skill.routine.projectile;

import com.amorabot.inscripted.tasks.base.Skillcast;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.List;

import static com.amorabot.inscripted.skill.Skills.getLargeHitbox;

public class ProjectileCollision {

    //TODO: Replace for 2 methods -> detection & execution //Detection can be a projectile standard method and execution a Consumer<Proj>
    //that can mutate its internal value (like changing targets,resetting travel distance, chain, explosion,...)
    public static boolean standardDetection(Projectile projectile){
        Vector currentPosition = projectile.getOrigin();
//TODO        Player attacker = Bukkit.getPlayer(projectile.getContext().getAttackerID());
//        assert attacker != null;
//        if (!attacker.isOnline()){projectile.setValid(false);}

        World projWorld = projectile.getProjectileWorld();
        double detectionRange = projectile.getDetectionRange();
        List<LivingEntity> nearbyEntities = (List<LivingEntity>) currentPosition.toLocation(projWorld).getNearbyLivingEntities(detectionRange);

        if (!nearbyEntities.isEmpty()){
            for (LivingEntity e : nearbyEntities){
//TODO                if (projectile.getBlacklistedEntities().contains(e.getUniqueId())){continue;}
//                if (projectile.getAffectedEntities().contains(e.getUniqueId())){continue;}

                BoundingBox entityAABB;
                if (e instanceof Player p){
                    entityAABB = getLargeHitbox(p);
                } else { //Its a mob
                    entityAABB = e.getBoundingBox();
                }
                BoundingBox arrowAABB = new BoundingBox(currentPosition.getX(), currentPosition.getY(), currentPosition.getZ(),
                        currentPosition.getX(), currentPosition.getY(), currentPosition.getZ());
                arrowAABB.expand(detectionRange/4);

                if (entityAABB.overlaps(arrowAABB)){
//                    if (!attacker.hasLineOfSight(e)){continue;}
//                    DamageRouter.entityDamage(attacker, e, DamageSource.HIT, projectile.getContext().getSkillUsed());
//                    projectile.getAffectedEntities().add(e.getUniqueId());
//                    if (projectile.isDestroyOnContact()){projectile.setValid(false);}
                    return true;
                }
                continue;
                //Its a regular mob/living entity

            }
        }
        return false;
    }

    public static void testCollisionExecution(Skillcast originalSkillcast){
        originalSkillcast.getPlayer().sendMessage("Colided!");
    }

    //TODO: Seek when found -> constant checks for nearby players and change behavior to seek + change target
}
