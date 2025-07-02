package com.amorabot.inscripted;

import com.amorabot.inscripted.commands.*;
import com.amorabot.inscripted.file.item.RelicEditor;
import com.amorabot.inscripted.file.profile.ProfileDatabase;
import com.amorabot.inscripted.item.relic.Relics;
import com.amorabot.inscripted.item.render.GlyphInfo;
import com.amorabot.inscripted.handlers.Combat.DamageHandler;
import com.amorabot.inscripted.handlers.Combat.InscriptedPlayerDeathEventListener;
import com.amorabot.inscripted.handlers.GUI.GUIHandler;
import com.amorabot.inscripted.handlers.Inventory.*;
import com.amorabot.inscripted.handlers.misc.JoinQuitHandler;
import com.amorabot.inscripted.handlers.misc.SunlightBurnHandler;
import com.amorabot.inscripted.file.item.InscriptionDataManager;
import com.amorabot.inscripted.item.inscription.table.InscriptionTable;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.tasks.ActionBarRenderer;
import com.amorabot.inscripted.utils.DelayedTask;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.logging.Logger;

public final class Inscripted extends JavaPlugin {
    private static Logger logger;
    private static Inscripted inscriptedPlugin;
//    private static BukkitTask holoDepleterTask;
//    private static BukkitTask combatLogger;
    private static BukkitTask actionBarRenderer;
    private World world;

    private MetadataValue metadataTag = new FixedMetadataValue(this, 0);

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger();
        inscriptedPlugin = this;
        this.world = Bukkit.getWorld("world");
        InscriptionTable.loadRawValues();
        Relics.init();


        reloadOnlinePlayerData();

        Utils.populatePrettyAlphabet();
//        GlobalCooldownManager.setup();

        commandsStartupRoutine();
        eventListenersStartupRoutine();

//        //Damage hologram depleter
//        holoDepleterTask = CombatHologramsDepleter.getInstance().runTaskTimer(this,(long) (Math.random()*11), 1L);
//        //Combat logger
//        combatLogger = CombatLogger.getInstance().runTaskTimer(this, (long) (Math.random()*11), 20L);

        //Interface renderer
        actionBarRenderer = ActionBarRenderer.getInstance().runTaskTimer(this, (long) (Math.random()*11), 5L);

        //Player regeneration
//        playerRegen = PlayerRegen.getInstance().runTaskTimer(this, 0, 10L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Utils.log("Shutting Down...");
        ProfileDatabase.saveLoadedProfiles();

//        if (holoDepleterTask != null && !holoDepleterTask.isCancelled()){
//            holoDepleterTask.cancel();
//        }
//        if (combatLogger != null && !combatLogger.isCancelled()){
//            combatLogger.cancel();
//        }
        if (actionBarRenderer != null && !actionBarRenderer.isCancelled()){
            actionBarRenderer.cancel();
        }
//        PlayerRegenManager.shutdown();
//        CombatHologramsDepleter.getInstance().shutdown();

//        try {
//            JSONProfileManager.saveAllToJSON();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    public static Logger getPluginLogger(){
        return logger;
    }
    public World getWorld(){
        return world;
    }
    public static Inscripted getPlugin(){
        return inscriptedPlugin;
    }
    public static BukkitScheduler getScheduler(){
        return getPlugin().getServer().getScheduler();
    }

    private void reloadOnlinePlayerData(){
        getScheduler().cancelTasks(this);
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        if (onlinePlayers.isEmpty()){return;}
        ProfileDatabase.reloadOnlinePlayers(onlinePlayers);
//        JSONProfileManager.reloadOnlinePlayers(Bukkit.getOnlinePlayers());
//        PlayerRegenManager.reloadOnlinePlayers();
//        PlayerPassivesManager.reloadOnlinePlayers();
//        PlayerBuffManager.reloadOnlinePlayers();

//        PlayerInterfaceRenderer.reloadHPDisplays();

        InscriptionDataManager.setupFiles();
//        Relics.setRelicArmorsData(RelicEditor.loadAllArmors());
//        Relics.setRelicWeaponsData(RelicEditor.loadAllWeapons());
        GlyphInfo.loadMappings();

//        ItemModifiersConfig.setup();

//        if (MobManager.spawningEnabled()){
//            log("RegisteredSpawners");
//            for (Spawners s : Spawners.values()){
//                log(s.toString());
//            }
//            MobManager.reinstantiateMobSpawners();
//        }

//        initializeRelicItemData();
    }

    private void commandsStartupRoutine(){
        getCommand("updatenbt").setExecutor(new UpdateNBT(this));
        getCommand("stats").setExecutor(new StatsCommand());
        getCommand("generateitem").setExecutor(new GenerateItem(this));
        getCommand("show").setExecutor(new Show());
        getCommand("template").setExecutor(new TemplateCommand());

        //Has tab executor functionality, if its all in MobCommand class, no need to setTabCompleter()
        getCommand("cast").setExecutor(new CastCommand());
        getCommand("color").setExecutor(new ColorTestsCommand());
        getCommand("mob").setExecutor(new MobCommand());
        getCommand("orb").setExecutor(new OrbCommand());
    }
    private void eventListenersStartupRoutine(){

        //---------   LISTENERS   ------------
        new JoinQuitHandler();
        new PlayerEquipmentHandler(this);
        new InventoryHandler();
        new DelayedTask(this);
        new GUIHandler(this);
        new DamageHandler(this);

        new SunlightBurnHandler();

        //CUSTOM EVENT LISTENERS
//        new ArmorEquipListener();
        new InscriptedPlayerDeathEventListener();
        new WeaponEquipListener();
        new CurrencyUsageListener();
    }

    public MetadataValue getMetadataTag(){
        return this.metadataTag;
    }
}
