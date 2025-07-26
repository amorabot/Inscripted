package com.amorabot.inscripted.skill.archetypes.wand;

import com.amorabot.inscripted.skill.type.subroutines.DurationSubroutine;
import com.amorabot.inscripted.tasks.base.Skillcast;
import org.bukkit.scheduler.BukkitRunnable;

public class WandSpecials {

    public static void meteor(Skillcast skillcastInstance){
        DurationSubroutine cryostatisRoutine = new DurationSubroutine(skillcastInstance);
        cryostatisRoutine.setRoutine(new BukkitRunnable() {
            @Override
            public void run() {

            }
        });
        cryostatisRoutine.startSubroutine(0);
    }
}
