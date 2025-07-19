package com.amorabot.inscripted.tasks.base;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.skill.casting.CastSource;
import com.amorabot.inscripted.skill.casting.CastType;
import com.amorabot.inscripted.skill.routine.SkillcastData;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.skill.routine.SkillcastContext;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public abstract class Skillcast extends PlayerboundTask{
    protected final SkillcastData castData;
    protected final int baseCooldownMod;

    public Skillcast(UUID playerID, Skills sourceSkill, CastSource castSource, int baseCDMod) {
        super(playerID);
        this.castData = new SkillcastData(new SkillcastContext(getPlayer(),sourceSkill),castSource,sourceSkill.isIgnoreOwner());
        this.baseCooldownMod = baseCDMod;
    }
    @Override
    public void run() {
        CastSource source = castData.getSource();
        if (!Inscripted.getPlugin().isEnabled() || source==null || source.equals(CastSource.MONSTER)){
            this.cancel();
            return;
        }
        if (invalidPlayer()){
            abort("Error: Invalid player for skillcast.("+this.getClass().getSimpleName()+")\nAborting...");
            return;
        }
        taskRoutine(getPlayer());
    }

    @Override
    public void start(long delay, long timer) { // Skills execute routines once by default. Persistent casts will execute a new and independent subtask
        register();
    }
    @Override
    public void register(){
        PlayerDataContainer playerData = PlayerDataContainer.getDataContainerFor(getPlayerID());
        if (playerData.skillcastBy(getCastedSkill(),getBaseCooldownMod())){
            run();
            return;
        }
        Utils.error("Invalid spellcast: In cooldown (" + playerData.fetchAbilityRemainingCooldown(getCastedSkill().getType()) + ").");
    }
    @Override
    public void unregister(){
        //Reset the cooldown for this skill
        PlayerDataContainer playerData = PlayerDataContainer.getDataContainerFor(getPlayerID());
        playerData.getSkillCooldowns().remove(getCastedSkill().getType());
        // Stop any subtasks
    }
    @Override
    protected void taskRoutine(Player player) {
        getCastData().getCastingContext().getSkillUsed().getSkillRoutine().accept(this);
    }

    public Skills getCastedSkill(){
        return getCastData().getCastingContext().getSkillUsed();
    }




    public static abstract class Simple extends Skillcast {

        public Simple(UUID playerID, Skills sourceSkill, CastSource castSource, WeaponAttackSpeeds weaponSpeed) {
            super(playerID,sourceSkill,castSource,weaponSpeed.getAbilityCooldownModifier());
        }

    }




    public static abstract class Persistent extends Skillcast {
        /*
        Persistent casts should be stored in a map
        PersisentAttacks should cancel themselves after some condition is met or after maxDuration is reached
        Aura casts should be stored on a "Active" map, when toggled, cancel and remove from that map
        When activating a aura, check if that Skill instance, for that player, is already active. If so, toggle(deactivate) it.
        */
        @Setter
        @Getter
        private int persistentRoutineID = -1;
        //TODO: maxDuration on persistent attacks, Auras are essentially toggles
        //+ castTime, complementing maxDuration

        public Persistent(UUID playerID, Skills sourceSkill, CastSource castSource,  WeaponAttackSpeeds weaponSpeed) {
            super(playerID, sourceSkill, castSource, weaponSpeed.getAbilityCooldownModifier());
        }

        @Override
        public void run() {
            /*
             For persistent casts, the skill routine defines and starts the subroutine task
             Then, the persistent subroutine ID must be set internally
            */
            taskRoutine(getPlayer());
            if (persistentRoutineID ==-1){
                abort("Persistent subroutine must be set!");
                return;
            }
            if (!isValidPersistentCast()){
                abort("Error: Invalid persistent cast internal data.");
                return;
            }
        }

        public boolean isValidPersistentCast(){
            CastSource source = getCastData().getSource();
            return Inscripted.getPlugin().isEnabled() &&
                    source != null &&
                    !source.equals(CastSource.MONSTER) &&
                    !invalidPlayer();
        }

        public double getSubroutinePeriodInSeconds(){
            if (!getCastData().getCastingContext().getSkillUsed().isPersistent()){return 0;}
            return getCastedSkill().getPersistentSkillData().period();
        }
        public double getSubroutineMaxDurationInSeconds(){
            if (!getCastData().getCastingContext().getSkillUsed().isPersistent()){return 0;}
            return getCastedSkill().getPersistentSkillData().maxDuration();
        }
    }
}
