package com.amorabot.rpgelements.handlers.Combat;

import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.rpgelements.components.Mobs.CustomMob;
import com.amorabot.rpgelements.managers.JSONProfileManager;
import com.amorabot.rpgelements.tasks.CombatLogger;
import com.amorabot.rpgelements.tasks.DamageHologramDepleter;
import com.amorabot.rpgelements.utils.ColorUtils;
import com.amorabot.rpgelements.utils.CraftingUtils;
import com.amorabot.rpgelements.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.*;

import static com.amorabot.rpgelements.utils.Utils.getRandomOffset;

public class MobDropHandler implements Listener {

    private RPGElements plugin;
//    private DecimalFormat formatter = new DecimalFormat("#.##");

    public MobDropHandler(RPGElements p){
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
