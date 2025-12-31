package com.amorabot.inscripted.skill.archetypes.mace;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.displays.DisplayBlock;
import com.amorabot.inscripted.math.LinalgMath;
import com.amorabot.inscripted.particle.ParticlePlotter;
import com.amorabot.inscripted.skill.routine.slam.Slam;
import com.amorabot.inscripted.skill.routine.slam.SlamConfig;
import com.amorabot.inscripted.skill.routine.slash.SlashConfig;
import com.amorabot.inscripted.skill.routine.slash.SlashSegment;
import com.amorabot.inscripted.skill.type.Attack;
import com.amorabot.inscripted.tasks.base.Skillcast;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.List;

import static com.amorabot.inscripted.displays.DisplayBlock.getBlockDisplayAt;

public class MaceSpecials {
    public static void earthquake(Skillcast skillcastInstance){
        if (!(skillcastInstance instanceof Attack.Basic basicAttackInstance)){return;}
        SlashConfig swingRendererData = new SlashConfig(SlashSegment::standardMaceSwing,
                18,50,2.2,0,0.2,0.2,0.4,
                new int[]{168, 107, 50}, new double[]{1,1},0.7F, 0.2);
        SlamConfig slamConfig = new SlamConfig(
                swingRendererData, Math.random() > 0.5, 20,1.2, 3.5, 20, 4);

        Slam slam = new Slam(skillcastInstance, basicAttackInstance.getAttackData(),
                slamConfig, swingRendererData.defaultRenderer(), MaceSpecials::earthquakeImpact);
    }
    private static void earthquakeImpact(Slam eq){
        Vector slamCenter = eq.getSlamCenter(); //TODO: fix fucked center location (precision varies with horizontal rotation)
        World world = eq.getOwner().getWorld();
        double slamRadius = eq.getSlamData().impactRadius();
        //Rendering
        List<Vector> externalRing = LinalgMath.plotCircleBorder(slamCenter,slamRadius,35);
        for (Vector ringPoint : externalRing){
            DisplayBlock ringDisplay = getBlockDisplayAt(ringPoint,world,0,Material.DIRT);
            double horiScale = 0.5 + (0.3 * Math.random());
            double vertiScale = 0.5 + (0.6 * Math.random());
            Transformation transformation = new Transformation(
                    new Vector3f(0F, -0.4F, 0F),
                    new AxisAngle4f((float) -Math.toRadians(Math.random()*30), 1, 0, 0),
                    new Vector3f((float) horiScale, (float) vertiScale, (float) horiScale),
                    new AxisAngle4f((float) Math.toRadians(Math.random()*45), 0, 0, 1)
            );
            ringDisplay.setTransformation(transformation);
            ringDisplay.setLerpValues(0,10);
        }
        ParticlePlotter.plotColoredCircleAt(slamCenter,world,168, 107, 50,1.5f, (float) slamRadius,15,false);
        Vector[] internalPoints = LinalgMath.plotPointsInsideHorizontalCircle(slamCenter,slamRadius,40);
        for (Vector internalPoint : internalPoints){
            ParticlePlotter.spawnColoredParticleAt(internalPoint,world,168, 107, 50,1.5f,1);
        }
        ParticlePlotter.spawnParticleAt(slamCenter,world, Particle.GUST);
        ParticlePlotter.plotDirectionalCircleAt(slamCenter,world, Particle.CRIT,0.3f,12,false,1.2f,true,0.1f);
        SoundAPI.playGenericSoundAtLocation(eq.getOwner(), slamCenter.toLocation(world), "entity.zombie.break_wooden_door", 0.3f, 0.2f);
    }

}
