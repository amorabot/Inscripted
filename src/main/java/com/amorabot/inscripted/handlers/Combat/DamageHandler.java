package com.amorabot.inscripted.handlers.Combat;

import com.amorabot.inscripted.APIs.DamageAPI;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.managers.JSONProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class DamageHandler implements Listener {

    private Inscripted plugin;
//    private DecimalFormat formatter = new DecimalFormat("#.##");

    public DamageHandler(Inscripted p){
        plugin = p;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event){
        DamageAPI.handleDamageEntityDamageEvents(event);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if (event.getEntity() instanceof Player){
            HealthComponent playerHealth = JSONProfileManager.getProfile(((Player) event.getEntity()).getUniqueId()).getHealthComponent();
            playerHealth.replenishLife();
        }
    }
}
