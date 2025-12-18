package com.amorabot.inscripted.skill.archetypes.axe;

import com.amorabot.inscripted.combat.damage.DamageRouter;
import com.amorabot.inscripted.combat.damage.DamageSource;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.skill.annotations.DurationSkill;
import com.amorabot.inscripted.skill.routine.SkillcastData;
import com.amorabot.inscripted.skill.routine.slash.SlashPresets;
import com.amorabot.inscripted.skill.routine.slash.SlashSegment;
import com.amorabot.inscripted.skill.type.Attack;
import com.amorabot.inscripted.skill.type.PersistentAttack;
import com.amorabot.inscripted.skill.type.subroutines.DurationSubroutine;
import com.amorabot.inscripted.tasks.base.Skillcast;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;

public class AxeSpecials {
    public static void cyclone(Skillcast skillcastInstance){
        if (!(skillcastInstance instanceof PersistentAttack cycloneInstance)){return;}
        AttackData attackData = cycloneInstance.getAttackData();
        DurationSkill durationData = cycloneInstance.getDurationData();
        Player caster = cycloneInstance.getPlayer();

        DurationSubroutine cycloneRoutine = new DurationSubroutine(skillcastInstance);
        World world = caster.getWorld();

        Objects.requireNonNull(caster.getAttribute(Attribute.GENERIC_JUMP_STRENGTH)).setBaseValue(0);
        PotionEffect slow = new PotionEffect(PotionEffectType.SLOWNESS, cycloneRoutine.getTotalDuration(), 3, true, false, false);
        slow.apply(caster);


        cycloneRoutine.setRoutine(new BukkitRunnable() {
            int elapsedFrames = 0;
            double currentPhase = Math.random()*Math.PI/3;
            final float radius = 3.6f;
            final double axeLength = 1.8;
            final double amplitude = 1;
            final double oscilationAngleStep = Math.PI/60;
            final double slashAngleStep = Math.PI/36;
            final int animationStepsPerIteration = 6;
            @Override
            public void run() {
                if (this.isCancelled()) return;
                cycloneRoutine.cancelIfInvalid();

                cycloneRoutine.addElapsedTime();
                if (cycloneRoutine.isExpired()){
                    //Whatever 'expired' routines
                    Objects.requireNonNull(caster.getAttribute(Attribute.GENERIC_JUMP_STRENGTH)).setBaseValue(0.42);
                    return;
                }

                final Location eyeLevelPlayerLoc = caster.getLocation().clone().add(0,1.1,0);

                for (int i = 0; i < animationStepsPerIteration; i++) {
                    for (int frame = 0; frame < durationData.refreshRate(); frame++) { //Emulate the 'inbetween' frames
                        double xPos = Math.sin(currentPhase)*radius;
                        double zPos = Math.cos(currentPhase)*radius;
                        Vector currentTipPoint = eyeLevelPlayerLoc.toVector().clone().add(new Vector(xPos, Math.sin(oscilationAngleStep*elapsedFrames)*(amplitude/2), zPos));
                        Vector currentHandlePoint = currentTipPoint.clone().midpoint(eyeLevelPlayerLoc.toVector());

                        SlashSegment.standardAxe(SlashPresets.STANDARD_AXE.getSlashConfigData(),world).accept(new Vector[]{currentHandlePoint,currentTipPoint});
                        currentPhase+=slashAngleStep;
                    }
                }

                // Replicating Slash collision logic
                SkillcastData cycloneCastData = skillcastInstance.getCastData();
                final List<Player> nearbyEntities = (List<Player>) eyeLevelPlayerLoc.getNearbyPlayers(radius + 0.2);

                for (Player entity : nearbyEntities){
                    if (cycloneCastData.getBlacklistedEntities().contains(entity.getUniqueId())){continue;}

                    if (!caster.hasLineOfSight(entity)){continue;}
                    cycloneCastData.getAffectedEntities().add(entity.getUniqueId());

                    AttackData slashAttackData = cycloneInstance.getAttackData();
                    if (slashAttackData==null) {continue;}
                    DamageRouter.hit(caster, entity, cycloneInstance,slashAttackData, DamageSource.HIT);
                }

                elapsedFrames++;
            }
        });
        cycloneRoutine.startSubroutine(0);
    }
}
