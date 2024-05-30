package com.amorabot.inscripted.utils;

import com.amorabot.inscripted.Inscripted;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Logger;

public class Utils {

    private static Map<String, String> PRETTY_CHARACTERS = new HashMap<>();
    private static Map<String, String> ROMAN_CHAR = new HashMap<>();
    private static Logger logger = Inscripted.getPluginLogger();

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
    public static void msgPlayerAB(Player player, String msg){
//        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(color(msg)));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ColorUtils.translateColorCodes(msg)));
    }
    public static float mapToPercentage(int flatDodge){
        return Float.valueOf(getPercentString(flatDodge));
    }
    public static String getPercentString(int flatDodge){
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(flatDodge/100.0f);
    }
    public static float toTwoDigitsFloat(float floatToConvert){
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Float.valueOf(decimalFormat.format(floatToConvert/100.0f));
    }

    public static void populatePrettyAlphabet(){
//        "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘꞯʀꜱᴛᴜᴠᴡxʏᴢ"
        PRETTY_CHARACTERS.put("A", "ᴀ");PRETTY_CHARACTERS.put("B", "ʙ");PRETTY_CHARACTERS.put("C", "ᴄ");
        PRETTY_CHARACTERS.put("D", "ᴅ");PRETTY_CHARACTERS.put("E", "ᴇ");PRETTY_CHARACTERS.put("F", "ꜰ");
        PRETTY_CHARACTERS.put("G", "ɢ");PRETTY_CHARACTERS.put("H", "ʜ");PRETTY_CHARACTERS.put("I", "ɪ");
        PRETTY_CHARACTERS.put("J", "ᴊ");PRETTY_CHARACTERS.put("K", "ᴋ");PRETTY_CHARACTERS.put("L", "ʟ");
        PRETTY_CHARACTERS.put("M", "ᴍ");PRETTY_CHARACTERS.put("N", "ɴ");PRETTY_CHARACTERS.put("O", "ᴏ");
        PRETTY_CHARACTERS.put("P", "ᴘ");PRETTY_CHARACTERS.put("Q", "ꞯ");PRETTY_CHARACTERS.put("R", "ʀ");
        PRETTY_CHARACTERS.put("S", "ꜱ");PRETTY_CHARACTERS.put("T", "ᴛ");PRETTY_CHARACTERS.put("U", "ᴜ");
        PRETTY_CHARACTERS.put("V", "ᴠ");PRETTY_CHARACTERS.put("W", "ᴡ");PRETTY_CHARACTERS.put("X", "x");
        PRETTY_CHARACTERS.put("Y", "ʏ");PRETTY_CHARACTERS.put("Z", "ᴢ");
    }
    private static String getPrettyCharacter(String charac){
        if (!PRETTY_CHARACTERS.containsKey(charac)){
            return charac;
        }
        return PRETTY_CHARACTERS.get(charac);
    }
    public static String convertToPrettyString(String originalText){
        StringBuilder convertedStringBuilder = new StringBuilder();
        originalText = originalText.toUpperCase();
        for (int i = 0; i < originalText.length(); i++){
            char currChar = originalText.charAt(i);
            if (currChar == ' '){
                convertedStringBuilder.append(currChar);
                continue;
            }
            String prettyChar = getPrettyCharacter(String.valueOf(currChar));
            convertedStringBuilder.append(prettyChar);
        }
        return convertedStringBuilder.toString();
    }
    public static void populateRomanChars(){
        ROMAN_CHAR.put("0","∅");
        ROMAN_CHAR.put("1", "I");
        ROMAN_CHAR.put("2", "II");
        ROMAN_CHAR.put("3", "III");
        ROMAN_CHAR.put("4", "IV");
        ROMAN_CHAR.put("5", "V");
        ROMAN_CHAR.put("6", "VI");
        ROMAN_CHAR.put("7", "VII");
        ROMAN_CHAR.put("8", "VIII");
        ROMAN_CHAR.put("9", "IX");
        ROMAN_CHAR.put("10", "X");
        ROMAN_CHAR.put("11", "XI");
        ROMAN_CHAR.put("12", "XII");
    }
    public static String getRomanChar(int value){
        String stringValue = String.valueOf(value);
        if (!ROMAN_CHAR.containsKey(stringValue)){
            return "*";
        }
        return ROMAN_CHAR.get(stringValue);
    }

    public static ItemStack createItem(Material type, int amount, boolean enchanted, boolean unbreakable, boolean hideUnbreakable
    , String name, String... lore){
        ItemStack item = new ItemStack(type, amount);
        ItemMeta meta = item.getItemMeta();

        if (enchanted){
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.EFFICIENCY, 1, true);
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

    public static double getRandomOffset(){ //-0.999 e 0.999...
        double random = Math.random();
        if (Math.random() > 0.5){random *= -1;}
        return random;
    }

    public static int getRoundedParametricValue(double r1, double r2, double t){
        return (int) Math.ceil(getParametricValue(r1, r2, t));
    }

    public static double getParametricValue(double r1, double r2, double t){
        return r1*(1.0 - t) + r2*t;
    }

    public static double getRandomInclusiveValue(double x1, double x2){
        double normalizedRand = getNormalizedValue();
        return x1*(1.0 - normalizedRand) + x2*normalizedRand; //Parametric mapping between x1 and x2
    }

    public static double getNormalizedValue(){
        return Math.random()/Math.nextDown(1.0);
        //nextDown(1) é o valor máximo de Math.random(), que não inclui 1.
        //Com isso, o valor é devidamente mapeado de 0-1
    }

    public static float applyPercentageTo(int baseValue, int percentMod){
        return baseValue * (1 + (percentMod)/100F);
    }
    public static float applyPercentageTo(float baseValue, int percentMod){
        return baseValue * (1 + (percentMod)/100F);
    }
    public static int[] vectorSum(int[] vec1, int[] vec2){
        int[] updatedVec = new int[vec1.length];
        if (vec1.length != vec2.length){return updatedVec;}
        for (int f = 0; f < vec1.length; f++){
            updatedVec[f] = (vec1[f] + vec2[f]);
        }
        return updatedVec;
    }
}
