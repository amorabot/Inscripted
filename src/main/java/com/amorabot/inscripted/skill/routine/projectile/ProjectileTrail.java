package com.amorabot.inscripted.skill.routine.projectile;

import com.amorabot.inscripted.particle.ParticlePlotter;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class ProjectileTrail {

    //Also possible via Enum holding a Consumer<Projectile> object, declared via constructor anonymously
    public static void basicArrow(Projectile projectile){
        World projWorld = projectile.getProjectileWorld();
        Vector pos = projectile.getOrigin();
        Vector vel = projectile.getVelocity().clone();
        ParticlePlotter.spawnColoredParticleAt(pos, projWorld, 91, 245, 56, 1.2F,1);
        ParticlePlotter.spawnDirectionalParticle(pos, vel.multiply(-1), 0.5f, projWorld, Particle.CRIT);
        //For debug
//        ParticlePlotter.spawnParticleAt(projectile.getTarget(),projWorld, Particle.END_ROD);
    }
    public static void basicWand(Projectile projectile){
        World projWorld = projectile.getProjectileWorld();
        Vector pos = projectile.getOrigin();
        ParticlePlotter.spawnColorTransitionParticleAt(pos, projWorld, 72, 139, 207, 36, 182, 212, 0.8F, 2);
        Vector particleDir = projectile.getVelocity().clone().multiply(-1).normalize();
        ParticlePlotter.spawnDirectionalParticle(pos, particleDir, 1.2F, projWorld, Particle.ELECTRIC_SPARK);
    }
    public static void rainOfArrowsTrail(Projectile projectile){
        World projWorld = projectile.getProjectileWorld();
        Vector pos = projectile.getOrigin();
        Vector particleDir = projectile.getVelocity().clone().normalize();
        ParticlePlotter.spawnColoredParticleAt(pos, projWorld, 113, 184, 62, 1.2F,1);
        ParticlePlotter.spawnDirectionalParticle(pos, particleDir, 1.5f, projWorld, Particle.CRIT);
        ParticlePlotter.spawnDirectionalParticle(pos, particleDir, 0.3f, projWorld, Particle.ASH);
    }

    public static void smokeBombTrail(Projectile projectile){
        World projWorld = projectile.getProjectileWorld();
        Vector pos = projectile.getOrigin();
        ParticlePlotter.spawnColorTransitionParticleAt(pos, projWorld, 255, 255, 255, 200, 220, 220, 0.8F, 2);
        ParticlePlotter.spawnParticleAt(pos,projWorld,Particle.SMOKE);
    }
    public static void meteorTrail(Projectile projectile){
        World projWorld = projectile.getProjectileWorld();
        Vector pos = projectile.getOrigin();
        ParticlePlotter.spawnColorTransitionParticleAt(pos, projWorld, 200, 200, 200, 242, 158, 12, 3F, 1);
        ParticlePlotter.spawnParticleAt(pos,projWorld, Particle.GUST);
        ParticlePlotter.spawnDirectionalParticle(pos,projectile.getVelocity().clone().normalize().multiply(-1),0.3f,projWorld,Particle.FIREWORK);
    }
}
