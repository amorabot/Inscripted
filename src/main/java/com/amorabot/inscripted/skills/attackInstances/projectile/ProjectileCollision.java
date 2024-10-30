package com.amorabot.inscripted.skills.attackInstances.projectile;

import com.amorabot.inscripted.APIs.damageAPI.DamageRouter;
import com.amorabot.inscripted.APIs.damageAPI.DamageSource;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.List;

import static com.amorabot.inscripted.skills.AbilityRoutines.getLargeHitbox;

public class ProjectileCollision {

    //TODO: Replace for 2 methods -> detection & execution //Detection can be a projectile standard method and execution a Consumer<Proj>
    //that can mutate its internal value (like changing targets,resetting travel distance, chain, explosion,...)
    public static void standard(Projectile projectile){
        Vector currentPosition = projectile.getPosition();
        Player attacker = Bukkit.getPlayer(projectile.getContext().getAttackerID());
        assert attacker != null;
        if (!attacker.isOnline()){projectile.setValid(false);}

        World projWorld = projectile.getProjectileWorld();
        double detectionRange = projectile.getDetectionRange();
        List<Player> nearbyPlayers = (List<Player>) currentPosition.toLocation(projWorld).getNearbyPlayers(detectionRange);

        if (!nearbyPlayers.isEmpty()){
            for (Player p : nearbyPlayers){
                if (projectile.getBlacklistedEntities().contains(p.getUniqueId())){continue;}
                if (projectile.getAffectedEntities().contains(p.getUniqueId())){continue;}
                BoundingBox playerAABB = getLargeHitbox(p);
                BoundingBox arrowAABB = new BoundingBox(currentPosition.getX(), currentPosition.getY(), currentPosition.getZ(),
                        currentPosition.getX(), currentPosition.getY(), currentPosition.getZ());
                arrowAABB.expand(detectionRange/4);

                if (playerAABB.overlaps(arrowAABB)){
                    if (!attacker.hasLineOfSight(p)){continue;}
                    DamageRouter.playerAttack(attacker, p, DamageSource.HIT, projectile.getContext().getSourceAbility());
                    projectile.getAffectedEntities().add(p.getUniqueId());
                    if (projectile.isDestroyOnContact()){projectile.setValid(false);}
                    return;
                }
            }
        }
    }

    //TODO: Seek when found -> constant checks for nearby players and change behavior to seek + change target
}
