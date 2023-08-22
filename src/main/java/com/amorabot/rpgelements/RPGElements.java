package com.amorabot.rpgelements;

import com.amorabot.rpgelements.commands.*;
import com.amorabot.rpgelements.components.CustomMob;
import com.amorabot.rpgelements.components.PlayerComponents.Profile;
import com.amorabot.rpgelements.handlers.*;
import com.amorabot.rpgelements.managers.JSONProfileManager;
import com.amorabot.rpgelements.utils.DelayedTask;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
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

    private BukkitTask task; //vai receber o bukkit runnable que vai operar a logica do spawn
    private World world;
    private Map<Entity, CustomMob> entities = new HashMap<>(); //TODO: limpar o hashmap no shutdown

    @Override
    public void onEnable() {
        // Plugin startup logic
//        Bukkit.getLogger().info("Hello, World!");
        logger = getLogger(); //pega o logger desse plugin, que dá acesso ao console do servidor
        log("O novo hello world!");

        new JSONProfileManager(this);
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

        SkillsUI skillsUI = new SkillsUI(this);

        getCommand("updatenbt").setExecutor(new UpdateNBT(this));
        getCommand("getnbt").setExecutor(new GetNBT(this));
        getCommand("generateweapon").setExecutor(new GenerateWeapon(this));
        getCommand("identify").setExecutor(new Identify(this));
        getCommand("recolor").setExecutor(new Recolor(this));
        getCommand("skills").setExecutor(skillsUI);
        getCommand("skills").setTabCompleter(skillsUI);
        getCommand("resetattributes").setExecutor(new ResetAttributes(this));
        getCommand("editmods").setExecutor(new EditMods(this));

        //---------   LISTENERS   ------------
//        new TorchHandler(this);
        new JoinQuitHandler(this);
        new WeaponEquipHandler(this);
        new DelayedTask(this);
//        new SkillsUIHandler(this);
        new GUIHandler(this);
        MobDropHandler dropHandler = new MobDropHandler(this);

        world = Bukkit.getWorld("world");
        spawnMobs(8,12, 5 * 20);
        new BukkitRunnable(){ //TODO: transformar em uma task separada
            Map<Entity, Integer> indicatorMap = dropHandler.getDamageIndicators();
            Set<Entity> indicators = indicatorMap.keySet();
            List<Entity> removal = new ArrayList<>();
            @Override
            public void run(){
                //Checar se os armorStands no hashMap ainda tem tempo sobrando
                for (Entity stand : indicators){
                    //Se o tempo associado com um armorStand for 0, devemos removê-lo
                    int ticksLeft = indicatorMap.get(stand);
                    if (ticksLeft == 0){
                        //Queremos remover o armorStand
                        stand.remove();
                        removal.add(stand);
                        continue;
                    }
                    //O stand em questão ainda tem ticks sobrando, portanto vamos atualizá-lo no hashMap
                    ticksLeft--;
                    indicatorMap.put(stand, ticksLeft);
                }
                indicators.removeAll(removal);
            }
        }.runTaskTimer(this, 0L, 1L);

        new BukkitRunnable(){
            @Override
            public void run() {
                for (Player currentPlayer : Bukkit.getOnlinePlayers()){
                    Profile playerProfile = JSONProfileManager.getProfile(currentPlayer.getUniqueId().toString());
                    float health = playerProfile.getHealthComponent().getMaxHealth();
                    int ward = playerProfile.getHealthComponent().getBaseWard();
                    float dps = playerProfile.getDamageComponent().getDPS();
                    msgPlayerAB(currentPlayer, "&c HP[" + health +"]" + "&7 // " + "&bWard[" + ward+ "]" + "     &7" + dps);
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

    public void spawnMobs(int areaSize, int mobCap, int timer){

        //Ao chamar o método spawnMobs, queremos iniciar um BukkitRunnable Anonimo rodando o seu método run()
        //que irá ser responsável pela lógica de spawn e irá rodar no timer especificado em runTaskTimer()
        CustomMob[] mobTypes = CustomMob.values();

        task = new BukkitRunnable() {
            @Override
            public void run(){
                //Checking if there are valid entities in the list
                //Criamos uma lista das entidades que devem ser removidas por invalidez
                try{
                    Set<Entity> spawned = entities.keySet(); //esse set está ligado diretamente com o hashMap, quaisquer
                    //mudanças feitas em spawned serão refletidas em entities.
                    List<Entity> removal = new ArrayList<>();
                    for (Entity entity : spawned){
                        if (entity.isDead() || !entity.isValid()){ //se não é valida/ está morta:
                            removal.add(entity);
                        }
                    }
                        //agora tiramos todas as entidades de removal da lista principal "entities"
                        // .removeAll() nos permite tirar todos os elementos na coleção "removal" da coleção "entities"
                        //agora "entities" possui espaços vagos e permitirá novos spawns
                        spawned.removeAll(removal);
                }catch(ConcurrentModificationException exeption){
                    Utils.error("Multiple concurrent attempts to alterate lists/collections");
                }

                //Spawning Algorithm

                int availableMobCap = mobCap - entities.size();
                if (availableMobCap<= 0){return;}

                int hordeSize = (int) (Math.random() * (availableMobCap + 1));
                int count = 0;
                while (count <= hordeSize){
                    count++;
                    int randX = getRandomWithNeg(areaSize);
                    int randZ = getRandomWithNeg(areaSize);
                    Block block = world.getHighestBlockAt(randX, randZ); //na coordenada escolhida, pegue o bloco mais alto
                    double xOffset = getRandomOffset();
                    double zOffset = getRandomOffset();
                    Location loc = block.getLocation().clone().add(xOffset,1,zOffset); //queremos spawnar 1 bloco acima do escolhido

                    if (!isSpawnable(loc)){
                        continue;
                    }

                    /*
                    O método de decisão do tipo de mob a ser spawnado é baseado em um número aleatório gerado para cada mob.
                    Com base nesse número, vemos em que área da distribuição o número está (A soma de todas as % de spawn
                    dos mobs deve totalizar 100)

                    Exemplo: random = 87
                    Iremos checar se 87 se encaixa nas chances de spawn do mob mais comum
                    No nosso caso, o mob mais comum é o NAGA_SHAMAN, com 60

                    Visto que 87 não está nesse range (é maior, portanto mais raro), vemos o proximo threshold de spawn
                    Para isso, adicionamos a chance de spawn do próximo mob mais raro em cima da chance do NAGA_SHAMAN
                    Nesse caso, 60 + 25 = 85.
                    Assim threshold de spawn muda e então checamos novamente se 87 se encaixa.

                    Nesse caso, ainda é mais raro (87 > 85)

                    Seguimos para o proximo mob e então analisamos o novo threshold (85 + 10 = 95)
                    Analisamos novamente se o numero se encaixa nesse threshold

                    Nesse caso se encaixa, portanto temos um spawn de BARO
                     */

                    double random = Math.random() * 101;
                    double previous = 0;
                    CustomMob typeToSpawn = mobTypes[0]; //Default mob spawn is [0]
                    for (CustomMob type : mobTypes){
                        previous += type.getSpawnChance();
                        if (random <= previous){
                            typeToSpawn = type;
                            break;
                        }
                    }

//                    entities.add(world.spawnEntity(loc, EntityType.ZOMBIE)); //adicionamos à var. entities todos os zombies
                    entities.put(typeToSpawn.spawn(loc), typeToSpawn);
                }
            }

        }.runTaskTimer(this, 0L, timer);

    }

    public boolean isSpawnable(Location location){ //Aqui podemos filtrar quaisquer spawns indesejados
        Block feetBlock = location.getBlock();
        Block headBlock = location.clone().add(0,1,0).getBlock();
        Block upperBlock = location.clone().add(0,2,0).getBlock();

        return feetBlock.isPassable() && !feetBlock.isLiquid()
                && headBlock.isPassable() && !headBlock.isLiquid()
                && upperBlock.isPassable() && !upperBlock.isLiquid();
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

    public Map<Entity, CustomMob> getCustomEntities(){
        return this.entities;
    }
    public World getWorld(){
        return world;
    }
}
