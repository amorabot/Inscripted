package com.amorabot.inscripted.components.Items.Weapon;

import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Abstract.ItemRenderer;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.inscripted.components.Items.Interfaces.ItemSubtype;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;
import java.util.Map;

import static com.amorabot.inscripted.utils.Utils.color;

public class WeaponRenderer implements ItemRenderer {
    @Override
    public void renderMainStat(Item itemData, List<String> itemLore) {
        Weapon weaponData = (Weapon) itemData;
        Map<DamageTypes, int[]> damages = weaponData.getLocalDamage();
        int indent = 1;

        itemLore.add("");
        serializeDamageLineComponent(damages, itemLore, indent, weaponData);
        itemLore.add("");
        addAtkSpeedLine(weaponData, itemLore);
        itemLore.add("");
        itemLore.add("@HEADER@");
    }

    @Override
    public <subType extends Enum<subType> & ItemSubtype> void renderDescription(Item itemData, List<String> itemLore, subType itemSubtype) {
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

    private void serializeDamageLineComponent(Map<DamageTypes, int[]> damages, List<String> itemLore, int indent, Weapon weaponData){
        for (DamageTypes dmgType : DamageTypes.values()){
            String dmgIcon = dmgType.getCharacter();
            if (dmgType.equals(DamageTypes.PHYSICAL)){
                dmgIcon = dmgType.getPhysicalDamageIcon(weaponData.getRange());
            }

            if (damages.containsKey(dmgType)){
                int[] extraDmg = damages.get(dmgType);
                TextComponent extraDmgComponent =
                        Component.text(dmgIcon+ " " + extraDmg[0] + " - " + extraDmg[1])
                                .color(dmgType.getTextColor());

                if (dmgType.equals(DamageTypes.PHYSICAL)){ //When it's the first line, append the "dmg: " prefix
                    serializeTextComponent(itemLore, getDmgPrefixComponent().append(extraDmgComponent), indent);
                    continue;
                }
                serializeTextComponent(itemLore, extraDmgComponent, indent+6);
            }
        }
    }
    private void addAtkSpeedLine(Weapon weaponData, List<String> itemLore){
        itemLore.add(color(Utils.convertToPrettyString(" &7Atk Speed: ") + "&8&l" + weaponData.getAtkSpeed()));
    }
}
