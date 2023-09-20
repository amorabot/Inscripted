package com.amorabot.rpgelements;

import com.amorabot.rpgelements.commands.*;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.rpgelements.components.Player.Profile;
import com.amorabot.rpgelements.handlers.*;
import com.amorabot.rpgelements.handlers.GUI.GUIHandler;
import com.amorabot.rpgelements.handlers.Inventory.ArmorEquipListener;
import com.amorabot.rpgelements.handlers.Inventory.PlayerEquipmentHandler;
import com.amorabot.rpgelements.managers.JSONProfileManager;
import com.amorabot.rpgelements.utils.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import static com.amorabot.rpgelements.utils.Utils.log;
import static com.amorabot.rpgelements.utils.Utils.msgPlayerAB;

public final class RPGElements extends JavaPlugin {
    private static Logger logger;
    private static RPGElements rpgElementsPlugin;
//    private BukkitTask task; //vai receber o bukkit runnable que vai operar a logica do spawn
    private World world;
//    private Map<Entity, CustomMob> entities = new HashMap<>(); //TODO: limpar o hashmap no shutdown

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger(); //pega o logger desse plugin, que dá acesso ao console do servidor
        rpgElementsPlugin = this;
        log("O novo hello world!");

        if (!new File(this.getDataFolder().getAbsolutePath() + "/profiles.json").exists()){
            try {
                String uuid = UUID.randomUUID().toString();
                JSONProfileManager.createProfile(uuid);
                log("creating dummy profile: " + uuid);
                log("attempting to do the save operation");
                JSONProfileManager.saveAllToJSON(); //vai criar o arquivo se ele não existe
                log("initial JSON saving complete.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        JSONProfileManager.loadOnlinePlayersOnReload(Bukkit.getOnlinePlayers());
//        ModifiersJSON.setup(this);

        //Todo: color interpolation for Uniques and corrupted items

        SkillsUI skillsUI = new SkillsUI(this);

        getCommand("updatenbt").setExecutor(new UpdateNBT(this));
        getCommand("getnbt").setExecutor(new GetNBT(this));
        getCommand("generateweapon").setExecutor(new GenerateWeapon(this));
        getCommand("generatearmor").setExecutor(new GenerateArmor(this));
        getCommand("identify").setExecutor(new Identify(this));
        getCommand("recolor").setExecutor(new Recolor(this));
//        getCommand("skills").setExecutor(skillsUI);
//        getCommand("skills").setTabCompleter(skillsUI);
        getCommand("resetattributes").setExecutor(new ResetAttributes(this));
        getCommand("editmods").setExecutor(new EditMods(this));

        //---------   LISTENERS   ------------
        new JoinQuitHandler(this);
        new PlayerEquipmentHandler(this);
        new DelayedTask(this);
        new GUIHandler(this);
//        new ArmorEquipListener();
        Bukkit.getServer().getPluginManager().registerEvents(new ArmorEquipListener(), this);

        new BukkitRunnable(){
            @Override
            public void run() {
                for (Player currentPlayer : Bukkit.getOnlinePlayers()){
                    Profile playerProfile = JSONProfileManager.getProfile(currentPlayer.getUniqueId());
                    float maxHealth = playerProfile.getHealthComponent().getMaxHealth();
                    float curHealth = playerProfile.getHealthComponent().getCurrentHealth();
                    float maxWard = playerProfile.getHealthComponent().getMaxWard();
                    float curWard = playerProfile.getHealthComponent().getCurrentWard();
                    float dps = playerProfile.getDamageComponent().getDPS();
                    String healthTextHex = DefenceTypes.HEALTH.getTextColor();
                    String wardTextHex = DefenceTypes.WARD.getTextColor();
                    String healthSegment = healthTextHex + curHealth + "&7/" + healthTextHex + maxHealth + DefenceTypes.HEALTH.getSpecialChar();
                    String wardSegment = wardTextHex + curWard + "&7/" + wardTextHex + maxWard + DefenceTypes.WARD.getSpecialChar();
                    msgPlayerAB(currentPlayer, (healthSegment +"   "+ wardSegment) + "     &7" + dps);
                }
            }
        }.runTaskTimer(this, 0L, 10L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("Shutting Down...");
        try {
            JSONProfileManager.saveAllToJSON();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Logger getPluginLogger(){
        return logger;
    }

    public int getRandomWithNeg(int range){
        int random = (int) (Math.random() * (range+1)); //gere um número no range
        if (Math.random() > 0.5){random *= -1;} //50% de ser negativo
        return random;
    }
    public double getRandomOffset(){ //nos dá um offset entre 0 e 0.999...
        double random = Math.random();
        if (Math.random() > 0.5){random *= -1;}
        return random;
    }
    public World getWorld(){
        return world;
    }
    public static RPGElements getPlugin(){
        return rpgElementsPlugin;
    }
}
