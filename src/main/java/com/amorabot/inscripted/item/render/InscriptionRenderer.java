package com.amorabot.inscripted.item.render;

import com.amorabot.inscripted.components.Player.archetypes.Archetypes;
import com.amorabot.inscripted.item.inscription.Inscription;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.*;

public class InscriptionRenderer {

    public static Comparator<Inscription> SORTER;
    public static Map<Integer,String> TIER_ICONS;
    static {
        SORTER = (o1, o2) -> {
            if (o2.isModifiable()){return 1;}
            if (o1.equals(o2)){return 0;}
            if (o1.getInscription().ordinal() < o2.getInscription().ordinal()){return -1;}
            return 1;
        };
        TIER_ICONS = new HashMap<>();
        TIER_ICONS.put(0,"∅");
        TIER_ICONS.put(1, "I");
        TIER_ICONS.put(2, "II");
        TIER_ICONS.put(3, "III");
        TIER_ICONS.put(4, "IV");
        TIER_ICONS.put(5, "V");
        TIER_ICONS.put(6, "VI");
        TIER_ICONS.put(7, "VII");
        TIER_ICONS.put(8, "VIII");
        TIER_ICONS.put(9, "IX");
        TIER_ICONS.put(10, "X");
        TIER_ICONS.put(11, "XI");
        TIER_ICONS.put(12, "XII");
    }

    public static List<Component> renderInscriptionList(List<Inscription> inscriptions, int padding){
        String valuesHex = InscriptedPalette.ITEM_VALUE.getColorString();
        List<Component> renderedInscriptions = new ArrayList<>();
        inscriptions.sort(SORTER);
        for (Inscription insc : inscriptions) {
            if (insc.isSpecial()){continue;}
            renderedInscriptions.add(getInscriptionAsComponent(insc,padding,valuesHex));
        }
        return renderedInscriptions;
    }
    public static Component getImplicitComponent(Inscription implicit, Archetypes archetype){
        InscriptedPalette archetypeColor = archetype.getColorOnPalette();
        Component displayNameComponent = MiniMessage.miniMessage().deserialize(implicit.getDisplayName(archetypeColor.getColorString())).color(archetypeColor.getColor());
        Component spacing = Component.text(" ");
        return spacing.append(displayNameComponent.append(spacing).append(getPostfixDetails(implicit))).append(spacing);
    }

    public static Component getInscriptionAsComponent(Inscription inscription, int padding, String valuesHex){
        Component displayNameComponent = MiniMessage.miniMessage().deserialize(inscription.getDisplayName(valuesHex)).color(InscriptedPalette.NEUTRAL_GRAY.getColor());
        Component paddingComponent = Component.text(" ".repeat(padding));
        Component spacing = Component.text(" ");
        return paddingComponent.append(displayNameComponent.append(spacing).append(getPostfixDetails(inscription)).append(spacing)).decoration(TextDecoration.ITALIC,false);
    }
    public static Component getPostfixDetails(Inscription inscription){
        return Component.text(inscription.getInscription().getDefinitionData().getAffix().getRuneIcon(), InscriptedPalette.DARK_GRAY.getColor()).append(
                Component.text(getTierChar(inscription.getTier(),inscription.getInscription().getTiers()), InscriptedPalette.DARKEST_TEXT.getColor())
        );
    }
    public static String getTierChar(int tier, int totalTiers){
        if (tier>totalTiers){return "//";}
        final int invertedTier = totalTiers - tier;
        if (!TIER_ICONS.containsKey(invertedTier)){
            return "*";
        }
        return TIER_ICONS.get(invertedTier);
    }
}
