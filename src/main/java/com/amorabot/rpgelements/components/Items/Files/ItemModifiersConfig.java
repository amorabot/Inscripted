package com.amorabot.rpgelements.components.Items.Files;

import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Deprecated
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
    private static File file; //Actual file, holding the info
    private static FileConfiguration fileConfig; //The data within the file becomes accessible through FileConfiguration

    //Finds or generates the config file.
    public static void setup(){
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("RPGElements").getDataFolder(), "modifiers.yml");

        if (!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException exception){
                exception.printStackTrace();
            }
        }

        fileConfig = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get(){
        return fileConfig;
    }

    public static void save(){
        if (!file.exists()){
            try{
                fileConfig.save(file);
            }catch (IOException exception){
                Utils.log("Unable to save modifiers.yml file");
            }
        }
    }

    public static void reload(){
        fileConfig = YamlConfiguration.loadConfiguration(file); //Retrieves the updated file data and stores it
    }
}
