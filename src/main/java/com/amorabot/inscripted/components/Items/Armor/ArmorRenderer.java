package com.amorabot.inscripted.components.Items.Armor;

import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Abstract.ItemRenderer;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DefenceTypes;
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

        Map<DefenceTypes, Integer> updatedArmorDefences = armorData.getLocalDefences();

        int hp = updatedArmorDefences.get(DefenceTypes.HEALTH);

        int ward = updatedArmorDefences.getOrDefault(DefenceTypes.WARD, 0);

        int armor = updatedArmorDefences.getOrDefault(DefenceTypes.ARMOR, 0);
        int dodge = updatedArmorDefences.getOrDefault(DefenceTypes.DODGE, 0);

        int indentation = 1;
        String margin = " ".repeat(2);
        int HPlines = 1;
        int DEFlines = 0;

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
            HPlines++;
        }
        if (armor > 0){
            armorLine = DefenceTypes.ARMOR.getStatColor()+ DefenceTypes.ARMOR.getSpecialChar() + " +" + armor + " Armor" ;
            DEFlines++;
        }
        if (dodge > 0){
            dodgeLine = DefenceTypes.DODGE.getTextColor()+ DefenceTypes.DODGE.getSpecialChar() + " +" + dodge + " Dodge" ;
            DEFlines++;
        }

        String firstLine = healthLine + margin;
        String DEFSpacing = " ".repeat(healthLength - 5) + margin + DEFcomponent;
        switch (DEFlines){
            case 0:
                itemLore.add(ColorUtils.translateColorCodes(HPcomponent.indent(indentation)));
                if (HPlines == 1){
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
                if (HPlines == 1){

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
                Utils.log("Number of defense stats not handled: " + DEFlines);
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
//        Implicits armorImplicit = armorData.getImplicit();
//        Implicits armorImplicit = null;
        String implicitString = "---placeholder---";
        //TODO: FIX ARMOR RENDERING
        if (!true){
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
