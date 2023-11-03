package com.amorabot.inscripted.commands;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Mobs.DefensePresets;
import com.amorabot.inscripted.components.Mobs.MobStats;
import com.amorabot.inscripted.components.Mobs.MobStatsContainer;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerRegenManager;
import com.amorabot.inscripted.utils.ColorUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

public class TemplateCommand implements CommandExecutor {

    public static Skeleton testDummy = null;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (!(commandSender instanceof Player)){
            return false;
        }
        Player player = (Player) commandSender;
        HealthComponent HP = JSONProfileManager.getProfile(player.getUniqueId()).getHealthComponent();
//        player.sendMessage("a");

        World playerWorld = player.getWorld();

        if (strings.length == 1){
            String action = strings[0];
            switch (action){
                case "add":
                    break;
                case "remove":
                    if (testDummy != null){
                        testDummy.remove();
                        testDummy = null;
                    } else {
                        return false;
                    }
                    break;
                case "kill":
                    for (Entity entity : playerWorld.getNearbyEntities(player.getLocation(), 2, 2, 2, (entity) -> entity.getType() == EntityType.SKELETON)){
                        entity.remove();
                    }
                    player.sendMessage("killed all nearby skeletons");
                case "toggle":
                    //Not persistent (ideal for temporary tags/ownership/toggles that are not essential in combat) -> if persistance is needed: scoreboard tags
                    if (!player.hasMetadata("Player")){
                        player.sendMessage("you are not a player!, turning you into one");
                        player.setMetadata("Player", new FixedMetadataValue(Inscripted.getPlugin(), "im a player!"));
                    } else {
                        player.sendMessage("you are a player! not anymore");
                        player.removeMetadata("Player", Inscripted.getPlugin());
                    }
                    return true;
                case "combatToggle":
//                    player.addScoreboardTag("Admin");

                    return true;
                case "createTeam":
                    Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
                    if (player.getScoreboard().equals(mainScoreboard)){
                        player.sendMessage("you are already registered in the main scb");
                    } else {
                        player.sendMessage("not registered yet, on it...");
                        player.setScoreboard(mainScoreboard);
                    }
                    Team redTeam = mainScoreboard.getTeam(ChatColor.RED + "team");
                    if (redTeam != null){
                        player.sendMessage("theres a red team!");
                    } else {
                        player.sendMessage("creating a red team...");
                        mainScoreboard.registerNewTeam(ChatColor.RED + "team");
                        return false;
                    }
                    redTeam.setColor(ChatColor.RED);
                    return true;
//                case "damageWard":
////                    HP.halfWard();
////                    PlayerRegenManager.playerHit(player.getUniqueId());
//                    return true;
//                case "zeroWard":
////                    HP.zeroWrd();
////                    PlayerRegenManager.playerHit(player.getUniqueId());
//                    return true;
            }
        }

        return true;
    }
}
