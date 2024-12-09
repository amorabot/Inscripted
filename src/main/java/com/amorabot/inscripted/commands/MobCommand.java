package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.APIs.MedicalCareAPI;
import com.amorabot.inscripted.components.Mobs.Bestiary;
import com.amorabot.inscripted.components.Mobs.Spawners;
import com.amorabot.inscripted.managers.MobManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MobCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (!(commandSender instanceof Player)){
            return false;
        }
        Player player = (Player) commandSender;
//        NamespacedKey mobKey = new NamespacedKey(Inscripted.getPlugin(), "INSCRIPTED_MOB");
        Location locationToSpawn = player.getLocation().clone();

        if (strings.length == 1){ //spawn a specific mob, lets map it

            switch (strings[0]){
                case "Dummy":
                    return true;
                case "TestMob":
                    break;
                case "Nengue":
                    break;
                case "Ratao":
//                    Mob ratao = (Mob) locationToSpawn.getWorld().spawnEntity(player.getLocation().clone(), EntityType.SILVERFISH);
                    Mob rataoLobotomizado = Bestiary.RATAO.spawnAt(player.getLocation().clone(),"PLAYER_SPAWN");
                    rataoLobotomizado.remove();
                    MedicalCareAPI.showMobGoals(player, rataoLobotomizado);
                    break;
                case "clearMobs":
                    MobManager.clearSpawners(true);
                    break;
                case "reload":
                    Spawners.reloadSpawners();
                    MobManager.reinstantiateMobSpawners();
                    break;
                case "stop":
                    MobManager.stopMobSpawning();
                    Spawners.setEnabled(false);
                    MobManager.clearSpawners(false);
                    break;
                case "enable":
                    Spawners.setEnabled(true);
                    Spawners.reloadSpawners();
                    break;
            }

        }


        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        List<String> mobList = new ArrayList<>();
        if (strings.length == 1){
            //Ideally comes from a Enum
            mobList.add("Dummy");
            mobList.add("TestMob");
            mobList.add("Nengue");
            mobList.add("Ratao");
            mobList.add("clearMobs");
            mobList.add("reload");
            mobList.add("stop");
            mobList.add("enable");

            return mobList;
        } else if (strings.length == 2) { //%Life modifiers, for example
            //Ideally comes from a Enum
            mobList.add("None");
            mobList.add("Double");

            return mobList;
        }
        return null;
    }
}
