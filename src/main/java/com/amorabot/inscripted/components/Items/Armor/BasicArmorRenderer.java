package com.amorabot.inscripted.components.Items.Armor;

import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Abstract.ItemRenderer;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.*;
//import com.amorabot.rpgelements.components.Items.DataStructures.Modifier;
import com.amorabot.inscripted.components.Items.DataStructures.NewModifier;
import com.amorabot.inscripted.components.Items.Interfaces.AffixTableSelector;
import com.amorabot.inscripted.utils.ColorUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.amorabot.inscripted.utils.Utils.color;

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
//            Utils.log("adding dodge line...");
//            String dodgeLine = DefenceTypes.DODGE.getTextColor()+ DefenceTypes.DODGE.getSpecialChar() + " +" + Utils.getPercentString(dodge) + "%" + " Dodge" ;
            String dodgeLine = DefenceTypes.DODGE.getTextColor()+ DefenceTypes.DODGE.getSpecialChar() + " +" + dodge + " Dodge" ; //Flat stat display
            itemLore.add(ColorUtils.translateColorCodes(dodgeLine.indent(indentation)));
        }
        if ((armor != 0 || dodge != 0)){
            itemLore.add("");
        }

        itemLore.add(color("@HEADER@"));
    }

    @Override
    public void renderMods(Item itemData, List<String> itemLore) {
        Armor armorData = (Armor) itemData;
        String valuesColor = "&#95dbdb";

//        List<Modifier<ArmorModifiers>> mods = armorData.getModifiers();
//        List<Modifier<ArmorModifiers>> prefixes = new ArrayList<>();
//        List<Modifier<ArmorModifiers>> suffixes = new ArrayList<>(); //TODO: ordenacao por ordinal value + affixType no Enum (prefix 1 > prefix 7, suffix 18 > suffix 28)
        List<NewModifier> mods = armorData.getModifiers();
        List<NewModifier> prefixes = new ArrayList<>();
        List<NewModifier> suffixes = new ArrayList<>();
        //Sorting prefixes and suffixes
        for (NewModifier mod : mods){
            if (mod.getModifier().getAffixType().equals(Affix.PREFIX)){
                prefixes.add(mod);
            } else {
                suffixes.add(mod);
            }
        }
//        for (Modifier<ArmorModifiers> mod : mods){
//            if (mod.getModifier().getAffixType() == Affix.PREFIX){
//                prefixes.add(mod);
//            } else {
//                suffixes.add(mod);
//            }
//        }
        for (NewModifier mod : prefixes){
            String modifierDisplayName = getModifierDisplayName(mod, valuesColor, 2);
            itemLore.add(ColorUtils.translateColorCodes(modifierDisplayName));
        }
        for (NewModifier mod : suffixes){
            String modifierDisplayName = getModifierDisplayName(mod, valuesColor, 2);
            itemLore.add(ColorUtils.translateColorCodes(modifierDisplayName));
        }
//        for (Modifier<ArmorModifiers> mod : prefixes){
//            String modifierDisplayName = getModifierDisplayName(mod, valuesColor, 2);
//            itemLore.add(ColorUtils.translateColorCodes(modifierDisplayName));
//        }
//        for (Modifier<ArmorModifiers> mod : suffixes){
//            String modifierDisplayName = getModifierDisplayName(mod, valuesColor, 2);
//            itemLore.add(ColorUtils.translateColorCodes(modifierDisplayName));
//        }
        if (armorData.getRarity() != ItemRarities.COMMON){
            itemLore.add(color("@FOOTER@"));
        }
    }

//    private String getModifierDisplayName(Modifier<ArmorModifiers> mod, String valuesColor, int indent) {
//        String modifierDisplayName = "&7" + mod.getModifier().getDisplayName();
//        RangeTypes rangeType = mod.getModifier().getRangeType();
//        switch (rangeType){
//            case SINGLE_VALUE -> {} //Utils.getPercentString(dodge)
//            case SINGLE_RANGE -> modifierDisplayName = (modifierDisplayName
//                    .replace("@value1@", valuesColor + mod.getValue()[0]+"&7")).indent(indent);
//            case DOUBLE_RANGE -> {
//                int[] values = mod.getValue();
//                modifierDisplayName = (modifierDisplayName
//                        .replace("@value1@", valuesColor +values[0]+"&7")
//                        .replace("@value2@", valuesColor +values[1]+"&7")).indent(indent);
//            }
//        }
//        return modifierDisplayName;
//    }
private String getModifierDisplayName(NewModifier mod, String valuesColor, int indent) {
    String modifierDisplayName = "&7" + mod.getModifier().getDisplayName();
    RangeTypes rangeType = mod.getModifier().getRangeType();
    switch (rangeType){
        case SINGLE_VALUE -> {} //Utils.getPercentString(dodge)
        case SINGLE_RANGE -> modifierDisplayName = (modifierDisplayName
                .replace("@value1@", valuesColor + mod.getValue()[0]+"&7")).indent(indent);
        case DOUBLE_RANGE -> {
            int[] values = mod.getValue();
            modifierDisplayName = (modifierDisplayName
                    .replace("@value1@", valuesColor +values[0]+"&7")
                    .replace("@value2@", valuesColor +values[1]+"&7")).indent(indent);
        }
    }
    return modifierDisplayName;
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
        Armor armorData = (Armor) itemData;
        ItemRarities rarity = itemData.getRarity();
        String tag = "";
        switch (rarity){
            case COMMON -> {
                tag += ("&f&l"+rarity);
                itemLore.add(color(tag));
                return;
            }
            case MAGIC -> tag += ("&9&l"+rarity);
            case RARE -> tag += ("&e&l"+rarity);
        }
        int starRating = armorData.getStarRating();
        if (starRating >= 0 && starRating<=50){
            tag += "&8 " + "★";
        } else if (starRating<=70){
            tag += "&7 " + "★";
        } else if (starRating<=90) {
            tag += "&f " + "★";
        } else {
            tag += "&6 " + "★";
        }
        itemLore.add(color(tag));
    }
}
