package com.amorabot.inscripted.components.Mobs;

import com.amorabot.inscripted.APIs.MedicalCareAPI;
import com.amorabot.inscripted.APIs.damageAPI.EntityStateManager;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Attack;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

@Getter
public enum Bestiary {

    RATAO(EntityType.SILVERFISH, 30, "Ratão",
            new int[]{60,120,  0,0,  0,0,  0,0,  0,0}, 110, 5, 0, 0, 0, 0, 0, 0,0,
            new HealthComponent(400,0),
            new DefenceComponent(0,20,20,30,70,20),
            List.of(),
            List.of("minecraft:climb_on_top_of_powder_snow","minecraft:silverfish_merge_with_stone","minecraft:silverfish_wake_up_friends"),
            5,0.3);



    @Getter
    private static final NamespacedKey mobPDCKey = new NamespacedKey(Inscripted.getPlugin(), "INSCRIPTED_MOB");
    @Getter
    private static final NamespacedKey idPDCKey = new NamespacedKey(Inscripted.getPlugin(), "SPAWNER_ID");


    private final EntityType type;
    private final Component displayName;
    private final MobStats stats;

    private final List<String> customAddedGoals;
    private final List<String> removedGoals;

    private final double sizeMod;
    private final double speedMod;

    Bestiary(EntityType type, int level, String displayName,
             int[] damage, float acc, int critChance, int critDmg, int shred, int maelstrom, int bleedChance, int firePen, int coldPen, int lightningPen,
             HealthComponent healthPreset, DefenceComponent defensePreset,
             List<String> addGoals, List<String> removeGoals,
             double sizeMod, double speedMod
             ){
        this.type = type;
        this.stats = new MobStats(level,new Attack(
                new int[]{damage[0],damage[1]},
                new int[]{damage[2],damage[3]},new int[]{damage[4],damage[5]},new int[]{damage[6],damage[7]},
                new int[]{damage[8],damage[9]}, acc, critChance, critDmg, shred, maelstrom, bleedChance,  firePen, coldPen, lightningPen),
                healthPreset,
                defensePreset);
        this.displayName = Component.text(displayName).appendSpace().append(Component.text("[Lv."+level+"]").color(NamedTextColor.GRAY));

        this.customAddedGoals = addGoals;
        this.removedGoals = removeGoals;

        this.sizeMod = sizeMod;
        this.speedMod = speedMod;
    }

    public Mob spawnAt(Location loc, String spawnerData){
        Mob mob = (Mob) loc.getWorld().spawnEntity(loc.clone(), type);
        mob.customName(displayName);

        mob.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(sizeMod);
        mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speedMod);

        mob.setCustomNameVisible(true);
        mob.setMaximumNoDamageTicks(1);
        mob.setPersistent(false);
        mob.setHealth(1);

        EntityStateManager.setInscriptedMobMeta(mob,spawnerData);
        //Keeping stat data in memory might be more advantageous for mobs
        //The Bestiary entry and SpawnerID can both be stored on the PDC so the stat data can be restored on restarts
        mob.getPersistentDataContainer().set(getMobPDCKey(), PersistentDataType.STRING, this.toString());
        mob.getPersistentDataContainer().set(getIdPDCKey(), PersistentDataType.STRING, spawnerData);

        //Adding custom AI goals
        //...

        //Removing any unwanted AI goals
        MedicalCareAPI.removeMobGoals(mob, getRemovedGoals());


        //With PDC approach for mob stat storage the mob state persists through restarts! + Stat data doesn't need to be restarted on server reset
//        mob.getPersistentDataContainer().set(getMobPDCKey(), new MobStatsContainer(), stats);
        return mob;
    }

    public static Bestiary getBestiaryEntryFor(LivingEntity mob){
        String bestiaryEntry = mob.getPersistentDataContainer().get(Bestiary.getMobPDCKey(), PersistentDataType.STRING);
        try {
            return Bestiary.valueOf(bestiaryEntry);
        } catch (IllegalArgumentException e) {
            Utils.error("Unable to map bestiary entry: " + bestiaryEntry);
            throw new RuntimeException(e);
        }
    }
    public static String getSpawnerIdFor(LivingEntity mob){
        return mob.getPersistentDataContainer().getOrDefault(Bestiary.getIdPDCKey(), PersistentDataType.STRING, "INVALID MOB");
    }


}
