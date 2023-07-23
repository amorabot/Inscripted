package com.amorabot.rpgelements.components.CraftingComponents.Builders;

import com.amorabot.rpgelements.Crafting.ItemRarities;
import com.amorabot.rpgelements.Crafting.RangeTypes;
import com.amorabot.rpgelements.Crafting.Weapons.Enums.DamageTypes;
import com.amorabot.rpgelements.Crafting.Weapons.Enums.WeaponTypes;
import com.amorabot.rpgelements.Crafting.Weapons.Modifiers.TierValuePair;
import com.amorabot.rpgelements.Crafting.Weapons.Modifiers.WeaponModifiers;
import com.amorabot.rpgelements.components.CraftingComponents.Items.BaseItem;
import com.amorabot.rpgelements.components.CraftingComponents.Items.Weapon;
import com.amorabot.rpgelements.utils.ColorUtils;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static com.amorabot.rpgelements.utils.Utils.color;

public class WeaponRenderer {

    public WeaponRenderer(){
    }
    public void render(BaseItem itemData, Weapon weaponData, ItemStack item){
        ItemMeta itemMeta = item.getItemMeta();

        List<String> lore = new ArrayList<>();
        renderDisplayName(itemData, weaponData, itemMeta);
        renderDamages(lore, itemData, weaponData);
        renderAllMods(lore, itemData, weaponData);
        renderRarityTag(lore, itemData);

        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
    }

    private void renderDisplayName(BaseItem itemData, Weapon weaponData, ItemMeta itemMeta){
        String displayNameColor = weaponData.getNameColor((WeaponTypes) itemData.getCategory());
        assert itemMeta != null;
        itemMeta.setDisplayName(ColorUtils.translateColorCodes("&" + displayNameColor + weaponData.getName()));
    }
    private void renderDamages(List<String> lore, BaseItem itemData, Weapon weaponData){
        int[] dmgRange = weaponData.getBaseDmg().get(DamageTypes.PHYSICAL);
        String baseDamage = "&c DMG: " + dmgRange[0] + " - " + dmgRange[1];
        lore.add(color(baseDamage));

        for (DamageTypes dmgType : DamageTypes.values()){
            if (weaponData.getBaseDmg().containsKey(dmgType)){
                switch (dmgType){
                    case PHYSICAL:
                        //modificar o dmg base
                        break;
                    case FIRE:
                        int[] fireRange = weaponData.getBaseDmg().get(DamageTypes.FIRE);
                        String fireDmg = "       &4" + fireRange[0] + " - " + fireRange[1];
                        lore.add(color(fireDmg));
                        break;
                    case COLD:
                        break;
                    case LIGHTNING:
                        break;
                    case ABYSSAL:
                        int[] abyssRange = weaponData.getBaseDmg().get(DamageTypes.ABYSSAL);
                        String abyssDmg = "       &d" + abyssRange[0] + " - " + abyssRange[1];
                        lore.add(color(abyssDmg));
                        break;
                }
            }
        }

        if (itemData.getRarity() != ItemRarities.COMMON){
            lore.add(color("&8 _______________________ "));
        }
    }

    private void renderAllMods(List<String> lore, BaseItem itemData, Weapon weaponData){
        renderModPool(lore, itemData.getPrefixes(), weaponData);
        renderModPool(lore, itemData.getSuffixes(), weaponData);
    }
    private void renderModPool(List<String> lore, List<Enum<?>> affixList, Weapon weaponData){
        for (Enum<?> mod : affixList){
            WeaponModifiers weaponMod = (WeaponModifiers) mod;
            TierValuePair modValues = weaponData.getModifierTierMap().get(weaponMod);

            int[] values = modValues.value();
            String valuesColor = "&#95dbdb";

            String modDisplayName = " " + weaponMod.getDisplayName();
            String modifiedString;
            if (weaponMod.getRangeType() == RangeTypes.SINGLE_RANGE){
                modifiedString = modDisplayName.replace("#", (valuesColor + values[0] + "&7"));
            } else {
                modifiedString = modDisplayName.replace("#", (valuesColor + values[0]));
                modifiedString = modifiedString.replace("@", (valuesColor + values[1] + "&7"));
            }
            lore.add(ColorUtils.translateColorCodes("&7" + modifiedString));
        }
    }
    private void renderRarityTag(List<String> lore, BaseItem itemData){
        lore.add("");
        String rarityString = itemData.getRarity().toString();
        String finalTag = "";
        switch (itemData.getRarity()){
            case COMMON:
                finalTag += "&F";
                break;
            case MAGIC:
                finalTag += "&9";
                break;
            case RARE:
                finalTag += "&E";
                break;
        }
        finalTag += "&l" + rarityString + " &6&l[&7" + itemData.getIlvl() + "&6&l]";
        lore.add(ColorUtils.translateColorCodes(finalTag));
    }
}
