package com.amorabot.inscripted.handlers.misc;

import com.amorabot.inscripted.Inscripted;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SunlightBurnHandler implements Listener {
    private static final List<String> sunlightBurnable = List.of("SKELETON", "ZOMBIE", "ZOMBIE_VILLAGER", "STRAY", "DROWNED", "PHANTOM");

    public SunlightBurnHandler(){
        Bukkit.getPluginManager().registerEvents(this, Inscripted.getPlugin());
    }

    @EventHandler
    public void onMobBurn(EntityCombustEvent event){
        if ((event instanceof EntityCombustByBlockEvent) || (event instanceof EntityCombustByEntityEvent)){return;}

        if (event.getEntity() instanceof Mob){
            Mob mob = (Mob) event.getEntity();
            if (!sunlightBurnable.contains(mob.getType().toString())){return;}
            ItemStack mobHelmet = mob.getEquipment().getHelmet();
            if (mobHelmet != null && mobHelmet.getType() != Material.AIR){return;} //It does have something "protecting" its head, ignore the event
            if (mob.getEyeLocation().getBlock().getLightFromSky() <= 11){return;} //No sun threat
            //If, after all, its a burnable mob that doesnt have anything in its head and is exposed to sunlight
            event.setCancelled(true);
        }
    }
}
