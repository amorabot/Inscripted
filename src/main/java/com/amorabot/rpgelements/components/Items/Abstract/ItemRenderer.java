package com.amorabot.rpgelements.components.Items.Abstract;

import com.amorabot.rpgelements.components.Items.Armor.Armor;
import com.amorabot.rpgelements.components.Items.Interfaces.AffixTableSelector;
import com.amorabot.rpgelements.components.Items.Weapon.Weapon;
import com.amorabot.rpgelements.utils.ColorUtils;
import com.amorabot.rpgelements.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

public interface ItemRenderer extends Serializable {

    void setDisplayName(String name, ItemStack item);
    void renderMainStat(Item itemData, List<String> itemLore);
    void renderMods(Item itemData, List<String> itemLore);
    <subType extends Enum<subType> & AffixTableSelector> void renderDescription(Item itemData, List<String> itemLore, subType itemSubtype);
    void renderTag(Item itemData, List<String> itemLore);
    default void placeDivs(List<String> itemLore){
        int longestLineLength = 0;
        for (String line : itemLore){
            String decoloredLine = Utils.decolor(line);
            String strippedLine = decoloredLine.strip();
            int lineLength = strippedLine.length();
            if (lineLength > longestLineLength){
                longestLineLength = lineLength;
            }
        }
        int divLength = (longestLineLength-1)/2;
        int offSet = (longestLineLength-divLength)/2;
        String div = "-".repeat(divLength);
        String divLine = div.indent(offSet);

        while(itemLore.contains("-div-")){
            int divIndex = itemLore.indexOf("-div-");
            itemLore.set(divIndex, Utils.color(("&8"+divLine).indent(1)));
        }
    }

    default <subType extends Enum<subType> & AffixTableSelector> void renderAllCustomLore(Item itemData, List<String> itemLore, subType itemSubtype){
        renderMainStat(itemData, itemLore);
        renderMods(itemData, itemLore);
        renderDescription(itemData, itemLore, itemSubtype);
        renderTag(itemData, itemLore);
        boolean weapon = itemData instanceof Weapon;
        boolean armor = itemData instanceof Armor;
        int mods = 0;
        if (weapon || armor){
            if (armor){ //Todo: consider unifying -> all items have mods (getters specify a class)
                mods = ((Armor) itemData).getModifiers().size();
            } else {
                mods = ((Weapon) itemData).getModifiers().size();
            }
        }
        placeRunicDiv(itemLore,mods);
    }

    default void placeRunicDiv(List<String> itemLore, int numberOfMods){
        int longestLineLength = 0;
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
        String footerCenterElement = "--=÷• ᚫ •÷=--"; //Todo: get random rune?
        TextComponent runicFooterCenterComponent = Component.text(footerCenterElement).color(NamedTextColor.DARK_GRAY);
        int offset = (longestLineLength - centerElementLength)/2;
        TextComponent finalRunicHeader = getHeaderTextComponent(offset, runicHeaderCenterComponent);

        String deserializedHeader = ColorUtils.translateColorCodes(LegacyComponentSerializer.legacyAmpersand().serialize(finalRunicHeader));
        String deserializedFooter = ColorUtils.translateColorCodes(LegacyComponentSerializer.legacyAmpersand().serialize(runicFooterCenterComponent));
        for (String loreLine : itemLore){
            String headerString = "@HEADER@";
            String footerString = "@FOOTER@";
            int offHeader = offset/2+1;
            if (loreLine.contains(headerString)){
                itemLore.set(itemLore.indexOf(headerString), loreLine.replace(headerString, deserializedHeader).indent(offHeader));
                continue;
            }
            if (loreLine.contains(footerString)){
                int footerOffset = offHeader + (deserializedHeader.length()-deserializedFooter.length())/2 - 2;
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
}
