package com.amorabot.inscripted.APIs;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.HitComponent;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.components.Mobs.MobStats;
import com.amorabot.inscripted.components.Mobs.MobStatsContainer;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerRegenManager;
import com.amorabot.inscripted.tasks.CombatLogger;
import com.amorabot.inscripted.tasks.DamageHologramDepleter;
import com.amorabot.inscripted.utils.CraftingUtils;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Map;

public class DamageAPI {
    private static final NamespacedKey mobKey = new NamespacedKey(Inscripted.getPlugin(), "INSCRIPTED_MOB");


    public static int[] processAttack(DefenceComponent defenderDefence, HitComponent attackerDamage){
        //Should only aplly calculations to the resulting damage (considering all special modifiers, reductions...
        int[] incomingHit = rollDamages(attackerDamage.getDamages());
        //Incoming hit processing...
        return applyDefences(incomingHit, attackerDamage, defenderDefence);
    }
    public static boolean attackResult(){
        return true;
    }
    private static int[] rollDamages(Map<DamageTypes, int[]> rawDamages){
        /*
        0: Physical
        1: Fire
        2: Lightning
        3: Cold
        4: Abyssal
         */
        int[] hitDamage = new int[5];
        hitDamage[0] = rollDamageType(rawDamages, DamageTypes.PHYSICAL);
        hitDamage[1] = rollDamageType(rawDamages, DamageTypes.FIRE);
        hitDamage[2] = rollDamageType(rawDamages, DamageTypes.LIGHTNING);
        hitDamage[3] = rollDamageType(rawDamages, DamageTypes.COLD);
        hitDamage[4] = rollDamageType(rawDamages, DamageTypes.ABYSSAL);

        return hitDamage;
    }

    //TODO: Possibly redundant method -> find other instances and merge
    private static int rollDamageType(Map<DamageTypes, int[]> rawDamages, DamageTypes type){
        if (rawDamages.containsKey(type)){
            int[] dmg = rawDamages.get(type);
            return CraftingUtils.getRandomNumber(dmg[0], dmg[1]);
        }
        return 0;
    }

    public static int[] applyDefences(int[] incomingHit, HitComponent attackerDamage, DefenceComponent defenderDefences){
        if (incomingHit[0] > 0 && defenderDefences.getFinalArmor()>0){ //Physical mitigation
            incomingHit[0] = mitigatePhysical(incomingHit[0], attackerDamage, defenderDefences.getFinalArmor());
        }
        applyElementalResistance(incomingHit, attackerDamage, 1, defenderDefences.getFireResistance());
        applyElementalResistance(incomingHit, attackerDamage, 2, defenderDefences.getLightningResistance());
        applyElementalResistance(incomingHit, attackerDamage, 3, defenderDefences.getColdResistance());
        applyElementalResistance(incomingHit, attackerDamage, 4, defenderDefences.getAbyssalResistance());

        return incomingHit;
    }
    private static int mitigatePhysical(int rawPhysicalDamage, HitComponent attackerDamage, float defenderArmor){
        //Armour will never prevent more damage than its value divided by X (e.g. X = 5 -> 1000 armour will never prevent more than 200 damage)
        //Bigger hits -> less effective armor
        //https://www.poewiki.net/wiki/Armour/math

        // Alternative -> armorMult = 100 / (100 + armor)
        int armorMitigationFactor = 10;
        float armorPenetration = (attackerDamage.getShred()/100F); // 0 - 1 Shred value
        float damageReduction = (defenderArmor / ( defenderArmor + armorMitigationFactor*rawPhysicalDamage )) - armorPenetration;
        float resultingDamage = rawPhysicalDamage - (rawPhysicalDamage * damageReduction);

        return (int) resultingDamage;
    }
    private static void applyElementalResistance(int[] incomingHit, HitComponent attackerDamage, int elementIndex, int elementalRes){
        //If the incoming damage is 0, or the there is no resulting resistance, there's no need to calculate changes
        float elementalPenetration = attackerDamage.getMaelstrom()/100F;
        if (incomingHit[elementIndex] > 0 && (elementalRes-elementalPenetration)!=0){
            incomingHit[elementIndex] = mitigateElemental(incomingHit[elementIndex], attackerDamage,  elementalRes);
        }
    }
    private static int mitigateElemental(int rawEleDamage, HitComponent attackerDamage, float defenderEleRes){
        //In case of negative resistances, elemental damage gets amplified
        //defenderEleRes is capped below 100, but can be negative
        float newDamage = ( 1 - ((defenderEleRes-attackerDamage.getMaelstrom())/100F) ) * rawEleDamage;
        return (int) newDamage;
    }



    public static void handleDamageEntityDamageEvents(EntityDamageByEntityEvent event){
        //Support for mutiple entityDamage events through polymorphism
        /*
        Entity -> Entity
        Player -> Player
        Player -> Entity
        Entity -> Player
        * */
        boolean playerDefender = event.getEntity() instanceof Player;
        boolean playerAttacker = event.getDamager() instanceof Player;

        event.setDamage(0.001);
        boolean missed;

        if (playerDefender ^ playerAttacker){ //XOR
            //Attacker and defender are not the same class (Player x LivingEntity, for instance)

            if (playerAttacker){ //Player is attacking a LivingEntity/Entity  (Check for mobs and normal living entities and normal entities
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
//                        Utils.log("The hit killed the entity previously, cancelling any damage caused by the root event");
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
        }
    }

    public static boolean playerVsEntityDamage(Player player, Entity entity, boolean playerAttacking){
        boolean hitLanded;

        if (playerAttacking){
            PersistentDataContainer entityDataContainer = entity.getPersistentDataContainer();
            //If its a custom mob
            if (entityDataContainer.has(mobKey, new MobStatsContainer())){
                Profile playerData = JSONProfileManager.getProfile(player.getUniqueId());
                MobStats mobData = entityDataContainer.get(mobKey, new MobStatsContainer());

                //TODO: make a miss function (play particles and all of that)
                hitLanded = attackResult();
                if (!hitLanded){
                    //Play some particles...
                    //If the hit missed, the event that caused it should be cancelled (return value is used outside the function)
                    return true;
                }
                CombatLogger.addToCombat(player);
                assert mobData != null;
                int[] incomingHit = processAttack(mobData.getMobDefence(), playerData.getDamageComponent().getHitData());
                String damageDebug = DamageHologramDepleter.getDamageString(incomingHit);
                DamageHologramDepleter.getInstance().createDamageHologram(incomingHit, entity);

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

        //Player Defending
        PersistentDataContainer mobDataContainer = entity.getPersistentDataContainer();

        //If its a custom mob:
        if (mobDataContainer.has(mobKey, new MobStatsContainer())){
            Profile playerProfile = JSONProfileManager.getProfile(player.getUniqueId());
            MobStats mobStats = mobDataContainer.get(mobKey, new MobStatsContainer());

            hitLanded = attackResult();
            if (!hitLanded){
                //Play some particles...
                //If the hit missed, the event that caused it should be cancelled (return value is used outside the function)
                return true;
            }

            assert mobStats != null;
            int[] incomingHit = DamageAPI.processAttack(playerProfile.getDefenceComponent(), mobStats.getMobHit());
            String damageDebug = DamageHologramDepleter.getDamageString(incomingHit);
            DamageHologramDepleter.getInstance().createDamageHologram(incomingHit, player);

            HealthComponent HPComponent = playerProfile.getHealthComponent();
            Utils.msgPlayer(player ,"&a&l<- " + damageDebug + " " + entity.getName());

            HPComponent.damage(incomingHit);
            playerDamaged(player);
            return false;
        }
        //Player is defending against a common mob, just miss all attacks
        return true;
    }

    public static void playerDamaged(Player player){
        HealthComponent HPComponent = JSONProfileManager.getProfile(player.getUniqueId()).getHealthComponent();
        double mappedHealth = HPComponent.getMappedHealth(20);
        player.setHealth(mappedHealth); //If the player dies here, any posterior damage will kill it too (since current life is 0)
//        if (mappedHealth == 0){
//            Utils.msgPlayer(player, "killing ya");
//        }
        PlayerRegenManager.playerHit(player.getUniqueId());
        CombatLogger.addToCombat(player);
    }

}
