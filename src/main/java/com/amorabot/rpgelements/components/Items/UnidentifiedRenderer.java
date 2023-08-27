package com.amorabot.rpgelements.components.Items;

import com.amorabot.rpgelements.components.Items.Abstract.Item;
import com.amorabot.rpgelements.components.Items.Abstract.ItemRenderer;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.rpgelements.components.Items.Interfaces.AffixTableSelector;
import com.amorabot.rpgelements.utils.ColorUtils;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static com.amorabot.rpgelements.utils.Utils.color;

public class UnidentifiedRenderer extends ItemRenderer {
    @Override
    public void setDisplayName(String name, ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(color(name));
        item.setItemMeta(itemMeta);
    }

    @Override
    public void renderMainStat(Item itemData, List<String> itemLore) {
    }

    @Override
    public void renderMods(Item itemData, List<String> itemLore) {
    }

    @Override
    public <subType extends Enum<subType> & AffixTableSelector> void renderDescription(Item itemData, List<String> itemLore, subType itemSubtype) {
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
    public void renderTag(Item itemData, List<String> itemLore) {
        ItemRarities rarity = itemData.getRarity();
        switch (rarity){
            case COMMON -> itemLore.add(color("&f&l"+rarity));
            case MAGIC -> itemLore.add(color("&9&l"+rarity));
            case RARE -> itemLore.add(color("&e&l"+rarity));
        }
    }
}
