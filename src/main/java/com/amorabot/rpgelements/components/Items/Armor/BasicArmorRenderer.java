package com.amorabot.rpgelements.components.Items.Armor;

import com.amorabot.rpgelements.components.Items.Abstract.Item;
import com.amorabot.rpgelements.components.Items.Abstract.ItemRenderer;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.Implicit;
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
        int hp = armorData.getBaseHealth();
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
        String healthLine = " &7Health: "+DefenceTypes.HEALTH.getTextColor()+ hp + DefenceTypes.HEALTH.getSpecialChar();
        itemLore.add(ColorUtils.translateColorCodes(healthLine));
        if (ward > 0){
            String wardLine = " &7Ward:   " + DefenceTypes.WARD.getTextColor()+ ward + DefenceTypes.WARD.getSpecialChar();
            itemLore.add(ColorUtils.translateColorCodes(wardLine));
        }
        itemLore.add("");

        if (armor > 0){
            String armorLine = " &7Armor:  " + DefenceTypes.ARMOR.getTextColor()+ armor + DefenceTypes.ARMOR.getSpecialChar();
            itemLore.add(ColorUtils.translateColorCodes(armorLine));
        }
        if (dodge > 0){
            String dodgeLine = " &7Dodge:  " + DefenceTypes.DODGE.getTextColor()+ dodge + DefenceTypes.DODGE.getSpecialChar();
            itemLore.add(ColorUtils.translateColorCodes(dodgeLine));
        }
        if ((armor != 0 || dodge != 0)){
            itemLore.add("");
        }

        itemLore.add(color("-div-"));
    }

    @Override
    public void renderMods(Item itemData, List<String> itemLore) {

    }

    @Override
    public <subType extends Enum<subType> & AffixTableSelector> void renderDescription(Item itemData, List<String> itemLore, subType itemSubtype) {
        Armor armorData = (Armor) itemData;
        itemLore.add("");
        itemLore.add(color("&7 Item Level: " + "&f&l" + armorData.getIlvl()));
        Implicit armorImplicit = armorData.getImplicit();
        String implicitString = armorImplicit.getDisplayName();
        if (!armorImplicit.isHybrid()){
            String passiveString = " &7Passive: " + implicitString;
            itemLore.add(ColorUtils.translateColorCodes(passiveString));
            itemLore.add("");
            return;
        }
        String[] implicitSegments = implicitString.split("-brk-");
        String passiveString1 = " &7Passive: " + implicitSegments[0];
        itemLore.add(ColorUtils.translateColorCodes(passiveString1));
        String passiveString2 = implicitSegments[1];
        itemLore.add(ColorUtils.translateColorCodes(passiveString2.indent(12)));
        itemLore.add("");
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
