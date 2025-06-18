package com.amorabot.inscripted.skill.routine.slam;

import com.amorabot.inscripted.particle.ParticlePlotter;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class SlamRenderers {

    public static void standardMaceImpact(Slam slam){
        Vector slamCenter = slam.getSlamCenter();
        World world = slam.getOwner().getWorld();
        double slamRadius = slam.getSlamData().impactRadius();

        ParticlePlotter.dustPlumeAt(slamCenter.toLocation(world), 20, 0.12f);
        ParticlePlotter.plotColoredCircleAt(slamCenter, world, 160,160,160, 1.5F, (float) slamRadius, 16);
    }
}
