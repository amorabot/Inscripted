package com.amorabot.inscripted.player;

import com.amorabot.inscripted.item.inscription.definition.TriggerTimes;
import com.amorabot.inscripted.item.inscription.definition.TriggerTypes;
import com.amorabot.inscripted.player.profile.PlayerEvents;
import org.bukkit.entity.Player;

public interface ProfileObserver {
    void onNotify(PlayerEvents event);
    void onNotify(TriggerTimes triggerTime, TriggerTypes combatTrigger, Player target, int[] incomingDamage);
}
