package com.amorabot.inscripted.APIs.damageAPI;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.skills.ParticlePlotter;
import com.amorabot.inscripted.tasks.CombatHologramsDepleter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CombatEffects {

    public static void playDodgeEffectsAt(Entity dodgeEntity, Entity targetAudience){
        CombatHologramsDepleter.getInstance().instantiateDodgeHologramAt(dodgeEntity.getLocation());
        dodgeEntity.getWorld().spawnParticle(Particle.END_ROD, dodgeEntity.getLocation().clone(), 3, 0, -1, 0, 0);
        if (!(targetAudience instanceof Player)){
            return;
        }
        SoundAPI.playDodgeFor(targetAudience, dodgeEntity.getLocation().clone());
    }

    public static void deathEffect(LivingEntity killedEntity){
        Location loc = killedEntity.getEyeLocation();
        World world = killedEntity.getWorld();
        ItemStack itemCrackData = new ItemStack(Material.NETHER_WART_BLOCK);
        ParticlePlotter.spawnBlockCrackPartileAt(loc.toVector(), world, itemCrackData.getType(),20,4.9);
    }
    //TODO: Death effect
}
