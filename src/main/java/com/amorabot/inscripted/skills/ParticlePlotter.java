package com.amorabot.inscripted.skills;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ParticlePlotter {

    public static void spawnParticleAt(Vector position, World world, Particle particle){
        world.spawnParticle(particle, position.getX(), position.getY(), position.getZ(), 0, 0, 0, 0);
    }
    public static void spawnColoredParticleAt(Vector position, World world, int r, int g, int b, float size, int quantity){
        Particle.DustOptions dustColor = new Particle.DustOptions(Color.fromRGB(r, g, b), size);
        world.spawnParticle(Particle.REDSTONE, position.toLocation(world), quantity, dustColor);
    }
    public static void spawnColorTransitionParticleAt(Vector position, World world, int r1, int g1, int b1, int r2, int g2, int b2, float size, int quantity){
        Particle.DustTransition dustTransition = new Particle.DustTransition(Color.fromRGB(r1,g1,b1), Color.fromRGB(r2, g2, b2), size);
        world.spawnParticle(Particle.DUST_COLOR_TRANSITION, position.toLocation(world), quantity, dustTransition);
    }
    public static void spawnBlockCrackPartileAt(Vector position, World world, Material blockMaterial){
        if (!blockMaterial.isBlock()){
            return;
        }
        ItemStack blockCrackData = new ItemStack(blockMaterial);
        world.spawnParticle(Particle.BLOCK_CRACK, position.getX(), position.getY(), position.getZ(), 0, 0, 0, 0, blockCrackData);
    }

    public static void lerpParticlesBetween(Vector begin, Vector end, float minDist, Particle particle, World world){
        int particleCount = (int) Math.round(Math.max(begin.distance(end)/minDist, 1));

        double xStep = (end.getX() - begin.getX())/particleCount;
        double yStep = (end.getY() - begin.getY())/particleCount;
        double zStep = (end.getZ() - begin.getZ())/particleCount;
        Vector step = new Vector(xStep, yStep, zStep);
        Vector particlePos = begin.clone();

        //Render the first particle at the start
        spawnParticleAt(particlePos, world, particle);
        //Increment the new particle position "particleCount" times
        for (int index = 0; index < particleCount; index++){
            particlePos.add(step);
            spawnParticleAt(particlePos, world, particle);
        }
        spawnParticleAt(particlePos, world, Particle.ELECTRIC_SPARK);
    }
    public static void coloredParticleLerp(Vector begin, Vector end, float minDist, World world, int r, int g, int b, float size){
        int particleCount = (int) Math.round(Math.max(begin.distance(end)/minDist, 1));

        double xStep = (end.getX() - begin.getX())/particleCount;
        double yStep = (end.getY() - begin.getY())/particleCount;
        double zStep = (end.getZ() - begin.getZ())/particleCount;
        Vector step = new Vector(xStep, yStep, zStep);
        Vector particlePos = begin.clone();

        //Render the first particle at the start
        spawnColoredParticleAt(particlePos, world, r,g,b, size, 1);
        //Increment the new particle position "particleCount" times
        for (int index = 0; index < particleCount; index++){
            particlePos.add(step);
            spawnColoredParticleAt(particlePos, world, r,g,b, size, 1);
        }
        spawnParticleAt(particlePos, world, Particle.ELECTRIC_SPARK);
    }
}
