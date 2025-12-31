package com.amorabot.inscripted.item.render;

import com.amorabot.inscripted.item.structure.Weapon.DamageTypes;
import com.amorabot.inscripted.item.structure.Armor.DefenceTypes;
import com.amorabot.inscripted.item.inscription.definition.Stats;
import com.amorabot.inscripted.item.structure.Armor.Armor;
import com.amorabot.inscripted.item.structure.Armor.LocalDefence;
import com.amorabot.inscripted.item.structure.Weapon.LocalDamage;
import com.amorabot.inscripted.item.structure.Weapon.RangeCategory;
import com.amorabot.inscripted.item.structure.Weapon.Weapon;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainStatRenderer implements ItemVisitor<List<Component>> {

    @Override
    public List<Component> visitWeapon(Weapon weapon) {
        List<Component> damageLines = new ArrayList<>();
        TextColor textColor = InscriptedPalette.NEUTRAL_GRAY.getColor();

        Component paddingComponent = Component.text(" ");

        //Render Damages
        LocalDamage localWeaponDamage = weapon.getDamage();
        if (localWeaponDamage.getWeaponDamage().isEmpty()){return damageLines;}

        //Render Physical first
        String physicalIcon = DamageTypes.PHYSICAL.getCharacter();
        if (weapon.getRange().equals(RangeCategory.RANGED)){physicalIcon = "\uD83C\uDFF9";}

        final int[] localPhys = localWeaponDamage.getDamage(DamageTypes.PHYSICAL);

        final String dmgElement = Utils.prettify("DMG:");
        Component dmgComponent = Component.text(dmgElement).appendSpace().color(textColor);
        final String physicalLine = (physicalIcon + " " + localPhys[0] + " - " + localPhys[1]);
        Component coloredDmgComponent = Component.text(physicalLine).color(DamageTypes.PHYSICAL.getDmgColor().getColor());
        damageLines.add(paddingComponent.append(dmgComponent).append(coloredDmgComponent).decoration(TextDecoration.ITALIC,false));

        //Render the remaining types
        for (DamageTypes dmgType : localWeaponDamage.getWeaponDamage().keySet()){
            if (dmgType.equals(DamageTypes.PHYSICAL)){continue;}
            final int[] dmgValues = localWeaponDamage.getDamage(dmgType);
            if (Arrays.stream(dmgValues).sum() == 0){continue;}

            String icon = dmgType.getCharacter();
            final String dmgLine = (icon+ " " + dmgValues[0] + " - " + dmgValues[1]);
            damageLines.add(Component.text(" ".repeat(7))
                    .append(Component.text(dmgLine)
                            .color(dmgType.getDmgColor().getColor())
                            .decoration(TextDecoration.ITALIC,false)));
        }
        //Empty line spacing
        damageLines.add(Component.text(""));

        //Render Atk speed line
        Component barComponent = weapon.getAtkSpeed().getAttackSpeedBarComponent();
        String atkSpeedElement = Utils.prettify("Atk Speed:");
        Component atkSpeedLine = Component.text(atkSpeedElement).color(textColor).appendSpace().append(barComponent);
        damageLines.add(paddingComponent.append(atkSpeedLine.decoration(TextDecoration.ITALIC,false)));

        damageLines.add(Component.text(""));

        return damageLines;
    }

    @Override
    public List<Component> visitArmor(Armor armor) {
        List<Component> defenceLines = new ArrayList<>();
        LocalDefence localDefences = armor.getDefences();
        TextColor textColor = InscriptedPalette.NEUTRAL_GRAY.getColor();

        //Build HP Line
        String HP = Utils.prettify("HP: ");
        Component hpHeader = Component.text(HP).color(textColor).decoration(TextDecoration.ITALIC,false);
        defenceLines.add(Component.text(" ").append(hpHeader));

        final Component offsetComponent = Component.text(" ".repeat(3));
        final int hpValue = localDefences.getDefence(DefenceTypes.HEALTH);
        Component hpComponent = getIndividualDefenceComponent(DefenceTypes.HEALTH,hpValue,Stats.HEALTH.getAlias(),InscriptedPalette.HEALTH.getColor());
        Component spacingComponent = Component.text(" ".repeat(5));

        //Append SOUL if necessary
        final int soulValue = localDefences.getDefence(DefenceTypes.SOUL);
        boolean hasSoul = soulValue!=0;
        if (hasSoul){
            //Build the component and append it to hpComponent
            Component soulComponent = getIndividualDefenceComponent(DefenceTypes.SOUL,soulValue,Stats.SOUL.getAlias(),InscriptedPalette.SOUL.getColor());
            Component composedHPLine = offsetComponent.append(hpComponent).append(spacingComponent).append(soulComponent).decoration(TextDecoration.ITALIC,false);
            defenceLines.add(composedHPLine);
        } else {
            defenceLines.add(offsetComponent.append(hpComponent).decoration(TextDecoration.ITALIC,false));
        }

        //Build Defences
        final int armorValue = localDefences.getDefence(DefenceTypes.ARMOR);
        final int dodgeValue = localDefences.getDefence(DefenceTypes.DODGE);
        boolean hasArmor = armorValue!=0;
        boolean hasDodge = dodgeValue!=0;

        if (!hasArmor && !hasDodge){
            defenceLines.add(Component.text(""));
            return defenceLines;
        }

        //Armor has at least 1 defence, lets build the Def. components
        String DEF = Utils.prettify("DEF: ");
        Component defHeader = Component.text(DEF).color(textColor).decoration(TextDecoration.ITALIC,false);
        defenceLines.add(Component.text(" ").append(defHeader));

        Component armorComponent;
        Component dodgeComponent;
        //Has both
        if (hasArmor && hasDodge){
            armorComponent = getIndividualDefenceComponent(DefenceTypes.ARMOR,armorValue,Stats.ARMOR.getAlias(),InscriptedPalette.ARMOR.getColor());
            dodgeComponent = getIndividualDefenceComponent(DefenceTypes.DODGE,dodgeValue,Stats.DODGE.getAlias(),InscriptedPalette.DODGE.getColor());
            Component composedDefLine = offsetComponent.append(armorComponent).append(spacingComponent).append(dodgeComponent).decoration(TextDecoration.ITALIC,false);
            defenceLines.add(composedDefLine);
            defenceLines.add(Component.text(""));
            return defenceLines;
        }

        //Has only 1
        if (hasArmor){
            armorComponent = getIndividualDefenceComponent(DefenceTypes.ARMOR,armorValue,Stats.ARMOR.getAlias(),InscriptedPalette.ARMOR.getColor());
            defenceLines.add(offsetComponent.append(armorComponent.decoration(TextDecoration.ITALIC,false)));
            defenceLines.add(Component.text(""));
            return defenceLines;
        }
        //Has only dodge
        dodgeComponent = getIndividualDefenceComponent(DefenceTypes.DODGE,dodgeValue,Stats.DODGE.getAlias(),InscriptedPalette.DODGE.getColor());
        defenceLines.add(offsetComponent.append(dodgeComponent.decoration(TextDecoration.ITALIC,false)));
        defenceLines.add(Component.text(""));
        return defenceLines;
    }

    //TODO: Make a mapping from DefenceTypes -> Stats & Palette, so only 1 is needed.
    private static @NotNull Component getIndividualDefenceComponent(DefenceTypes defence, int value, String statAlias, TextColor color){
        return Component.text(defence.getSpecialChar()+" +"+ value +" "+ statAlias).color(color);
    }
}
