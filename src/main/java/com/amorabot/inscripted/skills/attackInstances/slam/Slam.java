package com.amorabot.inscripted.skills.attackInstances.slam;

import com.amorabot.inscripted.APIs.damageAPI.DamageRouter;
import com.amorabot.inscripted.APIs.damageAPI.DamageSource;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.skills.PlayerAbilities;
import com.amorabot.inscripted.skills.attackInstances.AttackContext;
import com.amorabot.inscripted.skills.attackInstances.PlayerAttack;
import com.amorabot.inscripted.skills.attackInstances.slash.SlashConfigDTO;
import com.amorabot.inscripted.skills.math.LinalgMath;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public class Slam extends PlayerAttack {

    private final Vector slamCenter;
    private final SlamConfigDTO slamData;
    private final Function<Slam,Consumer<Vector[]>> slashRenderer;
    private final Consumer<Slam> impactRenderer;

    public Slam(Player player, PlayerAbilities sourceAbility, SlamConfigDTO configData,
                Function<Slam,Consumer<Vector[]>> slashRenderer, Consumer<Slam> impactRenderer){
        super(new AttackContext(player, sourceAbility), true);
        this.slashRenderer = slashRenderer;
        this.impactRenderer = impactRenderer;
        this.slamData = configData;
        SlashConfigDTO animationData = configData.slashAnimationData();
        final double finalDistToCenter = animationData.baseRadius() +
                Math.abs(animationData.finalLength() - animationData.startingLength());
        Vector targetOffset = LinalgMath.getHorizontalOrientation(player.getLocation()).clone()[1]
                .multiply(finalDistToCenter);
        this.slamCenter = player.getLocation().toVector().clone().add(targetOffset);
    }

    public static Slam generateSlam(){
        return null;
    }

    @Override
    public void execute() {
        Slam slamObject = this;
        //Render
        animate();
        //Apply impact effects
        int taskID = new BukkitRunnable(){
            @Override
            public void run() {
                //Instantiate the impact "animationDuration" ticks later
                int impactTaskID = new BukkitRunnable(){
                    @Override
                    public void run() {
                        //Wait "delayToImpact" frames to instantiate the effects
                        getImpactRenderer().accept(slamObject);
                        Entity slamOwner = getOwner();
                        assert slamOwner != null;
                        double impactRadius = slamData.impactRadius();
                        final List<LivingEntity> nearbyEntities = (List<LivingEntity>) slamCenter.toLocation(slamOwner.getWorld())
                                .getNearbyLivingEntities(impactRadius+0.3);
                        for (LivingEntity entity : nearbyEntities){
                            if (slamObject.getBlacklistedEntities().contains(entity.getUniqueId())){continue;}
                            if (entity instanceof Player){
                                if (!((Player) slamOwner).hasLineOfSight(entity)){continue;}
                                slamObject.getAffectedEntities().add(entity.getUniqueId());
                                DamageRouter.entityDamage((Player) slamOwner, entity, DamageSource.HIT, getContext().getSourceAbility());
                            } else {
                                //Do whatever the other entity does :D
                            }
                        }
                        //Post-slam effects can go here
                    }
                }.runTaskLater(Inscripted.getPlugin(), getSlamData().delayToImpact()).getTaskId();
            }
        }.runTaskLater(Inscripted.getPlugin(), getSlamData().animationDuration()).getTaskId();
    }

    private void animate(){
        Entity slamOwner = getOwner();
        Slam slamObject = this;
        boolean sprinting = false;
        if (slamOwner instanceof Player){sprinting = ((Player) slamOwner).isSprinting();}
        Location slashOwnerLoc = slamOwner.getLocation();
        Vector[][] points = plotSlamSlash(slashOwnerLoc, sprinting);
        SlashConfigDTO animationData = getSlamData().slashAnimationData();
        int framesPerIteration = Math.max(1,((animationData.segments()/ getSlamData().animationDuration())));
        int taskID = new BukkitRunnable(){
            int iteration = 0;
            int totalFrames = 0;
            @Override
            public void run() {
                for (int frame = 0; frame<framesPerIteration; frame++){
                    int currentSegmentIndex = iteration*framesPerIteration + frame;
                    Vector currentTip = points[0][currentSegmentIndex];
                    Vector currentHandle = points[1][currentSegmentIndex];

                    getSlashRenderer().apply(slamObject).accept(new Vector[]{currentHandle,currentTip});

                    totalFrames++;
                    if (totalFrames>=animationData.segments()){this.cancel();return;}
                }
                iteration++;
            }
        }.runTaskTimer(Inscripted.getPlugin(),0, 1).getTaskId();
    }

    private Vector[][] plotSlamSlash(Location playerLoc, boolean sprinting){
        SlamConfigDTO data = getSlamData();
        SlashConfigDTO animationData = data.slashAnimationData();
        return LinalgMath.plotSlam(playerLoc, data.rightHanded(), slamData.slashOffsetPhase(), data.handHeightReduction(),
                animationData.arc(), animationData.segments(), animationData.baseRadius(),animationData.skewFactor(),sprinting,
                animationData.startingLength(),animationData.finalLength()-animationData.startingLength(),
                animationData.initialOffset(), animationData.finalOffset()-animationData.initialOffset());
    }
}
