package com.amorabot.inscripted.APIs.damageAPI;

import com.amorabot.inscripted.Inscripted;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class EntityStateManager {

    private static final String INVULNERABLE = "Invul";
    private static final String DEAD = "Dead";
    private static final String INSCRIPTED_PLAYER = "Player";
    private static final String INSCRIPTED_MOB = "Mob";
    /*
    Killer -> Player Death event
    Target -> Mob targeting/ownership
    Skull tags -> Glowing, etc...
    */
    private static final String NEUTRAL = "Neutral";
    private static final String CHAOTIC = "Chaotic";



    public static boolean isPlayerDead(Player player){
        if (isInscriptedPlayer(player)){
            return isDead(player);
        }
        return false;
    }
    public static boolean isInscriptedPlayer(Player player){
        return player.hasMetadata(INSCRIPTED_PLAYER);
    }
    public static void setPlayerMetadata(Player p){
        if (!p.hasMetadata(INSCRIPTED_PLAYER)){
            p.setMetadata(INSCRIPTED_PLAYER, new FixedMetadataValue(Inscripted.getPlugin(), "gaming"));
        }
    }
    public static void removePlayerMetadata(Player p){
        p.removeMetadata(INSCRIPTED_PLAYER, Inscripted.getPlugin());
    }


    public static boolean isMob(LivingEntity l){
        return l.hasMetadata(INSCRIPTED_MOB);
    }
    public static String getMobSpawnerID(LivingEntity l){
        if (!isMob(l)){return "No internal data";}
        return l.getMetadata(INSCRIPTED_MOB).get(0).asString();
    }
    //SpawnerData can be the spawner's ID, a event name, etc...
    public static void setInscriptedMobMeta(LivingEntity l, String spawnerData){
        if (!l.hasMetadata(INSCRIPTED_MOB)){
            l.setMetadata(INSCRIPTED_MOB, new FixedMetadataValue(Inscripted.getPlugin(), spawnerData));
        }
    }

    public static boolean isDead(LivingEntity l){
        return l.hasMetadata(DEAD);
    }
    public static void setDead(LivingEntity l, boolean dead){
        if (dead){
            if (!l.hasMetadata(DEAD)){
                l.setMetadata(DEAD, new FixedMetadataValue(Inscripted.getPlugin(), "MORTEASOMPRASAO"));
            }
            return;
        }
        l.removeMetadata(DEAD,Inscripted.getPlugin());
    }
}
