package com.amorabot.inscripted.skills.attackInstances.projectile;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.skills.PlayerAbilities;
import com.amorabot.inscripted.skills.SteeringBehaviors;
import com.amorabot.inscripted.skills.attackInstances.AttackContext;
import com.amorabot.inscripted.skills.attackInstances.PlayerAttack;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

import static com.amorabot.inscripted.utils.Utils.limitVector;

@Getter
public class Projectile extends PlayerAttack {

    public static final Vector GRAVITY_VEC = new Vector(0, -0.05, 0);

    private final Vector position;
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
    private Consumer<Projectile> collision;

    public Projectile(Player attacker, PlayerAbilities sourceAbility,
                      Vector initialPos, Vector baseVelocity, Vector baseAcceleration, Vector targetPos,
                      boolean hasGravity, boolean ignoreBlocks, boolean destroyOnContact, double maxSpeed, double maxForce, double maxTravelDistance, double detectionRange,
                      SteeringBehaviors behavior,Consumer<Projectile> trail, Consumer<Projectile> collision){
        super(new AttackContext(attacker,sourceAbility));
        getBlacklistedEntities().add(attacker.getUniqueId());
        this.position = initialPos;
        this.velocity = baseVelocity;
        this.baseAcceleration = baseAcceleration;
        this.target = targetPos;

        this.behavior = behavior;
        //Gravity can be defined via attackerContext
        this.gravity = hasGravity;
        this.ignoreBlocks = ignoreBlocks;
        this.destroyOnContact = destroyOnContact;
        this.maxSpeed = maxSpeed;
        this.maxForce = maxForce;
        this.maxTravelDistance = maxTravelDistance;
        this.detectionRange = detectionRange;

        this.trailRenderer = trail;
        this.collision = collision;
    }

    @Override
    public void execute() {
        final int maxIterations = 100;
        final int subSteps = 2;
//        final Projectile data = this;
        int taskID = new BukkitRunnable(){
            int iterations = 0;
            @Override
            public void run() {
                if (iterations >= maxIterations || position.distance(target) < 1 || !valid){
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
        position.add(velocity);
        if (!ignoreBlocks){
            World projWorld = getProjectileWorld();
            if (projWorld.getBlockAt(position.toLocation(projWorld)).isSolid()){setValid(false);}
        }
        //Render
        trailRenderer.accept(this);
        //Check collisions?
        collision.accept(this);
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
        Entity projOwner = Bukkit.getEntity(getContext().getAttackerID());
        assert projOwner != null;
        return projOwner.getWorld();
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
