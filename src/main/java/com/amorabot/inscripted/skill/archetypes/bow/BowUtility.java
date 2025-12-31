package com.amorabot.inscripted.skill.archetypes.bow;

import com.amorabot.inscripted.combat.buffs.Buffs;
import com.amorabot.inscripted.combat.buffs.PlayerBuffManager;
import com.amorabot.inscripted.combat.buffs.categories.stat.StatBuff;
import com.amorabot.inscripted.displays.DisplayBlock;
import com.amorabot.inscripted.math.LinalgMath;
import com.amorabot.inscripted.particle.ParticlePlotter;
import com.amorabot.inscripted.skill.routine.projectile.Projectile;
import com.amorabot.inscripted.skill.type.subroutines.DurationSubroutine;
import com.amorabot.inscripted.tasks.base.Skillcast;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

public class BowUtility {
    public static void huntingGround(Skillcast skillcastInstance){
        DurationSubroutine huntingGroundRoutine = new DurationSubroutine(skillcastInstance);
        World world = skillcastInstance.getPlayer().getWorld();
        Location playerLoc = skillcastInstance.getPlayer().getLocation();
        Vector center = Projectile.aimAssistedRaycast(playerLoc,playerLoc.getDirection(),20);
//        Vector center = LinalgMath.projectHorizontalPlayerRaycast(20,skillcastInstance.getPlayer());
        final float radius = 5f;
        final int points = 40;
        final int debuffDuration = 60;
        final StatBuff playerPrecision = new StatBuff(Buffs.HUNTING_GROUNDS_PRECISION, skillcastInstance.getPlayer());
        PlayerBuffManager.addBuffToPlayer(playerPrecision,skillcastInstance.getPlayer().getUniqueId());


        Vector[] internalPoints = LinalgMath.plotPointsInsideHorizontalCircle(center,radius,50);
        for (Vector internalPoint : internalPoints){
            if (Math.random()>0.75){
                DisplayBlock grass = new DisplayBlock(internalPoint,world, Material.FERN,huntingGroundRoutine.getTotalDuration()+12,true);
                grass.scale(0.3+Math.random());
            }
        }

        huntingGroundRoutine.setRoutine(new BukkitRunnable() {
            @Override
            public void run() {
                if (this.isCancelled()) return;
                huntingGroundRoutine.cancelIfInvalid();
                ParticlePlotter.plotColoredCircleAt(
                        center,world,94, 156, 53,1.7f,radius,points,true
                );
                for (Vector internalPoint : internalPoints){
                    ParticlePlotter.spawnColoredParticleAt(internalPoint,world,168, 107, 50,1.5f,1);
                }
                huntingGroundRoutine.addElapsedTime();
                if (huntingGroundRoutine.isExpired()){
                    //Play skill sound

                    ParticlePlotter.plotDirectionalCircleAt(center,world,Particle.CRIT,radius,points,true,1.5f,true, 0.3f);
                    PotionEffect slow = new PotionEffect(PotionEffectType.SLOWNESS, debuffDuration, 1, true, false, false);
                    List<Player> affectedPlayers = (List<Player>) world.getNearbyPlayers(center.toLocation(world),radius);
                    List<UUID> immunePlayers = skillcastInstance.getCastData().getBlacklistedEntities();
                    boolean exposedPlayer = false;
                    for (Player p : affectedPlayers){
                        if (immunePlayers.contains(p.getUniqueId())){ continue; }
                        final StatBuff targetExposure = new StatBuff(Buffs.HUNTING_GROUNDS_EXPOSURE, p);
                        PlayerBuffManager.addBuffToPlayer(targetExposure,p.getUniqueId());
                        slow.apply(p);
                        exposedPlayer = true;
                    }
                    if (exposedPlayer){
                        PlayerBuffManager.addBuffToPlayer(playerPrecision,skillcastInstance.getPlayerID());
                    }
                    return;
                }
            }
        });
        huntingGroundRoutine.startSubroutine(0);
    }
}
