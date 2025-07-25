package com.amorabot.inscripted.skill.archetypes.item;

import com.amorabot.inscripted.combat.buffs.Buffs;
import com.amorabot.inscripted.combat.buffs.categories.healing.HealingBuff;
import com.amorabot.inscripted.combat.buffs.categories.stat.StatBuff;
import com.amorabot.inscripted.combat.damage.DamageRouter;
import com.amorabot.inscripted.combat.damage.DamageSource;
import com.amorabot.inscripted.item.inscription.definition.KeystoneIDs;
import com.amorabot.inscripted.combat.buffs.PlayerBuffManager;
import com.amorabot.inscripted.particle.ParticlePlotter;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.player.profile.component.HealthComponent;
import com.amorabot.inscripted.skill.type.Aura;
import com.amorabot.inscripted.skill.type.subroutines.AuraSubroutine;
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

    public static void registerPermafrost(Skillcast skillcast){
        AuraSubroutine permafrostSubroutine = new AuraSubroutine(skillcast);
        permafrostSubroutine.setRoutine(new BukkitRunnable() {
            final AtomicInteger counter = new AtomicInteger(1);
            final int animationSteps = 3;
            @Override
            public void run() {
                if (this.isCancelled()){return;}
                if (!permafrostSubroutine.checkParentCast(skillcast)) {
                    permafrostSubroutine.shutdown();
                    return;
                }
                permafrostRoutine(skillcast.getPlayer().getLocation()
                        ,counter, (int) (((Aura) skillcast).getSubroutinePeriodInSeconds() * 20)
                        ,animationSteps);
            }
        });
        permafrostSubroutine.startSubroutine(0);
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
            PotionEffect slowness = new PotionEffect(PotionEffectType.SLOWNESS, (int)(3*(permafrostPeriod)*1.1), 0, true, false, false);
            ParticlePlotter.plotCircleAt(centerLocation.toVector(),centerLocation.getWorld(), Particle.FALLING_WATER, (float) 3.0, 40);
            for (Player p : centerLocation.getNearbyPlayers((float) 3.0)){
                slowness.apply(p);
            }
            counter.set(1);
        }
    }

    public static void registerThunderstruck(Skillcast skillcast){
        AuraSubroutine thunderstruckSubroutine = new AuraSubroutine(skillcast);
        thunderstruckSubroutine.setRoutine(new BukkitRunnable() {
            final AttackData damageSnapshot = ((Aura) skillcast).getAuraDamage();
            final float radius = 2.5F;
            @Override
            public void run() {
                if (this.isCancelled()){return;}
                if (!thunderstruckSubroutine.checkParentCast(skillcast)) {
                    thunderstruckSubroutine.shutdown();
                    return;
                }
                thunderstruckRoutine(skillcast.getPlayer(),skillcast, damageSnapshot,radius);
            }
        });
        thunderstruckSubroutine.startSubroutine(0);
    }
    private static void thunderstruckRoutine(Player caster, Skillcast thunderstruckSkillcast, AttackData auraDamage, float radius){
        if (caster.isSneaking()){return;}
        Location playerLoc = caster.getLocation();
        World world = playerLoc.getWorld();
        ParticlePlotter.plotColoredCircleAt(playerLoc.toVector(), world, 160,160,160, 1.5F, radius, 16);
        ParticlePlotter.plotDirectionalCircleAt(playerLoc.toVector(),world, Particle.ELECTRIC_SPARK, (radius-0.1f), 16, true, 1.2f);
        ParticlePlotter.plotDirectionalCircleAt(playerLoc.toVector(),world,Particle.ELECTRIC_SPARK, (radius/2), 16, true, 1.2f);
        List<Player> nearbyEntities = (List<Player>) playerLoc.getNearbyPlayers(radius+0.1);
        for (Player entity : nearbyEntities){
            ParticlePlotter.thunderAt(entity.getLocation().clone(), 4, 16);
            if (entity.equals(caster)) {
                DamageRouter.hit(caster,entity,thunderstruckSkillcast,auraDamage, DamageSource.SELF);
                continue;
            }
            DamageRouter.hit(caster,entity,thunderstruckSkillcast,auraDamage, DamageSource.HIT);
        }
    }

    public static void registerRighteousFire(Skillcast skillcast){
        AuraSubroutine rfSubroutine = new AuraSubroutine(skillcast);
        rfSubroutine.setRoutine(new BukkitRunnable() {
            // TODO: custom built RF AttackData based on caster stats
            double phase = 0;
            final float radius = 2.7F;
            @Override
            public void run() {
                if (this.isCancelled()){return;}
                if (!rfSubroutine.checkParentCast(skillcast)) {
                    rfSubroutine.shutdown();
                    return;
                }
                righteousFireRoutine(skillcast.getPlayer(),null,radius,phase);
                phase += 25;
            }
        });
        rfSubroutine.startSubroutine(0);
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

    public static void registerWindsOfChange(Skillcast skillcast){
        AuraSubroutine wofSubroutine = new AuraSubroutine(skillcast);
        wofSubroutine.setRoutine(new BukkitRunnable() {
            final float particlesRadius = 0.8F;
            final Player owner = skillcast.getPlayer();
            @Override
            public void run() {
                if (owner.isSneaking() || this.isCancelled()){return;}
                if (!wofSubroutine.checkParentCast(skillcast)) {
                    wofSubroutine.shutdown();
                    return;
                }
                Location playerLoc = owner.getLocation();
                World world = playerLoc.getWorld();

                HealingBuff rejuv = new HealingBuff(Buffs.REJUVENATE);
                int baseHealing = rejuv.getFinalHealingTick(PlayerDataContainer.getProfile(skillcast.getPlayerID()));
                rejuv.createHealingTask(baseHealing, owner, owner);
                PlayerBuffManager.addBuffToPlayer(rejuv, skillcast.getPlayerID());

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
        });

        wofSubroutine.startSubroutine(5);
    }

    /*
        Truth table
        LL  ACTIVE  (toggle)
        T     T       T
        T     F       F
        F     T       F
        F     F       T
    */
    public static void registerBerserk(Skillcast skillcast){
        AuraSubroutine berserkSubroutine = new AuraSubroutine(skillcast);
        berserkSubroutine.setRoutine(new BukkitRunnable() {
            boolean lastState = false;
            @Override
            public void run() {
                if (this.isCancelled()){return;}
                if (!berserkSubroutine.checkParentCast(skillcast)) {
                    berserkSubroutine.shutdown();
                    return;
                }
                PlayerDataContainer dataContainer = PlayerDataContainer.getDataContainerFor(skillcast.getPlayerID());
                boolean hasAuraInstance = dataContainer.getActiveAuras().containsKey(skillcast.getCastedSkill());
                boolean hasKeystone = dataContainer.getEquipment().getSpecialInscriptions().getKeystones().contains(KeystoneIDs.BERSERK);
                boolean isValidActiveInstance = hasAuraInstance && hasKeystone;
                HealthComponent playerHealth = dataContainer.getProfile().getHealthComponent();
                // Berserk routine
                boolean activeBuff = playerHealth.isLowLife() == isValidActiveInstance; // !(LowLife ^ Valid)
                if (activeBuff){
                    if (!lastState){
                        lastState = activeBuff;
                        return;
                    }
                    // Trigger the aura's conditional Buff
                    PlayerBuffManager.addBuffToPlayer(new StatBuff(Buffs.BERSERK, skillcast.getPlayer()),skillcast.getPlayerID());
                }
            }
        });
        berserkSubroutine.startSubroutine(0);
    }
}
