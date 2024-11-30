package com.amorabot.inscripted.events.death;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class InscriptedPlayerDeathEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;


    @Getter
    private final Location respawnLocation;
    @Getter
    private final Player deadPlayer;

    public InscriptedPlayerDeathEvent(Player deadPlayer){
        this.respawnLocation = deadPlayer.getRespawnLocation();
        this.deadPlayer = deadPlayer;
    }


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
