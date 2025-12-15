package com.amorabot.inscripted.skill.routine.slash;

import com.amorabot.inscripted.particle.ParticlePlotter;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public class SlashSegment {
    // segment =>> [handle, tip]

    public static Consumer<Vector[]> standardSword(SlashConfig slashData, World worldToPlot){
        return segment -> {
            Vector handle = segment[0];
            Vector tip = segment[1];

            int[] color = slashData.baseColor();
            ParticlePlotter.coloredParticleLerp(handle, tip,0.3f, worldToPlot, color[0], color[1], color[2], slashData.particleSize());
            renderTip(segment, 0.1f, 0.2f, slashData, worldToPlot);
            ParticlePlotter.spawnParticleAt(tip, worldToPlot, Particle.ELECTRIC_SPARK);
        };
    }

    public static Consumer<Vector[]> standardAxe(SlashConfig slashData, World worldToPlot){
        return segment -> {
            Vector handle = segment[0];
            Vector tip = segment[1];

            int[] color = slashData.baseColor();

            ParticlePlotter.coloredParticleLerp(handle, tip,0.3f, worldToPlot, color[0], color[1], color[2], slashData.particleSize());
            ParticlePlotter.coloredParticleLerp(handle, tip, (float) Math.max(Math.random(), 0.1),
                    worldToPlot, 100, 60, 60, slashData.particleSize());
            renderTip(segment, 0.1f, 0.2f, slashData, worldToPlot);
            ParticlePlotter.spawnParticleAt(tip, worldToPlot, Particle.ELECTRIC_SPARK);
        };
    }

    public static Consumer<Vector[]> standardMaceSwing(SlashConfig slashData, World worldToPlot){
        return segment -> {
            Vector handle = segment[0];
            Vector tip = segment[1];

            int[] color = slashData.baseColor();
            ParticlePlotter.coloredParticleLerp(handle, tip,0.15f, worldToPlot, color[0], color[1], color[2], slashData.particleSize());
            renderTip(segment, 0.1f, 0.1f, slashData, worldToPlot);
            ParticlePlotter.spawnParticleAt(tip, worldToPlot, Particle.ELECTRIC_SPARK);
        };
    }

    public static Consumer<Vector[]> defaultTippedSlash(SlashConfig slashData, World worldToPlot){
        return segment -> {
            Vector handle = segment[0];
            Vector tip = segment[1];

            int[] color = slashData.baseColor();
            ParticlePlotter.coloredParticleLerp(handle, tip,0.15f, worldToPlot, color[0], color[1], color[2], slashData.particleSize());
            renderTip(segment, 0.1f, 0.1f, slashData, worldToPlot);
        };
    }
    public static Consumer<Vector[]> bloody(SlashConfig slashData, World worldToPlot){
        return segment -> {
            Vector handle = segment[0];
            Vector tip = segment[1];

            int[] color = slashData.baseColor();
            ParticlePlotter.coloredParticleLerp(handle, tip,0.15f, worldToPlot, color[0], color[1], color[2], slashData.particleSize());
            ParticlePlotter.coloredParticleLerp(handle, tip,0.3f, worldToPlot, 35, 10, 30, slashData.particleSize());
            renderTip(segment, 0.1f, 0.1f, slashData, worldToPlot, 92, 61, 64);
        };
    }

    public static void renderTip(Vector[] segment, float minDist, float addedSize, SlashConfig data, World world){
        float particleSize = data.particleSize();
        double tipPercent = data.tipPercentage();
        if (tipPercent<=0){return;}
        Vector swingMidpoint = segment[1].clone().subtract(segment[1].clone().subtract(segment[0]).multiply(tipPercent));
        ParticlePlotter.coloredParticleLerp(swingMidpoint, segment[1],minDist, world,
                Slash.tipColor[0],Slash.tipColor[1],Slash.tipColor[2], particleSize+addedSize);
    }
    public static void renderTip(Vector[] segment, float minDist, float addedSize, SlashConfig data, World world, int r, int g, int b){
        float particleSize = data.particleSize();
        double tipPercent = data.tipPercentage();
        if (tipPercent<=0){return;}
        Vector swingMidpoint = segment[1].clone().subtract(segment[1].clone().subtract(segment[0]).multiply(tipPercent));
        ParticlePlotter.coloredParticleLerp(swingMidpoint, segment[1],minDist, world,
                r,g,b, particleSize+addedSize);
    }
}
