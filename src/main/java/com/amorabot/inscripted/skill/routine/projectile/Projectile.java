package com.amorabot.inscripted.skill.routine.projectile;

import com.amorabot.inscripted.Inscripted;
//import com.amorabot.inscripted.skill.PlayerAbilities;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.skill.SteeringBehaviors;
import com.amorabot.inscripted.skill.annotations.ProjectileSkill;
import com.amorabot.inscripted.tasks.base.Skillcast;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.amorabot.inscripted.utils.Utils.limitVector;

@Getter
public class Projectile{

    public static final Vector GRAVITY_VEC = new Vector(0, -0.05, 0);

    private final Skillcast skillcast;
    private final AttackData attackData;

    private final Vector origin;
    private Vector velocity;
    private final Vector baseAcceleration;
    private Vector target;
    private final boolean gravity;
    private final boolean ignoreBlocks;
    private final boolean destroyOnContact;

    private final double maxSpeed;
    private final double maxForce;
    private final double maxTravelDistance;
    private final double detectionRange;

    @Setter
    private boolean valid = true;

    @Setter
    private SteeringBehaviors behavior;

    @Setter
    private Consumer<Projectile> trailRenderer;
    @Setter
    private Function<Projectile, Boolean> collisionDetection;
    private Consumer<Skillcast> collisionImpact;

    public Projectile(Skillcast skillcast, AttackData attackData, Vector initialPos, Vector baseVelocity, Vector baseAcceleration, Vector targetPos,
                      double maxTravelDistance,
                      ProjectileConfig projConfig){
        this.skillcast = skillcast;
        this.attackData = attackData;

        this.origin = initialPos;
        this.velocity = baseVelocity;
        this.baseAcceleration = baseAcceleration;
        this.target = targetPos;
        //TODO: check for special Keystone rules/spreads
        ProjectileSkill projSkillData = skillcast.getCastData().getCastingContext().getSkillUsed().getProjectileSkilLData();
        if (target == null || projSkillData.spread().equals(ProjectileGenerators.RADIAL)){
            this.behavior = SteeringBehaviors.STRAIGHT_LINE;
        } else {
            this.behavior = projSkillData.defaultSteering();
        }

        this.gravity = projConfig.hasGravity();
        this.ignoreBlocks = projConfig.ignoreBlocks();
        this.destroyOnContact = projConfig.destroyOnContact();
        this.maxSpeed = projConfig.maxSpeed();
        this.maxForce = projConfig.maxForce();
        this.maxTravelDistance = maxTravelDistance;
        this.detectionRange = projConfig.detectionRange();

        this.trailRenderer = projConfig.trail();
        this.collisionDetection = projConfig.collisionDetection();
        this.collisionImpact = projConfig.impactRoutine();

        execute();
    }

    public void execute() {
        final int maxIterations = 100;
        final int subSteps = 2;
        int taskID = new BukkitRunnable(){
            int iterations = 0;
            @Override
            public void run() {
                if (iterations >= maxIterations || origin.distance(target) < 1 || !valid){
                    this.cancel();
                    return;
                }

                for (int i = 0; i<subSteps; i++){
                    update();
                    iterations++;
                    //ONLY WORKS ASSUMING PROJECTILES AT FULL-SPEED AT ALL TIMES
                    if ((maxSpeed*iterations)>maxTravelDistance){setValid(false);}
                }


            }
        }.runTaskTimer(Inscripted.getPlugin(),0, 1).getTaskId();
    }

    public void update(){
        behavior.steer(this); //Changes velocity
        origin.add(velocity);
        if (!ignoreBlocks){
            World projWorld = getProjectileWorld();
            if (projWorld.getBlockAt(origin.toLocation(projWorld)).isSolid()){setValid(false);}
        }
        //Render
        trailRenderer.accept(this);
        //Check collisions?
        if (collisionDetection.apply(this)){
            Utils.log("Collision!");
        }
    }

    public void applyForce(Vector acceleration){
        Vector baseForce = getBaseAcceleration();
        if (baseForce == null || acceleration == null){return;}
        Vector resultingForce = baseForce.clone().add(acceleration);
        if (hasGravity()){
            resultingForce.add(GRAVITY_VEC);
        }
        //Cap the acceleration force
        resultingForce = limitVector(resultingForce, maxForce);
        this.velocity = limitVector(this.velocity.add(resultingForce), maxSpeed);
    }

    public World getProjectileWorld(){
        return skillcast.getPlayer().getWorld();
    }

    public void changeTarget(Vector newTarget){
        this.target = newTarget;
    }

    public boolean hasGravity(){
        return this.gravity;
    }

    public static Vector getRaytracedMaxDistance(Player player, double maxRange){
        RayTraceResult result = player.rayTraceBlocks(maxRange);
        if (result != null && result.getHitBlock() != null){
            return result.getHitPosition();
        } else {
            Location origin = player.getLocation().clone().add(0,1.5,0);
            return origin.toVector().add(origin.getDirection().clone().multiply(maxRange));
        }
    }

    public static Vector getRaytracedMaxDistance(Location playerLoc, Vector dir, double maxRange){
        RayTraceResult result = playerLoc.getWorld().rayTraceBlocks(playerLoc, dir, maxRange, FluidCollisionMode.NEVER, true);
        if (result != null && result.getHitBlock() != null){
            return result.getHitPosition();
        } else {
            return playerLoc.toVector().add(dir.clone().multiply(maxRange));
        }
    }
}
