package com.amorabot.inscripted.tasks.base;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.skill.casting.CastSource;
import com.amorabot.inscripted.skill.routine.SkillcastData;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.skill.routine.SkillcastContext;
import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class Skillcast extends PlayerboundTask{
    protected final SkillcastData castData;
    protected final int baseCooldownMod;

    public Skillcast(UUID playerID, Skills sourceSkill, CastSource castSource, int baseCDMod) {
        super(playerID);
        this.castData = new SkillcastData(new SkillcastContext(getPlayer(),sourceSkill),castSource);
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
    public void start(long delay, long timer) { // Skills execute routines once by default. Persistent casts will execute a new and independent task
        register();
        run();
    }
    @Override
    public void register(){
        //TODO: implement standard cooldown instantation
    }
    @Override
    public void unregister(){
        //Remove this skill cast from global CDs
        //stop this task
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
        final int period;
        int persistentRoutineID;
        //TODO: maxDuration on persistent attacks, Auras are essentially toggles
        //+ castTime, complementing maxDuration

        public Persistent(UUID playerID, Skills sourceSkill, CastSource castSource,  WeaponAttackSpeeds weaponSpeed, int taskPeriod) {
            super(playerID, sourceSkill, castSource, weaponSpeed.getAbilityCooldownModifier());
            this.period = taskPeriod;
        }
    }
}
