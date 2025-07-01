package com.amorabot.inscripted.handlers.Combat;

import com.amorabot.inscripted.APIs.MessageAPI;
import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.APIs.damageAPI.CombatEffects;
import com.amorabot.inscripted.APIs.damageAPI.DamageRouter;
import com.amorabot.inscripted.APIs.damageAPI.DamageSource;
import com.amorabot.inscripted.Inscripted;
//import com.amorabot.inscripted.components.HealthComponent;
//import com.amorabot.inscripted.components.Player.Profile;
//import com.amorabot.inscripted.components.Player.stats.StatCompiler;
//import com.amorabot.inscripted.file.profile.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerBuffManager;
import com.amorabot.inscripted.skill.PlayerAbilities;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.skill.casting.CastSource;
import com.amorabot.inscripted.item.structure.Weapon.Weapon;
import com.amorabot.inscripted.item.structure.Weapon.WeaponTypes;
import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.item.structure.Weapon.RangeCategory;
import com.amorabot.inscripted.item.structure.EquipmentSlots;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.equipment.PlayerEquipment;
import com.amorabot.inscripted.player.equipment.EquimentSlotData;
import com.amorabot.inscripted.item.structure.io.InscriptedItem;
import com.amorabot.inscripted.item.structure.io.ItemDeserializer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

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

        if (attacker instanceof Mob m){
            if (defender instanceof Player p){
                DamageRouter.entityDamage(m,p, DamageSource.HIT, PlayerAbilities.FIST);
                return;
            }
        } else {
            event.setCancelled(true);
        }

        if (attacker instanceof Player){
            Player p = (Player) attacker;
            ItemStack heldItem = p.getInventory().getItemInMainHand();
            
            // Check if player has an Inscripted weapon equipped
            if (hasInscriptedWeaponEquipped(p)) {
                Weapon equippedWeapon = getEquippedWeapon(p);
                WeaponTypes weaponType = equippedWeapon.getWeaponType();
                
                // Check if player has cooldown for this weapon type
                if (hasWeaponCooldown(p, weaponType)) {
                    // Player has cooldown, cancel the event but don't trigger BASIC skill
                    event.setCancelled(true);
                    return;
                }
                
                // Cancel the default attack and trigger the appropriate BASIC skill
                event.setCancelled(true);
                
                Skills basicSkill = mapWeaponTypeToBasicSkill(weaponType);
                WeaponAttackSpeeds attackSpeed = equippedWeapon.getAtkSpeed();
                
                // Cast the appropriate BASIC skill
                basicSkill.cast(p.getUniqueId(), CastSource.PLAYER, attackSpeed);
                return;
            }
            
            if (heldItem.getType().isAir()){ //If the player is punching
//                Profile playerProfile = JSONProfileManager.getProfile(p.getUniqueId());
//                if (!playerProfile.getEquipmentComponent().getSlot(ItemTypes.WEAPON).isIgnorable()){ //If punching with a equipped weapon, unequip
//                    playerProfile.getEquipmentComponent().setSlot(ItemTypes.WEAPON, null);
//                }
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

        PlayerBuffManager.clearAllBuffsFor(deadPlayer);

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

    private Skills mapWeaponTypeToBasicSkill(WeaponTypes weaponType) {
        return switch (weaponType) {
            case AXE -> Skills.BASIC_AXE_SLASH;
            case SWORD -> Skills.BASIC_SWORD_SLASH;
            case BOW -> Skills.BASIC_BOW_SHOT;
            case DAGGER -> Skills.BASIC_DAGGER_SLASH;
            case WAND -> Skills.BASIC_WAND_ATTACK;
            case MACE -> Skills.BASIC_MACE_SLAM;
        };
    }

    private Weapon getEquippedWeapon(Player player) {
        PlayerEquipment playerEquipment = PlayerDataContainer.getPlayerEquipment(player.getUniqueId());
        EquimentSlotData weaponSlotData = playerEquipment.getEquipmentData().get(EquipmentSlots.WEAPON);
        
        if (weaponSlotData == null || weaponSlotData.isIgnorable()) {
            return null;
        }
        
        // We need to access the actual weapon item from the slot data
        // Since the slot data doesn't directly expose the Item, we need to check the player's inventory
        return getWeaponFromPlayerInventory(player);
    }
    
    private Weapon getWeaponFromPlayerInventory(Player player) {
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        if (mainHandItem == null || mainHandItem.getType().isAir()) {
            return null;
        }
        
        // Check if it's an Inscripted weapon
        if (!InscriptedItem.hasInscriptedTag(mainHandItem)) {
            return null;
        }
        
        if (!ItemDeserializer.isWeapon(mainHandItem) || !ItemDeserializer.isIdentified(mainHandItem)) {
            return null;
        }
        
        return ItemDeserializer.deserializeWeaponData(mainHandItem);
    }

    private boolean hasInscriptedWeaponEquipped(Player player) {
        Weapon equippedWeapon = getEquippedWeapon(player);
        return equippedWeapon != null;
    }

    /**
     * Checks if the player has cooldown for the given weapon type.
     * @param player the player to check
     * @param weaponType the weapon type to check cooldown for
     * @return true if player has cooldown, false otherwise
     */
    private boolean hasWeaponCooldown(Player player, WeaponTypes weaponType) {
        Material cooldownMaterial = getWeaponCooldownMaterial(weaponType);
        return player.hasCooldown(cooldownMaterial);
    }

    /**
     * Gets the appropriate cooldown material for the given weapon type.
     * @param weaponType the weapon type
     * @return Material.SHEARS for melee weapons, Material.BOW for ranged weapons
     */
    private Material getWeaponCooldownMaterial(WeaponTypes weaponType) {
        RangeCategory rangeCategory = weaponType.getRange();
        return switch (rangeCategory) {
            case MELEE -> Material.SHEARS;
            case RANGED -> Material.BOW;
        };
    }
}
