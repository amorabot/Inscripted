package com.amorabot.inscripted.skills.item;

import com.amorabot.inscripted.APIs.damageAPI.DamageRouter;
import com.amorabot.inscripted.APIs.damageAPI.DamageSource;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.buffs.Buffs;
import com.amorabot.inscripted.components.buffs.categories.healing.HealingBuff;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerBuffManager;
import com.amorabot.inscripted.skills.ParticlePlotter;
import com.amorabot.inscripted.skills.PlayerAbilities;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class ItemPassiveAbilities {

    public static int activatePermafrost(Player keystoneHolder, int period, int animationSteps){
        return new BukkitRunnable() {

            int counter = 1;
            final float radius = 3; //Make it scale with AoE? :D

            @Override
            public void run() {
                Location playerLoc = keystoneHolder.getLocation();

                float radiusStep = radius /animationSteps;
                int colorVariance = 45;
                float colorStep = ((float) colorVariance) / animationSteps;

                ParticlePlotter.plotColoredCircleAt(playerLoc.toVector(),playerLoc.getWorld(),
                        (int) (200 - (counter-1)*colorStep),
                        (int) (200 - (counter-1)*colorStep),
                        255,
                        1.3F,
                        radiusStep*counter,
                        30);
                counter++;
                if (counter>3){
                    PotionEffect slowness = new PotionEffect(PotionEffectType.SLOWNESS, (int)(period*1.1), 0, true, false, false);
                    ParticlePlotter.plotCircleAt(playerLoc.toVector(),playerLoc.getWorld(), Particle.END_ROD, radius, 40);
                    for (Player p : playerLoc.getNearbyPlayers(radius)){
                        slowness.apply(p);
                    }
                    counter = 1;
                }
            }
        }.runTaskTimer(Inscripted.getPlugin(), period, period/animationSteps).getTaskId();
    }
    public static int activateThunderstruck(Player keystoneHolder, int period){
        return new BukkitRunnable() {

            final float radius = 2.5F; //Make it scale with AoE? :D

            @Override
            public void run() {
                if (keystoneHolder.isSneaking()){return;}
                Location playerLoc = keystoneHolder.getLocation();
                World world = playerLoc.getWorld();

                ParticlePlotter.plotColoredCircleAt(playerLoc.toVector(), world,
                        240,
                        200,
                        100,
                        1.3F,
                        radius,
                        30);
                ParticlePlotter.plotCircleAt(playerLoc.toVector(), world, Particle.ELECTRIC_SPARK, radius, 20);
                List<LivingEntity> nearbyEntities = (List<LivingEntity>) playerLoc.getNearbyLivingEntities(radius+0.1);
                //TODO: make isMob() and isPlayer() for inscripted entities
                for (LivingEntity entity : nearbyEntities){
                    ParticlePlotter.thunderAt(entity.getLocation().clone(), 4, 16);
                    DamageRouter.playerAttack(keystoneHolder, entity, DamageSource.HIT, PlayerAbilities.THUNDERSTRUCK_PASSIVE);
                }
            }
        }.runTaskTimer(Inscripted.getPlugin(), period, period).getTaskId();
    }
    public static int activateWindsOfChangeFor(Player keystoneHolder, int period){
        return new BukkitRunnable() {

            final float particlesRadius = 0.8F; //Make it scale with AoE? :D

            @Override
            public void run() {
                if (keystoneHolder.isSneaking()){return;}
                Location playerLoc = keystoneHolder.getLocation();
                World world = playerLoc.getWorld();

                HealingBuff rejuv = new HealingBuff(Buffs.REJUVENATE);
                int baseHealing = rejuv.getFinalHealingTick(JSONProfileManager.getProfile(keystoneHolder.getUniqueId()));
                rejuv.createHealingTask(baseHealing, keystoneHolder, keystoneHolder);
                PlayerBuffManager.addBuffToPlayer(rejuv, keystoneHolder);

                Vector centerVec = playerLoc.toVector().clone().subtract(new Vector(0,0.3,0));
                ParticlePlotter.plotColoredCircleAt(centerVec, world,
                        30,
                        210,
                        30,
                        1F,
                        particlesRadius,
                        15);
                ParticlePlotter.plotCircleAt(centerVec, world, Particle.TOTEM_OF_UNDYING, particlesRadius+0.1F, 25);
            }
        }.runTaskTimer(Inscripted.getPlugin(), period, period).getTaskId();
    }
}
