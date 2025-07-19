package com.amorabot.inscripted.skill.routine.projectile;

import com.amorabot.inscripted.combat.damage.DamageRouter;
import com.amorabot.inscripted.combat.damage.DamageSource;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.skill.routine.SkillcastData;
import com.amorabot.inscripted.skill.type.Attack;
import com.amorabot.inscripted.tasks.base.Skillcast;
import org.bukkit.World;
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
        Player attacker = projectile.getSkillcast().getPlayer();
        assert attacker != null;
        if (!attacker.isOnline()){projectile.setValid(false);}
        Skillcast projSkillcast = projectile.getSkillcast();
        SkillcastData scData = projectile.getSkillcast().getCastData();

        AttackData projAttackData = null;
        if (projSkillcast.getCastedSkill().isAttackSkill()){
            Attack projectileAttack = (Attack) projSkillcast;
            projAttackData = projectileAttack.getAttackData();
        }
        World projWorld = projectile.getProjectileWorld();
        double detectionRange = projectile.getDetectionRange();
        List<Player> nearbyEntities = (List<Player>) currentPosition.toLocation(projWorld).getNearbyPlayers(detectionRange);

        if (!nearbyEntities.isEmpty()){
            for (Player currentNearPlayer : nearbyEntities){
                if (scData.getBlacklistedEntities().contains(currentNearPlayer.getUniqueId())){continue;}
                if (scData.getAffectedEntities().contains(currentNearPlayer.getUniqueId())){continue;}

                BoundingBox entityAABB = getLargeHitbox(currentNearPlayer);
                BoundingBox arrowAABB = new BoundingBox(currentPosition.getX(), currentPosition.getY(), currentPosition.getZ(),
                        currentPosition.getX(), currentPosition.getY(), currentPosition.getZ());
                arrowAABB.expand(detectionRange/4);

                if (entityAABB.overlaps(arrowAABB)){
                    if (!attacker.hasLineOfSight(currentNearPlayer)){continue;}

                    if (projAttackData==null) {continue;}
                    DamageRouter.hit(attacker, currentNearPlayer, projSkillcast,projAttackData, DamageSource.HIT);
                    scData.getAffectedEntities().add(currentNearPlayer.getUniqueId());
                    if (projectile.isDestroyOnContact()){projectile.setValid(false);}
                    return true;
                }
            }
        }
        return false;
    }

    public static void testCollisionExecution(Skillcast originalSkillcast){
        originalSkillcast.getPlayer().sendMessage("Colided!");
    }

    //TODO: Seek when found -> constant checks for nearby players and change behavior to seek + change target
}
