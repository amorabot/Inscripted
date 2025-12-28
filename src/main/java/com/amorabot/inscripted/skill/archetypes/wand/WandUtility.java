package com.amorabot.inscripted.skill.archetypes.wand;

import com.amorabot.inscripted.math.LinalgMath;
import com.amorabot.inscripted.particle.ParticlePlotter;
import com.amorabot.inscripted.skill.routine.projectile.Projectile;
import com.amorabot.inscripted.skill.type.subroutines.DurationSubroutine;
import com.amorabot.inscripted.tasks.base.Skillcast;
import com.amorabot.inscripted.utils.DelayedTask;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;

public class WandUtility {
    public static void cryostasis(Skillcast skillcastInstance){
        DurationSubroutine cryostatisRoutine = new DurationSubroutine(skillcastInstance);
        final double raytraceMaxDistance = 10;
        final Location playerLoc = skillcastInstance.getPlayer().getLocation().clone();
        World world = skillcastInstance.getPlayer().getWorld();
        final Vector originalDir = playerLoc.getDirection().clone();
        final Vector center = Projectile.aimAssistedRaycast(playerLoc,originalDir,raytraceMaxDistance);

        cryostatisRoutine.setRoutine(new BukkitRunnable() {
            final float radius = 4f;
            final int points = 24;
            final int debuffDuration = 50;
            @Override
            public void run() {
                if (this.isCancelled()) return;
                cryostatisRoutine.cancelIfInvalid();
                double progress = ((double)( cryostatisRoutine.getElapsedTicks()) / cryostatisRoutine.getTotalDuration());
                ParticlePlotter.animatedColoredCircle(
                        center,world,174, 201, 245,1.5f,radius,points,Math.PI/6,progress, Utils.Easings::easeOutQuad
                );
                ParticlePlotter.spawnOffsetParticleAt(center.clone().add(new Vector(0,1,0)),world,Particle.SNOWFLAKE,radius/2,2,radius/2, 7);
                cryostatisRoutine.addElapsedTime();
                if (cryostatisRoutine.isExpired()){
                    Vector offsetCenter = center.clone().add(new Vector(0,0.3,0));
                    for (Vector v : LinalgMath.plotPointsInsideHorizontalCircle(offsetCenter,radius,60)){
                        ParticlePlotter.spawnColoredParticleAt(v,world,174, 201, 245,1f,1);
                    }
                    PotionEffect slow = new PotionEffect(PotionEffectType.SLOWNESS, debuffDuration, 2, true, false, false);
                    List<Player> affectedPlayers = (List<Player>) world.getNearbyPlayers(center.toLocation(world),radius);
                    for (Player p : affectedPlayers){
                        Objects.requireNonNull(p.getAttribute(Attribute.GENERIC_JUMP_STRENGTH)).setBaseValue(0);
                        new DelayedTask(new BukkitRunnable() {
                            @Override
                            public void run() {
                                Objects.requireNonNull(p.getAttribute(Attribute.GENERIC_JUMP_STRENGTH)).setBaseValue(0.42);
                            }
                        },debuffDuration);
                        slow.apply(p);
                    }
                    return;
                }
            }
        });
        cryostatisRoutine.startSubroutine(0);
    }



}
