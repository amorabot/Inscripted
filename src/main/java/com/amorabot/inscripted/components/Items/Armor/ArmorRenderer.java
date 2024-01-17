package com.amorabot.inscripted.components.Items.Armor;

import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Abstract.ItemRenderer;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Implicits;
import com.amorabot.inscripted.components.Items.Interfaces.ItemSubtype;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.Utils;

import java.util.List;
import java.util.Map;

import static com.amorabot.inscripted.utils.Utils.color;

public class ArmorRenderer implements ItemRenderer {
    @Override
    public void renderMainStat(Item itemData, List<String> itemLore) {
        Armor armorData = (Armor) itemData;
        Map<DefenceTypes, Integer> defencesMap = armorData.getDefencesMap();
        int hp = armorData.getBaseHealth();

        int ward = defencesMap.getOrDefault(DefenceTypes.WARD, 0);

        int armor = defencesMap.getOrDefault(DefenceTypes.ARMOR, 0);
        int dodge = defencesMap.getOrDefault(DefenceTypes.DODGE, 0);

        int indentation = 1;
        String margin = " ".repeat(2);
        int HP = 1;
        int DEF = 0;

        itemLore.add("");

        String HPcomponent = Utils.convertToPrettyString("&7HP: ");
        String DEFcomponent = Utils.convertToPrettyString("&7DEF: ");

        String healthLine = DefenceTypes.HEALTH.getTextColor()+ DefenceTypes.HEALTH.getSpecialChar() + " +" + hp + " Health" ;
        int healthLength = healthLine.length();

        String wardLine = "";
        String armorLine = "";
        String dodgeLine = "";

        if (ward > 0){
            wardLine = DefenceTypes.WARD.getTextColor()+ DefenceTypes.WARD.getSpecialChar() + " +" + ward + " Ward" ;
            HP++;
        }
        if (armor > 0){
            armorLine = DefenceTypes.ARMOR.getStatColor()+ DefenceTypes.ARMOR.getSpecialChar() + " +" + armor + " Armor" ;
            DEF++;
        }
        if (dodge > 0){
            dodgeLine = DefenceTypes.DODGE.getTextColor()+ DefenceTypes.DODGE.getSpecialChar() + " +" + dodge + " Dodge" ;
            DEF++;
        }

        String firstLine = healthLine + margin;
        String DEFSpacing = " ".repeat(healthLength - 5) + margin + DEFcomponent;
        switch (DEF){
            case 0:
                itemLore.add(ColorUtils.translateColorCodes(HPcomponent.indent(indentation)));
                if (HP == 1){
                    itemLore.add(ColorUtils.translateColorCodes(firstLine.indent(indentation)));
                } else {
                    itemLore.add(ColorUtils.translateColorCodes(firstLine.indent(indentation)));
                    itemLore.add(ColorUtils.translateColorCodes(wardLine.indent(indentation)));
                }
                break;
            case 1:
                if (armor > 0){
                    firstLine += "&8|" + margin + armorLine + " ";
                } else {
                    firstLine += "&8|" + margin + dodgeLine + " ";
                }
                if (HP == 1){

                    HPcomponent += DEFSpacing;

                    itemLore.add(ColorUtils.translateColorCodes(HPcomponent.indent(indentation)));
                    itemLore.add(ColorUtils.translateColorCodes(firstLine.indent(indentation)));

                } else {
                    int wardLength = wardLine.length();
                    int offset = healthLength-wardLength;
                    wardLine += " ".repeat(Math.abs(offset)) + margin + "&8|";

                    HPcomponent += DEFSpacing;

                    itemLore.add(ColorUtils.translateColorCodes(HPcomponent.indent(indentation)));
                    itemLore.add(ColorUtils.translateColorCodes(firstLine.indent(indentation)));
                    itemLore.add(ColorUtils.translateColorCodes(wardLine.indent(indentation)));
                }
                break;
            case 2:
                //It only makes sense, CURRENTLY, to have double defences armor with the hp stat being only health, lets assume that for now
                HPcomponent += DEFSpacing;
                itemLore.add(ColorUtils.translateColorCodes(HPcomponent.indent(indentation)));

                firstLine += "&8|" + margin + armorLine + " ";
                String secondLine = "";
                int secondLineOffset = healthLine.length()-5+2; //5 = hex, 2 = margin
                secondLine += " ".repeat(secondLineOffset) + margin + "&8|" + margin + dodgeLine + " ";

                itemLore.add(ColorUtils.translateColorCodes(firstLine.indent(indentation)));
                itemLore.add(ColorUtils.translateColorCodes(secondLine.indent(indentation)));

                break;
            default:
                Utils.log("Number of defense stats not handled: " + DEF);
                break;
        }

        itemLore.add("");
        itemLore.add(color("@HEADER@"));
    }

    @Override
    public <subType extends Enum<subType> & ItemSubtype> void renderDescription(Item itemData, List<String> itemLore, subType itemSubtype) {
        Armor armorData = (Armor) itemData;
        itemLore.add("");
        itemLore.add(color(Utils.convertToPrettyString(" &7Item Level: ") + "&f&l" + armorData.getIlvl()));
        Implicits armorImplicit = armorData.getImplicit();
        String implicitString = armorImplicit.getDisplayName();
        if (!armorImplicit.isHybrid()){
            String passiveString = Utils.convertToPrettyString(" &7Passive: ") + implicitString;
            itemLore.add(ColorUtils.translateColorCodes(passiveString));
            itemLore.add("");
            return;
        }
        String[] implicitSegments = implicitString.split("-brk-");
        String passiveString1 = Utils.convertToPrettyString(" &7Passive: ") + implicitSegments[0];
        itemLore.add(ColorUtils.translateColorCodes(passiveString1));
        String passiveString2 = implicitSegments[1];
        itemLore.add(ColorUtils.translateColorCodes(passiveString2.indent(12)));
        itemLore.add("");
    }
}
