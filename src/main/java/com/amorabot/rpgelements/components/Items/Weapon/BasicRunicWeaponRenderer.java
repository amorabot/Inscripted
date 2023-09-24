package com.amorabot.rpgelements.components.Items.Weapon;

import com.amorabot.rpgelements.components.Items.Abstract.Item;
import com.amorabot.rpgelements.components.Items.Abstract.ItemRenderer;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.Affix;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.RangeTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Modifier;
import com.amorabot.rpgelements.components.Items.Interfaces.AffixTableSelector;
import com.amorabot.rpgelements.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.amorabot.rpgelements.utils.Utils.color;

public class BasicRunicWeaponRenderer implements ItemRenderer {

    @Override
    public void setDisplayName(String name, ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(color(name));
        item.setItemMeta(itemMeta);
    }

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
    public void renderMods(Item itemData, List<String> itemLore) {
        Weapon weaponData = (Weapon) itemData;
        String valuesColor = "&#95dbdb"; //pale baby blue

        List<Modifier<WeaponModifiers>> mods = weaponData.getModifiers();
        List<Modifier<WeaponModifiers>> prefixes = new ArrayList<>();
        List<Modifier<WeaponModifiers>> suffixes = new ArrayList<>(); //Alternativa: Implementar equals() ou um comparador
        for (Modifier<WeaponModifiers> mod : mods){
            if (mod.getModifier().getAffixType() == Affix.PREFIX){
                prefixes.add(mod);
            } else {
                suffixes.add(mod);
            }
        }

        for (Modifier<WeaponModifiers> mod : prefixes){
            String modifierDisplayName = getDisplayString(mod, valuesColor);
            itemLore.add(ColorUtils.translateColorCodes(modifierDisplayName));
        }
        for (Modifier<WeaponModifiers> mod : suffixes){
            String modifierDisplayName = getDisplayString(mod, valuesColor);
            itemLore.add(ColorUtils.translateColorCodes(modifierDisplayName));
        }
        if (itemData.getRarity() != ItemRarities.COMMON){
            itemLore.add(color("@FOOTER@"));
        }
    }

    @NotNull
    private static String getDisplayString(Modifier<WeaponModifiers> mod, String valuesColor) {
        String modifierDisplayName = "&7" + mod.getModifier().getDisplayName() + "  ";
        RangeTypes rangeType = mod.getModifier().getRangeType();
        switch (rangeType){
            case SINGLE_VALUE -> {}
            case SINGLE_RANGE -> modifierDisplayName = (modifierDisplayName
                    .replace("@value1@", valuesColor + mod.getValue()[0]+"&7")).indent(2);
            case DOUBLE_RANGE -> {
                int[] values = mod.getValue();
                modifierDisplayName = (modifierDisplayName
                        .replace("@value1@", valuesColor +values[0]+"&7")
                        .replace("@value2@", valuesColor +values[1]+"&7")).indent(2);
            }
        }
        return modifierDisplayName;
    }

    @Override
    public <subType extends Enum<subType> & AffixTableSelector> void renderDescription(Item itemData, List<String> itemLore, subType itemSubtype) {
        Weapon weaponData = (Weapon) itemData;

        itemLore.add("");
        itemLore.add(color("&7 Item Level: " + "&f&l" + weaponData.getIlvl()));
        String passiveString = " &7Passive: " + weaponData.getImplicit().getDisplayName() + " ";
        itemLore.add(ColorUtils.translateColorCodes(passiveString));
        itemLore.add("");
    }

    @Override
    public void renderTag(Item itemData, List<String> itemLore) {
        Weapon weaponData = (Weapon) itemData;
        ItemRarities rarity = weaponData.getRarity();
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
        int starRating = weaponData.getStarRating();
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

    private void serializeTextComponent(List<String> lore, TextComponent textComponent, int indentation){
        String translatedString = ColorUtils.translateColorCodes(LegacyComponentSerializer.legacyAmpersand().serialize(textComponent));
        lore.add(translatedString.indent(indentation));
    }
    private TextComponent getDmgPrefixComponent(){
        return Component.text("DMG: ").color(NamedTextColor.GRAY);
    }

    private void serializeDamageLineComponent(Map<DamageTypes, int[]> damages, List<String> itemLore, int indent){
        for (DamageTypes dmgType : DamageTypes.values()){

            if (damages.containsKey(dmgType)){
                int[] extraDmg = damages.get(dmgType);
                TextComponent extraDmgComponent =
                        Component.text(dmgType.getCharacter()+" " + extraDmg[0] + " - " + extraDmg[1])
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
