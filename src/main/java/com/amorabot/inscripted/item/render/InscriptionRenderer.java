package com.amorabot.inscripted.item.render;

import com.amorabot.inscripted.item.inscription.Inscription;
import com.amorabot.inscripted.item.inscription.UniqueInscription;
import com.amorabot.inscripted.item.inscription.definition.InscriptionDefinition;
import com.amorabot.inscripted.player.Archetypes;
import com.amorabot.inscripted.item.inscription.ProceduralInscription;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.*;

public class InscriptionRenderer {

    public static Comparator<Inscription> SORTER;
    public static Map<Integer,String> TIER_ICONS;
    static {
        SORTER = (i1, i2) -> {
            if (i2.isEffect() && i1.isKeystone()){return 1;}
            if ((i2.isKeystone() || i2.isEffect())){return 1;}
            if (!i2.isModifiable()){return 1;}
            if (i1.equals(i2)){return 0;}
            if (i1.getInscriptionDefinition().getDisplayName().length() < i2.getInscriptionDefinition().getDisplayName().length()){return -1;}
            return -1;
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
        inscriptions.sort(SORTER); //Mutates the list
        for (Inscription insc : inscriptions) {
            String currentHex = valuesHex;
            if (insc instanceof UniqueInscription){currentHex = InscriptedPalette.RELIC.getColorString();}
            renderedInscriptions.add(getInscriptionAsComponent(insc,padding,currentHex));
            if (insc.isEffect()){renderedInscriptions.add(Component.text(""));}
        }
        return renderedInscriptions;
    }
    public static Component getImplicitComponent(ProceduralInscription implicit, Archetypes archetype){
        InscriptedPalette archetypeColor = archetype.getColorOnPalette();
        Component displayNameComponent = MiniMessage.miniMessage().deserialize(implicit.getDisplayName(archetypeColor.getColorString())).color(archetypeColor.getColor());
        Component spacing = Component.text(" ");
        return spacing.append(displayNameComponent.append(spacing).append(getPostfixDetails(implicit))).append(spacing);
    }

    public static Component getInscriptionAsComponent(Inscription inscription, int padding, String valuesHex){
        Component paddingComponent = Component.text(" ".repeat(padding));
        Component spacing = Component.text(" ");
        if (inscription instanceof UniqueInscription uniqueInscription){
            if (uniqueInscription.isEffect() || uniqueInscription.isKeystone()){
                String specialDisplayName = inscription.getDisplayName(valuesHex);
                Component specialInscriptionComponent = MiniMessage.miniMessage().deserialize(specialDisplayName);
                return paddingComponent.append(specialInscriptionComponent.append(spacing).append(getPostfixDetails(inscription)).append(paddingComponent)).decoration(TextDecoration.ITALIC,false);
            }
        }
        Component displayNameComponent = MiniMessage.miniMessage().deserialize(inscription.getDisplayName(valuesHex)).color(InscriptedPalette.NEUTRAL_GRAY.getColor());
        return paddingComponent.append(displayNameComponent.append(spacing).append(getPostfixDetails(inscription)).append(spacing)).decoration(TextDecoration.ITALIC,false);
    }
    public static Component getPostfixDetails(Inscription inscription){
        if (inscription instanceof UniqueInscription uniqueInscription){
            return Component.text(inscription.getInscriptionDefinition().getAffix().getRuneIcon()).color(InscriptedPalette.DARKEST_TEXT.getColor());
        }
        ProceduralInscription regularInscription = (ProceduralInscription) inscription;
        return Component.text(inscription.getInscriptionDefinition().getAffix().getRuneIcon(), InscriptedPalette.DARK_GRAY.getColor()).append(
                Component.text(getTierChar(regularInscription.getTier(), regularInscription.getInscription().getTiers()), InscriptedPalette.DARKEST_TEXT.getColor())
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
