package com.amorabot.inscripted.skills.attackInstances.slash;

import com.amorabot.inscripted.skills.ParticlePlotter;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public class SlashSegment {

    public static Consumer<Vector[]> standardSword(Slash slash){
        // segment =>> [handle, tip]
        return segment -> {
            Vector handle = segment[0];
            Vector tip = segment[1];

            int[] color = slash.getBaseColor();
            ParticlePlotter.coloredParticleLerp(handle, tip,0.3f, slash.getSlashWorld(), color[0], color[1], color[2], slash.getSlashData().particleSize());
            renderTip(segment, 0.1f, 0.2f, slash.getSlashData(), slash.getSlashWorld());
            ParticlePlotter.spawnParticleAt(tip, slash.getSlashWorld(), Particle.ELECTRIC_SPARK);
        };
    }

    public static Consumer<Vector[]> standardAxe(Slash slash){
        return segment -> {
            Vector handle = segment[0];
            Vector tip = segment[1];

            int[] color = slash.getBaseColor();

            ParticlePlotter.coloredParticleLerp(handle, tip,0.3f, slash.getSlashWorld(), color[0], color[1], color[2], slash.getSlashData().particleSize());
            ParticlePlotter.coloredParticleLerp(handle, tip, (float) Math.max(Math.random(), 0.1),
                    slash.getSlashWorld(), 100, 60, 60, slash.getSlashData().particleSize());
            renderTip(segment, 0.1f, 0.2f, slash.getSlashData(), slash.getSlashWorld());
            ParticlePlotter.spawnParticleAt(tip, slash.getSlashWorld(), Particle.ELECTRIC_SPARK);
        };
    }

    public static void renderTip(Vector[] segment, float minDist, float addedSize, SlashConfigDTO data, World world){
        float particleSize = data.particleSize();
        double tipPercent = data.tipPercentage();
        Vector swingMidpoint = segment[1].clone().subtract(segment[1].clone().subtract(segment[0]).multiply(tipPercent));
        ParticlePlotter.coloredParticleLerp(swingMidpoint, segment[1],minDist, world,
                Slash.tipColor[0],Slash.tipColor[1],Slash.tipColor[2], particleSize+addedSize);
    }
}
