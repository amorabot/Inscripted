package com.amorabot.inscripted.components.Items.Abstract;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.inscripted.components.Items.Interfaces.ItemSubtype;
import com.amorabot.inscripted.components.Items.modifiers.Inscription;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import static com.amorabot.inscripted.utils.Utils.color;

public interface ItemRenderer extends Serializable {

    default void setDisplayName(ItemRarities rarity, String name, ItemStack item, boolean isCorrupted, int quality) {
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        String qualitySuffix = "";
        if (quality > 0){
            qualitySuffix = " &f[&b+" + quality + "&f]";
        }

        if (isCorrupted){
            String originalHex;
            String corruptedHex = NamedTextColor.DARK_RED.asHexString();
            switch (rarity){
                case COMMON -> originalHex = NamedTextColor.WHITE.asHexString();
                case MAGIC -> originalHex = NamedTextColor.BLUE.asHexString();
                case RARE -> originalHex = NamedTextColor.YELLOW.asHexString();
                default -> originalHex = "#FFFFFF";
            }
            String openTag = "<gradient:@Hex1@:@Hex2@>";
            String closeTag = "</gradient>";
            String finalOpenTag = openTag.replace("@Hex1@",corruptedHex).replace("@Hex2@", originalHex);
            MiniMessage serializer = MiniMessage.builder().build();
            Component component = serializer.deserialize(finalOpenTag+name+closeTag);
            String legacyDispName = LegacyComponentSerializer.legacyAmpersand().serialize(component);
            itemMeta.setDisplayName(ColorUtils.translateColorCodes(legacyDispName+qualitySuffix));

        } else {
            itemMeta.setDisplayName(color(rarity.getColor() + name + qualitySuffix));
        }
        item.setItemMeta(itemMeta);
    }
    void renderMainStat(Item itemData, List<String> itemLore);
    default void renderMods(Item itemData, List<String> itemLore){
        String valuesColor = "&#95dbdb";

        List<Inscription> mods = itemData.getInscriptionList();

        Comparator<Inscription> modifierComparator = (o1, o2) -> {
            if (o2.isImbued()){
                return 1;
            }
            if (o1.equals(o2)){
                return 0;
            }
            if (o1.getInscription().ordinal() < o2.getInscription().ordinal()){
                return -1;
            }
            return 1;
        };
        mods.sort(modifierComparator);

        for (Inscription mod : mods){
            String modifierDisplayName = mod.getModifierDisplayName(valuesColor, 2);
            itemLore.add(ColorUtils.translateColorCodes(modifierDisplayName));
        }
        if (itemData.getRarity() != ItemRarities.COMMON){
            itemLore.add(color("@FOOTER@"));
        }
    }
    <subType extends Enum<subType> & ItemSubtype> void renderDescription(Item itemData, List<String> itemLore, subType itemSubtype);
    default void renderTag(Item itemData, List<String> itemLore) {
        ItemRarities rarity = itemData.getRarity();
        String tag = rarity.getColor()+"&l"+rarity;
        if (rarity.equals(ItemRarities.COMMON)){
            if (itemData.isCorrupted()){
                tag += " &4☠";
            }
            itemLore.add(color(tag));
            return;
        }
        if (itemData.isCorrupted()){
            tag += " &4☠";
        }
        double starRating = itemData.getStarRating();
        tag += mapStarRating(starRating);

        itemLore.add(color(tag));
    }
    default <subType extends Enum<subType> & ItemSubtype> void renderAllCustomLore(Item itemData, List<String> itemLore, subType itemSubtype){
        renderMainStat(itemData, itemLore);
        renderMods(itemData, itemLore);
        renderDescription(itemData, itemLore, itemSubtype);
        renderTag(itemData, itemLore);

        int mods = itemData.getInscriptionList().size();
        placeRunicDiv(itemData.getName().length(),itemLore,mods);
    }

    default void placeRunicDiv(int itemNameLength, List<String> itemLore, int numberOfMods){
        int longestLineLength = itemNameLength;
        for (String line : itemLore){
            String decoloredLine = ColorUtils.decolor(line);
            String strippedLine = decoloredLine.strip();
            int lineLength = strippedLine.length();
            longestLineLength = Math.max(lineLength, longestLineLength);
        }
//      "¦¡!ï÷ ¨ ╜ ╙"; ᚫ
        StringBuilder headerBar = new StringBuilder("- --=÷¦• ");
        String header = headerBar +  String.valueOf(numberOfMods)  + headerBar.reverse();

        StringBuilder footerBar = new StringBuilder("--=÷• ");
        String footer = footerBar + "ᚫ" + footerBar.reverse();


        int offset = 2;
        if (longestLineLength % 2 == 0){
            offset += (longestLineLength/2) + 1;
        } else {
            offset += (longestLineLength/2);
        }
        offset -= headerBar.length();
        offset = offset/2 - 4;
        String margin = " ".repeat(2);


        String runicHeader = Utils.color("&8"+header+margin).indent((offset));
        String runicFooter = Utils.color("&8"+footer+margin).indent((offset)+4);

        String headerString = "@HEADER@";
        String footerString = "@FOOTER@";

        for (String loreLine : itemLore){
            if (loreLine.contains(headerString)){
                itemLore.set(itemLore.indexOf(headerString), loreLine.replace(headerString, runicHeader));
                continue;
            }
            if (loreLine.contains(footerString)){
                itemLore.set(itemLore.indexOf(footerString), loreLine.replace(footerString, runicFooter));
            }
        }
    }

    //TODO: turn into config
    default String mapStarRating(double starRating){
        if (starRating >= 0 && starRating<=0.5D){
            return "&8 " + "★";
        } else if (starRating<=0.7D){
            return "&7 " + "★";
        } else if (starRating<=0.9D) {
            return "&f " + "★";
        } else {
            return "&6 " + "★";
        }
    }
}
