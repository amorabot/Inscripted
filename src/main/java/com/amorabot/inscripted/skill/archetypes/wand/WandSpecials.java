package com.amorabot.inscripted.skill.archetypes.wand;

import com.amorabot.inscripted.combat.damage.DamageRouter;
import com.amorabot.inscripted.combat.damage.DamageSource;
import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.math.LinalgMath;
import com.amorabot.inscripted.particle.ParticlePlotter;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.skill.annotations.DurationSkill;
import com.amorabot.inscripted.skill.casting.CastSource;
import com.amorabot.inscripted.skill.routine.projectile.*;
import com.amorabot.inscripted.skill.routine.slash.SlashPresets;
import com.amorabot.inscripted.skill.routine.slash.SlashSegment;
import com.amorabot.inscripted.skill.type.Attack;
import com.amorabot.inscripted.skill.type.PersistentAttack;
import com.amorabot.inscripted.skill.type.Utility;
import com.amorabot.inscripted.skill.type.subroutines.DurationSubroutine;
import com.amorabot.inscripted.tasks.base.Skillcast;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class WandSpecials {

    // Duration -> On Expire -> Meteor impact
    public static void meteor(Skillcast skillcastInstance){
        if (!(skillcastInstance instanceof PersistentAttack persistentAttack)){return;}
        AttackData attackData = persistentAttack.getAttackData();
        DurationSkill durationData = persistentAttack.getDurationData();

        DurationSubroutine meteorFallRoutine = new DurationSubroutine(skillcastInstance);
        World world = skillcastInstance.getPlayer().getWorld();
//        Vector center = LinalgMath.projectHorizontalPlayerRaycast(14,skillcastInstance.getPlayer());
        Location playerLoc = skillcastInstance.getPlayer().getLocation();
        Vector center = Projectile.aimAssistedRaycast(playerLoc,playerLoc.getDirection(),14);
        Vector projDir = LinalgMath.rotateAroundX(new Vector(0,1,0),Math.toRadians(Math.random()*10));
        projDir = LinalgMath.rotateAroundZ(projDir,Math.toRadians(Math.random()*20));
        final float radius = 3.5f;
        final int points = 20;
        final double meteorHeight = 20;
        ProjectileConfig projectileConfig = new ProjectileConfig(false,true,false,
                (meteorHeight / meteorFallRoutine.getTotalDuration()), 0.04, 0.1,
                ProjectileTrail::meteorTrail, ProjectileCollision::noCollision, ProjectileImpacts::noImpact);
        Vector origin = Projectile.getRaytracedMaxDistance(center.toLocation(world), projDir.clone(), meteorHeight); //Raycast facing up

        Projectile meteorProj = new Projectile(skillcastInstance,attackData,origin,
                projDir.multiply(-1*( (projectileConfig.maxSpeed()) )  )
                ,new Vector(0,0,0),center,meteorHeight,projectileConfig);

        meteorFallRoutine.setRoutine(new BukkitRunnable() {
            @Override
            public void run() {
                if (this.isCancelled()) return;
                meteorFallRoutine.cancelIfInvalid();

                double animationProgress = ((double)( meteorFallRoutine.getElapsedTicks()) / meteorFallRoutine.getTotalDuration());
                ParticlePlotter.plotColoredCircleAt(center,world,242, 158, 12,0.8f,
                        (float) (radius*Utils.Easings.easeOutExpo(animationProgress)),points,false);
                meteorFallRoutine.addElapsedTime();
                if (meteorFallRoutine.isExpired()){
                    //Whatever 'expired' routines
                    meteorProjectileImpact(skillcastInstance,center);
                    return;
                }
            }
        });
        meteorFallRoutine.startSubroutine(0);

    }

    public static void meteorProjectileImpact(Skillcast meteorSkillcast, Vector impactPos){
        Skills parentSkillcastSourceSkill = meteorSkillcast.getCastedSkill();
        if (!parentSkillcastSourceSkill.equals(Skills.METEOR)){
            Utils.error("Who the fuck instanced this");
            return;
        }
        new Attack.Basic(meteorSkillcast.getPlayerID(),impactPos,Skills.METEOR_IMPACT, CastSource.SUB_SKILL, WeaponAttackSpeeds.NORMAL).start(0,0);
    }

    public static void meteorImpact(Skillcast skillcastInstance){
        if (!(skillcastInstance instanceof Attack.Basic basicAttackInstance)){return;}
        Player caster = skillcastInstance.getPlayer();
        World world = caster.getWorld();
        Vector castOrigin = skillcastInstance.getCastData().getCastingContext().getOrigin();
        AttackData attackData = basicAttackInstance.getAttackData();

        ParticlePlotter.spawnParticleAt(castOrigin,world, Particle.EXPLOSION);
        ParticlePlotter.spawnOffsetParticleAt(castOrigin,world,Particle.GUST,1.2,1,1.2,5);
        for (Vector flame : LinalgMath.plotPointsInsideHorizontalCircle(castOrigin,6,25)){
            ParticlePlotter.spawnParticleAt(flame,world,Particle.FLAME);
        }



        List<Player> nearbyPlayers = (List<Player>) castOrigin.toLocation(world).getNearbyPlayers(4f);
        List<UUID> blacklistedEntities = skillcastInstance.getCastData().getBlacklistedEntities();
        for (Player p : nearbyPlayers){
            if (blacklistedEntities.contains(p.getUniqueId())){
                continue;
            }
            if (attackData==null) {continue;}
            DamageRouter.hit(caster, p, basicAttackInstance,attackData, DamageSource.HIT);
        }
    }
}
