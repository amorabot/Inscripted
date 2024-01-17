package com.amorabot.inscripted.components.Items.Abstract;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.inscripted.components.Items.DataStructures.Modifier;
import com.amorabot.inscripted.components.Items.Interfaces.ItemSubtype;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import static com.amorabot.inscripted.utils.Utils.color;

public interface ItemRenderer extends Serializable {

    default void setDisplayName(String name, ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(color(name));
        item.setItemMeta(itemMeta);
    }
    void renderMainStat(Item itemData, List<String> itemLore);
    default void renderMods(Item itemData, List<String> itemLore){
        String valuesColor = "&#95dbdb";

        List<Modifier> mods = itemData.getModifierList();

        Comparator<Modifier> modifierComparator = (o1, o2) -> {
            if (o1.equals(o2)){
                return 0;
            }
            if (o1.getModifierOrdinal() < o2.getModifierOrdinal()){
                return -1;
            }
            return 1;
        };
        mods.sort(modifierComparator);

        for (Modifier mod : mods){
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
        String tag = "";
        if (rarity.equals(ItemRarities.COMMON)){
            tag += (rarity.getColor()+"&l"+rarity);
            itemLore.add(color(tag));
            return;
        }
        tag += (rarity.getColor()+"&l"+rarity);
        double starRating = itemData.getStarRating();
        tag += mapStarRating(starRating);

        itemLore.add(color(tag));
    }
    default <subType extends Enum<subType> & ItemSubtype> void renderAllCustomLore(Item itemData, List<String> itemLore, subType itemSubtype){
        renderMainStat(itemData, itemLore);
        renderMods(itemData, itemLore);
        renderDescription(itemData, itemLore, itemSubtype);
        renderTag(itemData, itemLore);

        int mods = itemData.getModifierList().size();
        placeRunicDiv(itemData.getName().length(),itemLore,mods);
    }

    default void placeRunicDiv(int itemNameLength, List<String> itemLore, int numberOfMods){
        int longestLineLength = itemNameLength;
        for (String line : itemLore){
            String decoloredLine = Utils.decolor(line);
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

    //TODO: make it a config!!!!!!!!!!!!!!!!!!!!!!!!!!!
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
