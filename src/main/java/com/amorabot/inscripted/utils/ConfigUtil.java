package com.amorabot.inscripted.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class ConfigUtil {
    private File file;
    private FileConfiguration config;

    public ConfigUtil(Plugin plugin, String path){
        this(plugin.getDataFolder().getAbsolutePath() + "/" + path);
        //aqui é chamado o construtor que usa apenas o path
        //getDataFolder() retorna o path em que o plugin especificado está armazenado
        //passar o plugin é necessário pois a partir dele conseguimos acessar o folder desejado na pasta do server
    }

    public ConfigUtil(String path){ //com o path completo podemos organizar o arquivo no local certo
        this.file = new File(path);
        this.config = YamlConfiguration.loadConfiguration(this.file); //cria uma config yaml a partir do arquivo
    }

    public boolean save(){
        try {
            this.config.save(this.file); //criamos um arquivo generico com o path desejado e então criamos um
            //arquivo de config yml armazenado em this.config . Ao tentar salvar o arquivo config no path indicado,
            //se ele existe, será sobrescrito, senão, será criado.
            Utils.log("Saving configuration file...");
            return true;
        }catch (Exception e){
            Utils.error("Error while saving configuration");
            e.printStackTrace();
            return false;
        }
    }

    public File getFile(){
        return this.file;
    }
    public FileConfiguration getConfig(){
        return this.config;
    }

}
