package com.amorabot.inscripted.APIs.damageAPI;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.tasks.CombatHologramsDepleter;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CombatEffects {

    public static void playDodgeEffectsAt(Entity dodgeEntity, Entity targetAudience){
        CombatHologramsDepleter.getInstance().instantiateDodgeHologramAt(dodgeEntity.getLocation());
        dodgeEntity.getWorld().spawnParticle(Particle.END_ROD, dodgeEntity.getLocation().clone(), 3, 0, -1, 0, 0);
        if (!(targetAudience instanceof Player)){
            return;
        }
        SoundAPI.playDodgeFor(targetAudience, dodgeEntity.getLocation().clone());
    }
    //TODO: Death effect
}
