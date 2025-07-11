package com.amorabot.inscripted.skill.archetypes.item;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.combat.buffs.Buffs;
import com.amorabot.inscripted.combat.buffs.categories.stat.StatBuff;
import com.amorabot.inscripted.item.inscription.definition.KeystoneIDs;
import com.amorabot.inscripted.combat.buffs.PlayerBuffManager;
import com.amorabot.inscripted.particle.ParticlePlotter;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.player.profile.component.HealthComponent;
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
        if (!checkAuraCast(skillcast)){return;}
        Aura auraSkillcast = (Aura) skillcast;
        final int animationSteps = 3;
        int periodInTicks = (int) (auraSkillcast.getSubroutinePeriodInSeconds() * 20);
        BukkitRunnable permafrostSubroutine =  new BukkitRunnable() {
            final AtomicInteger counter = new AtomicInteger(1);
            @Override
            public void run() {
                // Subtasks can only run if the parent skillcast is valid
                cancelIfInvalidParentAura(auraSkillcast,this);
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
        if (!checkAuraCast(skillcast)){return;}
        Aura auraSkillcast = (Aura) skillcast;
        final float radius = 2.5F; //Make it scale with AoE? :D
        int periodInTicks = (int) (auraSkillcast.getSubroutinePeriodInSeconds() * 20);
        BukkitRunnable thunderstruckSubroutine =  new BukkitRunnable() {
            final AttackData damageSnapshot = auraSkillcast.getAuraDamage();
            @Override
            public void run() {
                cancelIfInvalidParentAura(auraSkillcast,this);
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


    public static void righteousFire(Skillcast skillcast){
        if (!checkAuraCast(skillcast)){return;}
        Aura auraSkillcast = (Aura) skillcast;
        final float radius = 2.7F; //Make it scale with AoE? :D
        int periodInTicks = (int) (auraSkillcast.getSubroutinePeriodInSeconds() * 20);
        BukkitRunnable RFSubroutine =  new BukkitRunnable() {
            // TODO: custom built RF AttackData based on caster stats
            double phase = 0;
            @Override
            public void run() {
                cancelIfInvalidParentAura(auraSkillcast,this);
                righteousFireRoutine(skillcast.getPlayer(),null,radius,phase);
                phase += 25;
            }
        };
        int RFInstanceID = RFSubroutine.runTaskTimer(Inscripted.getPlugin(),0, periodInTicks).getTaskId();
        auraSkillcast.setPersistentRoutineID(RFInstanceID);
    }
    private static void righteousFireRoutine(Player caster, AttackData auraDamage, float radius, double currentPhase){
        renderRFRadius(caster,radius,50, currentPhase);
        List<LivingEntity> nearbyEntities = (List<LivingEntity>) caster.getLocation().getNearbyLivingEntities(radius+0.1);
//        for (LivingEntity entity : nearbyEntities){
//            //TODO: Damage entity
//        }
    }
    private static void renderRFRadius(Player caster, float radius, int numPoints, double currentPhase){
        final int[] color = new int[]{255, 148, 61};
        Location playerLoc = caster.getLocation();
        World world = playerLoc.getWorld();
        Vector center = playerLoc.toVector();
        Vector xAxis = new Vector(1,0,0);
        Vector zAxis = new Vector(0,0,1);
        for (int n = 0; n<numPoints; n++){
            double normalValue = Utils.getNormalizedValue();
            double easedValue = Utils.Easings.easeOutQuad(normalValue);
            double intermediateRadius = normalValue * radius;
            double randomPhase = Utils.getRandomInclusiveValue(0, 359);
            double rad = (randomPhase/180) * Math.PI;
            Vector relativeXPos = xAxis.clone().multiply(intermediateRadius* Math.cos(rad));
            Vector relativeZPos = zAxis.clone().multiply(intermediateRadius* Math.sin(rad));
            Vector randomPoint = center.clone().add(relativeXPos).add(relativeZPos);
            ParticlePlotter.spawnColoredParticleAt(randomPoint, world,
                    (int)(color[0] * easedValue),
                    (int)(color[1] * easedValue),
                    (int)(color[2] * easedValue),
                    1.3F,
                    numPoints);
            double rand = Math.random();
            if (rand>=0.8){
                ParticlePlotter.spawnParticleAt(randomPoint,world,Particle.SMOKE);
                if (rand>0.9){
                    ParticlePlotter.spawnParticleAt(randomPoint,world,Particle.FLAME);
                }
            }
        }
        double externalRadPhase = ((currentPhase % 360)/180) * Math.PI;
        Vector offset = new Vector(0,0.2,0);
        Vector relativeXPos = xAxis.clone().multiply(radius* Math.cos(externalRadPhase));
        Vector relativeZPos = zAxis.clone().multiply(radius* Math.sin(externalRadPhase));
        Vector circlingPoint = center.clone().add(relativeXPos).add(relativeZPos).add(offset);
        ParticlePlotter.spawnParticleAt(circlingPoint,world,Particle.CRIT);
        ParticlePlotter.plotColoredCircleAt(center, world,
                color[0],
                color[1],
                color[2],
                1.4F,
                radius,
                20);
        ParticlePlotter.plotColoredCircleAt(center.clone().subtract(offset), world,
                224, 176, 123,
                1.2F,
                radius,
                10);
    }


//    public static int activateWindsOfChangeFor(Player keystoneHolder, int period){
//        return new BukkitRunnable() {
//
//            final float particlesRadius = 0.8F; //Make it scale with AoE? :D
//
//            @Override
//            public void run() {
//                if (keystoneHolder.isSneaking()){return;}
//                Location playerLoc = keystoneHolder.getLocation();
//                World world = playerLoc.getWorld();
//
//                HealingBuff rejuv = new HealingBuff(Buffs.REJUVENATE);
////                int baseHealing = rejuv.getFinalHealingTick(JSONProfileManager.getProfile(keystoneHolder.getUniqueId()));
////                rejuv.createHealingTask(baseHealing, keystoneHolder, keystoneHolder);
//                PlayerBuffManager.addBuffToPlayer(rejuv, keystoneHolder);
//
//                Vector centerVec = playerLoc.toVector().clone().subtract(new Vector(0,0.3,0));
//                ParticlePlotter.plotColoredCircleAt(centerVec, world,
//                        30,
//                        210,
//                        30,
//                        1F,
//                        particlesRadius,
//                        15);
//                ParticlePlotter.plotCircleAt(centerVec, world, Particle.TOTEM_OF_UNDYING, particlesRadius+0.1F, 25);
//            }
//        }.runTaskTimer(Inscripted.getPlugin(), period, period).getTaskId();
//    }

    public static void berserk(Skillcast skillcast){
        if (!checkAuraCast(skillcast)){return;}
        Aura auraSkillcast = (Aura) skillcast;
        int periodInTicks = (int) (auraSkillcast.getSubroutinePeriodInSeconds() * 20);
        BukkitRunnable berserkSubroutine =  new BukkitRunnable() {
            boolean lastState = false;
            @Override
            public void run() {
                cancelIfInvalidParentAura(auraSkillcast,this);
                PlayerDataContainer dataContainer = PlayerDataContainer.getDataContainerFor(skillcast.getPlayerID());
                boolean hasAuraInstance = dataContainer.getActiveAuras().containsKey(skillcast.getCastedSkill());
                boolean hasKeystone = dataContainer.getEquipment().getSpecialInscriptions().getKeystones().contains(KeystoneIDs.BERSERK);
                boolean isValidActiveInstance = hasAuraInstance && hasKeystone;
                HealthComponent playerHealth = dataContainer.getProfile().getHealthComponent();
                // Berserk routine
                /*
                    Truth table
                    LL  ACTIVE  (toggle)
                    T     T       T
                    T     F       F
                    F     T       F
                    F     F       T
                */
                boolean activeBuff = playerHealth.isLowLife() == isValidActiveInstance; // !(LowLife ^ Valid)
                if (activeBuff){
                    if (!lastState){
                        Utils.error("State change triggered: " + activeBuff);
                        lastState = activeBuff;
                        return;
                    }
                    // Trigger the aura's conditional Buff
                    PlayerBuffManager.addBuffToPlayer(new StatBuff(Buffs.BERSERK, skillcast.getPlayer()),skillcast.getPlayerID());
                }
            }
        };
        int berserkInstanceID = berserkSubroutine.runTaskTimer(Inscripted.getPlugin(),periodInTicks, periodInTicks).getTaskId();
        auraSkillcast.setPersistentRoutineID(berserkInstanceID);
    }

    private static boolean checkAuraCast(Skillcast skillcast){
        boolean isAura = skillcast instanceof Aura;
        if (!(isAura)){
            Utils.error("Invalid item aura instancing");
        }
        return isAura;
    }
    private static boolean cancelIfInvalidParentAura(Aura aura, BukkitRunnable subtask){
        boolean invalidAura = !aura.isValidPersistentCast();
        if (invalidAura){
            Utils.error("Cancelling aura subtask...");
            subtask.cancel();
        }
        return invalidAura;
    }
}
