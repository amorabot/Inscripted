package com.amorabot.inscripted.components.Mobs;

import com.amorabot.inscripted.APIs.damageAPI.CombatEffects;
import com.amorabot.inscripted.APIs.damageAPI.EntityStateManager;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.ItemBuilder;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Items.relic.enums.Keystones;
import com.amorabot.inscripted.components.Player.archetypes.Archetypes;
import com.amorabot.inscripted.skills.ParticlePlotter;
import com.amorabot.inscripted.tasks.PlayerInterfaceRenderer;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

@Getter
public class InscriptedMob {

    private static boolean debugMode = true;

    //Regen task
    //Threat Map<Player,int>
    //HP Display

    private final TextDisplay hpDisplay;
    private final Mob mobEntity; //Contains spawner data inside its PDC
    private MobStats stats;


    public InscriptedMob(Bestiary mob, Location loc, String spawnerData){
        this.mobEntity = mob.spawnAt(loc, spawnerData);//Stat data stored withing the PDC already

        //In case stats are not stored withing the PDC
        this.stats = new MobStats(mob.getStats());

        this.hpDisplay = PlayerInterfaceRenderer.createHPDisplayFor(getMobEntity());
        getHpDisplay().text(getMobHPBar());
    }
    public InscriptedMob(LivingEntity existingMob){
        this.mobEntity = (Mob) existingMob;
        Bestiary bestiaryEntry = Bestiary.getBestiaryEntryFor(existingMob);
        this.stats = bestiaryEntry.getStats();



        //Remove existing disp entity
        existingMob.getPassengers().get(0).remove();

        this.hpDisplay = PlayerInterfaceRenderer.createHPDisplayFor(getMobEntity());
        getHpDisplay().text(getMobHPBar());
    }

    public void destroyData(){
        this.mobEntity.remove();
        this.stats = null;
    }

    public boolean takeDamage(int[] atkrDamage, Set<Keystones> atkrKeystones){
        MobStats mobStats = getStats();
        HealthComponent mobHP = mobStats.getMobHealth();
        mobHP.damage(atkrDamage, Set.of(), atkrKeystones);
        getHpDisplay().text(getMobHPBar());
        if (mobHP.getCurrentHealth()==0){
            kill();
            if (debugMode){Utils.log("Killing mob!");}
            return true;
        }
        if (debugMode){Utils.log("Mobs updated hp: " + mobHP.getCurrentHealth());}
        return false;
    }

    private Component getMobHPBar(){
        Component baseBar = getStats().getHealthComponent().getHealthBarComponent();
        return Component.text(getMobEntity().getName()).appendNewline().appendNewline().append(baseBar).appendNewline();
    }

    public void kill(){
//        this.inactive = true;
        this.mobEntity.setHealth(0);
        this.getHpDisplay().remove();
        int postDeathTaskID = new BukkitRunnable(){
            final LivingEntity deadEntity = getMobEntity();
            @Override
            public void run() {
                deadEntity.remove();
                CombatEffects.deathEffect(deadEntity);

                dropAttempt();
            }
        }.runTaskLater(Inscripted.getPlugin(),10L).getTaskId();
        //Death API call + Drops call
        //Effect task?
    }

    public String getSpawnerID(){
        String spawnerID = EntityStateManager.getMobSpawnerID(getMobEntity());
        if (debugMode){ Utils.log("Fetching mob spawner ID data: " + spawnerID); }
        return spawnerID;
    }

    public boolean dropAttempt(){
        Item generatedItem;
        Archetypes[] allArchetypes = Archetypes.values();
        ItemRarities[] rarities = ItemRarities.values();
        Archetypes randomItemArchetype = allArchetypes[Utils.getRandomIntBetween(0,allArchetypes.length-1)];
        ItemRarities randomItemRarity = rarities[Utils.getRandomIntBetween(0,rarities.length-1)];
        double dropRand = Math.random();
        if (dropRand > 0.7){
            if (Math.random() > 0.5){ //Weapon
                generatedItem = ItemBuilder.randomItem(ItemTypes.WEAPON, randomItemArchetype.getWeaponType(), getStats().getMobLevel(), randomItemRarity,  true, false);
            } else {//Armor
                ItemTypes[] armorSlots = new ItemTypes[]{ItemTypes.HELMET,ItemTypes.CHESTPLATE,ItemTypes.LEGGINGS,ItemTypes.BOOTS};
                ItemTypes randomArmorSlot = armorSlots[Utils.getRandomIntBetween(0,armorSlots.length-1)];
                generatedItem = ItemBuilder.randomItem(randomArmorSlot, randomItemArchetype.getArmorType(), getStats().getMobLevel(), randomItemRarity,  true, false);
            }
            Location entityDeathLocation = getMobEntity().getLocation();
            entityDeathLocation.getWorld().dropItem(entityDeathLocation, generatedItem.getItemForm());
            return true;
        } else {
            Utils.log("No drop!");
            return false;
        }
    }
}
