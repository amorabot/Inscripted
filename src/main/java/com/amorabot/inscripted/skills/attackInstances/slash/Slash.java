package com.amorabot.inscripted.skills.attackInstances.slash;

import com.amorabot.inscripted.APIs.damageAPI.DamageRouter;
import com.amorabot.inscripted.APIs.damageAPI.DamageSource;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.skills.PlayerAbilities;
import com.amorabot.inscripted.skills.attackInstances.AttackContext;
import com.amorabot.inscripted.skills.attackInstances.PlayerAttack;
import com.amorabot.inscripted.skills.math.LinalgMath;
import com.amorabot.inscripted.skills.math.OrientedBoundingBox;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.amorabot.inscripted.skills.AbilityRoutines.getLargeHitbox;

@Getter
@Setter
public class Slash extends PlayerAttack {
    public static final int[] tipColor = new int[]{247, 242, 198};

    private Vector[][] points;
    private OrientedBoundingBox hitbox;
    private final SlashConfigDTO slashData;
    private final Function<Slash, Consumer<Vector[]>> segmentRenderer;

    private boolean mirrored;
    private boolean inverted;
    private boolean randomized;

    private boolean valid = true;

    public Slash(Player player, PlayerAbilities sourceAbility, SlashConfigDTO configData,
                 boolean isMirrored, boolean isInverted, boolean isRandomized, Function<Slash, Consumer<Vector[]>> renderer, double... planeRotation) {
        super(new AttackContext(player, sourceAbility), true);
        this.segmentRenderer = renderer;

        this.slashData = configData;
        this.mirrored = isMirrored;
        this.inverted = isInverted;
        this.randomized = isRandomized;

        Vector[] slashOrientation = defineOrientation(player.getLocation(),planeRotation);

        //Valid plotting will only occour when the given orientation is valid
        this.points = plot(player.getLocation(),player.isSprinting(), slashOrientation);

        //Once plotted, the points will dictate wether the slash is still valid
        this.valid = checkValidity(points[0]);
        if (!valid){return;}
        //If not valid implies no hitbox

        Vector[] mergedPoints = new Vector[points.length*points[0].length];
        for (int i = 0; i<points[0].length; i++){
            mergedPoints[i] = points[0][i];
            mergedPoints[i+points[0].length] = points[1][i];
        }
        this.hitbox = new OrientedBoundingBox(mergedPoints,slashOrientation);
        this.hitbox.expandFromCenter(0.5);
    }

    @Override
    public void execute() {
        if (!valid){return;}
        //Render
        animate(2); //Defaults to 2, for now
//        this.hitbox.render(getSlashWorld());
        //Collision
        Entity slashOwner = getOwner();
        assert slashOwner != null;
        double finalOffset = getSlashData().finalOffset();
        double attackRadius = getSlashData().baseRadius();
        final List<LivingEntity> nearbyEntities = (List<LivingEntity>) slashOwner.getLocation().getNearbyLivingEntities(finalOffset+attackRadius+2);
        List<LivingEntity> affectedEntities = checkCollisions(nearbyEntities);
        for (LivingEntity entity : affectedEntities){
            if (this.getBlacklistedEntities().contains(entity.getUniqueId())){continue;}
            if (entity instanceof Player){
                if (!((Player) slashOwner).hasLineOfSight(entity)){continue;}
                this.getAffectedEntities().add(entity.getUniqueId());
                DamageRouter.playerAttack((Player) slashOwner, entity, DamageSource.HIT, getContext().getSourceAbility());
            } else {
                //Do whatever the other entity does :D
            }
        }

    }

    private Vector[] defineOrientation(Location playerLoc, double... planeRotation){
        Vector initialDirection;
        Vector perpendicularAxis;
        Vector slashPlaneNormal;

        if (Math.abs(playerLoc.getPitch()+90) <= 2){ //2 degree "facing up" threshold
            initialDirection = new Vector(0, 1, 0);
            //If the player is looking UP, we have their YAW to define a "facing" direction
//            double yawRad = (playerLoc.getYaw()) /180*Math.PI;
            double yawRad;
            if (playerLoc.getYaw()>0){
                yawRad = -(playerLoc.getYaw()) /180*Math.PI;
            } else {
                yawRad = (180-playerLoc.getYaw()) /180*Math.PI;
            }
            //Plane normal and "facing"
            double xPos = Math.sin(yawRad);
            double zPos = Math.cos(yawRad);
            Vector planeNormal = new Vector(xPos, 0, zPos);
            perpendicularAxis = planeNormal.clone().crossProduct(initialDirection);

            slashPlaneNormal = planeNormal;
        } else {
            initialDirection = playerLoc.getDirection().clone();
            Vector horizontalVec = new Vector(initialDirection.getX(), 0, initialDirection.getZ());
            perpendicularAxis = initialDirection.clone().crossProduct(horizontalVec).normalize();
            slashPlaneNormal = perpendicularAxis.clone().crossProduct(initialDirection);

            //Slash randomization when view angle is low
            if (planeRotation!= null && planeRotation.length>0){
                if (playerLoc.getPitch() <=15 && playerLoc.getPitch() >= -15){ //When inside the 30deg view angle, apply the plane rotation
                    double rotationAngle;
                    if (isRandomized() && planeRotation.length==2){
                        rotationAngle = Utils.getRandomInclusiveValue(planeRotation[0],planeRotation[1]);
                    } else {
                        rotationAngle = planeRotation[0];
                    }
                    if (isMirrored()){rotationAngle = -rotationAngle;}
                    perpendicularAxis = LinalgMath.rotateAroundGenericAxis(initialDirection.clone(),perpendicularAxis.clone(), rotationAngle/180*Math.PI);

                    slashPlaneNormal = perpendicularAxis.clone().crossProduct(initialDirection); //Update and override plane normal
                }
            }
        }

        if (isInverted()){perpendicularAxis.multiply(-1);}
        return new Vector[]{perpendicularAxis,initialDirection,slashPlaneNormal};
    }

    //randomizedAngleInterval must be a 1st quadrant angle value in DEG
    public Vector[][] plot(Location playerLoc, boolean sprinting, Vector[] orientation){
        Vector[][] points = new Vector[2][getSlashData().segments()];

        if (!checkValidity(orientation)){
            return points;
        }

        final double arc = getSlashData().arc();
        final double angleStep = ( arc / getSlashData().segments()) /180*Math.PI;
        double startingPhase = (arc/2) /180*Math.PI;
        if (isMirrored()){
            startingPhase = -startingPhase;
        }

        final Vector playerPos = playerLoc.toVector().clone().add(new Vector(0, 1.4, 0));

        double minDistanceFromOrigin = 0.3;
        if (sprinting){
            minDistanceFromOrigin+=1.4;
        }

        Vector initialDirection = orientation[1];
        Vector perpendicularAxis = orientation[0];

        final Vector attackCenter = playerPos.clone().add(initialDirection.clone().multiply(minDistanceFromOrigin));

        double t = startingPhase;
        for (int segmentIndex = 0; segmentIndex < getSlashData().segments(); segmentIndex++){
            double animationProgress = ((double) (segmentIndex)/ getSlashData().segments());


            double swipeSize = getSlashData().startingLength() + (getSlashData().finalLength()-getSlashData().startingLength())*animationProgress;
            double handleOffset = getSlashData().initialOffset() + (getSlashData().finalOffset()-getSlashData().initialOffset())*animationProgress;

            Vector tip =
                    attackCenter.clone()
                            .add( initialDirection.clone().multiply((getSlashData().baseRadius()+handleOffset)*Math.cos(t)) )
                            .add( perpendicularAxis.clone().multiply((getSlashData().baseRadius()+handleOffset)*Math.sin(t)) );
            Vector radialDirection = attackCenter.clone().subtract(tip).normalize();

            Vector handle = tip.clone().add(radialDirection.multiply(swipeSize));

            points[0][segmentIndex] = tip;
            points[1][segmentIndex] = handle;

            if (isMirrored()){
                t += angleStep;
                continue;
            }
            t -= angleStep;
        }

        return points;
    }

    public void animate(int animationDuration){
        Vector[][] points = getPoints();
        Slash data = this;
        int framesPerIteration = Math.max(1,(getSlashData().segments()/animationDuration));
        int taskID = new BukkitRunnable(){
            int iteration = 0;
            int totalFrames = 0;
            @Override
            public void run() {
                for (int frame = 0; frame<framesPerIteration; frame++){
                    int currentSegmentIndex = iteration*framesPerIteration + frame;
                    Vector currentTip = points[0][currentSegmentIndex];
                    Vector currentHandle = points[1][currentSegmentIndex];

                    data.getSegmentRenderer().apply(data).accept(new Vector[]{currentHandle,currentTip});

                    totalFrames++;
                    if (totalFrames>=getSlashData().segments()){this.cancel();return;}
                }
                iteration++;
            }
        }.runTaskTimer(Inscripted.getPlugin(),0, 1).getTaskId();
    }

    public List<LivingEntity> checkCollisions(List<LivingEntity> entityList){
        List<LivingEntity> affectedEntities = new ArrayList<>();
        for (LivingEntity entity : entityList){
            if (entity instanceof Player){
                if (getHitbox().intersects(getLargeHitbox((Player) entity))){
                    affectedEntities.add(entity);
                }
            }
            //Different entity handling...
        }
        return affectedEntities;
    }

    public int[] getBaseColor(){
        return slashData.baseColor();
    }

    public World getSlashWorld(){
        Entity projOwner = Bukkit.getEntity(getContext().getAttackerID());
        assert projOwner != null;
        return projOwner.getWorld();
    }

    private boolean checkValidity(Vector[] vectorsToCheck){
        for (Vector vector : vectorsToCheck){
            if (vector.isZero()){
                return false;
            }
        }
        return true;
    }
}
