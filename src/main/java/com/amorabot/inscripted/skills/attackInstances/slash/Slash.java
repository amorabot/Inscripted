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

        Vector[] slashOrientation = LinalgMath.defineOrientation(
                player.getLocation(),
                isMirrored,isInverted,isRandomized,
                15, planeRotation);

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

    public static Slash generateSlash(){
        //Constructor call + execute()
        return null;
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

    //randomizedAngleInterval must be a 1st quadrant angle value in DEG
    public Vector[][] plot(Location playerLoc, boolean sprinting, Vector[] orientation){
        if (!checkValidity(orientation)){
            return new Vector[2][getSlashData().segments()];
        }

        return LinalgMath.plotSlash(playerLoc, orientation, slashData.skewFactor(), false,
                slashData.baseRadius(), slashData.arc(), slashData.segments(), isMirrored(),sprinting,
                getSlashData().startingLength(), getSlashData().finalLength()-getSlashData().startingLength(),
                getSlashData().initialOffset(), getSlashData().finalOffset()- getSlashData().initialOffset(),0
                );
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
