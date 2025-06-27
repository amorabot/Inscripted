package com.amorabot.inscripted.skill.item;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.buffs.Buffs;
import com.amorabot.inscripted.components.buffs.categories.healing.HealingBuff;
import com.amorabot.inscripted.managers.PlayerBuffManager;
import com.amorabot.inscripted.particle.ParticlePlotter;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.skill.type.Aura;
import com.amorabot.inscripted.tasks.base.Skillcast;
import com.amorabot.inscripted.utils.Utils;
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
import java.util.concurrent.atomic.AtomicInteger;

public class ItemAuras {

    // TODO: Encapsulate skill routine logic inside -> Keystones || Separate class

    public static void permafrost(Skillcast skillcast){
        // Is Aura check
        if (!(skillcast instanceof Aura auraSkillcast)){
            Utils.error("Invalid item aura instancing");
            return;
        }
        final int animationSteps = 3;
        int periodInTicks = (int) (auraSkillcast.getSubroutinePeriodInSeconds() * 20);
        BukkitRunnable permafrostSubroutine =  new BukkitRunnable() {
            final AtomicInteger counter = new AtomicInteger(1);
            @Override
            public void run() {
                // Subtasks can only run if the parent skillcast is valid
                if (!auraSkillcast.isValidPersistentCast()){
                    Utils.error("Cancelling aura subtask...");
                    this.cancel();
                    return;
                }
                // Actual skill logic
                permafrostRoutine(skillcast.getPlayer().getLocation(),counter, periodInTicks, animationSteps);
            }
        };
        // Subtask instancing
        int permafrostInstanceID = permafrostSubroutine.runTaskTimer(Inscripted.getPlugin(),0, periodInTicks/animationSteps).getTaskId();
        auraSkillcast.setPersistentRoutineID(permafrostInstanceID);
    }
    private static void permafrostRoutine(Location centerLocation, AtomicInteger counter, int permafrostPeriod, int animationSteps){
        float radiusStep = (float) 3.0 / animationSteps;
        int colorVariance = 45;
        float colorStep = ((float) colorVariance) / animationSteps;

        ParticlePlotter.plotColoredCircleAt(centerLocation.toVector(),centerLocation.getWorld(),
                (int) (200 - (counter.get()-1)*colorStep),
                (int) (200 - (counter.get()-1)*colorStep),
                255,
                1.3F,
                radiusStep*counter.get(),
                30);
        counter.getAndIncrement();

        if (counter.get() > 3){
            PotionEffect slowness = new PotionEffect(PotionEffectType.SLOWNESS, (int)(permafrostPeriod*1.1), 0, true, false, false);
            ParticlePlotter.plotCircleAt(centerLocation.toVector(),centerLocation.getWorld(), Particle.FALLING_WATER, (float) 3.0, 40);
            for (Player p : centerLocation.getNearbyPlayers((float) 3.0)){
                slowness.apply(p);
            }
            counter.set(1);
        }
    }


    public static void thunderstruck(Skillcast skillcast){
        if (!(skillcast instanceof Aura auraSkillcast)){
            Utils.error("Invalid item aura instancing");
            return;
        }
        final float radius = 2.5F; //Make it scale with AoE? :D
        int periodInTicks = (int) (auraSkillcast.getSubroutinePeriodInSeconds() * 20);
        BukkitRunnable thunderstruckSubroutine =  new BukkitRunnable() {
            final AttackData damageSnapshot = auraSkillcast.getAuraDamage();
            @Override
            public void run() {
                if (!auraSkillcast.isValidPersistentCast()){
                    Utils.error("Cancelling aura subtask...");
                    this.cancel();
                    return;
                }
                thunderstruckRoutine(skillcast.getPlayer(),damageSnapshot,radius);
            }
        };
        int thunderstruckInstanceID = thunderstruckSubroutine.runTaskTimer(Inscripted.getPlugin(),0, periodInTicks).getTaskId();
        auraSkillcast.setPersistentRoutineID(thunderstruckInstanceID);
    }
    private static void thunderstruckRoutine(Player caster, AttackData auraDamage, float radius){
        if (caster.isSneaking()){return;}
        Location playerLoc = caster.getLocation();
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
        for (LivingEntity entity : nearbyEntities){
            ParticlePlotter.thunderAt(entity.getLocation().clone(), 4, 16);
            //TODO: Damage entity
        }
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
//                int baseHealing = rejuv.getFinalHealingTick(JSONProfileManager.getProfile(keystoneHolder.getUniqueId()));
//                rejuv.createHealingTask(baseHealing, keystoneHolder, keystoneHolder);
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
