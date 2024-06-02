package com.amorabot.inscripted.components.Items;

import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Abstract.ItemRenderer;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.inscripted.components.Items.Interfaces.ItemSubtype;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static com.amorabot.inscripted.utils.Utils.color;

public class UnidentifiedRenderer implements ItemRenderer {
    @Override
    public void renderMainStat(Item itemData, List<String> itemLore) {
    }

    @Override
    public void renderMods(String valuesColor, Item itemData, List<String> itemLore) {
    }

    @Override
    public <subType extends Enum<subType> & ItemSubtype> void renderDescription(Item itemData, List<String> itemLore, subType itemSubtype) {
        itemLore.add("");
        itemLore.add(ColorUtils.translateColorCodes("   &4&l- Hidden --   "));
        itemLore.add(Utils.color("   &8Use a Scroll"));
        itemLore.add(Utils.color("   &8of Knowledge"));
        itemLore.add(Utils.color("   &8to reveal"));
        itemLore.add(Utils.color("   &8it's potential"));
        itemLore.add(ColorUtils.translateColorCodes("   &4&l----------"));
        itemLore.add("");
        itemLore.add(Utils.color("&7 Item Level: " + "&f&l" + itemData.getIlvl()));
        itemLore.add("");
    }

    @Override
    public <subType extends Enum<subType> & ItemSubtype>  void renderTag(Item itemData, List<String> itemLore, subType itemSubtype) {
        ItemRarities rarity = itemData.getRarity(); //TODO: rework the whole renderer
        switch (rarity){
            case COMMON -> itemLore.add(color("&f&l"+rarity + " " + itemSubtype));
            case MAGIC -> itemLore.add(color("&9&l"+rarity + " " + itemSubtype));
            case RARE -> itemLore.add(color("&e&l"+rarity + " " + itemSubtype));
        }
    }
}
