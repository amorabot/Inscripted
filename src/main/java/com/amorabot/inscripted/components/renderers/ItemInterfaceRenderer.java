package com.amorabot.inscripted.components.renderers;

import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Armor.Armor;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.inscripted.components.Items.Interfaces.ItemSubtype;
import com.amorabot.inscripted.components.Items.Weapon.RangeCategory;
import com.amorabot.inscripted.components.Items.Weapon.Weapon;
import com.amorabot.inscripted.components.Items.modifiers.Inscription;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;
import com.amorabot.inscripted.components.Items.modifiers.data.ModifierData;
import com.amorabot.inscripted.components.Items.relic.enums.Effects;
import com.amorabot.inscripted.components.Items.relic.enums.Keystones;
import com.amorabot.inscripted.components.Player.archetypes.Archetypes;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ItemInterfaceRenderer {

    public static final TextColor highlightColor = InscriptedPalette.NEUTRAL_GRAY.getColor();
    public static final String TOP_RUNIC_BAR = "- --=÷¦• ";
    public static final String BOT_RUNIC_BAR = "--=÷• ";
    public static final String inscriptionsHeader = Utils.convertToPrettyString("Inscriptions: ") + "<color>•÷¦[ <n> ]¦÷=--  ";
    public static final String inscriptionsFooter = "-   --  ---  ----=÷• ᚫ •÷=---";
    //      "¦¡!ï÷ ¨ ╜ ╙"; ᚫ

    public static Component getInscriptionHeader(int numOfInscriptions, int padding){
        Component paddingComponent = Component.text(" ".repeat(padding));
        Component preHeaderComponent = Component.text("-•÷ ").color(InscriptedPalette.DARKEST_TEXT.getColor());
        return paddingComponent.append(preHeaderComponent.append(MiniMessage.miniMessage().deserialize(inscriptionsHeader,
                        Placeholder.parsed("n", String.valueOf(numOfInscriptions)),
                        Placeholder.parsed("color", "<"+InscriptedPalette.DARKEST_TEXT.getColorString()+">")).color(highlightColor)
                            .decoration(TextDecoration.ITALIC,false)));
    }
    public static Component getInscriptionFooter(int padding){
        Component paddingComponent = Component.text(" ".repeat(padding));
        return paddingComponent.append(Component.text(inscriptionsFooter).color(InscriptedPalette.DARKEST_TEXT.getColor())).decoration(TextDecoration.ITALIC,false);
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


    public static List<Component> renderDamage(Weapon weaponData, int indent){
        List<Component> damageComponent = new ArrayList<>();
        Component indentComponent = Component.text(" ".repeat(indent));
        //Render Damages
        Map<DamageTypes, int[]> damages = weaponData.getLocalDamage();
        if (damages.isEmpty()){return damageComponent;}
        for (DamageTypes dmgType : DamageTypes.values()){
            if (!damages.containsKey(dmgType)){continue;}

            String icon = dmgType.getCharacter();
            int[] dmgValues = damages.get(dmgType);
            if (dmgType.equals(DamageTypes.PHYSICAL)){
                if (weaponData.getRange().equals(RangeCategory.RANGED)){icon = "\uD83C\uDFF9";}
                final String dmgElement = Utils.convertToPrettyString("DMG:");
                Component dmgComponent = Component.text(dmgElement).appendSpace().color(highlightColor);
                final String physicalLine = (icon+ " " + dmgValues[0] + " - " + dmgValues[1]);
                Component coloredDmgComponent = Component.text(physicalLine).color(dmgType.getDmgColor().getColor());
                damageComponent.add(indentComponent.append(dmgComponent).append(coloredDmgComponent).decoration(TextDecoration.ITALIC,false));
                continue;
            }
            final String dmgLine = (icon+ " " + dmgValues[0] + " - " + dmgValues[1]);
            damageComponent.add(Component.text(" ".repeat(indent+6)).append(Component.text(dmgLine).color(dmgType.getDmgColor().getColor()).decoration(TextDecoration.ITALIC,false)));
        }
        //Empty line spacing
        damageComponent.add(Component.text(""));

        //Render Atk speed line
        Component barComponent = weaponData.getAtkSpeed().getAttackSpeedBarComponent();
        String atkSpeedElement = Utils.convertToPrettyString("Atk Speed:");
        Component atkSpeedLine = Component.text(atkSpeedElement).color(highlightColor).appendSpace().append(barComponent);
        damageComponent.add(indentComponent.append(atkSpeedLine.decoration(TextDecoration.ITALIC,false)));

        return damageComponent;
    }
    public static List<Component> renderDefences(Armor armorData, int padding){
        List<Component> renderedDefences = new ArrayList<>();
        final int maxLength = 24*(GlyphInfo.getGlyphWidth(' ')); //X times the whitespace pixel width

        final String hpIndicator = Utils.convertToPrettyString("HP:");
        final int hpIndicatorLength = GlyphInfo.countStringPixelLength(hpIndicator)+6;

        final String defIndicator = Utils.convertToPrettyString("DEF:");

        Map<DefenceTypes, Integer> updatedArmorDefences = armorData.getLocalDefences();

        final int health = updatedArmorDefences.getOrDefault(DefenceTypes.HEALTH, 1);
        final String healthString = ' ' + DefenceTypes.HEALTH.getSpecialChar() + ' ' + health + " Health";
        final int healthLength = GlyphInfo.countStringPixelLength(healthString);

        final int ward = updatedArmorDefences.getOrDefault(DefenceTypes.WARD, 0);
        String wardString = "";
        boolean hasWard = ward>0;
        if (hasWard){
            wardString = ' ' + DefenceTypes.WARD.getSpecialChar() + ' ' + ward + " Ward";
        }
        final int wardLength = GlyphInfo.countStringPixelLength(wardString);

        Utils.log("max: " + maxLength + " | hp: " + hpIndicatorLength + " | health: " + healthLength + " | ward: " + wardLength);

        final int armor = updatedArmorDefences.getOrDefault(DefenceTypes.ARMOR, 0);
        String armorString = "";
        boolean hasArmor = armor>0;
        if (hasArmor){
            armorString = ' ' + DefenceTypes.ARMOR.getSpecialChar() + ' ' + armor + " Armor  ";
        }

        final int dodge = updatedArmorDefences.getOrDefault(DefenceTypes.DODGE, 0);
        String dodgeString = "";
        boolean hasDodge = dodge>0;
        if (hasDodge){
            dodgeString = ' ' + DefenceTypes.DODGE.getSpecialChar() + " " + dodge + " Dodge  ";
        }


        boolean has2ndLine = (hasWard) || (hasArmor&&hasDodge);
        boolean noDefences = (!hasArmor && !hasDodge);

        //Building the actual components with a fixed char length
        Component indicatorLinePadding = getDefencePadding(maxLength, hpIndicatorLength, !noDefences);
        Component indicatorLine = Component.text(hpIndicator).color(InscriptedPalette.NEUTRAL_GRAY.getColor()).append(indicatorLinePadding);

        Component firstLinePadding = getDefencePadding(maxLength, healthLength, !noDefences);
        Component fixedStatLine = Component.text(healthString).color(InscriptedPalette.HEALTH.getColor()).append(firstLinePadding);

        Component secondStatLine;
        Component renderedFirstLine;
        Component lineStartPadding = Component.text(" ".repeat(padding));
        if (noDefences){
            renderedFirstLine = Component.text(hpIndicator).color(InscriptedPalette.NEUTRAL_GRAY.getColor()).decoration(TextDecoration.ITALIC, false);
        } else {
            renderedFirstLine = indicatorLine.append(Component.text(defIndicator)
                            .color(InscriptedPalette.NEUTRAL_GRAY.getColor()))
                    .decoration(TextDecoration.ITALIC, false);
        }
        renderedDefences.add(lineStartPadding.append(renderedFirstLine));

        if (has2ndLine){
            if (hasWard){
                Component secondLinePadding = getDefencePadding(maxLength, wardLength, !noDefences);
                secondStatLine = Component.text(wardString).color(InscriptedPalette.WARD.getColor())
                        .append(secondLinePadding);
                if (noDefences){
                    renderedDefences.add(lineStartPadding.append(fixedStatLine.decoration(TextDecoration.ITALIC,false)));
                    renderedDefences.add(lineStartPadding.append(secondStatLine.decoration(TextDecoration.ITALIC,false)));
                    return renderedDefences;
                }

                //The item should only have a single defence type (1st line)
                if (hasArmor){
                    renderedDefences.add(lineStartPadding.append(
                            fixedStatLine.append(Component.text(armorString).color(InscriptedPalette.ARMOR.getColor())).decoration(TextDecoration.ITALIC,false)));
                } else { //Must have dodge
                    renderedDefences.add(lineStartPadding.append(
                            fixedStatLine.append(Component.text(dodgeString).color(InscriptedPalette.DODGE.getColor())).decoration(TextDecoration.ITALIC,false)));
                }
                renderedDefences.add(lineStartPadding.append(secondStatLine.decoration(TextDecoration.ITALIC,false)));
                return renderedDefences;
            }
            //Its a 2-defence gear piece
            secondStatLine = getDefencePadding(maxLength, 3,true);
            //"HP side" complete, lets render the defence side
            renderedDefences.add(lineStartPadding.append(
                    fixedStatLine.append(Component.text(armorString).color(InscriptedPalette.ARMOR.getColor())).decoration(TextDecoration.ITALIC,false)));
            renderedDefences.add(lineStartPadding.append(
                    secondStatLine.append(Component.text(dodgeString).color(InscriptedPalette.DODGE.getColor())).decoration(TextDecoration.ITALIC,false)));
            return renderedDefences;
        }
        //Its a health+def piece
        if (hasArmor) {
            fixedStatLine = fixedStatLine.append(Component.text(armorString).color(InscriptedPalette.ARMOR.getColor()));
        }
        if (hasDodge) {
            fixedStatLine = fixedStatLine.append(Component.text(dodgeString).color(InscriptedPalette.DODGE.getColor()));
        }
        renderedDefences.add(lineStartPadding.append(fixedStatLine.decoration(TextDecoration.ITALIC,false)));

        return renderedDefences;
    }

    private static Component getDefencePadding(int maxPixelWidth, int elementWidth, boolean hasDefences){
        final int whitespacePixelSize = GlyphInfo.getGlyphWidth(' ');
        double whitespaces = Math.ceil(((maxPixelWidth - elementWidth)/(double)whitespacePixelSize));
        String whiteSpaceString = " ".repeat((int) whitespaces);
        if (hasDefences){
            return Component.text(whiteSpaceString)
//                    .append(Component.text("|").color(InscriptedPalette.DARKEST_TEXT.getColor())
                            .appendSpace();
        }
        return Component.text(whiteSpaceString);
    }



    public static List<Component> renderInscriptions(Item itemData, int padding){
        List<Component> renderedInscriptions = new ArrayList<>();

        List<Inscription> inscriptions = itemData.getInscriptionList();
        inscriptions.sort(getInscriptionComparator());
        for (Inscription currentInscription : inscriptions){
            ModifierData inscData = currentInscription.getInscription().getData();
            if (inscData.isEffect() || inscData.isKeystone()){continue;}
            renderedInscriptions.add(currentInscription.asComponent(padding));
        }
        return renderedInscriptions;
    }

    public static List<Component> renderSpecialInscriptionDescription(InscriptionID inscID, int padding){

        List<Component> description = new ArrayList<>();

        Component paddingComponent = Component.text(" ".repeat(padding));
        if (inscID.getData().isKeystone()){
            try{
                Keystones keystone = Keystones.valueOf(inscID.toString());
                appendDescriptionLine(description, keystone.getDescription(), paddingComponent);
                return description;
            } catch (IllegalArgumentException e) {
                Utils.error("Invalid keystone mapping @ ItemInterfaceRenderer");
            }
        }
        if (inscID.getData().isEffect()){
            try{
                Effects effect = Effects.valueOf(inscID.toString());
                appendDescriptionLine(description, effect.getDescription(), paddingComponent);
                return description;
            } catch (IllegalArgumentException e) {
                Utils.error("Invalid effect mapping @ ItemInterfaceRenderer");
            }
        }
        return new ArrayList<>();

    }
    private static void appendDescriptionLine(List<Component> descriptionComponents, String[] desc, Component paddingComponent){
        Component pointerComponent = Component.text(">| ").decorate(TextDecoration.BOLD);
        for (int k = 0; k < desc.length; k++){
            if (k==0){
                Component firstLineComp = Component.text(desc[k]);
                descriptionComponents.add(paddingComponent.append(pointerComponent)
                        .append(firstLineComp.decoration(TextDecoration.ITALIC,false))
                        .append(paddingComponent).color(InscriptedPalette.DARK_GRAY.getColor()));
                continue;
            }
            Component lineComponent = Component.text(" " + desc[k]);
            descriptionComponents.add(paddingComponent.append(lineComponent.decoration(TextDecoration.ITALIC,false))
                    .append(paddingComponent).color(InscriptedPalette.DARK_GRAY.getColor()));
        }
    }



    public static List<Component> renderRequirements(Item itemData, int padding){
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
        return renderedRequirements;
    }
    public static <subType extends Enum<subType> & ItemSubtype> Component renderImplicit(Item itemData, int padding, subType itemSubtype){
        Component passiveIndicator = Component.text(Utils.convertToPrettyString("Passive:"));
        Component paddingComponent = Component.text(" ".repeat(padding));
        Archetypes itemArchetype = Archetypes.mapArchetypeFor(itemSubtype);
        assert itemArchetype!=null;
        Component implicitComponent = itemData.getImplicit().asComponent(0,itemArchetype);
        return InscriptedPalette.colorizeComponent(
                paddingComponent.append(passiveIndicator).appendSpace().append(implicitComponent).append(paddingComponent)
                ,highlightColor)
                .decoration(TextDecoration.ITALIC,false);
    }
    public static Component renderDescription(Item itemData, String slotName, int padding){
        ItemRarities rarity = itemData.getRarity();
        Component paddingComponent = Component.text(" ".repeat(padding));
        Component tagsComponent = renderDescriptionTags(itemData.getStarRating(), itemData.isCorrupted());
        Component descriptionComponent = Component.text(rarity.toString()+ " " + slotName).decorate(TextDecoration.BOLD).color(rarity.getColorComponent().getColor());

        return paddingComponent.append(descriptionComponent).appendSpace().append(tagsComponent).decoration(TextDecoration.ITALIC,false);
    }
    private static Component renderDescriptionTags(double starRating, boolean corrupted){
        Component starRatingIcon = getStarIcon(starRating);
        if(corrupted){
            Component corruptionIcon = Component.text("☠").color(InscriptedPalette.CORRUPTED.getColor());
            return starRatingIcon.appendSpace().append(corruptionIcon);
        }
        return starRatingIcon;
    }
    private static Component getStarIcon(double starRating){
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



    public static Component getRunicLine(boolean top, int... args){
        final int numberOfMods = args[0];
        final int longestLineLength = args[1];
        final int minPadding = 2;

        if (top){
            StringBuilder headerBar = new StringBuilder(TOP_RUNIC_BAR);
            Component topBarComponent = Component.text(headerBar +  String.valueOf(numberOfMods)  + headerBar.reverse());
            final int offset = Math.max(minPadding,(longestLineLength/2)-TOP_RUNIC_BAR.length());
            Component topOffset = Component.text(" ".repeat(offset));
            return InscriptedPalette.colorizeComponent(topOffset.append(topBarComponent), InscriptedPalette.DARK_GRAY.getColor()).decoration(TextDecoration.ITALIC,false);
        }
        StringBuilder footerBar = new StringBuilder(BOT_RUNIC_BAR);
        Component botBarComponent = Component.text(footerBar + "ᚫ" + footerBar.reverse());
        final int offset = Math.max(minPadding+3,(longestLineLength/2)-BOT_RUNIC_BAR.length());
        Component botOffset = Component.text(" ".repeat(offset));
        return InscriptedPalette.colorizeComponent(botOffset.append(botBarComponent), InscriptedPalette.DARK_GRAY.getColor()).decoration(TextDecoration.ITALIC,false);
    }

    public static int getHighestStringLengthFor(Item itemData, int inscriptionsPadding){
        int highestInscriptionSize = itemData.getName().length();
        int addedSize = (2*inscriptionsPadding) + 2; //Lateral padding + rune icon
        for (Inscription insc : itemData.getInscriptionList()){
            final int displayNameLineLength = insc.getInscription().getDisplayName().length() + addedSize + Utils.getRomanChar(insc.getTier()).length();
            if (displayNameLineLength > (highestInscriptionSize)){
                highestInscriptionSize = displayNameLineLength;
            }
        }
        return highestInscriptionSize;
    }
    private static Comparator<Inscription> getInscriptionComparator(){
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
}
