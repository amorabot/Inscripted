package com.amorabot.inscripted.handlers.Combat;

import com.amorabot.inscripted.APIs.DamageAPI;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Mobs.MobStats;
import com.amorabot.inscripted.components.Mobs.MobStatsContainer;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.tasks.CombatLogger;
import com.amorabot.inscripted.tasks.DamageHologramDepleter;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class DamageHandler implements Listener {

    private Inscripted plugin;
//    private DecimalFormat formatter = new DecimalFormat("#.##");

    public DamageHandler(Inscripted p){
        plugin = p;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event){
        /*
        Entity -> Entity
        Player -> Player
        Player -> Entity
        Entity -> Player
        * */
        boolean playerDefender = event.getEntity() instanceof Player;
        boolean playerAttacker = event.getDamager() instanceof Player;

        if (playerDefender ^ playerAttacker){
            //Attacker and defender are not the same class (Player x LivingEntity, for instance)

            if (playerAttacker){ //Player is attacking a LivingEntity
                Player player = (Player) event.getDamager();
                LivingEntity entity = (LivingEntity) event.getEntity();

                PersistentDataContainer entityDataContainer = entity.getPersistentDataContainer();
                //Should be turned into a static var.
                NamespacedKey mobKey = new NamespacedKey(Inscripted.getPlugin(), "INSCRIPTED_MOB");

                if (entityDataContainer.has(mobKey, new MobStatsContainer())){
                    Profile playerProfile = JSONProfileManager.getProfile(player.getUniqueId());
                    MobStats mobStats = entityDataContainer.get(mobKey, new MobStatsContainer());

                    if (DamageAPI.attackResult()){ //Testing if the hit landed to begin with
                        //Since the hit landed, the player should be tagged "onCombat"
                        CombatLogger.addToCombat(player);
                        int[] incomingHit = DamageAPI.processAttack(mobStats.getMobHealth(), mobStats.getMobDefence(), playerProfile.getDamageComponent());

                        String damageDebug = DamageHologramDepleter.getDamageString(incomingHit);
                        DamageHologramDepleter.getInstance().createDamageHologram(incomingHit, entity);
                        float mobCurrentHP = mobStats.getMobHealth().getCurrentHealth();
                        if (mobCurrentHP == 0){
                            entity.setHealth(0);
                        } else {
                            event.setDamage(0.001);
                            //The persistent container needs to be set again, else the dummy becomes immortal (health doesnt update)
                            entityDataContainer.set(mobKey, new MobStatsContainer(), mobStats);
                        }
                        player.sendMessage(damageDebug + Utils.color("&c -> "+ entity.getCustomName() +" &7&l[" + mobCurrentHP + "]"));
                        return;
                    }
                }
            } else {
                //Player is defending against a entity
                Player player = (Player) event.getEntity();
                LivingEntity entity = (LivingEntity) event.getDamager();

                event.setDamage(0.001);
                Utils.msgPlayer(player,":D");
            }
        } else {
            // Player x Player  OR  Entity X Entity
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
    }
}
