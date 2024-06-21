package com.amorabot.inscripted.components.Items.Abstract;

import com.amorabot.inscripted.components.Items.Armor.ArmorTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.inscripted.components.Items.Interfaces.ItemSubtype;
import com.amorabot.inscripted.components.Items.modifiers.Inscription;
import com.amorabot.inscripted.components.Items.modifiers.data.KeystoneData;
import com.amorabot.inscripted.components.Items.modifiers.data.ModifierData;
import com.amorabot.inscripted.components.Items.modifiers.data.UniqueEffectData;
import com.amorabot.inscripted.components.Items.modifiers.unique.Effects;
import com.amorabot.inscripted.components.Items.modifiers.unique.Keystones;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import static com.amorabot.inscripted.utils.Utils.color;

public interface ItemRenderer extends Serializable {

    default void setDisplayName(ItemRarities rarity, String name, ItemStack item, boolean isCorrupted, int quality) {
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        String qualitySuffix = "";
        if (quality > 0){
            qualitySuffix = " &f[&b+" + quality + "&f]";
        }

        if (isCorrupted){
            String originalHex;
            String corruptedHex = NamedTextColor.DARK_RED.asHexString();
            switch (rarity){
                case COMMON -> originalHex = NamedTextColor.WHITE.asHexString();
                case MAGIC -> originalHex = NamedTextColor.BLUE.asHexString();
                case RARE -> originalHex = NamedTextColor.YELLOW.asHexString();
                default -> originalHex = "#FFFFFF";
            }
            String openTag = "<gradient:@Hex1@:@Hex2@>";
            String closeTag = "</gradient>";
            String finalOpenTag = openTag.replace("@Hex1@",corruptedHex).replace("@Hex2@", originalHex);
            MiniMessage serializer = MiniMessage.builder().build();
            Component component = serializer.deserialize(finalOpenTag+name+closeTag);
            String legacyDispName = LegacyComponentSerializer.legacyAmpersand().serialize(component);
            itemMeta.setDisplayName(ColorUtils.translateColorCodes(legacyDispName+qualitySuffix));

        } else {
            itemMeta.setDisplayName(color(rarity.getColor() + name + qualitySuffix));
        }
        item.setItemMeta(itemMeta);
    }
    void renderMainStat(Item itemData, List<String> itemLore);

    default Comparator<Inscription> getInscriptionComparator(){
        return (o1, o2) -> {
            if (o2.isImbued()){
                return 1;
            }
            if (o1.equals(o2)){
                return 0;
            }
            if (o1.getInscription().ordinal() < o2.getInscription().ordinal()){
                return -1;
            }
            return 1;
        };
    }

    default void renderKeystonesAndEffects(Item itemData, List<String> itemLore){
        if (!itemData.getRarity().equals(ItemRarities.RELIC)){return;}
        List<Inscription> inscriptions = itemData.getInscriptionList();

        inscriptions.sort(getInscriptionComparator());

        //Keystones first
        boolean hasKeystone = false;
        for (Inscription insc : inscriptions){
            ModifierData data = insc.getInscription().getData();
            if (!data.isKeystone()){continue;}
            hasKeystone = true;
            Keystones keystone = ((KeystoneData) data).keystone();
            int padding = 2;
            //TODO: encapsulate keystone and effect rendering (name + description)
            String rawName = "&c&l"+insc.getInscription().getDisplayName();
            String keystoneName = ColorUtils.translateColorCodes(rawName).indent(padding);
            itemLore.add(keystoneName);
            String[] desc = keystone.getDescription();
            String headerLine = ColorUtils.translateColorCodes(("&8&l>| &8"+Utils.convertToPrettyString(desc[0])).indent(padding+2));
            itemLore.add(headerLine);
            for(int k = 1; k < desc.length; k++){
                String keystoneDescriptionLine = ColorUtils.translateColorCodes(("&8"+Utils.convertToPrettyString(desc[k])).indent(padding+6));
                itemLore.add(keystoneDescriptionLine);
            }
        }
        //Then effects
        boolean hasUniqueEffect = false;
        if (hasKeystone){
            itemLore.add("");
        }
        for (Inscription insc : inscriptions){
            ModifierData data = insc.getInscription().getData();
            if (!data.isUniqueEffect()){continue;}
            hasUniqueEffect = true;
            Effects effect = ((UniqueEffectData) data).uniqueEffect();
            int padding = 2;
            String rawName = ("&c&l"+(insc.getInscription().getDisplayName()));
            String effectDisplayName = ColorUtils.translateColorCodes((rawName+" ".repeat(padding)).indent(padding));
            itemLore.add(effectDisplayName);
            String[] desc = effect.getDescription();
            String headerLine = ColorUtils.translateColorCodes(("&8&l>| &8"+Utils.convertToPrettyString(desc[0])).indent(padding+2));
            itemLore.add(headerLine);
            for(int e = 1; e < desc.length; e++){
                String effectDescriptionLine = ColorUtils.translateColorCodes(("&8"+Utils.convertToPrettyString(desc[e])).indent(padding+6));
                itemLore.add(effectDescriptionLine);
            }
        }
        //-----------
        if (hasUniqueEffect){
            itemLore.add("");
        }
    }

    default void renderMods(String valuesColor, Item itemData, List<String> itemLore){


        List<Inscription> mods = itemData.getInscriptionList();
        mods.sort(getInscriptionComparator());

        for (Inscription mod : mods){
            String modifierDisplayName;
            if (mod.getInscription().getData().isUnique()){
//                modifierDisplayName = mod.getModifierDisplayName("&4", 2);
                modifierDisplayName = mod.getModifierDisplayName("&#ff7081", 2); //Hex color for unique mods
            } else {
                modifierDisplayName = mod.getModifierDisplayName(valuesColor, 2);
            }
            if (modifierDisplayName.isEmpty()){continue;}
            itemLore.add(ColorUtils.translateColorCodes(modifierDisplayName));
        }
        if (itemData.getRarity() != ItemRarities.COMMON){
            itemLore.add(color("@FOOTER@"));
        }
    }
    <subType extends Enum<subType> & ItemSubtype> void renderDescription(Item itemData, List<String> itemLore, subType itemSubtype);
    default <subType extends Enum<subType> & ItemSubtype> void renderTag(Item itemData, List<String> itemLore,subType itemSubtype) {
        ItemRarities rarity = itemData.getRarity();
        StringBuilder tag = new StringBuilder(rarity.getColor()).append("&l").append(rarity);
        if (itemSubtype instanceof ArmorTypes armorType){
            tag.append(" ").append(itemData.getCategory());
        } else {
            tag.append(" ").append(itemSubtype);
        }
        if (rarity.equals(ItemRarities.COMMON)){
            if (itemData.isCorrupted()){tag.append(" &4☠");}
            itemLore.add(color(tag.toString()));
            return;
        }
        if (itemData.isCorrupted()){ tag.append(" &4☠"); }
        tag.append(mapStarRating(itemData.getStarRating()));
        itemLore.add(color(tag.toString()));
    }
    default <subType extends Enum<subType> & ItemSubtype> void renderAllCustomLore(Item itemData, List<String> itemLore, subType itemSubtype){
        renderMainStat(itemData, itemLore);
        renderKeystonesAndEffects(itemData, itemLore);
        renderMods("&#95dbdb", itemData, itemLore);
        renderDescription(itemData, itemLore, itemSubtype);
        renderTag(itemData, itemLore, itemSubtype);

        int mods = itemData.getInscriptionList().size();
        placeRunicDiv(itemData.getName().length(),itemLore,mods);
    }

    default void placeRunicDiv(int itemNameLength, List<String> itemLore, int numberOfMods){
        int longestLineLength = itemNameLength;
        for (String line : itemLore){
            String decoloredLine = ColorUtils.decolor(line);
            String strippedLine = decoloredLine.strip();
            int lineLength = strippedLine.length();
            longestLineLength = Math.max(lineLength, longestLineLength);
        }
//      "¦¡!ï÷ ¨ ╜ ╙"; ᚫ
        StringBuilder headerBar = new StringBuilder("- --=÷¦• ");
        String header = headerBar +  String.valueOf(numberOfMods)  + headerBar.reverse();

        StringBuilder footerBar = new StringBuilder("--=÷• ");
        String footer = footerBar + "ᚫ" + footerBar.reverse();


        int offset = 2;
        if (longestLineLength % 2 == 0){
            offset += (longestLineLength/2) + 1;
        } else {
            offset += (longestLineLength/2);
        }
        offset -= headerBar.length();
        offset = offset/2 - 4;
        String margin = " ".repeat(2);


        String runicHeader = Utils.color("&8"+header+margin).indent((offset));
        String runicFooter = Utils.color("&8"+footer+margin).indent((offset)+4);

        String headerString = "@HEADER@";
        String footerString = "@FOOTER@";

        for (String loreLine : itemLore){
            if (loreLine.contains(headerString)){
                itemLore.set(itemLore.indexOf(headerString), loreLine.replace(headerString, runicHeader));
                continue;
            }
            if (loreLine.contains(footerString)){
                itemLore.set(itemLore.indexOf(footerString), loreLine.replace(footerString, runicFooter));
            }
        }
    }

    //TODO: turn into config
    default String mapStarRating(double starRating){
        if (starRating >= 0 && starRating<=0.5D){
            return "&8 " + "★";
        } else if (starRating<=0.7D){
            return "&7 " + "★";
        } else if (starRating<=0.9D) {
            return "&f " + "★";
        } else {
            return "&6 " + "★";
        }
    }
}
