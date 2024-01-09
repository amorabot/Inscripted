package com.amorabot.inscripted.APIs.damageAPI;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Attack;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Mobs.MobStats;
import com.amorabot.inscripted.components.Mobs.MobStatsContainer;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerRegenManager;
import com.amorabot.inscripted.tasks.CombatLogger;
import com.amorabot.inscripted.tasks.CombatHologramsDepleter;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class DamageHandler {
    private static final NamespacedKey mobKey = new NamespacedKey(Inscripted.getPlugin(), "INSCRIPTED_MOB");

    public static void handleDamageEntityDamageEvents(EntityDamageByEntityEvent event){
        //Support for mutiple entityDamage events through polymorphism
        /*
        Entity -> Entity
        Player -> Player

        Player -> Entity
        Entity -> Player
        */
        boolean isPlayerDefender = event.getEntity() instanceof Player;
        boolean isPlayerAttacker = event.getDamager() instanceof Player;

        event.setDamage(0.001);
        boolean missed;

        if (isPlayerDefender ^ isPlayerAttacker){ //XOR
            //Attacker and defender are not the same class (Player x LivingEntity, for instance)

            if (isPlayerAttacker){ //Player is attacking a LivingEntity/Entity  (Check for mobs and normal living entities and normal entities
                Player player = (Player) event.getDamager();
                //Damaging the entity
                missed = playerVsEntityDamage(player, event.getEntity(), true);
            } else {
                //Player is defending against a entity
                Player player = (Player) event.getEntity();
                //Damaging the player
                missed = playerVsEntityDamage(player, event.getDamager(), false);
            }

            if (!missed){ //If the hit did not miss, lets check if the recieving entity is dead after all damage processing
                if (event.getEntity() instanceof LivingEntity){
                    LivingEntity livingEntity = (LivingEntity) event.getEntity();
                    if (livingEntity.isDead()){
                        event.setCancelled(true);
                        return;
                    }
                }
            } else {
                //If it's a miss, the event should be cancelled
                event.setCancelled(true);
            }
            return;
        } else {
            // Player x Player  OR  Entity X Entity

            if (isPlayerAttacker){ //Player is attacking a LivingEntity/Entity  (Check for mobs and normal living entities and normal entities
                Player attacker = (Player) event.getDamager();
                Player defender = (Player) event.getEntity();
                //Damaging the defending player
                //TODO: Add specific damage mapping for pvp (mainly changing armor and dodge scaling)
                missed = playerVsPlayerDamage(attacker, defender);

                if (!missed){

                    if (defender.isDead()){
                        Utils.log("he ded");
                        event.setCancelled(true);
                    }
//                    attacker.sendMessage("PK :D");

                } else {
                    event.setCancelled(true);
                }
            } else {
                Utils.log("Entity X Entity not handled yet");
            }
        }
    }

    public static boolean playerVsPlayerDamage(Player attacker, Player defender){
        boolean hitLanded;

        Profile attackerProfile = JSONProfileManager.getProfile(attacker.getUniqueId());
        Profile defenderProfile = JSONProfileManager.getProfile(defender.getUniqueId());

        Attack atk = attackerProfile.getDamageComponent().getHitData();
        DefenceComponent def = defenderProfile.getDefenceComponent();

        hitLanded = AttackProcessor.attackResult(atk, def);

        if (!hitLanded){
            CombatEffects.playDodgeEffectsAt(defender, attacker);
            return true;
        }
        int[] incomingHit = AttackProcessor.processAttack(def, atk);
        String damageHoloString = Attack.getDamageString(incomingHit);

        CombatHologramsDepleter.getInstance().instantiateDamageHologramAt(defender.getLocation(), incomingHit);

        //DMG DEBUG FOR DEFdr ---------------------------------------------
        Utils.msgPlayer(defender ,"&c&l<- " + damageHoloString  + " &ffrom " + attacker.getName());
        //Adds defender to combat
        playerDamaged(defender, incomingHit);

        //DMG DEBUG FOR ATKr ---------------------------------------------
        Utils.msgPlayer(attacker, damageHoloString + "&a&l -> &f" + defender.getName() +
                " &c&l[" + ( int )(defenderProfile.getHealthComponent().getCurrentHealth()) + "]");
        //Adds attacker to combat
        CombatLogger.addToPvPCombat(attacker, defender);

        return false;
    }

    public static boolean playerVsEntityDamage(Player player, Entity entity, boolean playerAttacking){
        boolean hitLanded;

        if (playerAttacking){
            PersistentDataContainer entityDataContainer = entity.getPersistentDataContainer();
            //If its a custom mob
            if (entityDataContainer.has(mobKey, new MobStatsContainer())){
                Profile playerData = JSONProfileManager.getProfile(player.getUniqueId());
                MobStats mobData = entityDataContainer.get(mobKey, new MobStatsContainer());

                assert mobData != null;
                hitLanded = AttackProcessor.attackResult(playerData.getDamageComponent().getHitData(), mobData.getMobDefence());
                if (!hitLanded){
                    CombatEffects.playDodgeEffectsAt(entity, player);
                    //If the hit missed, the event that caused it should be cancelled (return value is used outside the function)
                    return true;
                }
                CombatLogger.addToCombat(player);
                int[] incomingHit = AttackProcessor.processAttack(mobData.getMobDefence(), playerData.getDamageComponent().getHitData());
                String damageDebug = Attack.getDamageString(incomingHit);
                CombatHologramsDepleter.getInstance().instantiateDamageHologramAt(entity.getLocation(), incomingHit);

                HealthComponent mobHP = mobData.getMobHealth();
                mobHP.damage(incomingHit);
                if (mobHP.getCurrentHealth() == 0){
                    if (entity instanceof Mob){
                        Mob mobEntity = (Mob) entity;
                        mobEntity.setHealth(0);
                        Utils.msgPlayer(player, damageDebug + "&a&l -> " + entity.getName() + " &4&lDEAD");
                        return false;
                    }
                    //Other types of entities must be handled later
                    Utils.log(player.getName() + " just killed a " + entity.getClass());
                    entity.remove();
                }
                //Since the mob didnt die, lets update its container:
                entityDataContainer.set(mobKey, new MobStatsContainer(), mobData);

                Utils.msgPlayer(player, damageDebug + "&a&l -> " + entity.getName() + " &c&l" + mobHP.getCurrentHealth());
                return false;
            } else {
                if (entity instanceof LivingEntity){
                    LivingEntity livingEntity = (LivingEntity) entity;
                    livingEntity.remove();
                }
                Utils.log("Player -> Generic entity (Not handled yet)");
            }
            return false;
        }

        //Player is defending against a entity
        PersistentDataContainer mobDataContainer = entity.getPersistentDataContainer();

        //If its a custom mob:
        if (mobDataContainer.has(mobKey, new MobStatsContainer())){
            Profile playerProfile = JSONProfileManager.getProfile(player.getUniqueId());
            MobStats mobStats = mobDataContainer.get(mobKey, new MobStatsContainer());

            assert mobStats != null;
            hitLanded = AttackProcessor.attackResult(mobStats.getMobHit(), playerProfile.getDefenceComponent());
            if (!hitLanded){
                CombatEffects.playDodgeEffectsAt(player, entity);
                //If the hit missed, the event that caused it should be cancelled (return value is used outside the function)
                return true;
            }

            int[] incomingHit = AttackProcessor.processAttack(playerProfile.getDefenceComponent(), mobStats.getMobHit());
            String damageDebug = Attack.getDamageString(incomingHit);
            CombatHologramsDepleter.getInstance().instantiateDamageHologramAt(player.getLocation(), incomingHit);

            Utils.msgPlayer(player ,"&a&l<- " + damageDebug + " " + entity.getName());

            playerDamaged(player, incomingHit);
            return false;
        }
        //Player is defending against a common mob, just miss all attacks
        return true;
    }

    //Handles the effects of a player being hit
    public static void playerDamaged(Player player, int[] incomingHit){
        HealthComponent HPComponent = JSONProfileManager.getProfile(player.getUniqueId()).getHealthComponent();

        HPComponent.damage(incomingHit);

        double mappedHealth = HPComponent.getMappedHealth(20);
        player.setHealth(mappedHealth); //If the player dies here, any posterior damage will kill it too (since current life is 0)
        PlayerRegenManager.startWardRegenCooldownFor(player.getUniqueId());
        CombatLogger.addToCombat(player);
    }

}
