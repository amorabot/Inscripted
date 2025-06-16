package com.amorabot.inscripted.skill.attackInstances.slam;

import com.amorabot.inscripted.particle.ParticlePlotter;
import com.amorabot.inscripted.skill.attackInstances.slash.SlashConfig;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

import static com.amorabot.inscripted.skill.attackInstances.slash.SlashSegment.renderTip;

public class SlamRenderers {

    public static void standardMaceImpact(Slam slam){
        Vector slamCenter = slam.getSlamCenter();
        World world = slam.getOwner().getWorld();
        double slamRadius = slam.getSlamData().impactRadius();

        ParticlePlotter.dustPlumeAt(slamCenter.toLocation(world), 20, 0.12f);
        ParticlePlotter.plotColoredCircleAt(slamCenter, world, 160,160,160, 1.5F, (float) slamRadius, 16);
    }
}
