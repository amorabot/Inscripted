package com.amorabot.inscripted.skill.type.subroutines;

import com.amorabot.inscripted.tasks.base.Skillcast;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class PersistentSubroutine {
    protected static final boolean DEBUG_MODE = true;

    @Getter
    @Setter
    protected BukkitRunnable routine;
    protected final Skillcast parentSkillcast;

    protected PersistentSubroutine(Skillcast parentSkillcast, BukkitRunnable routine){
        this.parentSkillcast = parentSkillcast;
        this.routine = routine;
    }
    protected PersistentSubroutine(Skillcast parentSkillcast){
        this.parentSkillcast = parentSkillcast;
        if (DEBUG_MODE) Utils.error("Subroutine not defined on instantiation");
    }

    private void cancelIfInvalidParentCast(){
        if (!checkParentCast(parentSkillcast)){
            if (DEBUG_MODE) Utils.error("Invalid parent cast, cancelling subtask...");
            routine.cancel();
        }
    }
    public void shutdown(){
        if (routine==null){
            if (DEBUG_MODE) Utils.error("No persistent subroutine to shutdown...");
            return;
        }
        if (routine.isCancelled()){
            if (DEBUG_MODE) Utils.error("Persistent subroutine already cancelled.");
            return;
        }
        //Cancel & Remove from active persistent skill map
        routine.cancel();
        uninstantiate();
    }
    public boolean checkParentCast(Skillcast skillcast){
        if (skillcast instanceof Skillcast.Persistent persistentSkillcast){
            return (isValid() && persistentSkillcast.isValidPersistentCast() && parentSkillcast.getPlayer().isOnline());
        }
        return false;
    }

    public abstract boolean isValid();
    public abstract void uninstantiate();
    public abstract void startSubroutine(int delay);
    //TODO: subroutineVisitor?
}
