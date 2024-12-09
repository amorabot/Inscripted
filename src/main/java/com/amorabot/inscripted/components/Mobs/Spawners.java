package com.amorabot.inscripted.components.Mobs;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.managers.MobManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

//Provisory enum class to hold predefined spawner data -> Will be transformed to a .yml file
@Getter
public enum Spawners {

    RATAO(Bestiary.RATAO, new Location(Inscripted.getPlugin().getWorld(),-1399,67,-622), 20F, 12,2,(int) (20*10));


    private final MobSpawner spawnerData;
    @Getter
    @Setter
    public static boolean enabled = false;

    Spawners(Bestiary mob, Location loc, float range, int mobLimit, int maxPackSize, int period){
        String key = this.toString();
        this.spawnerData = new MobSpawner(key,mob,loc,range,mobLimit,maxPackSize,period);
        if (isEnabled()){
            MobManager.putSpawner(key, getSpawnerData());
        }
    }

    public static void reloadSpawners(){
        for (Spawners s : Spawners.values()){
            MobSpawner data = s.getSpawnerData();
            MobSpawner.restartSpawnerTask(data);
            MobManager.putSpawner(data.getSpawnerID(),data);
        }
    }


}
