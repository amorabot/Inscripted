package pluginstudies.pluginstudies.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pluginstudies.pluginstudies.PluginStudies;
import pluginstudies.pluginstudies.components.PlayerComponents.Profile;
import pluginstudies.pluginstudies.components.PlayerComponents.Attributes;
import pluginstudies.pluginstudies.components.PlayerComponents.Stats;
import pluginstudies.pluginstudies.utils.ConfigUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static pluginstudies.pluginstudies.utils.Utils.log;

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

    //Uma vez criado o hashMap para guardar a lista de perfis e os usuários associados, serão criados métodos para
    //facilitar a criação e adição de perfis

    public void loadProfileFromConfig(Player player) { //pegamos os stats do arquivo e então salvamos no HashMap
//        for (String id : config.getConfigurationSection("").getKeys(false)){
//            //ConfigurationSection vai retornar uma seção com todas as entradas no path especificado.
//            //nesse caso, ele abrange todos os IDs salvos, uma vez que pega tudo na profundidade raiz ""
//            //então usamos .getKeys() para obtermos um set de Strings para podemos utilizar/loopar
//            //o argumento deep: false nos permite pegar apenas a Key na profundidade original, ignorando as child-nodes
//            //assim temos um set com Strings que será percorrido e então usado para carregar as informações desejadas
//            //com base nos IDs lidos
        String id = player.getUniqueId().toString();
//         config.getList()
        UUID uuid = UUID.fromString(id);
        int points = config.getInt(uuid + ".attributes.points");

        int intelligence = config.getInt(uuid + ".attributes.intelligence");
        int agility = config.getInt(uuid + ".attributes.agility");
        int strength = config.getInt(uuid + ".attributes.strength");
        Attributes attributes = new Attributes(points, intelligence, agility, strength);

        int maxHealth = config.getInt(uuid + ".stats.maxHealth");
        int health = config.getInt(uuid + ".stats.baseHealth");
        int healthPercent = config.getInt(uuid + ".stats.health%");

        int ward = config.getInt(uuid + ".stats.baseWard");
        int wardPercent = config.getInt(uuid + ".stats.ward%");

        int evasion = config.getInt(uuid + ".stats.evasion");
        int evasionPercent = config.getInt(uuid + ".stats.evasion%");

        int armor = config.getInt(uuid + ".stats.armor");
        int armorPercent = config.getInt(uuid + ".stats.armor%");

        int dps = config.getInt(uuid + ".stats.DPS");

        Stats stats = new Stats(health, healthPercent, ward, wardPercent, armor, armorPercent, evasion, evasionPercent);

        Profile profile = new Profile(attributes, stats);
        profiles.put(uuid, profile); //instancia esse perfil na memória do servidor
        log("Loaded profile for the user " + id);
    }
//    }
    public void saveProfilesToConfig(){ //pegamos os stats do HashMap e então salvamos no arquivo config
        for (UUID uuid : profiles.keySet()){     //.keySet() retorna um set com todas as Keys do HashMap
            saveProfile(uuid);
        }
    }
    public void saveProfile(UUID uuid){
        Profile profile = profiles.get(uuid);
        Attributes attributes = profile.getAttributes();
        Stats stats = profile.getStats();
        //aqui estamos salvando a key value ID com várias child-nodes associadas (indicadas por .something)
        //cada .childnode indica um grau de profundidade a mais. no caso, os stats são child-nodes de skills
        //e skills é uma child node ligada a ID
        //a organização da Key ID no config file seria dessa forma:
        //   ID:
        //      attributes:
        //              points:
        //              intelligence:
        //              ...
        config.set(uuid + ".attributes.points", attributes.getPoints());// ---------------- STATS ----------------
        config.set(uuid + ".attributes.intelligence", attributes.getIntelligence());
        config.set(uuid + ".attributes.agility", attributes.getAgility());
        config.set(uuid + ".attributes.strength", attributes.getStrength());

        config.set(uuid + ".stats.maxHealth", stats.getMaxHealth());
        config.set(uuid + ".stats.baseHealth", stats.getBaseHealth());          // ---------------- HEALTH ----------------
        config.set(uuid + ".stats.health%", stats.getHealthPercent());

        config.set(uuid + ".stats.maxWard", stats.getMaxWard());
        config.set(uuid + ".stats.baseWard", stats.getBaseWard());              // ---------------- WARD ----------------
        config.set(uuid + ".stats.ward%", stats.getWardPercent());

        config.set(uuid + ".stats.armor", stats.getArmor());            // ---------------- ARMOR ----------------
        config.set(uuid + ".stats.armor%", stats.getArmorPercent());

        config.set(uuid + ".stats.evasion", stats.getEvasion());        // ---------------- EVASION ----------------
        config.set(uuid + ".stats.evasion%", stats.getEvasionPercent());

        config.set(uuid + ".stats.DPS", stats.getDPS());                // ---------------- DPS ----------------
        log("Saving profile for the user " + uuid);

//        Field[] profileFields = Profile.class.getDeclaredFields();
//        Field (0/1)
//        Field.DECLARED
    }
    public Profile createNewProfile(Player player){
        Attributes attributes = new Attributes(10, 0, 0, 0);
        Stats stats = new Stats(40, 0 ,1);
        Profile profile = new Profile(attributes, stats);
        profiles.put(player.getUniqueId(), profile); //criamos um perfil com as carac. desejadas e então o vinculamos
        //ao id do player em questão ao colocá-lo no hashmap
        return profile;
    }
    public Profile getPlayerProfile(UUID id){
        return profiles.get(id);
    }
    public boolean isNewPlayer(Player player) {
        String id = player.getUniqueId().toString();
        UUID uuid = player.getUniqueId();
        return !config.contains(uuid + "");
    }
    public void unloadProfile(UUID uuid){
        profiles.remove(uuid);
    }
}
