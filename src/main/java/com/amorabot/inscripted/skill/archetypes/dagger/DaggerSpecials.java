package com.amorabot.inscripted.skill.archetypes.dagger;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.combat.damage.DamageRouter;
import com.amorabot.inscripted.combat.damage.DamageSource;
import com.amorabot.inscripted.math.LinalgMath;
import com.amorabot.inscripted.math.OrientedBoundingBox;
import com.amorabot.inscripted.particle.ParticlePlotter;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.skill.type.Attack;
import com.amorabot.inscripted.tasks.base.Skillcast;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

import static com.amorabot.inscripted.skill.Skills.getLargeHitbox;

public class DaggerSpecials {
    public static void viperStrike(Skillcast skillcastInstance){
        if (!(skillcastInstance instanceof Attack.Basic viperStrikeInstance)){return;}
        int[][] colors = new int[][]{{155, 235, 52},{170, 12, 176}};
        Location playerLoc = viperStrikeInstance.getPlayer().getLocation().clone();
        Vector[] slashOrientation = LinalgMath.getHorizontalOrientation(playerLoc);
        final double fangOffset = 1.5;
        Vector offsetVector = slashOrientation[0].clone().multiply(fangOffset/2);
        final int segments = 12;
        final int slashArc = 50;
        final double slashBaseRadius = 1.3;
        final double initialOffsetFromCenter = 0;
        final double deltaCenterOffset = 0.2;
        final double startingLength = 0.7;
        final double deltaLength = -0.6;

        final Vector[][] viperSlashPoints = LinalgMath.plotSlash(playerLoc,slashOrientation,new double[]{1,1},true,slashBaseRadius, slashArc,
                segments,false,false,startingLength,deltaLength, initialOffsetFromCenter,deltaCenterOffset, 0);
        final OrientedBoundingBox viperStrikeHitbox = defineViperStrikeHitbox(viperSlashPoints,slashOrientation,fangOffset);
        AttackData viperStrikeAttack = viperStrikeInstance.getAttackData();
        List<Player> nearbyPlayers = (List<Player>) playerLoc.getNearbyPlayers(slashBaseRadius+0.5);

        for (Player p : nearbyPlayers){
            if (p.equals(viperStrikeInstance.getPlayer())){continue;}
            if (viperStrikeHitbox.intersects(getLargeHitbox(p))){
                DamageRouter.hit(viperStrikeInstance.getPlayer(), p,viperStrikeInstance,viperStrikeAttack, DamageSource.HIT);
            }
        }

        final int animationDuration = 3;
        int framesPerIteration = Math.max(1,(segments/animationDuration));
        int taskID = new BukkitRunnable(){
            int iteration = 0;
            int totalFrames = 0;
            @Override
            public void run() {
                try {
                    for (int frame = 0; frame<framesPerIteration; frame++){
                        int currentSegmentIndex = iteration*framesPerIteration + frame;
                        Vector currentTip = viperSlashPoints[0][currentSegmentIndex];
                        Vector currentHandle = viperSlashPoints[1][currentSegmentIndex];

                        //Left fang
                        ParticlePlotter.lerpColorTransitionParticle(currentHandle.clone().add(offsetVector),currentTip.clone().add(offsetVector),0.25f,playerLoc.getWorld(),
                                colors[0][0],colors[0][1],colors[0][2], colors[1][0],colors[1][1],colors[1][2],0.5f);
                        attemptPotionEffect(currentHandle,playerLoc.getWorld(),0.15,colors,fangOffset,0.4,fangOffset);
                        //Right fang
                        ParticlePlotter.lerpColorTransitionParticle(currentHandle.clone().subtract(offsetVector),currentTip.clone().subtract(offsetVector),0.25f,playerLoc.getWorld(),
                                colors[0][0],colors[0][1],colors[0][2], colors[1][0],colors[1][1],colors[1][2],0.5f);
                        attemptPotionEffect(currentHandle,playerLoc.getWorld(),0.15,colors,fangOffset,0.4,fangOffset);

                        totalFrames++;
                        if (totalFrames>=segments){this.cancel();return;}
                    }
                    iteration++;
                } catch (Exception exception){
                    this.cancel();
                    return;
                }
            }
        }.runTaskTimer(Inscripted.getPlugin(),0, 1).getTaskId();
    }
    private static OrientedBoundingBox defineViperStrikeHitbox(Vector[][] pointCloud, Vector[] orientation, double fangOffset){
        final int segments = pointCloud[0].length;
        Vector[] flattenedSlashPoints = new Vector[2*segments];
        for (int i = 0; i < pointCloud[0].length; i++) {
            flattenedSlashPoints[i] = pointCloud[0][i];
            flattenedSlashPoints[i + segments] = pointCloud[1][i];
        }
        OrientedBoundingBox hitbox = new OrientedBoundingBox(flattenedSlashPoints,orientation);
        hitbox.expandDirectional(0,true, fangOffset/2);
        hitbox.expandDirectional(0,false, fangOffset/2);
        return hitbox;
    }
    private static void attemptPotionEffect(Vector position, World world, double weight, int[][] effectColors, double offX, double offY, double offZ){
        if (Math.random()<Math.min(1,weight)){
            if (Math.random()>0.5){
                ParticlePlotter.coloredPotionEffect(position,world,200,
                        effectColors[0][0],effectColors[0][1],effectColors[0][2], offX,offY,offZ);
            } else {
                ParticlePlotter.coloredPotionEffect(position,world,200,
                        effectColors[1][0],effectColors[1][1],effectColors[1][2], offX,offY,offZ);
            }
        }
    }
}
