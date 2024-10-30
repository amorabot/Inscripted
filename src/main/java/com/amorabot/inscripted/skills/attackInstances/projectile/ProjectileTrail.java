package com.amorabot.inscripted.skills.attackInstances.projectile;

import com.amorabot.inscripted.skills.ParticlePlotter;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class ProjectileTrail {

    //Also possible via Enum holding a Consumer<Projectile> object, declared via constructor anonymously
    public static void basicArrow(Projectile projectile){
        World projWorld = projectile.getProjectileWorld();
        Vector pos = projectile.getPosition();
        Vector vel = projectile.getVelocity().clone();
        ParticlePlotter.spawnColoredParticleAt(pos, projWorld, 91, 245, 56, 1.2F,1);
        ParticlePlotter.spawnDirectionalParticle(pos, vel.multiply(-1), 0.5f, projWorld, Particle.CRIT);
        //For debug
//        ParticlePlotter.spawnParticleAt(projectile.getTarget(),projWorld, Particle.END_ROD);
    }
    public static void basicWand(Projectile projectile){
        World projWorld = projectile.getProjectileWorld();
        Vector pos = projectile.getPosition();
        ParticlePlotter.spawnColorTransitionParticleAt(pos, projWorld, 72, 139, 207, 36, 182, 212, 0.8F, 2);
        Vector particleDir = projectile.getVelocity().clone().multiply(-1).normalize();
        ParticlePlotter.spawnDirectionalParticle(pos, particleDir, 1.2F, projWorld, Particle.ELECTRIC_SPARK);
    }
}
