package com.amorabot.inscripted.skill.routine.slash;

import com.amorabot.inscripted.APIs.damageAPI.DamageRouter;
import com.amorabot.inscripted.APIs.damageAPI.DamageSource;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.math.LinalgMath;
import com.amorabot.inscripted.math.OrientedBoundingBox;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.skill.PlayerAbilities;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.tasks.base.Skillcast;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.amorabot.inscripted.skill.AbilityRoutines.getLargeHitbox;

@Getter
@Setter
public class Slash{
    public static final int[] tipColor = new int[]{247, 242, 198};

    private final Skillcast skillcast;
    private final AttackData attackData;

    private Vector[][] points;
    private OrientedBoundingBox hitbox;
    private final SlashConfig slashData;
    private final BiFunction<SlashConfig,World, Consumer<Vector[]>> segmentRenderer;

    private boolean mirrored;
    private boolean inverted;
    private boolean randomized;

    private boolean valid = true;

    public Slash(Skillcast skillcast, AttackData attackData, SlashConfig configData, boolean cosmetic,
                 Location castingLocation, boolean isMirrored, boolean isInverted, boolean isRandomized, double... planeRotation) {
        this.skillcast = skillcast;
        this.attackData = attackData;
        this.segmentRenderer = configData.defaultRenderer();

        this.slashData = configData;
        this.mirrored = isMirrored;
        this.inverted = isInverted;
        this.randomized = isRandomized;

        Player player = skillcast.getPlayer();
        Vector[] slashOrientation = LinalgMath.defineOrientation(
                castingLocation,
                isMirrored,isInverted,isRandomized,
                15, planeRotation);

        //Valid plotting will only occour when the given orientation is valid
        this.points = plot(player.getLocation(),player.isSprinting(), slashOrientation);

        //Once plotted, the points will dictate whether the slash is still valid
        this.valid = checkValidity(points[0]);
        if (!valid){return;}
        defineHitbox(slashOrientation);
        execute(cosmetic);
    }

//    @Override
    public void execute(boolean cosmetic) {
        animate(2);
        if (!cosmetic){
            checkCollisions();
        }
    }


    private void checkCollisions(){
        LivingEntity slashOwner = getSkillcast().getPlayer();
        double finalOffset = getSlashData().finalOffset();
        double attackRadius = getSlashData().baseRadius();
        final List<LivingEntity> nearbyEntities = (List<LivingEntity>) slashOwner.getLocation().getNearbyLivingEntities(finalOffset+attackRadius+2);
        List<LivingEntity> affectedEntities = checkCollisions(nearbyEntities);

        for (LivingEntity entity : affectedEntities){
            if (getSkillcast().getCastData().getBlacklistedEntities().contains(entity.getUniqueId())){continue;}

            if (!slashOwner.hasLineOfSight(entity)){continue;}
            
            // Add to affected entities and blacklist to prevent multiple hits
            getSkillcast().getCastData().getAffectedEntities().add(entity.getUniqueId());
            getSkillcast().getCastData().getBlacklistedEntities().add(entity.getUniqueId());
            
            // Apply damage through the custom damage router system
            Skills skillUsed = getSkillcast().getCastData().getCastingContext().getSkillUsed();
            PlayerAbilities playerAbility = convertSkillToPlayerAbility(skillUsed);
            DamageRouter.entityDamage((Player) slashOwner, entity, DamageSource.HIT, playerAbility);
        }
    }

    // Helper method to convert Skills enum to PlayerAbilities enum
    private PlayerAbilities convertSkillToPlayerAbility(Skills skill) {
        try {
            // Convert by name - both enums have the same names for basic attacks
            return PlayerAbilities.valueOf(skill.name());
        } catch (IllegalArgumentException e) {
            // Fallback to FIST if conversion fails
            return PlayerAbilities.FIST;
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
                try {
                    for (int frame = 0; frame<framesPerIteration; frame++){
                        int currentSegmentIndex = iteration*framesPerIteration + frame;
                        Vector currentTip = points[0][currentSegmentIndex];
                        Vector currentHandle = points[1][currentSegmentIndex];

                        data.getSegmentRenderer().apply(slashData,getSlashWorld()).accept(new Vector[]{currentHandle,currentTip});

                        totalFrames++;
                        if (totalFrames>=getSlashData().segments()){this.cancel();return;}
                    }
                    iteration++;
                } catch (Exception exception){
                    this.cancel();
                    return;
                }
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
            if (entity instanceof Mob mob){
                if (getHitbox().intersects(mob.getBoundingBox())){
                    affectedEntities.add(mob);
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
        return getSkillcast().getPlayer().getWorld();
    }

    private boolean checkValidity(Vector[] vectorsToCheck){
        for (Vector vector : vectorsToCheck){
            if (vector.isZero()){
                return false;
            }
        }
        return true;
    }

    private void defineHitbox(Vector[] slashOrientation){
        Vector[] mergedPoints = new Vector[points.length*points[0].length];
        for (int i = 0; i<points[0].length; i++){
            mergedPoints[i] = points[0][i];
            mergedPoints[i+points[0].length] = points[1][i];
        }
        this.hitbox = new OrientedBoundingBox(mergedPoints,slashOrientation);
        getHitbox().expandFromCenter(0.5);
    }
}
