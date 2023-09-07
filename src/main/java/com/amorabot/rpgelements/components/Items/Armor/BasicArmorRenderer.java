package com.amorabot.rpgelements.components.Items.Armor;

import com.amorabot.rpgelements.components.Items.Abstract.Item;
import com.amorabot.rpgelements.components.Items.Abstract.ItemRenderer;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.rpgelements.components.Items.Interfaces.AffixTableSelector;
import com.amorabot.rpgelements.utils.ColorUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

import static com.amorabot.rpgelements.utils.Utils.color;

public class BasicArmorRenderer implements ItemRenderer {
    @Override
    public void setDisplayName(String name, ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(color(name));
        item.setItemMeta(itemMeta);
    }

    @Override
    public void renderMainStat(Item itemData, List<String> itemLore) {
        Armor armorData = (Armor) itemData;
        Map<DefenceTypes, Integer> defencesMap = armorData.getDefencesMap();
        int hp = defencesMap.get(DefenceTypes.HEALTH);
        int ward = 0;
        if (defencesMap.containsKey(DefenceTypes.WARD)){
            ward = defencesMap.get(DefenceTypes.WARD);
        }

        int armor = 0;
        if (defencesMap.containsKey(DefenceTypes.ARMOR)){
            armor = defencesMap.get(DefenceTypes.ARMOR);
        }
        int dodge = 0;
        if (defencesMap.containsKey(DefenceTypes.DODGE)){
            dodge = defencesMap.get(DefenceTypes.DODGE);
        }

//        String healthLine = " ";
        if (hp > 0){
            String valueHex = DefenceTypes.HEALTH.getStatColor();
            String textHex = DefenceTypes.HEALTH.getTextColor();
            String hpString = valueHex + hp + "❤ " + textHex + "Health";
//            healthLine = healthLine + hpString;
            itemLore.add(ColorUtils.translateColorCodes(hpString.indent(1)));
        }
        if (ward > 0){
            String valueHex = DefenceTypes.WARD.getStatColor();
            String textHex = DefenceTypes.WARD.getTextColor();
            String wardString = valueHex + ward + "✤ " + textHex + "Ward";
            itemLore.add(ColorUtils.translateColorCodes(wardString.indent(1)));
//            if (!healthLine.equals(" ")) {
//                healthLine = healthLine + "*s*" + wardString;
//            } else {
//                healthLine = healthLine + wardString;
//            }
        }
        //If none of the values were added, the string remains blank and should not be added
//        if (!healthLine.equals(" ")){
//            if (healthLine.contains("*s*")){
//                healthLine = healthLine.replace("*s*", "   ");
//            }
//            itemLore.add(ColorUtils.translateColorCodes(healthLine));
//        }
        itemLore.add("");

        String defenceLine = " ";
        if (armor > 0){
            String valueHex = DefenceTypes.ARMOR.getStatColor();
            String textHex = DefenceTypes.ARMOR.getTextColor();
            String armorString = valueHex + armor + "\uD83D\uDEE1 " + textHex + "Armor";
            defenceLine = defenceLine + armorString;
        }
        if (dodge > 0){
            String valueHex = DefenceTypes.DODGE.getStatColor();
            String textHex = DefenceTypes.DODGE.getTextColor();
            String dodgeString = valueHex + dodge + "\uD83C\uDF00 " + textHex + "Dodge";
            if (!defenceLine.equals(" ")) {
                defenceLine = defenceLine + "*s*" + dodgeString;
            } else {
                defenceLine = defenceLine + dodgeString;
            }
        }
        //If none of the values were added, the string remains blank and should not be added
        if (!defenceLine.equals(" ")){
            if (defenceLine.contains("*s*")){
                defenceLine = defenceLine.replace("*s*", "   ");
            }
            itemLore.add(ColorUtils.translateColorCodes(defenceLine));
        }


        itemLore.add(color("-div-"));
//        if (itemData.getRarity() != ItemRarities.COMMON){
//            itemLore.add(color("-div-"));
//        }
    }

    @Override
    public void renderMods(Item itemData, List<String> itemLore) {

    }

    @Override
    public <subType extends Enum<subType> & AffixTableSelector> void renderDescription(Item itemData, List<String> itemLore, subType itemSubtype) {

    }

    @Override
    public void renderTag(Item itemData, List<String> itemLore) {
//        Weapon weaponData = (Weapon) itemData;
        ItemRarities rarity = itemData.getRarity();
        switch (rarity){
            case COMMON -> itemLore.add(color("&f&l"+rarity));
            case MAGIC -> itemLore.add(color("&9&l"+rarity));
            case RARE -> itemLore.add(color("&e&l"+rarity));
        }
    }
}
