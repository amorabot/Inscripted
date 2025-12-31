package com.amorabot.inscripted.skill.archetypes.wand;

import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.skill.routine.projectile.Projectile;
import com.amorabot.inscripted.skill.routine.projectile.ProjectileCollision;
import com.amorabot.inscripted.skill.routine.projectile.ProjectileConfig;
import com.amorabot.inscripted.skill.routine.projectile.ProjectileTrail;
import com.amorabot.inscripted.skill.type.Attack;
import com.amorabot.inscripted.skill.annotations.ProjectileSkill;
import com.amorabot.inscripted.tasks.base.Skillcast;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class WandBasicAttacks {

    public static void standardWandAttack(Skillcast skillcast){
        if (!(skillcast instanceof Attack.Basic basicAttackInstance)){return;}
        Skills sourceSkill = skillcast.getCastData().getCastingContext().getSkillUsed();
        ProjectileSkill projData = sourceSkill.getProjectileSkilLData();
        AttackData attackData = basicAttackInstance.getAttackData();

        int maxRange = 12;
        int projectiles = PlayerDataContainer.getProfile(skillcast.getPlayerID()).getDamageComponent().getExtraProjectiles() + projData.baseProjectiles();
        ProjectileConfig projectileConfig = new ProjectileConfig(false,false,true,
                30D / 20, 0.09, 1.2,
                ProjectileTrail::basicWand, ProjectileCollision::standardMultiprojDetection, ProjectileCollision::testCollisionExecution);
        Location playerLocation = skillcast.getPlayer().getLocation().clone().add(0,1.4,0);
        Vector target = null;
        boolean uniqueTarget = skillcast.getCastData().getCastingContext().getSkillUsed().getProjectileSkilLData().uniqueTarget();
        if (uniqueTarget){
            target = Projectile.getRaytracedMaxDistance(playerLocation, playerLocation.getDirection().clone(), maxRange);
        }

        projData.spread().instantiate(skillcast,attackData,projectileConfig,playerLocation,target, projectiles, maxRange, 12);
    }
}
