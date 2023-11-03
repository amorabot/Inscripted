//package com.amorabot.inscripted.tasks;
//
//import com.amorabot.inscripted.components.HealthComponent;
//import com.amorabot.inscripted.components.Player.Profile;
//import com.amorabot.inscripted.managers.JSONProfileManager;
//import com.amorabot.inscripted.utils.Utils;
//import org.bukkit.Bukkit;
//import org.bukkit.entity.Player;
//import org.bukkit.scheduler.BukkitRunnable;
//
//public class PlayerRegen extends BukkitRunnable {
//
//    private static final PlayerRegen INSTANCE = new PlayerRegen();
//
//    private PlayerRegen(){
//    }
//
//
//    @Override
//    public void run() {
////        for (Player player : Bukkit.getOnlinePlayers()){
////            Profile playerProfile = JSONProfileManager.getProfile(player.getUniqueId());
////            HealthComponent HPComponent = playerProfile.getHealthComponent();
////            if (HPComponent.getCurrentHealth() == HPComponent.getMaxHealth()){
////                continue;
////            }
////            int HPS = HPComponent.getHealthRegen();
////
////            double mappedHealth = HPComponent.getMappedHealth(20);
////            if ((mappedHealth - player.getHealth())>=0.5D){
////                player.setHealth(mappedHealth);
////            }
////            player.sendMessage(Utils.color("&a&l+" + HPS));
////            HPComponent.regenHealth(HPS);
////        }
//    }
//
//    public static PlayerRegen getInstance() {
//        return INSTANCE;
//    }
//}
