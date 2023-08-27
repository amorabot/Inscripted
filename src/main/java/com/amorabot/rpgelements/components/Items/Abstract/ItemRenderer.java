package com.amorabot.rpgelements.components.Items.Abstract;

import com.amorabot.rpgelements.components.Items.Interfaces.AffixTableSelector;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.List;

public abstract class ItemRenderer implements Serializable {

    public abstract void setDisplayName(String name, ItemStack item);
    public abstract void renderMainStat(Item itemData, List<String> itemLore);
    public abstract void renderMods(Item itemData, List<String> itemLore);
    public abstract <subType extends Enum<subType> & AffixTableSelector> void renderDescription(Item itemData, List<String> itemLore, subType itemSubtype);
    public abstract void renderTag(Item itemData, List<String> itemLore);
    public void placeDivs(List<String> itemLore){
        int longestLineLength = 0;
        for (String line : itemLore){
            String decoloredLine = Utils.decolor(line);
            if (decoloredLine.length() > longestLineLength){
                longestLineLength = decoloredLine.length();
            }
        }
        int divLength = (longestLineLength-1)/2;
        int offSet = (longestLineLength-divLength)/2;
        String div = "-".repeat(divLength);
        String divLine = div.indent(offSet);

        while(itemLore.contains("-div-")){
            int divIndex = itemLore.indexOf("-div-");
            itemLore.set(divIndex, Utils.color("&8"+divLine));
        }
    }
}
