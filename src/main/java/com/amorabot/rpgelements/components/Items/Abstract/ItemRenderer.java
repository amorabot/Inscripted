package com.amorabot.rpgelements.components.Items.Abstract;

import com.amorabot.rpgelements.components.Items.Interfaces.AffixTableSelector;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.List;

public abstract interface ItemRenderer extends Serializable {

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
}
