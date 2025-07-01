package com.amorabot.inscripted.skill.routine.projectile;

import com.amorabot.inscripted.APIs.damageAPI.DamageRouter;
import com.amorabot.inscripted.APIs.damageAPI.DamageSource;
import com.amorabot.inscripted.skill.PlayerAbilities;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.tasks.base.Skillcast;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.List;

import static com.amorabot.inscripted.skill.AbilityRoutines.getLargeHitbox;

public class ProjectileCollision {

    //TODO: Replace for 2 methods -> detection & execution //Detection can be a projectile standard method and execution a Consumer<Proj>
    //that can mutate its internal value (like changing targets,resetting travel distance, chain, explosion,...)
    public static boolean standardDetection(Projectile projectile){
        Vector currentPosition = projectile.getOrigin();
        Player attacker = projectile.getSkillcast().getPlayer();
        if (attacker == null || !attacker.isOnline()){
            projectile.setValid(false);
            return false;
        }

        World projWorld = projectile.getProjectileWorld();
        double detectionRange = projectile.getDetectionRange();
        List<LivingEntity> nearbyEntities = (List<LivingEntity>) currentPosition.toLocation(projWorld).getNearbyLivingEntities(detectionRange);

        if (!nearbyEntities.isEmpty()){
            for (LivingEntity e : nearbyEntities){
                if (projectile.getSkillcast().getCastData().getBlacklistedEntities().contains(e.getUniqueId())){continue;}
                if (projectile.getSkillcast().getCastData().getAffectedEntities().contains(e.getUniqueId())){continue;}

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
                    if (!attacker.hasLineOfSight(e)){continue;}
                    
                    // Apply damage through the custom damage router system with AttackData
                    Skills skillUsed = projectile.getSkillcast().getCastData().getCastingContext().getSkillUsed();
                    PlayerAbilities playerAbility = convertSkillToPlayerAbility(skillUsed);
                    DamageRouter.entityDamage(attacker, e, DamageSource.HIT, playerAbility, projectile.getAttackData());
                    
                    // Track affected entities and destroy projectile if needed
                    projectile.getSkillcast().getCastData().getAffectedEntities().add(e.getUniqueId());
                    projectile.getSkillcast().getCastData().getBlacklistedEntities().add(e.getUniqueId());
                    
                    if (projectile.isDestroyOnContact()){
                        projectile.setValid(false);
                    }
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

    // Helper method to convert Skills enum to PlayerAbilities enum
    private static PlayerAbilities convertSkillToPlayerAbility(Skills skill) {
        try {
            // Convert by name - both enums have the same names for basic attacks
            return PlayerAbilities.valueOf(skill.name());
        } catch (IllegalArgumentException e) {
            // Fallback to FIST if conversion fails
            return PlayerAbilities.FIST;
        }
    }

    //TODO: Seek when found -> constant checks for nearby players and change behavior to seek + change target
}
