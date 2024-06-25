package com.amorabot.inscripted.utils;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class DelayedTask implements Listener {
    private static Plugin plugin = null;
    private int id = -1;

    public DelayedTask(Plugin instance){
        plugin = instance;
    }

    public DelayedTask(Runnable runnable){
        this(runnable, 0); //chamando o outro construtor, no caso de não ser especificado o delay
    }

    public DelayedTask(Runnable runnable, long delay){
        if (plugin.isEnabled()){
            id = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable, delay);
            //Runs the runnable routine after delay ticks, on the server's main thread
        }else {
            runnable.run();
        }
    }

    public int getId(){
        return id;
    }
}
