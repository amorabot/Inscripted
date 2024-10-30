package com.amorabot.inscripted.skills.archetypes.mace;

import com.amorabot.inscripted.APIs.damageAPI.DamageRouter;
import com.amorabot.inscripted.APIs.damageAPI.DamageSource;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.skills.AbilityRoutines;
import com.amorabot.inscripted.skills.ParticlePlotter;
import com.amorabot.inscripted.skills.PlayerAbilities;
import com.amorabot.inscripted.skills.attackInstances.slam.Slam;
import com.amorabot.inscripted.skills.attackInstances.slam.SlamConfigDTO;
import com.amorabot.inscripted.skills.attackInstances.slam.SlamRenderers;
import com.amorabot.inscripted.skills.attackInstances.slash.SlashConfigDTO;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class MaceBasicAttacks {

    public static void standardMaceSlamBy(Player player, PlayerAbilities mappedAbility){
        boolean rightHanded = Math.random() > 0.5;

        SlashConfigDTO slamAnimationData = new SlashConfigDTO(
                12,70,1.7,0,0.25,0.2,0.5,
                new int[]{220,160,190}, new double[]{1.1,1},0.7F, 0.25);
        SlamConfigDTO slamConfig = new SlamConfigDTO(
                slamAnimationData, rightHanded, 50,1.9,
                2.3, 2, 3);

        Slam slam = new Slam(player, mappedAbility, slamConfig, SlamRenderers::standardMaceSlash, SlamRenderers::standardMaceImpact);

        slam.execute();
    }

    public static void newMaceBasicAttackFor(Player player, PlayerAbilities mappedAbility){
        double slamRadius = 1.5;

        boolean isMirrored = Math.random() > 0.5;
        int duration = 4;
        double attackCenterOffset = 1.1;
        AbilityRoutines.newVerticalSwipeAnimationBy(player, 90, 12, duration, 1.7, attackCenterOffset,
                0.2, 0.4, -0.2, -0.5,
                200,160,200,
                null,1, 0.5, 1.2, isMirrored, 30);


        World playerWorld = player.getWorld();
        Vector playerToSlamOffset = player.getLocation().getDirection().clone().setY(0).normalize().multiply(attackCenterOffset+0.7);
        Vector slamCenter = player.getLocation().toVector().clone().add(playerToSlamOffset).subtract(new Vector(0, 0.4, 0));
        int taskID = new BukkitRunnable() {
            @Override
            public void run() {
                ParticlePlotter.spawnParticleAt(slamCenter.clone().add(new Vector(0,0.5,0)), playerWorld, Particle.SWEEP_ATTACK);
                ParticlePlotter.dustPlumeAt(slamCenter.toLocation(playerWorld), 20, 0.12f);
                ParticlePlotter.plotColoredCircleAt(slamCenter, playerWorld, 160,160,160, 1.5F, (float) slamRadius, 16);
//                ParticlePlotter.plotCircleAt(slamCenter, playerWorld, Particle.ELECTRIC_SPARK, (float) slamRadius, 16);
                ParticlePlotter.plotDirectionalCircleAt(slamCenter,playerWorld,Particle.ELECTRIC_SPARK, (float) (slamRadius-0.1f), 16, false, 0.3f);
                //Checking collisions
                final List<Player> nearbyPlayers = (List<Player>) slamCenter.toLocation(playerWorld).getNearbyPlayers(slamRadius+0.2);
                nearbyPlayers.remove(player);

                for (Player p : nearbyPlayers){
                    DamageRouter. playerAttack(player, p, DamageSource.HIT, mappedAbility);
                }
            }
        }.runTaskLater(Inscripted.getPlugin(), duration).getTaskId();
    }
}
