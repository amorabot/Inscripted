package com.amorabot.inscripted.skill.casting;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static com.amorabot.inscripted.managers.CasterStateManager.playToggleSound;

@Setter
@Getter
public class CasterState {
    private static final int alternateCastingTimeout = 3;//Seconds

    private boolean disabledCasting;

    private boolean alternateCasting;
    private Skills lastSkill;
    private BukkitRunnable timeoutTask;

    public CasterState(){
        this.disabledCasting = false;

        this.alternateCasting = false;
        this.lastSkill = null;
    }

    public void toggleDisabledCasting(){
        this.disabledCasting = !disabledCasting;
    }

    public void toggleAlternateCasting(){
        this.alternateCasting = !alternateCasting;
    }
    public void resetCastingState(){
        this.alternateCasting = false;
    }

    public void setLastestAbility(Skills ability){
        this.lastSkill = ability;
    }
    public boolean hasCasted(){
        return lastSkill != null;
    }

    public void activateAlternateCasting(Player caster){
        setAlternateCasting(true);
        if (timeoutTask!=null && !timeoutTask.isCancelled()){ //refresh
            Utils.log("Cancelled running task");
            timeoutTask.cancel();
        }
        setTimeoutTask(getNewTimeoutTask(caster));
        getTimeoutTask().runTaskTimer(Inscripted.getPlugin(),0,3);
    }
    private BukkitRunnable getNewTimeoutTask(Player caster){
        return new BukkitRunnable() {
            final long startTime = System.currentTimeMillis();
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long elapsed = currentTime-startTime;
                assert caster != null;
                if ((!caster.isOnline()) || (elapsed>=alternateCastingTimeout*1000)){
                    playToggleSound(caster,0.3f);
                    caster.sendMessage(Component.text("Spellcast expired").color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
                    if (!this.isCancelled()){
                        setAlternateCasting(false);
                    }
                    this.cancel();
                    return;
                }
            }
        };
    }
    public void reset(){
        if (timeoutTask!=null){
            getTimeoutTask().cancel();
        }
        setAlternateCasting(false);
    }
}
