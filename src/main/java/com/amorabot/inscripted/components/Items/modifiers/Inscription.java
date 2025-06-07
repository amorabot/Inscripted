//package com.amorabot.inscripted.components.Items.modifiers;
//
//import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;
//import com.amorabot.inscripted.components.Items.DataStructures.Enums.RangeTypes;
//import com.amorabot.inscripted.components.Items.modifiers.data.HybridInscriptionData;
//import com.amorabot.inscripted.components.Items.modifiers.data.InscriptionData;
//import com.amorabot.inscripted.components.Items.modifiers.data.ModifierData;
//import com.amorabot.inscripted.components.Items.modifiers.data.StatDefinition;
//import com.amorabot.inscripted.player.Archetypes;
//import com.amorabot.inscripted.item.render.InscriptedPalette;
//import com.amorabot.inscripted.utils.Utils;
//import lombok.EqualsAndHashCode;
//import lombok.Getter;
//import lombok.Setter;
//import net.kyori.adventure.text.Component;
//import net.kyori.adventure.text.format.TextColor;
//import net.kyori.adventure.text.format.TextDecoration;
//
//import java.io.Serializable;
//
//@Getter
//@Setter
//@EqualsAndHashCode
//public class Inscription implements Serializable {
//
//    private InscriptionID inscription;
//    private int tier; //0-maxTier
//    //Reconsider storing max tier (calculable)
//    private int maxTier;
//    private double basePercentile; //0-1
//    private boolean imbued = false;
//
//    public Inscription(InscriptionID mod, int chosenTier, int highestTierAvailable){
//        this.inscription = mod;
//        //Capped to the mod's maximum tier
//        this.tier = Math.min(chosenTier, mod.getTotalTiers());
//        this.maxTier = highestTierAvailable; //its the mod's highest possible tier, given the item level that generated it
//        this.basePercentile = Utils.getNormalizedValue();
//    }
//
//    private Component buildStandardRelicInscriptionComponent(InscriptionData inscriptionData, int padding, String rawDisplayName){
////        RangeTypes range = inscriptionData.getDefinitionData().rangeType();
////        int[] mappedValues = this.getMappedFinalValue();
//
////        String valuesHex = "#824575";
////        Component replacedDisplayName = range.substitutePlaceholders(mappedValues, rawDisplayName, valuesHex);
////        Component paddingComponent = Component.text(" ".repeat(padding));
////        return InscriptedPalette.colorizeComponent(paddingComponent.append(replacedDisplayName)
////                .append(paddingComponent), ItemInterfaceRenderer.highlightColor).decoration(TextDecoration.ITALIC,false);
//        return null;
//    }
//
//    private Component buildStandardInscriptionComponent(InscriptionData inscriptionData, Component inscriptionDetails, int padding, String rawDisplayName, boolean isImplicit,Archetypes... implicitArchetype){
////        RangeTypes range = inscriptionData.getDefinitionData().rangeType();
////        int[] mappedValues = this.getMappedFinalValue();
//
////        Component replacedDisplayName;
////        String valuesHex = InscriptedPalette.ITEM_VALUE.getColorString();
//
//        //In the particular case its a standard implicit:
//        if (isImplicit){
////            assert (implicitArchetype != null);
////            assert (implicitArchetype[0] != null);
////            //Overriding the values color to match the Archetype's color
////            valuesHex = implicitArchetype[0].getColor().getColorString();
////            //Substituting the implicit's placeholder
////            replacedDisplayName = range.substitutePlaceholders(mappedValues, rawDisplayName, valuesHex);
//
////            return buildInscriptionComponent(replacedDisplayName,inscriptionDetails,padding,implicitArchetype[0].getColor().getColor());
//        }
//
//        //Its a regular standard Inscription
////        replacedDisplayName = range.substitutePlaceholders(mappedValues, rawDisplayName, valuesHex);
////        //Lets colorize the resulting component
////        if (isImbued()){
////            //The valuesHex is standard (Subject to change)
////            return buildInscriptionComponent(replacedDisplayName,inscriptionDetails,padding,InscriptedPalette.ITEM_VALUE.getColor());
////        }
////        return buildInscriptionComponent(replacedDisplayName,inscriptionDetails,padding,ItemInterfaceRenderer.highlightColor);
//        return null;
//    }
//    private Component buildInscriptionComponent(Component displayName, Component details, int padding, TextColor textColor){
//        displayName = InscriptedPalette.colorizeComponent(displayName, textColor);
//        return Component.text(" ".repeat(padding)).append(displayName).appendSpace().append(details);
//    }
//    private Component getInscriptionDetails(int paddingRight){
//        Component runicIcon = InscriptedPalette.colorizeComponent(
//                Component.text(getInscription().getData().getAffixType().getRuneIcon()),
//                InscriptedPalette.DARK_GRAY.getColor());
////        Component uncoloredTierIndicator = Component.text(Utils.getRomanChar(this.tier));
////        if (this.tier == this.maxTier){uncoloredTierIndicator = uncoloredTierIndicator.append(Component.text("*"));}
////        Component tierIndicator = InscriptedPalette.colorizeComponent(uncoloredTierIndicator, InscriptedPalette.DARKEST_TEXT.getColor());
//        return null;
////        return runicIcon.appendSpace().append(tierIndicator).append(Component.text(" ".repeat(paddingRight)));
//    }
//
//
//
//    private Component buildHybridInscription(HybridInscriptionData hybridInscriptionData, Component inscriptionDetails, int padding, String rawDisplayName, boolean isImplicit,Archetypes... implicitArchetype){
////        StatDefinition[] modDataArray = hybridInscriptionData.getStatDefinitions();
////        int hybridMods = modDataArray.length;
////        String[] templates = rawDisplayName.split(HybridInscriptionData.HYBRID_SEPARATOR);
////
////        Component[] replacedDisplayNames = new Component[hybridMods];
////        String valuesColorHex = InscriptedPalette.ITEM_VALUE.getColorString();
////        if (isImplicit){valuesColorHex = implicitArchetype[0].getColor().getColorString();} //If its a implicit, change the color for values
////
////        for (int i = 0; i < hybridMods; i++){
////            StatDefinition currentInscription = modDataArray[i];
////            int[] currentMappedValues = this.getMappedFinalValue(i);
////            replacedDisplayNames[i] = currentInscription.rangeType().substitutePlaceholders(currentMappedValues,templates[i],valuesColorHex);
////        }
////
////        //In the particular case its a implicit:
////        if (isImplicit){
////            assert (implicitArchetype[0] != null);
////            return buildHybridInscriptionComponent(replacedDisplayNames,inscriptionDetails,padding,implicitArchetype[0].getColor().getColor());
////        }
////
////        //There are no ways to imbue a implicit, the valuesColorHex should be standard
////        return buildHybridInscriptionComponent(replacedDisplayNames,inscriptionDetails,padding,ItemInterfaceRenderer.highlightColor);
//        return null;
//    }
////    private Component buildHybridInscriptionComponent(Component[] replacedDisplayNames, Component details, int padding, TextColor textColor){
////        final int mods = replacedDisplayNames.length;
////        Component finalHybridComponent = Component.text("");
////        for (int k = 0; k<mods; k++){
////            Component currentDisplayName = replacedDisplayNames[k];
////            finalHybridComponent = finalHybridComponent.append(currentDisplayName);
////            if (k==mods-1){continue;}
////            finalHybridComponent = finalHybridComponent.appendSpace().append(Component.text(HybridInscriptionData.HYBRID_SEPARATOR)).appendSpace();
////        }
////        finalHybridComponent = InscriptedPalette.colorizeComponent(finalHybridComponent, textColor);
////        return Component.text(" ".repeat(padding)).append(finalHybridComponent).appendSpace().append(details);
////    }
//    public void imbue(){
//        this.imbued = true;
//    }
//}
