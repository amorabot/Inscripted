package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.displays.DisplayBlock;
import com.amorabot.inscripted.displays.Models;
import com.amorabot.inscripted.gui.instances.RelicSelection;
import com.amorabot.inscripted.player.Archetypes;
import com.amorabot.inscripted.combat.buffs.Buffs;
import com.amorabot.inscripted.combat.buffs.categories.damage.DamageBuff;
import com.amorabot.inscripted.combat.buffs.categories.stat.StatBuff;
import com.amorabot.inscripted.combat.buffs.PlayerBuffManager;
import com.amorabot.inscripted.math.LinalgMath;
import com.amorabot.inscripted.particle.ParticlePlotter;
import com.amorabot.inscripted.player.renderer.HealthBarGenerator;
import com.amorabot.inscripted.skill.routine.projectile.Projectile;
import com.amorabot.inscripted.math.OrientedBoundingBox;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.DelayedTask;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class TemplateCommand implements CommandExecutor {

    /*
    entity.wither_skeleton.ambient
    wither.break_block
    minecraft:entity.arrow.hit_player
    entity.player.attack.knockback - dish dish

     */

    public static Skeleton testDummy = null;
    private static DisplayBlock testDisplay = null;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (!(commandSender instanceof Player)){
            return false;
        }
        Player player = (Player) commandSender;
        World playerWorld = player.getWorld();

        if (strings.length == 1){
            String action = strings[0];
            switch (action){
                case "bar":
                    HealthBarGenerator.getHealthBarSegmentsFor(player);
                    break;
                case "toggle":
                    //Not persistent (ideal for temporary tags/ownership/toggles that are not essential in combat) -> if persistance is needed: scoreboard tags
                    if (!player.hasMetadata("Player")){
                        player.sendMessage("you are not a player!, turning you into one");
                        player.setMetadata("Player", new FixedMetadataValue(Inscripted.getPlugin(), "im a player!"));
                    } else {
                        player.sendMessage("you are a player! not anymore");
                        player.removeMetadata("Player", Inscripted.getPlugin());
                    }
                    return true;
                case "color":
                    String temp = "&"+ Archetypes.GLADIATOR.getColorOnPalette() + " :D " + "&7testando";
                    player.sendMessage(temp);
                    player.sendMessage(ColorUtils.decolor(temp));
                    player.sendMessage(ColorUtils.translateColorCodes(temp));
                    return true;
                case "bleed":
                    DamageBuff bleed = new DamageBuff(Buffs.BLEED);
                    int baseDamage = (int) (Math.random()*100);
                    Utils.error("Current base DoT: " + baseDamage);
                    int[] dot = bleed.convertBaseHit(baseDamage);
                    bleed.createDamageTask(dot, player, true, player);

//                    PlayerBuffManager.addBuffToPlayer(bleed, player);
//                    bleed.activate();
                    return true;
                case "stat":
                    StatBuff fortify = new StatBuff(Buffs.BERSERK, player);
                    Utils.log("Applying berserk to " + player.getName());
                    PlayerBuffManager.addBuffToPlayer(fortify, player.getUniqueId());
                    return true;
                case "tailwind":
                    StatBuff tailwind = new StatBuff(Buffs.TAILWIND, player);
                    player.sendMessage("Applying tailwind!");
                    PlayerBuffManager.addBuffToPlayer(tailwind, player.getUniqueId());
                    return true;
                case "cripple":

                    return true;
                case "rejuv":

                    return true;
                case "ui":
                    new RelicSelection(player).open();
                    return true;
                case "circle":
                    Location loc = player.getLocation().clone().add(0,1.5,0);
                    Vector raytracePos = Projectile.getRaytracedMaxDistance(loc, loc.getDirection(), 10);
                    ParticlePlotter.spawnParticleAt(raytracePos, loc.getWorld(), Particle.GUST);
//                    Vector[] points = LinalgMath.plotPointsInsideNonAlignedCircle(raytracePos,loc.getDirection(), 6, 200);
                    Vector[] points = LinalgMath.plotPointsInsideNonAlignedCircle(raytracePos,loc.getDirection(), 6, 12);
//                    Vector[] points = LinalgMath.plotNonAlignedCircleBorder(raytracePos,loc.getDirection(), 6, 100);
                    for (Vector point : points){
                        ParticlePlotter.spawnParticleAt(point, loc.getWorld(), Particle.END_ROD);
                    }

                    Vector zAxis = loc.getDirection();
                    Vector xAxis = zAxis.clone().crossProduct(new Vector(0,1,0)).normalize();
                    Vector yAxis = xAxis.clone().crossProduct(zAxis);
                    OrientedBoundingBox spreadOBB = new OrientedBoundingBox(points, new Vector[]{xAxis,yAxis,zAxis});
//                    spreadOBB.expandDirectional(2, true, 3);
                    spreadOBB.expandFromCenter(3);
                    spreadOBB.render(playerWorld);
                    if (spreadOBB.intersects(player.getBoundingBox())){Utils.msgPlayer(player, "CollisioN!");}
                    return true;
                case "banner":
                    Models.instantiateWarBanner(player.getLocation().toVector(),playerWorld,60);
                    return true;
                case "tp":
                    if (testDisplay==null) return false;
                    new DelayedTask(new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (testDisplay != null){
                                testDisplay.teleportTo(player.getLocation());
                                testDisplay.scale(2.5 * Math.random());
                                testDisplay.setLerpValues(0,20);

//                                testDisplay.getBlock().setDisplayHeight(3);
//                                testDisplay.teleport(player.getLocation());

//                                Vector pos = player.getLocation().toVector();
//                                testDisplay.setTeleportDuration(10);
//                                Transformation delayedTrans = testDisplay.getTransformation();
//                                testDisplay.setInterpolationDelay(-1);
//                                testDisplay.setInterpolationDuration(40);
//                                delayedTrans.getTranslation().set(pos.getX(),pos.getY(),pos.getZ());
//                                delayedTrans.getTranslation().lerp(new Vector3f((float) pos.getX(), (float) pos.getY(), (float) pos.getZ()),0.5f);
//                                testDisplay.setTransformation(delayedTrans);
                            }
                        }
                    },20);
                    return true;
                case "create":
                    testDisplay = new DisplayBlock(player.getLocation().toVector(),playerWorld,Material.BLACK_BANNER,100,true);
                    testDisplay.setTpLerp(10);
//                    Transformation trans = test.getTransformation();
//                    trans.getScale().set(10);
//                    trans.getLeftRotation().y = 0.5f;

                    return true;
                case "lerp":
                    if (testDisplay==null) return false;
                    int lerpDuration = 20;
                    Matrix4f mat = new Matrix4f().scale(0.5F); // scale to 0.5x - smaller item
                    testDisplay.animateKeyframes(mat,lerpDuration,baseMatrix -> baseMatrix.rotateY(((float) Math.toRadians(180)) + 0.1F));
                    return true;
            }
        }

        return true;
    }
}
