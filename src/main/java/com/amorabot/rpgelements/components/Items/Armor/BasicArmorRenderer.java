package com.amorabot.rpgelements.components.Items.Armor;

import com.amorabot.rpgelements.components.Items.Abstract.Item;
import com.amorabot.rpgelements.components.Items.Abstract.ItemRenderer;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.*;
import com.amorabot.rpgelements.components.Items.DataStructures.Modifier;
import com.amorabot.rpgelements.components.Items.Interfaces.AffixTableSelector;
import com.amorabot.rpgelements.utils.ColorUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
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
        Armor armorData = (Armor) itemData;
        String valuesColor = "&#95dbdb";

        List<Modifier<ArmorModifiers>> mods = armorData.getModifiers();
        List<Modifier<ArmorModifiers>> prefixes = new ArrayList<>();
        List<Modifier<ArmorModifiers>> suffixes = new ArrayList<>(); //Alternativa: Implementar equals()[atributo a atributo] ou um comparador
        for (Modifier<ArmorModifiers> mod : mods){
            if (mod.getModifier().getAffixType() == Affix.PREFIX){
                prefixes.add(mod);
            } else {
                suffixes.add(mod);
            }
        }

        for (Modifier<ArmorModifiers> mod : prefixes){
            String modifierDisplayName = "&7" + mod.getModifier().getDisplayName();
            RangeTypes rangeType = mod.getModifier().getRangeType();
            switch (rangeType){
                case SINGLE_VALUE -> {}
                case SINGLE_RANGE -> modifierDisplayName = (modifierDisplayName
                        .replace("@value1@", valuesColor+mod.getValue()[0]+"&7")).indent(1);
                case DOUBLE_RANGE -> {
                    int[] values = mod.getValue();
                    modifierDisplayName = (modifierDisplayName
                            .replace("@value1@", valuesColor+values[0]+"&7")
                            .replace("@value2@", valuesColor+values[1]+"&7")).indent(1);
                }
            }
            itemLore.add(ColorUtils.translateColorCodes(modifierDisplayName));
        }
        for (Modifier<ArmorModifiers> mod : suffixes){
            String modifierDisplayName = "&7" + mod.getModifier().getDisplayName();
            RangeTypes rangeType = mod.getModifier().getRangeType();
            switch (rangeType){
                case SINGLE_VALUE -> {}
                case SINGLE_RANGE -> modifierDisplayName = (modifierDisplayName
                        .replace("@value1@", valuesColor+mod.getValue()[0]+"&7")).indent(1);
                case DOUBLE_RANGE -> {
                    int[] values = mod.getValue();
                    modifierDisplayName = (modifierDisplayName
                            .replace("@value1@", valuesColor+values[0]+"&7")
                            .replace("@value2@", valuesColor+values[1]+"&7")).indent(1);
                }
            }
            itemLore.add(ColorUtils.translateColorCodes(modifierDisplayName));
        }
        if (armorData.getRarity() != ItemRarities.COMMON){
            itemLore.add(color("-div-"));
        }
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
