package pluginstudies.pluginstudies.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import pluginstudies.pluginstudies.PluginStudies;
import pluginstudies.pluginstudies.components.CustomMob;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pluginstudies.pluginstudies.utils.Utils.color;

public class MobDropHandler implements Listener {

    private PluginStudies plugin;
    private Map<Entity, CustomMob> customEntities;
    private Map<Entity, Integer> damageIndicators = new HashMap<>(); //Map de entity e duração do indicator
    private DecimalFormat formatter = new DecimalFormat("#.##");

    public MobDropHandler(PluginStudies p){
        plugin = p;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        customEntities = plugin.getCustomEntities();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        //Só ligamos para entidades no hashMap de mobs custom
        Entity rawEntity = event.getEntity();
        if (!customEntities.containsKey(rawEntity)){
            return;
        } //Agora sabemos que a entity que estamos lidando é um customMob
        CustomMob mob = customEntities.get(rawEntity);

        LivingEntity entity = (LivingEntity) rawEntity;
        double damage = event.getFinalDamage();
        double health = entity.getHealth() + entity.getAbsorptionAmount(); //vida total é vida + absorption
        if (health > damage){ //se o dano não foi suficiente para matar (sobreviveu):
            health -= damage;
            entity.setCustomName(color(mob.getName() + "&r&c [" + (int) health + "/" + (int) mob.getMaxHealth() + "♥]"));
        }
        //Damage indicator hologram
        Location loc = entity.getLocation().clone().add(plugin.getRandomOffset(),1, plugin.getRandomOffset());
        plugin.getWorld().spawn(loc, ArmorStand.class, armorStand -> {
            //Aqui dizemos o que deve ser feito com o armorStand que será spawnado antes de ser renderizado propriamente
            //Por enquanto ele é genérico.
            armorStand.setMarker(true); //removes hitboxes
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setSmall(true);
            armorStand.setCustomNameVisible(true);
            armorStand.setCustomName(color("&c" + formatter.format(damage)));

            damageIndicators.put(armorStand, 20 + 10); //o Integer é em ticks, 20ticks = 1seg
        });
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        //Só queremos que o comportamento mude para entidades no hashMap contendo os mobs custom, portanto
        if (!customEntities.containsKey(event.getEntity())){
            return;
        }
        event.setDroppedExp(0);
        event.getDrops().clear(); //pega todos os drops que iriam dropar e limpa essa table
        customEntities.remove(event.getEntity()).tryDropLoot(event.getEntity().getLocation());
    }

    public Map<Entity, Integer> getDamageIndicators(){
        return damageIndicators;
    }
}
