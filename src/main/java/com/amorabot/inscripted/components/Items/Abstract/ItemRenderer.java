package com.amorabot.inscripted.components.Items.Abstract;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.inscripted.components.Items.DataStructures.Modifier;
import com.amorabot.inscripted.components.Items.Interfaces.AffixTableSelector;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

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

        List<Modifier> mods = itemData.getModifiers();

        Comparator<Modifier> modifierComparator = (o1, o2) -> {
            //TODO: Unique priority
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
    <subType extends Enum<subType> & AffixTableSelector> void renderDescription(Item itemData, List<String> itemLore, subType itemSubtype);
    default void renderTag(Item itemData, List<String> itemLore) {
        ItemRarities rarity = itemData.getRarity();
        String tag = "";
        switch (rarity){
            case COMMON -> {
                tag += ("&f&l"+rarity);
                itemLore.add(color(tag));
                return;
            }
            case MAGIC -> tag += ("&9&l"+rarity);
            case RARE -> tag += ("&e&l"+rarity);
        }
        int starRating = itemData.getStarRating();
        tag += mapStarRating(starRating);
        itemLore.add(color(tag));
    }
    default <subType extends Enum<subType> & AffixTableSelector> void renderAllCustomLore(Item itemData, List<String> itemLore, subType itemSubtype){
        renderMainStat(itemData, itemLore);
        renderMods(itemData, itemLore);
        renderDescription(itemData, itemLore, itemSubtype);
        renderTag(itemData, itemLore);

        int mods = itemData.getModifiers().size();
        placeRunicDiv(itemData.getName().length(),itemLore,mods);
    }

    default void placeRunicDiv(int itemNameLength, List<String> itemLore, int numberOfMods){
        int longestLineLength = itemNameLength;
        for (String line : itemLore){
            String decoloredLine = Utils.decolor(line);
            String strippedLine = decoloredLine.strip();
            int lineLength = strippedLine.length();
            if (lineLength > longestLineLength){
                longestLineLength = lineLength;
            }
        }
//        String chars = "¦¡!ï÷ ¨ ╜ ╙"; ᚫ
        String headerCenterElement = "[• "+numberOfMods+" •]";
        int centerElementLength = headerCenterElement.length();
        if (longestLineLength - centerElementLength<=2){
            //Add no-offset bar
            return;
        }
        TextComponent runicHeaderCenterComponent = Component.text(headerCenterElement);
        String footerCenterElement = "--=÷• ᚫ •÷=--";
        TextComponent runicFooterCenterComponent = Component.text(footerCenterElement).color(NamedTextColor.DARK_GRAY);
        int offset = (longestLineLength - centerElementLength)/2;
        TextComponent finalRunicHeader = getHeaderTextComponent(offset, runicHeaderCenterComponent);

        String deserializedHeader = ColorUtils.translateColorCodes(LegacyComponentSerializer.legacyAmpersand().serialize(finalRunicHeader));
        String deserializedFooter = ColorUtils.translateColorCodes(LegacyComponentSerializer.legacyAmpersand().serialize(runicFooterCenterComponent));
        for (String loreLine : itemLore){
            String headerString = "@HEADER@";
            String footerString = "@FOOTER@";
            int offHeader = (offset/2)+1;
            if (loreLine.contains(headerString)){
                itemLore.set(itemLore.indexOf(headerString), loreLine.replace(headerString, deserializedHeader).indent(offHeader));
                continue;
            }
            if (loreLine.contains(footerString)){
                int footerOffset = offHeader + (deserializedHeader.length()-deserializedFooter.length())/2 - 3;
                itemLore.set(itemLore.indexOf(footerString), loreLine.replace(footerString, deserializedFooter).indent(footerOffset));
            }
        }
    }

    @NotNull
    private TextComponent getHeaderTextComponent(int offset, TextComponent runicDivCenter) {
        StringBuilder leftBar = new StringBuilder();
        for (int i = 1; i<= offset; i++){
            if (i % 2 == 0){
                if (i==6){
                    continue;
                }
                leftBar.append("!");
            } else if (i % 3 == 0) {
                leftBar.append("¡");
            } else if (i % 5 == 0) {
                leftBar.append("¦");
            } else if (i % 7 == 0) {
                leftBar.append("ï");
            } else {
                leftBar.append("÷");
            }
        }
        TextComponent leftComponent = Component.text("-="+leftBar);
        TextComponent rightComponent = Component.text(leftBar.reverse()+"=-");
        return leftComponent.append(runicDivCenter).append(rightComponent).color(NamedTextColor.DARK_GRAY);
    }

    //TODO: make it a config!!!!!!!!!!!!!!!!!!!!!!!!!!!
    default String mapBasePercentileColor(float percentile){
        return "&"+TextColor.lerp(percentile, TextColor.color(210, 255, 64), TextColor.color(64, 255, 118)).asHexString();
    }
    default String mapStarRating(int starRating){
        if (starRating >= 0 && starRating<=50){
            return "&8 " + "★";
        } else if (starRating<=70){
            return "&7 " + "★";
        } else if (starRating<=90) {
            return "&f " + "★";
        } else {
            return "&6 " + "★";
        }
    }
}
