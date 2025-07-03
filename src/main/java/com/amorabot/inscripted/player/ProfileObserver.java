package com.amorabot.inscripted.player;

import com.amorabot.inscripted.item.inscription.definition.TriggerTypes;
import com.amorabot.inscripted.player.profile.ProfileEvents;
import org.bukkit.entity.LivingEntity;

public interface ProfileObserver {
    void onNotify(ProfileEvents event);
    void onNotify(TriggerTypes combatTrigger, LivingEntity target);
}
