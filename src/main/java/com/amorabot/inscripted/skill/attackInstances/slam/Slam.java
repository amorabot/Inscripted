package com.amorabot.inscripted.skill.attackInstances.slam;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.skill.attackInstances.slash.SlashConfig;
import com.amorabot.inscripted.math.LinalgMath;
import com.amorabot.inscripted.tasks.base.Skillcast;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Getter
public class Slam{
    private final Skillcast skillcast;
    private final AttackData attackData;

    private final Vector slamCenter;
    private final SlamConfigDTO slamData;
    private final BiFunction<SlashConfig, World, Consumer<Vector[]>> slashRenderer;
    private final Consumer<Slam> impactRenderer;

    public Slam(Skillcast skillcast, AttackData attackData, SlamConfigDTO configData,
                BiFunction<SlashConfig,World, Consumer<Vector[]>> slashRenderer, Consumer<Slam> impactRenderer){
        this.skillcast = skillcast;
        this.attackData = attackData;
        this.slashRenderer = slashRenderer;
        this.impactRenderer = impactRenderer;
        this.slamData = configData;
        Player slamOwner = getOwner();
        SlashConfig swingAnimationData = configData.slashAnimationData();
        final double finalDistToCenter = swingAnimationData.baseRadius() +
                Math.abs(swingAnimationData.finalLength() - swingAnimationData.startingLength());
        Vector targetOffset = LinalgMath.getHorizontalOrientation(slamOwner.getLocation()).clone()[1]
                .multiply(finalDistToCenter);
        this.slamCenter = slamOwner.getLocation().toVector().clone().add(targetOffset);
    }

    public Player getOwner(){
        return skillcast.getPlayer();
    }

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
                        LivingEntity slamOwner = getOwner();
                        double impactRadius = slamData.impactRadius();
                        final List<LivingEntity> nearbyEntities = (List<LivingEntity>) slamCenter.toLocation(slamOwner.getWorld())
                                .getNearbyLivingEntities(impactRadius+0.3);
                        for (LivingEntity entity : nearbyEntities){
                            if (slamObject.getSkillcast().getCastData().getBlacklistedEntities().contains(entity.getUniqueId())){continue;}

                            if (!(slamOwner).hasLineOfSight(entity)){continue;}
                            slamObject.getSkillcast().getCastData().getAffectedEntities().add(entity.getUniqueId());
//                            DamageRouter.entityDamage((Player) slamOwner, entity, DamageSource.HIT, getContext().getSkillUsed());
                        }
                        //Post-slam effects can go here
                    }
                }.runTaskLater(Inscripted.getPlugin(), getSlamData().delayToImpact()).getTaskId();
            }
        }.runTaskLater(Inscripted.getPlugin(), getSlamData().animationDuration()).getTaskId();
    }

    private void animate(){
        LivingEntity slamOwner = getOwner();
        Slam slamObject = this;
        boolean sprinting = false;
        //Handle different entities later
        if (slamOwner != null){sprinting = ((Player) slamOwner).isSprinting();}
        Location slashOwnerLoc = slamOwner.getLocation();
        Vector[][] points = plotSlamSlash(slashOwnerLoc, sprinting);
        SlashConfig animationData = getSlamData().slashAnimationData();
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

                    getSlashRenderer().apply(
                            slamObject.slamData.slashAnimationData(),slamOwner.getWorld()
                            ).accept(new Vector[]{currentHandle,currentTip});

                    totalFrames++;
                    if (totalFrames>=animationData.segments()){this.cancel();return;}
                }
                iteration++;
            }
        }.runTaskTimer(Inscripted.getPlugin(),0, 1).getTaskId();
    }

    // Make this functionality as a Slash constructor flag?
    private Vector[][] plotSlamSlash(Location playerLoc, boolean sprinting){
        SlamConfigDTO data = getSlamData();
        SlashConfig animationData = data.slashAnimationData();
        return LinalgMath.plotSlam(playerLoc, data.rightHanded(), slamData.slashOffsetPhase(), data.handHeightReduction(),
                animationData.arc(), animationData.segments(), animationData.baseRadius(),animationData.skewFactor(),sprinting,
                animationData.startingLength(),animationData.finalLength()-animationData.startingLength(),
                animationData.initialOffset(), animationData.finalOffset()-animationData.initialOffset());
    }
}
