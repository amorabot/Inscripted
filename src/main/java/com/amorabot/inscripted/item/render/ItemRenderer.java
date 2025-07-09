package com.amorabot.inscripted.item.render;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.item.relic.Relics;
import com.amorabot.inscripted.item.structure.io.ItemDeserializer;
import com.amorabot.inscripted.player.Archetypes;
import com.amorabot.inscripted.item.inscription.ProceduralInscription;
import com.amorabot.inscripted.item.structure.Item;
import com.amorabot.inscripted.item.structure.ItemRarities;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemRenderer {
    public static final String inscriptionsHeader = Utils.convertToPrettyString("Inscriptions: ") + "<color>•÷¦[ <n> ]¦÷=--  ";
    public static final String inscriptionsFooter = "-   --  ---   ----=÷• ᚫ •÷=---";

    public static void imprintLore(ItemStack item, Item itemData, List<Component> lore, boolean identified){
        item.lore(lore);

        item.editMeta((itemMeta)-> {
            assert itemMeta != null;
            itemMeta.setUnbreakable(true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            // Band-aid fix, fixed in paper 1.21.4+
            itemMeta.addAttributeModifier(Attribute.GENERIC_LUCK, new AttributeModifier(
                    new NamespacedKey(Inscripted.getPlugin(), "dummy"),
                    0,
                    AttributeModifier.Operation.ADD_NUMBER
            ));
        });

        if (identified){
            setDisplayName(itemData.getName(),item,itemData.getRarity(),itemData.isCorrupted(),itemData.getQuality());
            return;
        }
        setDisplayName("Unidentified " + itemData.getGenericSubtype().getSubtypeDisplayName(itemData),item,itemData.getRarity(),false,0);
    }

    public static void appendRelicFlavorText(ItemStack item, Relics relic){
        ItemMeta itemMeta = item.getItemMeta();
        List<Component> currentLore = itemMeta.lore();
        assert currentLore != null;
        String rawFlavorText = ItemDeserializer.getRelicData(item,relic);
        List<String> flavorText = Arrays.stream(rawFlavorText.split("<br>")).toList();
        for (String flavorTextLine : flavorText){
            currentLore.add(Component.text("  "+flavorTextLine).color(InscriptedPalette.DARK_GRAY.getColor()));
        }
        currentLore.add(Component.text(""));
        itemMeta.lore(currentLore);
        item.setItemMeta(itemMeta);
    }

    public static List<Component> render(Item itemData){
        final int mainStatPadding = 1;
        final int inscriptionsPadding = 4;

        Component emptyLine = Component.text("");
        Component descriptionLine = renderDescription(itemData);

        List<Component> lore = new ArrayList<>();
        lore.add(emptyLine);

        lore.addAll(itemData.renderMainStat(new MainStatRenderer()));

        lore.add(getInscriptionHeader(itemData.getInscriptions().size(),mainStatPadding));
        lore.addAll(InscriptionRenderer.renderInscriptionList(itemData.getInscriptions(),inscriptionsPadding));
        lore.add(getInscriptionFooter(mainStatPadding));

        lore.add(emptyLine);
        lore.add(getImplicitLine(itemData.getImplicit(),itemData.getArchetype(),mainStatPadding));
        lore.add(emptyLine);
        lore.addAll(renderRequirements(itemData,mainStatPadding));
        lore.add(emptyLine);

        lore.add(descriptionLine);

        return lore;
    }

    public static Component getImplicitLine(ProceduralInscription implicit, Archetypes itemArchetype, int padding){
        Component passiveIndicator = Component.text(Utils.convertToPrettyString("Passive:"));
        Component paddingComponent = Component.text(" ".repeat(padding));
        Component implicitComponent = InscriptionRenderer.getImplicitComponent(implicit, itemArchetype);
        return InscriptedPalette.colorizeComponent(
                        paddingComponent.append(passiveIndicator).appendSpace().append(implicitComponent)
                        ,InscriptedPalette.TINTED_BEIGE.getColor())
                .decoration(TextDecoration.ITALIC,false);
    }
    private static List<Component> renderRequirements(Item itemData, int padding){
        List<Component> renderedRequirements = new ArrayList<>();

        Component ilvlComponent = InscriptedPalette.colorizeComponent(
                Component.text(itemData.getIlvl()+"↑").decorate(TextDecoration.BOLD)
                , InscriptedPalette.WHITE.getColor());
        Component requitementIndicator = Component.text(Utils.convertToPrettyString("Requirements:"));
        Component paddingComponent = Component.text(" ".repeat(padding));
        renderedRequirements.add
                (paddingComponent.append(requitementIndicator)
                        .appendSpace()
                        .append(ilvlComponent)
                        .append(paddingComponent)
                        .color(InscriptedPalette.TINTED_BEIGE.getColor()));
        //Room for adding any new eventual requirements
        return renderedRequirements;
    }
    private static Component renderDescription(Item itemData){
        ItemRarities rarity = itemData.getRarity();
        Component tagsComponent = renderDescriptionTags(itemData.getStarRating(), itemData.isCorrupted());
        Component descriptionComponent = Component.text(rarity.toString()+ " " + itemData.getGenericSubtype().getSubtypeDisplayName(itemData))
                .decorate(TextDecoration.BOLD).color(rarity.getColorComponent().getColor());

        return descriptionComponent.appendSpace().append(tagsComponent).decoration(TextDecoration.ITALIC,false);
    }
    private static Component renderDescriptionTags(double starRating, boolean corrupted){
        Component starRatingIcon = getStarIcon(starRating);
        if(corrupted){
            Component corruptionIcon = Component.text("☠").color(InscriptedPalette.CORRUPTED.getColor());
            return starRatingIcon.appendSpace().append(corruptionIcon);
        }
        return starRatingIcon;
    }

    public static Component getStarIcon(double starRating){
        Component star = Component.text("★");
        if (starRating >= 0 && starRating<=0.5D){
            return star.color(InscriptedPalette.DARK_GRAY.getColor());
        } else if (starRating<=0.7D){
            return star.color(InscriptedPalette.NEUTRAL_GRAY.getColor());
        } else if (starRating<=0.9D) {
            return star.color(InscriptedPalette.WHITE.getColor());
        } else {
            return star.color(NamedTextColor.GOLD);
        }
    }


    public static void setDisplayName(String name, ItemStack item, ItemRarities rarity, boolean isCorrupted, int quality){
        Component nameComponent;
        Component qualityComponent;
        if (quality>0){
            qualityComponent = MiniMessage.miniMessage().deserialize(" <white>[<qual><white>]", Placeholder.parsed("qual", "<red>+"+quality));
        } else {qualityComponent = Component.text("");}
        if (isCorrupted){
            String originalHex = rarity.getColorComponent().getColorString();
            String corruptedHex = InscriptedPalette.CORRUPTED.getColor().asHexString();

            String openTag = "<gradient:@Hex1@:@Hex2@>";
            String closeTag = "</gradient>";
            String finalOpenTag = openTag.replace("@Hex1@",corruptedHex).replace("@Hex2@", originalHex);

            MiniMessage mmBuilder = MiniMessage.builder().build();
            nameComponent = mmBuilder.deserialize(finalOpenTag+name+closeTag);

            Component gradientName = nameComponent.append(qualityComponent).decoration(TextDecoration.ITALIC, false);
            item.editMeta((itemMeta)-> itemMeta.displayName(gradientName));
        } else {
            Component regularDisplayName = Component.text(name).color(rarity.getColorComponent().getColor()).append(qualityComponent).decoration(TextDecoration.ITALIC, false);
            item.editMeta((itemMeta)->itemMeta.displayName(regularDisplayName));
        }
    }
    public static Component getInscriptionHeader(int numOfInscriptions, int padding){
        Component paddingComponent = Component.text(" ".repeat(padding));
        Component preHeaderComponent = Component.text("-•÷ ").color(InscriptedPalette.DARKEST_TEXT.getColor());
        return paddingComponent.append(preHeaderComponent.append(MiniMessage.miniMessage().deserialize(inscriptionsHeader,
                        Placeholder.parsed("n", String.valueOf(numOfInscriptions)),
                        Placeholder.parsed("color", "<"+InscriptedPalette.DARKEST_TEXT.getColorString()+">")).color(InscriptedPalette.NEUTRAL_GRAY.getColor())
                .decoration(TextDecoration.ITALIC,false)));
    }
    public static Component getInscriptionFooter(int padding){
        Component paddingComponent = Component.text(" ".repeat(padding));
        return paddingComponent.append(Component.text(inscriptionsFooter).color(InscriptedPalette.DARKEST_TEXT.getColor())).decoration(TextDecoration.ITALIC,false);
    }
}