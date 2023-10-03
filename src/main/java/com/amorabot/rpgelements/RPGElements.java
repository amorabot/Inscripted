package com.amorabot.rpgelements;

import com.amorabot.rpgelements.commands.*;
import com.amorabot.rpgelements.components.HealthComponent;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.rpgelements.components.Player.Profile;
import com.amorabot.rpgelements.handlers.Combat.CombatDamageHandler;
import com.amorabot.rpgelements.handlers.Combat.MobDropHandler;
import com.amorabot.rpgelements.handlers.GUI.GUIHandler;
import com.amorabot.rpgelements.handlers.Inventory.ArmorEquipListener;
import com.amorabot.rpgelements.handlers.Inventory.PlayerEquipmentHandler;
import com.amorabot.rpgelements.handlers.Inventory.WeaponEquipListener;
import com.amorabot.rpgelements.handlers.JoinQuitHandler;
import com.amorabot.rpgelements.managers.JSONProfileManager;
import com.amorabot.rpgelements.tasks.CombatLogger;
import com.amorabot.rpgelements.tasks.DamageHologramDepleter;
import com.amorabot.rpgelements.tasks.PlayerInterfaceRenderer;
import com.amorabot.rpgelements.utils.DelayedTask;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import static com.amorabot.rpgelements.utils.Utils.log;
import static com.amorabot.rpgelements.utils.Utils.msgPlayerAB;

public final class RPGElements extends JavaPlugin {
    private static Logger logger;
    private static RPGElements rpgElementsPlugin;
    private static BukkitTask holoDepleterTask;
    private static BukkitTask combatLogger;
    private static BukkitTask playerInterfaceRenderer;
    private World world;

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger(); //pega o logger desse plugin, que dá acesso ao console do servidor
        rpgElementsPlugin = this;
        this.world = Bukkit.getWorld("world");
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
//        getWorld().getLivingEntities()

        //Todo: color interpolation for corrupted items

        getCommand("updatenbt").setExecutor(new UpdateNBT(this));
        getCommand("getnbt").setExecutor(new GetNBT(this));
        getCommand("generateweapon").setExecutor(new GenerateWeapon(this));
        getCommand("generatearmor").setExecutor(new GenerateArmor(this));
        getCommand("identify").setExecutor(new Identify(this));
        getCommand("recolor").setExecutor(new Recolor(this));
        getCommand("resetattributes").setExecutor(new ResetAttributes(this));
        getCommand("editmods").setExecutor(new EditMods(this));
        getCommand("show").setExecutor(new Show());
        getCommand("template").setExecutor(new TemplateCommand());

        //---------   LISTENERS   ------------
        new JoinQuitHandler(this);
        new PlayerEquipmentHandler(this);
        new DelayedTask(this);
        new GUIHandler(this);
        new CombatDamageHandler();
        new MobDropHandler(this);

        //CUSTOM EVENT LISTENERS
        new ArmorEquipListener();
        new WeaponEquipListener();

        //Damage hologram depleter
        holoDepleterTask = DamageHologramDepleter.getInstance().runTaskTimer(this,0, 1L);
        //Combat logger
        combatLogger = CombatLogger.getInstance().runTaskTimer(this, 0, 20L);
        //Interface renderer
        playerInterfaceRenderer = PlayerInterfaceRenderer.getInstance().runTaskTimer(this, 0, 5L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("Shutting Down...");
        if (holoDepleterTask != null && !holoDepleterTask.isCancelled()){
            holoDepleterTask.cancel();
        }
        if (combatLogger != null && !combatLogger.isCancelled()){
            combatLogger.cancel();
        }
        if (playerInterfaceRenderer != null && !playerInterfaceRenderer.isCancelled()){
            playerInterfaceRenderer.cancel();
        }
        PlayerInterfaceRenderer.shutdownAllBars();

        try {
            JSONProfileManager.saveAllToJSON();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Logger getPluginLogger(){
        return logger;
    }
    public World getWorld(){
        return world;
    }
    public static RPGElements getPlugin(){
        return rpgElementsPlugin;
    }
}
