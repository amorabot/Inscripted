package com.amorabot.inscripted;

import com.amorabot.inscripted.commands.*;
import com.amorabot.inscripted.components.Items.Armor.ArmorTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Tiers;
import com.amorabot.inscripted.components.Items.DataStructures.ModifierIDs;
import com.amorabot.inscripted.components.Items.Files.ItemModifiersConfig;
import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;
import com.amorabot.inscripted.handlers.Combat.DamageHandler;
import com.amorabot.inscripted.handlers.GUI.GUIHandler;
import com.amorabot.inscripted.handlers.Inventory.*;
import com.amorabot.inscripted.handlers.misc.JoinQuitHandler;
import com.amorabot.inscripted.handlers.misc.SunlightBurnHandler;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerRegenManager;
import com.amorabot.inscripted.tasks.CombatLogger;
import com.amorabot.inscripted.tasks.CombatHologramsDepleter;
import com.amorabot.inscripted.tasks.PlayerInterfaceRenderer;
import com.amorabot.inscripted.utils.DelayedTask;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static com.amorabot.inscripted.utils.Utils.log;

public final class Inscripted extends JavaPlugin {
    private static Logger logger;
    private static Inscripted inscriptedPlugin;
    private static BukkitTask holoDepleterTask;
    private static BukkitTask combatLogger;
    private static BukkitTask playerInterfaceRenderer;
    private World world;

    private MetadataValue metadataTag = new FixedMetadataValue(this, 0);

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger();
        inscriptedPlugin = this;
        this.world = Bukkit.getWorld("world");

        initializeProfileJSON();
        reloadRoutine();
        ModifierIDs.loadModifiers();
        Utils.populatePrettyAlphabet();
        Utils.populateRomanChars();
//        getWorld().getLivingEntities()

        commandsStartupRoutine();
        eventListenersStartupRoutine();

        //Damage hologram depleter
        holoDepleterTask = CombatHologramsDepleter.getInstance().runTaskTimer(this,0, 1L);
        //Combat logger
        combatLogger = CombatLogger.getInstance().runTaskTimer(this, 0, 20L);
        //Interface renderer
        playerInterfaceRenderer = PlayerInterfaceRenderer.getInstance().runTaskTimer(this, 0, 5L);
        //Player regeneration
//        playerRegen = PlayerRegen.getInstance().runTaskTimer(this, 0, 10L);
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
        PlayerRegenManager.shutdown();
        CombatHologramsDepleter.getInstance().shutdown();

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
    public static Inscripted getPlugin(){
        return inscriptedPlugin;
    }

    private void reloadRoutine(){
        JSONProfileManager.reloadOnlinePlayers(Bukkit.getOnlinePlayers());
        PlayerRegenManager.reloadOnlinePlayers();

        ItemModifiersConfig.setup();
    }

    private void initializeProfileJSON(){
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
    }

    private static void populateArmorConfigSection(){

        FileConfiguration config = inscriptedPlugin.getConfig();

        String armorRoot = ArmorTypes.class.getSimpleName();
        String defString = DefenceTypes.class.getSimpleName();
        for (ArmorTypes armorType : ArmorTypes.values()){
            String currTypeStringPath = armorRoot+"."+armorType.toString();

            String defencePath = currTypeStringPath+"."+defString;
            List<String> defList = new ArrayList<>();
            defList.add(DefenceTypes.WARD.toString());
            defList.add(DefenceTypes.ARMOR.toString());
            defList.add(DefenceTypes.DODGE.toString());
            config.set(defencePath, defList);

            for (Tiers tier : Tiers.values()){

                String currTierStringPath = currTypeStringPath+"."+tier.toString()+".";

                String namePath = currTierStringPath+"NAME";
                config.set(namePath, "TEMPLATE");

                List<String> mappedDefs = config.getStringList(defencePath);
                for (String def : mappedDefs){
                    String slotDefenceStringPath = currTierStringPath + def;
                    config.set(slotDefenceStringPath, 69);
                }

                for (ItemTypes armorSlot : ItemTypes.values()){
                    if (armorSlot.equals(ItemTypes.WEAPON)){continue;}

                    String slotHealthPath = currTierStringPath + armorSlot;
                    config.set(slotHealthPath, 99);
                }
            }
        }

        inscriptedPlugin.saveConfig();
    }

    private static void populateWeaponConfigSection(){
        FileConfiguration config = inscriptedPlugin.getConfig();

        String weaponRoot = WeaponTypes.class.getSimpleName();
        for (WeaponTypes weaponType : WeaponTypes.values()){
            String currWeaponTypePath = weaponRoot + "." + weaponType + ".";
            //Fields: Name, Base Damages
            for (Tiers tier : Tiers.values()){
                String currTierPath = currWeaponTypePath + tier + ".";

                String namePath = currTierPath + "NAME";
                String damagePath = currTierPath + "BASE_DAMAGE";

                config.set(namePath, "TEMPREITO");
                config.set(damagePath, new int[2]);
            }
        }

        inscriptedPlugin.saveConfig();
    }

    private void commandsStartupRoutine(){
        getCommand("updatenbt").setExecutor(new UpdateNBT(this));
        getCommand("stats").setExecutor(new StatsCommand(this));
        getCommand("generateitem").setExecutor(new GenerateItem(this));
        getCommand("identify").setExecutor(new Identify(this));
        getCommand("recolor").setExecutor(new Recolor(this));
        getCommand("resetattributes").setExecutor(new ResetAttributes(this));
        getCommand("editmods").setExecutor(new EditMods(this));
        getCommand("show").setExecutor(new Show());
        getCommand("template").setExecutor(new TemplateCommand());

        //Has tab executor functionality, if its all in MobCommand class, no need to setTabCompleter()
        getCommand("mob").setExecutor(new MobCommand());
        getCommand("orb").setExecutor(new OrbCommand());
    }
    private void eventListenersStartupRoutine(){

        //---------   LISTENERS   ------------
        new JoinQuitHandler(this);
        new PlayerEquipmentHandler(this);
        new InventoryHandler();
        new DelayedTask(this);
        new GUIHandler(this);
        new DamageHandler(this);

        new SunlightBurnHandler();

        //CUSTOM EVENT LISTENERS
        new ArmorEquipListener();
        new WeaponEquipListener();
        new CurrencyUsageListener();
    }

    public MetadataValue getMetadataTag(){
        return this.metadataTag;
    }
}
