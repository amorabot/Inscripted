package com.amorabot.inscripted.skill.archetypes.bow;

import com.amorabot.inscripted.combat.buffs.Buffs;
import com.amorabot.inscripted.combat.buffs.PlayerBuffManager;
import com.amorabot.inscripted.combat.buffs.categories.stat.StatBuff;
import com.amorabot.inscripted.math.LinalgMath;
import com.amorabot.inscripted.particle.ParticlePlotter;
import com.amorabot.inscripted.skill.type.subroutines.DurationSubroutine;
import com.amorabot.inscripted.tasks.base.Skillcast;
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
        Vector center = LinalgMath.projectHorizontalPlayerRaycast(20,skillcastInstance.getPlayer());
        final float radius = 5f;
        final int points = 40;
        final int debuffDuration = 60;
        final StatBuff playerPrecision = new StatBuff(Buffs.HUNTING_GROUNDS_PRECISION, skillcastInstance.getPlayer());

        huntingGroundRoutine.setRoutine(new BukkitRunnable() {
            @Override
            public void run() {
                if (this.isCancelled()) return;
                huntingGroundRoutine.cancelIfInvalid();
                ParticlePlotter.plotColoredCircleAt(
                        center,world,94, 156, 53,1.7f,radius,points,true
                );
                huntingGroundRoutine.addElapsedTime();
                if (huntingGroundRoutine.isExpired()){
                    ParticlePlotter.plotDirectionalCircleAt(center,world, Particle.ELECTRIC_SPARK,radius,points,true,1.3f,true,0.1f);
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
