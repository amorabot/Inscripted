package com.amorabot.inscripted.handlers.Combat;

import com.amorabot.inscripted.APIs.MessageAPI;
import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.combat.CombatEffects;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.combat.buffs.PlayerBuffManager;
import com.amorabot.inscripted.item.structure.io.ItemDeserializer;
import com.amorabot.inscripted.skill.casting.CastType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import static com.amorabot.inscripted.handlers.Inventory.PlayerEquipmentHandler.isValidItem;
import static com.amorabot.inscripted.handlers.Inventory.PlayerEquipmentHandler.weaponCast;

public class DamageHandler implements Listener {

    private Inscripted plugin;

    public DamageHandler(Inscripted p){
        plugin = p;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event){
        //new way to handle damage:
        /*
        player.attack();
        player.setKiller();
        any custom routines are executed, any needed damage is done within them, and after all, the event damage is cancelled

        or -> custom death event: handling items to keep, no losing xp, death teleports player to last established spawnpoint(hearthstone), ...
         */

        Entity attacker = event.getDamager();
        Entity defender = event.getEntity();

//        if (attacker instanceof Mob m){
//            if (defender instanceof Player p){
////                DamageRouter.entityDamage(m,p, DamageSource.HIT, PlayerAbilities.FIST);
//                return;
//            }
//        } else {
//            event.setCancelled(true);
//        }

        if (attacker instanceof Player p){

            ItemStack heldItem = p.getInventory().getItemInMainHand();
            if (heldItem.getType().isAir()){ //If the player is punching
//                FIST
                //Temporary---------------------------
                if (defender instanceof Player){
                    event.setCancelled(true);
                    return;
                }//---------------------------
//                if (defender instanceof LivingEntity def){ //TODO: check why resulting holograms are not interpolating(FROM THIS CALL ONLY)
//                    DamageRouter.playerAttack(p, def, DamageSource.HIT);
//                    return;
//                }
            }
            boolean validClickedWeapon = isValidItem(heldItem) & ItemDeserializer.isWeapon(heldItem);
            if (validClickedWeapon) {
                weaponCast(p, heldItem, CastType.BASIC_ATTACK, 69);
                event.setCancelled(true);
                return;
            }

//            PersistentDataContainer dataContainer = heldItem.getItemMeta().getPersistentDataContainer();
//            boolean isWeapon = FunctionalItemAccessInterface.isItemType(FunctionalItemAccessInterface.WEAPON_TAG, dataContainer);
//            if (isWeapon){
//                Weapon weaponData = FunctionalItemAccessInterface.deserializeWeaponData(dataContainer);
//                if (weaponData == null){return;}
//                PlayerEquipmentHandler.basicAttackBy(p,heldItem,weaponData.getSubtype());
//            }
        }

//        com.amorabot.inscripted.APIs.damageAPI.DamageHandler.handleDamageEntityDamageEvents(event);
    }

    @EventHandler
    public void onEntityDeath(PlayerDeathEvent event){
        event.setShouldPlayDeathSound(false);
        Player deadPlayer = event.getEntity();
        if (deadPlayer.getKiller() != null) {
            Audience audience = Audience.audience(event.getEntity(), deadPlayer.getKiller());
            SoundAPI.playDeathSoundFor(audience, event.getEntity().getLocation());
            event.deathMessage(MessageAPI.deathMessage(deadPlayer.getKiller(), event.getPlayer()));
        } else {
            event.deathMessage(Component.text(deadPlayer.getName() + " ☠").color(NamedTextColor.RED));
        }

        PlayerBuffManager.clearAllBuffsFor(deadPlayer.getUniqueId());

        /*
        If the player's HP is tempered with immediatly, in game death effects are cancelled (Teleport, automatic HP remapping)
        When implementing custom deaths (predefined respawns, etc...), keep this in mind
        */
//        new DelayedTask(new BukkitRunnable() {
//            @Override
//            public void run() {
////                StatCompiler.updateProfile(deadPlayer.getUniqueId());
//
////                HealthComponent.replenishHitPoints(deadPlayer);
//            }
//        }, 5
//        );

        CombatEffects.deathEffect(deadPlayer);
        //Death effect -> TODO: Move this block to CombatEffects class
//        for (int i = 0; i < 20; i++){
//        }
    }
}
