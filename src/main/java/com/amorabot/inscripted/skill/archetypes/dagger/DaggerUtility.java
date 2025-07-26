package com.amorabot.inscripted.skill.archetypes.dagger;

import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.particle.ParticlePlotter;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.skill.annotations.ProjectileSkill;
import com.amorabot.inscripted.skill.casting.CastSource;
import com.amorabot.inscripted.skill.routine.projectile.Projectile;
import com.amorabot.inscripted.skill.routine.projectile.ProjectileCollision;
import com.amorabot.inscripted.skill.routine.projectile.ProjectileConfig;
import com.amorabot.inscripted.skill.routine.projectile.ProjectileTrail;
import com.amorabot.inscripted.skill.type.Attack;
import com.amorabot.inscripted.skill.type.Utility;
import com.amorabot.inscripted.skill.type.subroutines.DurationSubroutine;
import com.amorabot.inscripted.tasks.base.Skillcast;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DaggerUtility {

    public static void smokeBomb(Skillcast skillcastInstance){
        Skills sourceSkill = skillcastInstance.getCastedSkill();
        ProjectileSkill projData = sourceSkill.getProjectileSkilLData();
        AttackData throwAttack = new AttackData(skillcastInstance.getPlayerID(),sourceSkill,PlayerDataContainer.getDataContainerFor(skillcastInstance.getPlayerID()).getGlobalStats());

        int maxRange = 30;
        ProjectileConfig projectileConfig = new ProjectileConfig(true,false,true,
                15D / 20, 0.04, 0.8,
                ProjectileTrail::smokeBombTrail, ProjectileCollision::standardDetection, ProjectileCollision::smokeBombCollision);
        Location playerLocation = skillcastInstance.getPlayer().getLocation().clone().add(0,1.2,0);
        Vector target = null;
        boolean uniqueTarget = skillcastInstance.getCastedSkill().getProjectileSkilLData().uniqueTarget();
        if (uniqueTarget){
            target = Projectile.getRaytracedMaxDistance(playerLocation, playerLocation.getDirection().clone(), maxRange);
        }

        projData.spread().instantiate(skillcastInstance,throwAttack,projectileConfig,playerLocation,target, 1, maxRange, 90D);
    }
    public static void smokeBombCloud(Skillcast skillcastInstance){
        DurationSubroutine smokebombRoutine = new DurationSubroutine(skillcastInstance);
        smokebombRoutine.setRoutine(new BukkitRunnable() {
            @Override
            public void run() {
                if (this.isCancelled()) return;
                smokebombRoutine.cancelIfInvalid();
                //Routine
                ParticlePlotter.plotDirectionalCircleAt(skillcastInstance.getCastData().getCastingContext().getOrigin(),
                        skillcastInstance.getPlayer().getWorld(), Particle.CAMPFIRE_COSY_SMOKE,1f,10,true,1.3f);

                smokebombRoutine.addElapsedTime();
            }
        });
        smokebombRoutine.startSubroutine(0);
    }
}
