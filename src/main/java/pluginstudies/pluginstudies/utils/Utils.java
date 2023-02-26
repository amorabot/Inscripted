package pluginstudies.pluginstudies.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pluginstudies.pluginstudies.PluginStudies;

import java.util.logging.Logger;

public class Utils {

    private static Logger logger = PluginStudies.getPluginLogger();

    public static void log(String ... messages){ // ... indica um numero indefinido de strings a serem passadas
        for (String message : messages){
            logger.info(message);
        }
    }
    public static void warn(String ... messages){
        for (String message : messages){
            logger.warning(message);
        }
    }
    public static void error(String ... messages){
        for (String message : messages){
            logger.severe(message);
        }
    }

    public static String color(String text){
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    public static String decolor(String text){
        return ChatColor.stripColor(text); //só tira a característica depois do &, ainda pode ser recolorido
    }
    public static String recolor(String text){
        return text.replace(ChatColor.COLOR_CHAR, '&');
    }

    public static void msgPlayer(Player player, String ... strings){
        for (String msg : strings){
            player.sendMessage(color(msg));
        }
    }
}
