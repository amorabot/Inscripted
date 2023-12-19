package com.amorabot.inscripted.components.Items.Weapon;

import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Abstract.ItemRenderer;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.components.Items.Interfaces.AffixTableSelector;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;
import java.util.Map;

import static com.amorabot.inscripted.utils.Utils.color;

public class BasicRunicWeaponRenderer implements ItemRenderer {
    @Override
    public void renderMainStat(Item itemData, List<String> itemLore) {
        Weapon weaponData = (Weapon) itemData;
        Map<DamageTypes, int[]> damages = weaponData.getBaseDamage();
        int indent = 1;

        itemLore.add("");
        serializeDamageLineComponent(damages, itemLore, indent);
        itemLore.add("");
        itemLore.add("@HEADER@");
    }

    @Override
    public <subType extends Enum<subType> & AffixTableSelector> void renderDescription(Item itemData, List<String> itemLore, subType itemSubtype) {
        Weapon weaponData = (Weapon) itemData;

        itemLore.add("");
        itemLore.add(color(Utils.convertToPrettyString(" &7Item Level: ") + "&f&l" + weaponData.getIlvl()));
        String passiveString = Utils.convertToPrettyString(" &7Passive: ") + weaponData.getImplicit().getDisplayName() + " ";
        itemLore.add(ColorUtils.translateColorCodes(passiveString));
        itemLore.add("");
    }

    private void serializeTextComponent(List<String> lore, TextComponent textComponent, int indentation){
        String translatedString = ColorUtils.translateColorCodes(LegacyComponentSerializer.legacyAmpersand().serialize(textComponent));
        lore.add(translatedString.indent(indentation));
    }
    private TextComponent getDmgPrefixComponent(){
        return Component.text("ᴅᴍɢ: ").color(NamedTextColor.GRAY);
    }

    private void serializeDamageLineComponent(Map<DamageTypes, int[]> damages, List<String> itemLore, int indent){
        for (DamageTypes dmgType : DamageTypes.values()){

            if (damages.containsKey(dmgType)){
                int[] extraDmg = damages.get(dmgType);
                TextComponent extraDmgComponent =
                        Component.text(dmgType.getCharacter()+ " " + extraDmg[0] + " - " + extraDmg[1])
                                .color(dmgType.getColorComponent());

                if (dmgType.equals(DamageTypes.PHYSICAL)){
                    serializeTextComponent(itemLore, getDmgPrefixComponent().append(extraDmgComponent), indent);
                    continue;
                }
                serializeTextComponent(itemLore, extraDmgComponent, indent+6);
            }
        }
    }
}
