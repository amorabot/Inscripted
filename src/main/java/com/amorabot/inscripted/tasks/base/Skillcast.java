package com.amorabot.inscripted.tasks.base;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.item.inscription.definition.Stats;
import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.skill.casting.CastSource;
import com.amorabot.inscripted.skill.routine.SkillcastData;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.skill.routine.SkillcastContext;
import com.amorabot.inscripted.skill.type.subroutines.PersistentSubroutine;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;

@Getter
public abstract class Skillcast extends PlayerboundTask{
    protected static final boolean DEBUG_MODE = false;

    protected final SkillcastData castData;
    protected final int baseCooldownMod;

    public Skillcast(UUID playerID, Vector skillcastOrigin, Skills sourceSkill, CastSource castSource, int baseCDMod) {
        super(playerID);
        this.castData = new SkillcastData(new SkillcastContext(getPlayer(),sourceSkill,skillcastOrigin),castSource,sourceSkill.isIgnoreOwner());
        int playerCDR = (int) PlayerDataContainer.getDataContainerFor(playerID).getGlobalStats().calculateStatValue(Stats.COOLDOWN_REDUCTION)[0];
        this.baseCooldownMod = baseCDMod + playerCDR;
    }
    public Skillcast(UUID playerID, Skills sourceSkill, CastSource castSource, int baseCDMod) {
        super(playerID);
        this.castData = new SkillcastData(new SkillcastContext(getPlayer(),sourceSkill,getPlayer().getLocation().toVector()),castSource,sourceSkill.isIgnoreOwner());
        int playerCDR = (int) PlayerDataContainer.getDataContainerFor(playerID).getGlobalStats().calculateStatValue(Stats.COOLDOWN_REDUCTION)[0];
        this.baseCooldownMod = baseCDMod + playerCDR;
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

        public Simple(UUID playerID, Vector skillcastOrigin, Skills sourceSkill, CastSource castSource, WeaponAttackSpeeds weaponSpeed) {
            super(playerID,skillcastOrigin, sourceSkill,castSource,weaponSpeed.getAbilityCooldownModifier());
        }
        public Simple(UUID playerID, Skills sourceSkill, CastSource castSource, WeaponAttackSpeeds weaponSpeed) {
            super(playerID,sourceSkill,castSource,weaponSpeed.getAbilityCooldownModifier());
        }

    }



    @Setter
    @Getter
    public static abstract class Persistent extends Skillcast {
        /*
        Persistent casts should be stored in a map
        PersisentAttacks should cancel themselves after some condition is met or after maxDuration is reached
        Aura casts should be stored on a "Active" map, when toggled, cancel and remove from that map
        When activating a aura, check if that Skill instance, for that player, is already active. If so, toggle(deactivate) it.
        */

//        private int persistentRoutineID = -1;
        private PersistentSubroutine subroutine;
        public Persistent(UUID playerID, Vector skillcastOrigin, Skills sourceSkill, CastSource castSource,  WeaponAttackSpeeds weaponSpeed) {
            super(playerID, skillcastOrigin, sourceSkill, castSource, weaponSpeed.getAbilityCooldownModifier());
        }
        public Persistent(UUID playerID, Skills sourceSkill, CastSource castSource,  WeaponAttackSpeeds weaponSpeed) {
            super(playerID, sourceSkill, castSource, weaponSpeed.getAbilityCooldownModifier());
        }
        /*
        Cast -> Register cooldown -> if successful, check the current persistent skill map for possible
        */
        @Override
        public void register(){
            PlayerDataContainer playerData = PlayerDataContainer.getDataContainerFor(getPlayerID());
            if (playerData.skillcastBy(getCastedSkill(),getBaseCooldownMod())){
                run();
                playerData.renewPersistentSkillInstance(getCastedSkill(),subroutine);
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
        public void run() {
            taskRoutine(getPlayer());
            if (subroutine == null){
                abort("Persistent subroutine must be set!");
                return;
            }
            if (!isValidPersistentCast()){
                abort("Error: Invalid persistent cast internal data.");
                return;
            }
        }

        @Override
        public void abort(String message){
            //Persistent skills are only run once, no need for original isCancelled() check
            Utils.error(message);
            unregister();
        }


        public boolean isValidPersistentCast(){
            CastSource source = getCastData().getSource();
            return Inscripted.getPlugin().isEnabled() &&
                    source != null &&
                    !source.equals(CastSource.MONSTER) &&
                    !invalidPlayer();
        }
        public double getSubroutineMaxDurationInSeconds(){
            if (!getCastedSkill().isDuration()){return 0;}
            return getCastedSkill().getDurationSkillData().duration();
        }
        public int getSubroutineRefreshRate(){
            if (!getCastedSkill().isDuration()){return 0;}
            return getCastedSkill().getDurationSkillData().refreshRate();
        }
    }
}
