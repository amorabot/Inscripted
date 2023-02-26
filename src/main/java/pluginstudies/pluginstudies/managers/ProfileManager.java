package pluginstudies.pluginstudies.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pluginstudies.pluginstudies.PluginStudies;
import pluginstudies.pluginstudies.components.Profile;
import pluginstudies.pluginstudies.components.Skills;
import pluginstudies.pluginstudies.utils.ConfigUtil;
import pluginstudies.pluginstudies.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileManager {

    private PluginStudies plugin; //precisaremos de uma instância do plugin principal para podermos utilizar depois
    private Map<UUID, Profile> profiles = new HashMap<>();
    //aqui criamos um Map<K, V> onde K são os os ids únicos de jogadores e Profiles são os perfis que iremos criar
    //para cada jogador e associar ao seu UUID específico. O UUID erá extraído com base no objeto player e guardado
    //no map para guardarmos e relacionarmos players e perfis.
    private ConfigUtil profileConfig;
    private FileConfiguration config;

    public ProfileManager(PluginStudies plugin){
        this.plugin = plugin;
        this.profileConfig = plugin.getProfileConfig();
        this.config = profileConfig.getConfig();
    }

    //Uma vez criado o hashMap para guardar a lista de perfis e os usuários associados, serão criados método para
    //facilitar a criação e adição de perfis

    public void loadProfilesFromConfig(){ //pegamos os stats do arquivo e então salvamos no HashMap
        for (String id : config.getConfigurationSection("").getKeys(false)){
            //ConfigurationSection vai retornar uma seção com todas as entradas no path especificado.
            //nesse caso, ele abrange todos os IDs salvos, uma vez que pega tudo na profundidade raiz ""
            //então usamos .getKeys() para obtermos um set de Strings para podemos utilizar/loopar
            //o argumento deep: false nos permite pegar apenas a Key na profundidade original, ignorando as child-nodes
            //assim temos um set com Strings que será percorrido e então usado para carregar as informações desejadas
            //com base nos IDs lidos

            UUID uuid = UUID.fromString(id);
            int points = config.getInt(uuid + ".skills.points");
            int health = config.getInt(uuid + ".skills.health");
            int intelligence = config.getInt(uuid + ".skills.intelligence");
            int agility = config.getInt(uuid + ".skills.agility");
            int strength = config.getInt(uuid + ".skills.strength");
            Skills skills = new Skills(points, health, intelligence, agility, strength);
            Profile profile = new Profile( skills );
            profiles.put(uuid, profile);
            Utils.log("Loaded profile for the user " + id);
        }
    }
    public void saveProfilesToConfig(){ //pegamos os stats do HashMap e então salvamos no arquivo config
        for (UUID uuid : profiles.keySet()){     //.keySet() retorna um set com todas as Keys do HashMap
            String id = uuid.toString();
            Profile profile = profiles.get(uuid);
            Skills skills = profile.getSkills();
            //aqui estamos salvando a key value ID com várias child-nodes associadas (indicadas por .something)
            //cada .childnode indica um grau de profundidade a mais. no caso, os stats são child-nodes de skills
            //e skills é uma child node ligada a ID
            //a organização da Key ID no config file seria dessa forma:
            //   ID:
            //      skills:
            //          points:
            //          health:
            //          ...
            config.set(id + ".skills.points", skills.getPoints());
            config.set(id + ".skills.health", skills.getHealth());
            config.set(id + ".skills.intelligence", skills.getIntelligence());
            config.set(id + ".skills.agility", skills.getAgility());
            config.set(id + ".skills.strength", skills.getStrength());
            Utils.log("Saving profile for the user " + id);
        }
    }
    public Profile createNewProfile(Player player){
        Skills skills = new Skills(10, 0, 0, 0, 0);
        Profile profile = new Profile(skills);
        profiles.put(player.getUniqueId(), profile); //criamos um perfil com as carac. desejadas e então o vinculamos
        //ao id do player em questão ao colocá-lo no hashmap
        return profile;
    }
    public Profile getPlayerProfile(UUID id){
        return profiles.get(id);
    }
}
