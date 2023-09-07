package com.amorabot.rpgelements.components.Items.Weapon;

import com.amorabot.rpgelements.components.Items.Abstract.Item;
import com.amorabot.rpgelements.components.Items.Abstract.ItemRenderer;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.Affix;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemRarities;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.RangeTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.DamageTypes;
import com.amorabot.rpgelements.components.Items.Interfaces.AffixTableSelector;
import com.amorabot.rpgelements.components.Items.DataStructures.Modifier;
import com.amorabot.rpgelements.utils.ColorUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static com.amorabot.rpgelements.utils.Utils.color;

public class BasicWeaponRenderer implements ItemRenderer {

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

        int[] dmgRange = weaponData.getBaseDamage().get(DamageTypes.PHYSICAL);
        String baseDamage = "&c DMG: " + dmgRange[0] + " - " + dmgRange[1];
        itemLore.add(color(baseDamage));

        for (DamageTypes dmgType : DamageTypes.values()){
            if (dmgType == DamageTypes.PHYSICAL){continue;}
            String newDamageString = "";
            if (weaponData.getBaseDamage().containsKey(dmgType)){
                String colorString = dmgType.getColor();
                int[] extraDmgRange = weaponData.getBaseDamage().get(dmgType);
                newDamageString = (colorString + extraDmgRange[0] + " - " + extraDmgRange[1]).indent(7);
            }
            if (!newDamageString.equals("")){
                itemLore.add(ColorUtils.translateColorCodes(newDamageString));
            }
        }

        if (itemData.getRarity() != ItemRarities.COMMON){
            itemLore.add(color("-div-"));
        }
    }

    @Override
    public void renderMods(Item itemData, List<String> itemLore) {
        Weapon weaponData = (Weapon) itemData;
        String valuesColor = "&#95dbdb"; //pale baby blue
//        String valuesColor = "&#63bf41";//"&#41bf76"; //"&#63a889"; //"&#5bc998"; //pale blue-ish green

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
            String modifierDisplayName = "&7" + mod.getModifier().getDisplayName();
            RangeTypes rangeType = mod.getModifier().getRangeType();
            switch (rangeType){
                case SINGLE_VALUE -> {}
                case SINGLE_RANGE -> modifierDisplayName = (modifierDisplayName
                        .replace("@value1@", valuesColor+mod.getValue()[0]+"&7")).indent(1);
                case DOUBLE_RANGE -> {
                    int[] values = mod.getValue();
                    modifierDisplayName = (modifierDisplayName
                            .replace("@value1@", valuesColor+values[0]+"&7")
                            .replace("@value2@", valuesColor+values[1]+"&7")).indent(1);
                }
            }
            itemLore.add(ColorUtils.translateColorCodes(modifierDisplayName));
        }
        for (Modifier<WeaponModifiers> mod : suffixes){
            String modifierDisplayName = "&7" + mod.getModifier().getDisplayName();
            RangeTypes rangeType = mod.getModifier().getRangeType();
            switch (rangeType){
                case SINGLE_VALUE -> {}
                case SINGLE_RANGE -> modifierDisplayName = (modifierDisplayName
                        .replace("@value1@", valuesColor+mod.getValue()[0]+"&7")).indent(1);
                case DOUBLE_RANGE -> {
                    int[] values = mod.getValue();
                    modifierDisplayName = (modifierDisplayName
                            .replace("@value1@", valuesColor+values[0]+"&7")
                            .replace("@value2@", valuesColor+values[1]+"&7")).indent(1);
                }
            }
            itemLore.add(ColorUtils.translateColorCodes(modifierDisplayName));
        }
        itemLore.add(color("-div-"));
    }

    @Override
    public <subType extends Enum<subType> & AffixTableSelector> void renderDescription(Item itemData, List<String> itemLore, subType itemSubtype) {
        Weapon weaponData = (Weapon) itemData;
        WeaponTypes subType = (WeaponTypes) itemSubtype;

        itemLore.add("");
        itemLore.add(color("&7 Item Level: " + "&f&l" + weaponData.getIlvl()));
        String passiveString = " &7Passive: " + weaponData.getImplicit().getDisplayName();
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

}
