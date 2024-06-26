package com.amorabot.inscripted.skills;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import static com.amorabot.inscripted.utils.Utils.getRandomOffset;

public class ParticlePlotter {

    public static void spawnParticleAt(Vector position, World world, Particle particle){
        ParticleBuilder particleBuilder = new ParticleBuilder(particle);
        particleBuilder.location(position.toLocation(world));
        particleBuilder.receivers(30);
        particleBuilder.count(0);
        particleBuilder.spawn();
    }
    public static void spawnOffsetParticleAt(Vector position, World world, Particle particle, double offX, double offY, double offZ){
        ParticleBuilder particleBuilder = new ParticleBuilder(particle);
        particleBuilder.location(position.toLocation(world));

        particleBuilder.offset(offX, offY, offZ);

        particleBuilder.receivers(10);
        particleBuilder.count(0);
        particleBuilder.spawn();
    }
    public static void spawnDifuseParticleAt(Vector position, World world, Particle particle, double velocity){
        ParticleBuilder particleBuilder = new ParticleBuilder(particle);
        particleBuilder.location(position.toLocation(world));
        particleBuilder.receivers(30);
        particleBuilder.extra(velocity);
        particleBuilder.spawn();
    }
    public static void spawnColoredParticleAt(Vector position, World world, int r, int g, int b, float size, int quantity){
        ParticleBuilder particle = new ParticleBuilder(Particle.DUST);
        particle.location(position.toLocation(world));
        particle.receivers(30);
        particle.color(Color.fromRGB(r,g,b), size);
        particle.count(quantity);

        particle.spawn();
    }
    public static void spawnOffsetColoredParticleAt(Vector position, World world, int r, int g, int b, float size, int quantity, double offX, double offY, double offZ){
        ParticleBuilder particle = new ParticleBuilder(Particle.DUST);
        particle.location(position.toLocation(world));

        particle.offset(offX, offY, offZ);

        particle.receivers(10);
        particle.color(Color.fromRGB(r,g,b), size);
        particle.count(quantity);

        particle.spawn();
    }
    public static void plotCircleAt(Vector center, World world, Particle particle, float radius, int points){
        double angleStep = 2*Math.PI/points;
        for (double a = 0; a < 2*Math.PI; a+=angleStep){
            double xPos = Math.sin(a)*radius;
            double zPos = Math.cos(a)*radius;
            Vector currentPoint = center.clone().add(new Vector(xPos, 0.4D, zPos));
            spawnParticleAt(currentPoint, world, particle);
        }
    }
    public static void plotColoredCircleAt(Vector center, World world, int r, int g, int b, float particSize, float radius, int points){
        double angleStep = 2*Math.PI/points;
        for (double a = 0; a < 2*Math.PI; a+=angleStep){
            double xPos = Math.sin(a)*radius;
            double zPos = Math.cos(a)*radius;
            Vector currentPoint = center.clone().add(new Vector(xPos, 0.4D, zPos));
            spawnColoredParticleAt(currentPoint, world, r,g,b, particSize, 1);
        }
    }
    public static void spawnColorTransitionParticleAt(Vector position, World world, int r1, int g1, int b1, int r2, int g2, int b2, float size, int quantity){
        ParticleBuilder particle = new ParticleBuilder(Particle.DUST_COLOR_TRANSITION);
        particle.location(position.toLocation(world));
        particle.receivers(30);
        particle.colorTransition(Color.fromRGB(r1,g1,b1), Color.fromRGB(r2, g2, b2), size);
        particle.count(quantity);

        particle.spawn();
    }
    public static void spawnBlockCrackPartileAt(Vector position, World world, Material blockMaterial, int quantity, double velocity){
        if (!blockMaterial.isBlock()){
            return;
        }
        ParticleBuilder particle = new ParticleBuilder(Particle.BLOCK);
        BlockData blockData = blockMaterial.createBlockData();
        particle.location(world,position.getX(),position.getY(),position.getZ());
        particle.receivers(30);
        particle.data(blockData);
        particle.count(quantity);
        particle.extra(velocity);
//        particle.offset(20, 20, 20);
        particle.spawn();
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
//        spawnParticleAt(particlePos, world, Particle.ELECTRIC_SPARK);
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
//        spawnParticleAt(particlePos, world, Particle.ELECTRIC_SPARK);
    }

    public static void thunderAt(Location loc, int segments, double height){
        double heightStep = height/segments;
        Vector baseHeight = loc.toVector().clone();
        for (int i = 0; i < segments; i++){
            double xOff = (0.5+i)*getRandomOffset();
            double zOff = (0.5+i)*getRandomOffset();
            Vector segmentEnd = new Vector(xOff, heightStep*(Math.max(0.6, Math.random())), zOff).add(baseHeight);
            coloredParticleLerp(baseHeight, segmentEnd, 0.3F, loc.getWorld(),
                    255, 240, 200, 1.5F);
            baseHeight = segmentEnd;
        }
    }
}
