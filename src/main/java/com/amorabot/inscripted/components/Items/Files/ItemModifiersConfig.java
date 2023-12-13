package com.amorabot.inscripted.components.Items.Files;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ItemModifiersConfig {
    /*
    https://bukkit.fandom.com/wiki/Configuration_API_Reference#Setting_Values   -   referencia para yml config

    aqui estamos salvando a key value ID com várias child-nodes associadas (indicadas por .something)
    cada .childnode indica um grau de profundidade a mais. no caso, os stats são child-nodes de skills
    e skills é uma child node ligada a ID
    a organização da Key ID no config file seria dessa forma:
       ID:
          attributes:
                  points:
                  intelligence:
                  ...

            saveDefaultConfig();
            getConfig() lê o arquivo config.yml disponível no momento (em resources)
            getList() procura, no arquivo, a key com o nome do path indicado. retorna uma List<> com os itens

            Como um dos usos para o config file, podemos ler o conteúdo e usá-lo no código de forma rápida:

            ConfigUtil config = new ConfigUtil(this, "test.yml");
            config.getConfig().set("hello", "world");//existem os gets e os sets para escrever ou recuperar info. do arquivo.
            //ao setar, definimos a primeira string como key e a segunda como value
            config.save();
    */


    /*
    To get a HashMap, a ConfigurationSection must must first be retrieved. You can return the configuration with getConfigurationSection method.
    The getValues method will return the values in the ConfigurationSection as a map, it takes a boolean which controls if the nested maps will
    be returned in the map.

    Usage:
    this.getConfig().getConfigurationSection("path.to.map").getValues(false)
     */
    private static File file; //Actual file, holding the info
    private static YamlConfiguration config;

    //Finds or generates the config file.
    public static void setup(){
        file = new File(Inscripted.getPlugin().getDataFolder(), "modifiers.yml");

        if (!file.exists()){
            Inscripted.getPlugin().saveResource("modifiers.yml", false);
        }

        config = new YamlConfiguration();
        config.options().parseComments(true);

        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        Utils.log("Loading modifiers config");
        }


    public static YamlConfiguration get(){
        return config;
    }

    public static void set(String path, Object value){
        config.set(path, value);

        save();
    }

    public static void save(){
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void reload(){
        config = YamlConfiguration.loadConfiguration(file); //Retrieves the updated file data and stores it
    }
}
