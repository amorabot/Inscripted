package com.amorabot.inscripted.skill.routine.slam;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.combat.damage.DamageRouter;
import com.amorabot.inscripted.combat.damage.DamageSource;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.skill.routine.SkillcastData;
import com.amorabot.inscripted.skill.routine.slash.SlashConfig;
import com.amorabot.inscripted.math.LinalgMath;
import com.amorabot.inscripted.skill.type.Attack;
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
    private final SlamConfig slamData;
    private final BiFunction<SlashConfig, World, Consumer<Vector[]>> slashRenderer;
    private final Consumer<Slam> impactRoutine;

    public Slam(Skillcast skillcast, AttackData attackData, SlamConfig configData,
                BiFunction<SlashConfig,World, Consumer<Vector[]>> slashRenderer, Consumer<Slam> impactRoutine){
        this.skillcast = skillcast;
        this.attackData = attackData;
        this.slashRenderer = slashRenderer;
        this.impactRoutine = impactRoutine;
        this.slamData = configData;
        Player slamOwner = getOwner();
        SlashConfig swingAnimationData = configData.slashAnimationData();
        final double finalDistToCenter = swingAnimationData.baseRadius() +
                Math.abs(swingAnimationData.finalLength() - swingAnimationData.startingLength());
        Vector targetOffset = LinalgMath.getHorizontalOrientation(slamOwner.getLocation()).clone()[1]
                .multiply(finalDistToCenter);
        this.slamCenter = slamOwner.getLocation().toVector().clone().add(targetOffset);

        execute();
    }

    public Player getOwner(){
        return skillcast.getPlayer();
    }

    public void execute() {
        Slam slamObject = this;
        Player slamOwner = getOwner();
        Skillcast slamSkillcast = slamObject.getSkillcast();
        SkillcastData scData = slamSkillcast.getCastData();
//        scData.getBlacklistedEntities().add(slamOwner.getUniqueId());
        AttackData slamAttackData = null;
        if (slamSkillcast.getCastedSkill().isAttackSkill()){
            Attack slamAttack = (Attack) slamSkillcast;
            slamAttackData = slamAttack.getAttackData();
        }
        //Render
        animate();
        //Apply impact effects
        AttackData finalSlamAttackData = slamAttackData;
        int taskID = new BukkitRunnable(){
            @Override
            public void run() {
                //Instantiate the impact "animationDuration" ticks later
                int impactTaskID = new BukkitRunnable(){
                    @Override
                    public void run() {
                        //Wait "delayToImpact" frames to instantiate the effects
                        getImpactRoutine().accept(slamObject);
                        double impactRadius = slamData.impactRadius();
                        final List<Player> nearbyEntities = (List<Player>) slamCenter.toLocation(slamOwner.getWorld()).getNearbyPlayers(impactRadius+0.3);
                        for (Player currentNearbyPlayer : nearbyEntities){
                            if (scData.getBlacklistedEntities().contains(currentNearbyPlayer.getUniqueId())){continue;}

                            if (!(slamOwner).hasLineOfSight(currentNearbyPlayer)){continue;}
                            scData.getAffectedEntities().add(currentNearbyPlayer.getUniqueId());

                            if (finalSlamAttackData ==null) {continue;}
                            DamageRouter.hit(getOwner(), currentNearbyPlayer, slamSkillcast, finalSlamAttackData, DamageSource.HIT);
                        }
                        //Post-slam effects can go here
                    }
                }.runTaskLater(Inscripted.getPlugin(), getSlamData().delayToImpact()).getTaskId();
            }
        }.runTaskLater(Inscripted.getPlugin(), getSlamData().animationDuration()).getTaskId();
    }

    private void animate(){
        Player slamOwner = getOwner();
        Slam slamObject = this;
        boolean sprinting = false;
        //Handle different entities later
        if (slamOwner != null){sprinting = slamOwner.isSprinting();}
        assert slamOwner != null;
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
        SlamConfig data = getSlamData();
        SlashConfig animationData = data.slashAnimationData();
        return LinalgMath.plotSlam(playerLoc, data.rightHanded(), slamData.slashOffsetPhase(), data.handHeightReduction(),
                animationData.arc(), animationData.segments(), animationData.baseRadius(),animationData.skewFactor(),sprinting,
                animationData.startingLength(),animationData.finalLength()-animationData.startingLength(),
                animationData.initialOffset(), animationData.finalOffset()-animationData.initialOffset());
    }
}
