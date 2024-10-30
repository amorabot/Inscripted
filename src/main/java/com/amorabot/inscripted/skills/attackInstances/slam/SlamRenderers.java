package com.amorabot.inscripted.skills.attackInstances.slam;

import com.amorabot.inscripted.skills.ParticlePlotter;
import com.amorabot.inscripted.skills.attackInstances.slash.SlashConfigDTO;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

import static com.amorabot.inscripted.skills.attackInstances.slash.SlashSegment.renderTip;

public class SlamRenderers {

    public static Consumer<Vector[]> standardMaceSlash(Slam slam){
        return segment -> {
            Vector handle = segment[0];
            Vector tip = segment[1];

            SlashConfigDTO slamAnimationData = slam.getSlamData().slashAnimationData();
            World world = slam.getOwner().getWorld();
            int[] color = slamAnimationData.baseColor();
            ParticlePlotter.coloredParticleLerp(handle, tip,0.15f, world, color[0], color[1], color[2], slamAnimationData.particleSize());
            renderTip(segment, 0.1f, 0.1f, slamAnimationData, world);
            ParticlePlotter.spawnParticleAt(tip, world, Particle.ELECTRIC_SPARK);
        };
    }

    public static void standardMaceImpact(Slam slam){
        Vector slamCenter = slam.getSlamCenter();
        World world = slam.getOwner().getWorld();
        double slamRadius = slam.getSlamData().impactRadius();

        ParticlePlotter.spawnParticleAt(slamCenter.clone().add(new Vector(0,0.5,0)), world, Particle.SWEEP_ATTACK);
        ParticlePlotter.spawnParticleAt(slamCenter, world, Particle.END_ROD);
        ParticlePlotter.dustPlumeAt(slamCenter.toLocation(world), 20, 0.12f);
        ParticlePlotter.plotColoredCircleAt(slamCenter, world, 160,160,160, 1.5F, (float) slamRadius, 16);
        ParticlePlotter.plotDirectionalCircleAt(slamCenter,world,Particle.ELECTRIC_SPARK, (float) (slamRadius-0.1f), 16, false, 0.3f);
    }
}
