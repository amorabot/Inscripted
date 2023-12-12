package com.amorabot.inscripted.APIs;

import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import java.util.List;

public class MedicalCareAPI {

    public static void showMobGoals(Player player, Mob mob){

        Utils.msgPlayer(player, "&eAI Goals for: " + mob.getName());
        Bukkit.getMobGoals().getAllGoals(mob).forEach(goal -> {
            Utils.msgPlayer(player, "" + goal.getKey().getNamespacedKey());
        });
    }

    public static void removeMobGoals(Mob mob, List<String> goalsToRemove){ //Lobotomy :D
        if (goalsToRemove.isEmpty()){return;}

        Bukkit.getMobGoals().getAllGoals(mob).forEach(goal -> {
            String currentGoalName = goal.getKey().getNamespacedKey().toString();
            if (goalsToRemove.contains(currentGoalName)){
                Bukkit.getMobGoals().removeGoal(mob, goal);
            }
        });
    }
}
