package com.amorabot.rpgelements.handlers.Combat;

import com.amorabot.rpgelements.APIs.DamageAPI;
import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.DamageComponent;
import com.amorabot.rpgelements.components.HealthComponent;
import com.amorabot.rpgelements.components.Player.Profile;
import com.amorabot.rpgelements.managers.JSONProfileManager;
import com.amorabot.rpgelements.tasks.CombatLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class CombatDamageHandler implements Listener {

    public CombatDamageHandler(){
        Bukkit.getPluginManager().registerEvents(this, RPGElements.getPlugin());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if (event.getEntity() instanceof Player){
            Player player = ((Player) event.getEntity());
            CombatLogger.addToCombat(player.getUniqueId());

        }
    }
}
