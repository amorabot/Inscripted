package com.amorabot.inscripted.handlers.Combat;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.tasks.CombatLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class CombatDamageHandler implements Listener {

    public CombatDamageHandler(){
        Bukkit.getPluginManager().registerEvents(this, Inscripted.getPlugin());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if (event.getEntity() instanceof Player){
            Player player = ((Player) event.getEntity());
            CombatLogger.addToCombat(player.getUniqueId());

        }
    }
}
