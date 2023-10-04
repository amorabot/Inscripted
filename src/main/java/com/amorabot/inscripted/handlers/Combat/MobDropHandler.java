package com.amorabot.inscripted.handlers.Combat;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.tasks.CombatLogger;
import com.amorabot.inscripted.tasks.DamageHologramDepleter;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobDropHandler implements Listener {

    private Inscripted plugin;
//    private DecimalFormat formatter = new DecimalFormat("#.##");

    public MobDropHandler(Inscripted p){
        plugin = p;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event){
//    public void onEntityDamage(EntityDamageEvent event){
        if (event.getDamager() instanceof Player){
            LivingEntity entity = (LivingEntity) event.getEntity(); //Assuming its a living entity
            Player player = (Player) event.getDamager();
            CombatLogger.addToCombat(player.getUniqueId());
            DamageHologramDepleter.getInstance().createDamageHologram(player, entity);
            //Wynn escape
//        Vector dirVec = player.getLocation().getDirection().clone().normalize();
//        Vector velVector = dirVec.multiply(-2);
//        player.setVelocity(velVector);


        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
    }
}
