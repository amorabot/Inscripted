package com.amorabot.inscripted.components.Items.Armor;

import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Abstract.ItemRenderer;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Implicits;
import com.amorabot.inscripted.components.Items.Interfaces.AffixTableSelector;
import com.amorabot.inscripted.utils.ColorUtils;

import java.util.List;
import java.util.Map;

import static com.amorabot.inscripted.utils.Utils.color;

public class BasicArmorRenderer implements ItemRenderer {
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

        int indentation = 1;
        String healthLine = DefenceTypes.HEALTH.getTextColor()+ DefenceTypes.HEALTH.getSpecialChar() + " +" + hp + " Health" ;
        itemLore.add(ColorUtils.translateColorCodes(healthLine.indent(indentation)));
        if (ward > 0){
            String wardLine = DefenceTypes.WARD.getTextColor()+ DefenceTypes.WARD.getSpecialChar() + " +" + ward + " Ward" ;
            itemLore.add(ColorUtils.translateColorCodes(wardLine.indent(indentation)));
        }
        itemLore.add("");

        if (armor > 0){
            String armorLine = DefenceTypes.ARMOR.getStatColor()+ DefenceTypes.ARMOR.getSpecialChar() + " +" + armor + " Armor" ;
            itemLore.add(ColorUtils.translateColorCodes(armorLine.indent(indentation)));
        }
        if (dodge > 0){
            String dodgeLine = DefenceTypes.DODGE.getTextColor()+ DefenceTypes.DODGE.getSpecialChar() + " +" + dodge + " Dodge" ; //Flat stat display
            itemLore.add(ColorUtils.translateColorCodes(dodgeLine.indent(indentation)));
        }
        if ((armor != 0 || dodge != 0)){
            itemLore.add("");
        }

        itemLore.add(color("@HEADER@"));
    }

    @Override
    public <subType extends Enum<subType> & AffixTableSelector> void renderDescription(Item itemData, List<String> itemLore, subType itemSubtype) {
        Armor armorData = (Armor) itemData;
        itemLore.add("");
        itemLore.add(color("&7 Item Level: " + "&f&l" + armorData.getIlvl()));
        Implicits armorImplicit = armorData.getImplicit();
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
}
