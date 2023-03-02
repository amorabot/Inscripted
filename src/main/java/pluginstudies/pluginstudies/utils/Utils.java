package pluginstudies.pluginstudies.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pluginstudies.pluginstudies.PluginStudies;

import java.util.*;
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

    public static ItemStack createItem(Material type, int amount, boolean enchanted, boolean unbreakable, boolean hideUnbreakable
    , String name, String... lore){
        ItemStack item = new ItemStack(type, amount);
        ItemMeta meta = item.getItemMeta();

        if (enchanted){
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
        }
        if (unbreakable){
            meta.setUnbreakable(true);
        }
        if (hideUnbreakable){
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }

        if (name != null){
            meta.setDisplayName(color(name));
        }

        if (lore != null){
            List<String> itemLore = new ArrayList<>();
            for (String line : lore){
                itemLore.add(color(line));
            }
            meta.setLore(itemLore);
        }

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack enchantItem(ItemStack item, Enchantment enchant, int level){
        item.addUnsafeEnchantment(enchant, level);
        return item;
    }

    public static ItemStack[] makeArmorSet(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots){
        ItemStack[] armor = new ItemStack[4];
        armor[3] = helmet;
        armor[2] = chestplate;
        armor[1] = leggings;
        armor[0] = boots;

        return armor;
    }
    // METHODS FOR THE CREATION OF CRAFTING ENUM TYPES ---------------------------------------------
    public static List<int[]> generateRange(int min, int max){
//        List<int[]> range = Arrays.asList(new int[]{min, max});
        List<int[]> range = new ArrayList<>();
        range.add(new int[]{min, max});

        return range;
    }
    public static List<int[]> generateRange(int min1, int max1, int min2, int max2){
//        List<int[]> range = Arrays.asList(new int[]{min1, max1}, new int[]{min2, max2});
        List<int[]> range = new ArrayList<>();
        range.add(new int[]{min1, max1});
        range.add(new int[]{min2, max2});

        return range;
    }
    public static Map<Integer, List<int[]>> mapRanges(int[] itemLevels, List<int[]>... ranges){
        //A lista de valores de ilvl será associada manualmente com os ranges correspondentes
        //Primeiro precisamos checar se a quantidade de Keys equivale a de Values
        List<List<int[]>> rangeList = Arrays.asList(ranges);
        if (!(itemLevels.length == rangeList.size())){
            return null;
        }
        Map<Integer, List<int[]>> ilvlMap = new HashMap<>();
        for (int i = 0; i<itemLevels.length; i++){
            ilvlMap.put(itemLevels[i], rangeList.get(i));
        }
        return ilvlMap;
    }
}
